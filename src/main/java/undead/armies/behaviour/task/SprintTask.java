package undead.armies.behaviour.task;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.Single;
import undead.armies.parser.config.type.DecimalType;
import undead.armies.parser.config.type.NumberType;

public class SprintTask extends BaseTask
{
    public static final DecimalType sprintDistance = new DecimalType("sprintDistance", "how close the undead mob needs to be to the target to start running", 6.0);
    public static final DecimalType alwaysSprintWhenDistanceIsThisFar = new DecimalType("alwaysSprintWhenDistanceIsThisFar", "how far the undead mob needs to be to the target to start running", 20.0);
    public static final NumberType cooldown = new NumberType("cooldown", "cooldown between sprinting." , 460);
    protected boolean sprinting = false;
    protected int triggerAfter = 0;
    @Override
    public boolean handleTask(@NotNull Single single)
    {
        final int tickCount = single.pathfinderMob.tickCount;
        if(this.triggerAfter > tickCount)
        {
            return false;
        }
        else if(this.sprinting)
        {
            this.sprinting = false;
            single.pathfinderMob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1,true,false));
            this.triggerAfter = SprintTask.cooldown.value + tickCount;
        }
        else
        {
            if(single.pathfinderMob.getTarget() == null)
            {
                return false;
            }
            final double distance = single.currentPosition.distanceTo(single.pathfinderMob.getTarget().position());
            if(distance > SprintTask.sprintDistance.value && distance < SprintTask.alwaysSprintWhenDistanceIsThisFar.value && !single.pathfinderMob.isPassenger())
            {
                return false;
            }
            this.sprinting = true;
            single.pathfinderMob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 2,true,false));
            this.triggerAfter = 40 + tickCount;
        }
        return true;
    }
}
