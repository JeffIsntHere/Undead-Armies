package undead.armies.util.container;

import java.util.ArrayList;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import undead.armies.behaviour.group.Stacking;

public class MobGroup
{
    enum GroupTask
    {
        stacking,
        idle
    }
    public final ArrayList<PathfinderMob> pathFinderMobArrayList = new ArrayList<>();
    public int tickCount = 0;
    public void tick()
    {
        if(tickCount == 0)
        {

        }
        tickCount++;
        if(tickCount >= pathFinderMobArrayList.size())
        {
            tickCount = 0;
        }
    }/*
    public static void onHitListener(final LivingDamageEvent.Post livingDamageEventPost, final LivingEntity livingEntity, final MobSingle mobSingle)
    {
        if(mobSingle.stack.flag)
        {
            mobSingle.stack.counter++;
            if(mobSingle.stack.counter == Stacking.hitCounterToMakeStackingMobsFall || livingDamageEventPost.getNewDamage() >= Stacking.hitDamageToMakeStackingMobsFall)
            {
                livingEntity.unRide();
                mobSingle.stack.counter = 0;
            }
        }
    }*/
}
