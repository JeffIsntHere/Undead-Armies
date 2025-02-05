package undead.armies.behaviour.task;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.single.Single;

public class DismountTask extends BaseTask
{
    protected AttributeInstance passengerSpeed = null;
    @Override
    public boolean handleTask(@NotNull Single single)
    {
        if(this.passengerSpeed == null)
        {
            this.passengerSpeed = single.pathfinderMob.getAttribute(Attributes.MOVEMENT_SPEED);
            if(this.passengerSpeed == null)
            {
                return false;
            }
        }
        final Entity vehicle = single.pathfinderMob.getVehicle();
        if(vehicle == null || !vehicle.onGround())
        {
            return false;
        }
        final AttributeInstance vehicleSpeed = ((LivingEntity) vehicle).getAttribute(Attributes.MOVEMENT_SPEED);
        if(vehicleSpeed == null || vehicleSpeed.getValue() + 0.1f < this.passengerSpeed.getValue())
        {
            single.pathfinderMob.stopRiding();
            return true;
        }
        final LivingEntity target = single.pathfinderMob.getTarget();
        if(target != null && target.position().y < single.currentPosition.y)
        {

        }
        return false;
    }
}
