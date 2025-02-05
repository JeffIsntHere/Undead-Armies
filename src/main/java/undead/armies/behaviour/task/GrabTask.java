package undead.armies.behaviour.task;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.single.Single;

public class GrabTask extends BaseTask
{
    public static final double grabDistance = 2.0d;
    @Override
    public boolean handleTask(@NotNull Single single)
    {
        final LivingEntity target = single.pathfinderMob.getTarget();
        if(target == null)
        {
            return false;
        }
        if(single.currentPosition.distanceTo(target.position()) > GrabTask.grabDistance)
        {
            return false;
        }
        int amplifier = (target.getEffect(MobEffects.MOVEMENT_SLOWDOWN) != null) ? target.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() + 1 : 1;
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, amplifier, true,false));
        single.pathfinderMob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 2, true,false));
        return true;
    }
}
