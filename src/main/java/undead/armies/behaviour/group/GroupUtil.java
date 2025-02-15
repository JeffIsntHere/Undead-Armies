package undead.armies.behaviour.group;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.Single;

public final class GroupUtil
{
    public static final GroupUtil instance = new GroupUtil();
    private GroupUtil(){};
    public boolean isValidTarget(final LivingEntity livingEntity)
    {
        return !livingEntity.isDeadOrDying() && !(livingEntity instanceof ServerPlayer serverPlayer && (serverPlayer.isCreative() || serverPlayer.isSpectator()));
    }
    public boolean isInvalidTarget(final LivingEntity livingEntity)
    {
        return livingEntity.isDeadOrDying() || (livingEntity instanceof ServerPlayer serverPlayer && (serverPlayer.isCreative() || serverPlayer.isSpectator()));
    }
    public boolean shouldJoin(@NotNull final Single single, final LivingEntity target)
    {
        if(single.pathfinderMob.getTarget() == null)
        {
            return true;
        }
        return GroupUtil.instance.getTargetPriority(single) < GroupUtil.instance.getTargetPriority(single, target);
    }
    public double getCapability(@NotNull final LivingEntity livingEntity)
    {
        final AttributeMap attributeMap = livingEntity.getAttributes();
        final AttributeInstance health = attributeMap.getInstance(Attributes.MAX_HEALTH);
        final AttributeInstance damage = attributeMap.getInstance(Attributes.ATTACK_DAMAGE);
        final AttributeInstance damageSpeed = attributeMap.getInstance(Attributes.ATTACK_SPEED);
        double output = 1.0d;
        if(health != null)
        {
            output *= health.getValue();
        }
        if(damage != null)
        {
            output *= damage.getValue();
        }
        if(damageSpeed != null)
        {
            output *= damageSpeed.getValue();
        }
        return output;
    }
    public double getTargetPriority(@NotNull final Single single, @NotNull final LivingEntity target)
    {
        if(target == null)
        {
            return 0.0d;
        }
        return GroupUtil.instance.getCapability(single.pathfinderMob) / GroupUtil.instance.getCapability(target) / single.pathfinderMob.distanceTo(target);
    }
    public double getTargetPriority(@NotNull final Single single)
    {
        final LivingEntity target = single.pathfinderMob;
        if(target == null)
        {
            return 0.0d;
        }
        return GroupUtil.instance.getCapability(single.pathfinderMob) / GroupUtil.instance.getCapability(target) / single.pathfinderMob.distanceTo(target);
    }
}
