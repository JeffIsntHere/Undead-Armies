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
    public static final NumberType duration = new NumberType("duration", "duration for sprinting", 80);
    public static final NumberType amplifier = new NumberType("amplifier", 1);
    protected int triggerAfter = 0;
    @Override
    public boolean handleTask(@NotNull Single single, final int arguments)
    {
        final int tickCount = single.pathfinderMob.tickCount;
        if(this.triggerAfter > tickCount || (arguments & 8) == 8 || (arguments & 1) != 1)
        {
            return false;
        }
        final double distance = single.position().distanceTo(single.pathfinderMob.getTarget().position());
        if(distance > SprintTask.sprintDistance.value && distance < SprintTask.alwaysSprintWhenDistanceIsThisFar.value)
        {
            return false;
        }
        this.triggerAfter = tickCount + SprintTask.cooldown.value;
        single.pathfinderMob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, SprintTask.duration.value, SprintTask.amplifier.value,true,false));
        return true;
    }
}
