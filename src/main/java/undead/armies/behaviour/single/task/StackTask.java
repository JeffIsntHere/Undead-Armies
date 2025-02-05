package undead.armies.behaviour.single.task;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import undead.armies.Util;
import undead.armies.base.GetSingle;
import undead.armies.behaviour.single.Single;

import java.util.List;

public class StackTask extends BaseTask
{
    public static final AABB stackingBox = new AABB(-2.0,-1.0,-2.0,2.0,1.0,2.0);
    @Override
    public boolean handleTask(@NotNull Single single)
    {
        if(Util.isMoving(single))
        {
            return false;
        }
        final LivingEntity target = single.pathfinderMob.getTarget();
        if(target == null || target.position().y <= single.currentPosition.y || single.pathfinderMob.isPassenger() || single.pathfinderMob.isVehicle())
        {
            return false;
        }
        final List<Entity> entities = single.pathfinderMob.level().getEntities(single.pathfinderMob, stackingBox);
        for(Entity entity : entities)
        {
            if(!(entity instanceof GetSingle))
            {
                continue;
            }
            final PathfinderMob pathfinderMob = ((GetSingle) entity).getSingle().pathfinderMob;
            if(pathfinderMob.isDeadOrDying() || pathfinderMob.getTarget() != target)
            {
                continue;
            }
            if(pathfinderMob.isVehicle())
            {
                final List<Entity> passengers = pathfinderMob.getPassengers();
                for(Entity passenger : passengers)
                {
                    passenger.startRiding(single.pathfinderMob);
                }
            }
            single.pathfinderMob.startRiding(pathfinderMob);
            return true;
        }
        return false;
    }
}
