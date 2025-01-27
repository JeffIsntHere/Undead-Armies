package undead.armies.behaviour.single.task;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.single.Single;

public class SprintTask extends BaseTask
{
    public static final double sprintDistance = 6.0d;
    public static final double alwaysSprintWhenDistanceIsThisFar = 20.0d;
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
            this.triggerAfter = 460 + tickCount;
        }
        else
        {
            if(single.pathfinderMob.getTarget() == null)
            {
                return false;
            }
            final double distance = single.currentPosition.distanceTo(single.pathfinderMob.getTarget().position());
            if(distance > SprintTask.sprintDistance && distance < SprintTask.alwaysSprintWhenDistanceIsThisFar && !single.pathfinderMob.isPassenger())
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
