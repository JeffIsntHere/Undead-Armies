package undead.armies.behaviour.task;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import undead.armies.base.GetSingle;
import undead.armies.behaviour.Single;
import undead.armies.parser.config.type.NumberType;

import java.util.List;

public class StackTask extends BaseTask
{
    public static final NumberType cooldown = new NumberType("cooldown", "cooldown for each stacking attempt.", 20);
    public int triggerAfter = 0;
    @Override
    public boolean handleTask(@NotNull Single single, final int arguments)
    {
        triggerAfter--;
        if(triggerAfter > 0 || (arguments & 2) == 2)
        {
            return false;
        }
        triggerAfter = StackTask.cooldown.value;
        final LivingEntity target = single.pathfinderMob.getTarget();
        if((arguments & 1) != 1 || target.position().y <= single.position().y)
        {
            return false;
        }
        final double x = single.position().x;
        final double y = single.position().y;
        final double z = single.position().z;
        final AABB stackingBox = new AABB(x - 1.0d, y - 0.1d, z - 1.0d, x + 1.0d, y + 0.1d, z + 1.0d);
        final List<Entity> entities = single.pathfinderMob.level().getEntities(single.pathfinderMob, stackingBox);
        for(Entity entity : entities)
        {
            if(!(entity instanceof GetSingle))
            {
                continue;
            }
            final PathfinderMob pathfinderMob = ((GetSingle) entity).getSingle().pathfinderMob;
            if(pathfinderMob.isDeadOrDying() || pathfinderMob.getTarget() != target || pathfinderMob.is(single.pathfinderMob) || pathfinderMob.position().y < y)
            {
                continue;
            }
            if(pathfinderMob.isVehicle())
            {
                final List<Entity> passengers = pathfinderMob.getPassengers();
                Entity currentPathfinderMob = pathfinderMob;
                while(currentPathfinderMob.isVehicle())
                {
                    currentPathfinderMob = currentPathfinderMob.getPassengers().getFirst();
                }
                for(Entity passenger : passengers)
                {
                    if(passenger.is(currentPathfinderMob))
                    {
                        continue;
                    }
                    passenger.startRiding(currentPathfinderMob);
                }
            }
            single.pathfinderMob.startRiding(pathfinderMob);
            return true;
        }
        return false;
    }
}
