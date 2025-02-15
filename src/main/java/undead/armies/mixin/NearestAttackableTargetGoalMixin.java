package undead.armies.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import undead.armies.base.GetTargetType;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class NearestAttackableTargetGoalMixin<T extends LivingEntity> extends TargetGoal implements GetTargetType<T>
{
    @Shadow
    protected final Class<T> targetType;

    public NearestAttackableTargetGoalMixin(Mob pMob, boolean pMustSee)
    {
        super(pMob, pMustSee);
        targetType = null;
    }
    @Override
    public Class<T> targetType()
    {
        return this.targetType;
    }
}
