package undead.armies.behaviour.task;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import undead.armies.UndeadArmies;
import undead.armies.Util;
import undead.armies.base.GetSingle;
import undead.armies.behaviour.Single;

import java.util.List;

public class StackTask extends BaseTask
{
    @Override
    public boolean handleTask(@NotNull Single single)
    {
        if(Util.isMoving(single))
        {
            return false;
        }
        final LivingEntity target = single.pathfinderMob.getTarget();
        if(target == null || target.position().y <= single.currentPosition.y || single.pathfinderMob.isVehicle())
        {
            return false;
        }
        UndeadArmies.logger.debug("attempting to find a partner to stack with!");
        final double x = single.currentPosition.x;
        final double y = single.currentPosition.y;
        final double z = single.currentPosition.z;
        final AABB stackingBox = new AABB(x - 3.0d, y - 3.0d, z - 3.0d, x + 3.0d, y + 3.0d, z + 3.0d);
        final List<Entity> entities = single.pathfinderMob.level().getEntities(single.pathfinderMob, stackingBox);
        UndeadArmies.logger.debug("found : " + entities.size() + " potential partners");
        for(Entity entity : entities)
        {
            UndeadArmies.logger.debug("checking!");
            if(!(entity instanceof GetSingle))
            {
                continue;
            }
            final PathfinderMob pathfinderMob = ((GetSingle) entity).getSingle().pathfinderMob;
            if(pathfinderMob.isDeadOrDying() || pathfinderMob.getTarget() != target || pathfinderMob.is(single.pathfinderMob) || pathfinderMob.position().y < y)
            {
                UndeadArmies.logger.debug("check failed 2!");
                continue;
            }
            if(pathfinderMob.isVehicle())
            {
                UndeadArmies.logger.debug("check detour 1!");
                final List<Entity> passengers = pathfinderMob.getPassengers();
                Entity currentPathfinderMob = pathfinderMob;
                while(currentPathfinderMob.isVehicle())
                {
                    currentPathfinderMob = currentPathfinderMob.getPassengers().getFirst();
                }
                for(Entity passenger : passengers)
                {
                    UndeadArmies.logger.debug("loopin!");
                    if(passenger.is(currentPathfinderMob))
                    {
                        continue;
                    }
                    passenger.startRiding(currentPathfinderMob);
                }
            }
            UndeadArmies.logger.debug("found : one!");
            single.pathfinderMob.startRiding(pathfinderMob);
            return true;
        }
        UndeadArmies.logger.debug("failed!");
        return false;
    }
}
