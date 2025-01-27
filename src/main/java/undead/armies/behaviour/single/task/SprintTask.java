package undead.armies.behaviour.single.task;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.single.Single;

public class SprintTask extends BaseTask
{
    protected boolean sprinting = false;
    protected boolean stopMoving = false;
    protected int triggerAfter = 0;
    @Override
    public boolean handleTask(@NotNull Single single)
    {
        final int tickCount = single.pathfinderMob.tickCount;
        if(triggerAfter > tickCount)
        {
            return false;
        }
        if(this.stopMoving)
        {
            this.stopMoving = false;
            single.pathfinderMob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 5));
            triggerAfter = 120 + tickCount;
        }
        else if(this.sprinting)
        {
            this.sprinting = false;
            single.pathfinderMob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1));
            triggerAfter = 40 + tickCount;
            this.stopMoving = true;
        }
        else
        {
            if(single.pathfinderMob.getTarget() == null)
            {
                return false;
            }
            if(single.currentPosition.distanceTo(single.pathfinderMob.getTarget().position()) > 10.0d)
            {
                return false;
            }
            this.sprinting = true;
            single.pathfinderMob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 2));
            triggerAfter = 40 + tickCount;
        }
        return true;
    }
}
