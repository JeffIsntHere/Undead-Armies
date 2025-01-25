package undead.armies.behaviour.single.task;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.single.Single;

public class SprintTask extends BaseTask
{
    protected boolean sprinting = false;
    @Override
    public boolean handleTask(@NotNull Single single)
    {
        if(single.pathfinderMob.getTarget() == null)
        {
            return false;
        }
        if(this.sprinting)
        {
            this.sprinting = false;
            single.pathfinderMob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 2));
        }
        else
        {
            this.sprinting = true;
            single.pathfinderMob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, 4));
        }
        return true;
    }
}
