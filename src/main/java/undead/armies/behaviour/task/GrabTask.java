package undead.armies.behaviour.task;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.Single;
import undead.armies.parser.config.type.DecimalType;
import undead.armies.parser.config.type.NumberType;

public class GrabTask extends BaseTask
{
    public static final NumberType maxSlowdown = new NumberType("maxSlowness", "the maximum level of slowness the target can have.", 5);
    public static final DecimalType grabDistance = new DecimalType("grabDistance", 2.0d);
    @Override
    public boolean handleTask(@NotNull Single single, final int arguments)
    {
        if((arguments & 1) != 1)
        {
            return false;
        }
        final LivingEntity target = single.pathfinderMob.getTarget();
        if(single.position().distanceTo(target.position()) > GrabTask.grabDistance.value)
        {
            return false;
        }
        int amplifier = Math.min((target.getEffect(MobEffects.MOVEMENT_SLOWDOWN) != null) ? target.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() + 1 : 1, GrabTask.maxSlowdown.value);
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, amplifier, true,false));
        single.pathfinderMob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 2, true,false));
        return true;
    }
}
