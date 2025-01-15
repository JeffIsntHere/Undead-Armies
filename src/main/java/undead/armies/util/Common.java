package undead.armies.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import undead.armies.behaviour.group.Stacking;
import undead.armies.util.container.MobSingle;
import undead.armies.util.container.MobGroup;

import java.util.ArrayList;

public final class Common
{
    public static final ArrayList<MobGroup> mobGroupArrayList = new ArrayList<>();
    public static void processTick(final PathfinderMob pathfinderMob, final MobSingle mobSingle)
    {
        if(pathfinderMob.tickCount % mobSingle.actionCooldown != 0)
        {
            return;
        }
        for(MobGroup mobGroup : Common.mobGroupArrayList)
        {
            mobGroup.tick();
        }
    }
    public static void onHitListener(final LivingDamageEvent.Post livingDamageEventPost, final LivingEntity livingEntity, final MobSingle mobSingle)
    {
        Stacking.onHitListener(livingDamageEventPost, livingEntity, mobSingle);
    }
    public static void addAdditionalData(final CompoundTag compoundTag, final MobSingle mobSingle)
    {
        mobSingle.saveInCompoundTag(compoundTag);
    }
    public static MobSingle readAdditionalData(final CompoundTag compoundTag)
    {
        return new MobSingle(compoundTag);
    }
}
