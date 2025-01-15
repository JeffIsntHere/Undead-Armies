package undead.armies.util.container;

import java.util.ArrayList;

import net.minecraft.world.entity.PathfinderMob;

public class MobGroup
{
    public static final int groupActionCooldown = 10;
    public final ArrayList<PathfinderMob> pathFinderMobArrayList = new ArrayList<>();
    public int tickCount = 0;
    public int tickMethodCallCounter = 0;
    public int removePathFinderMobMethodCallCounter = 0;
    public void tick()
    {
        this.tickMethodCallCounter++;
        if(this.tickMethodCallCounter + this.removePathFinderMobMethodCallCounter >= pathFinderMobArrayList.size())
        {
            if(this.removePathFinderMobMethodCallCounter != 0)
            {
                this.cleanPathFinderMobArrayList();
                this.removePathFinderMobMethodCallCounter = 0;
            }
            if(this.tickCount % MobGroup.groupActionCooldown == 0)
            {

            }
            this.tickCount++;
            this.tickMethodCallCounter = 0;
        }
    }
    public void cleanPathFinderMobArrayList()
    {
        this.pathFinderMobArrayList.removeIf(pathFinderMob -> (pathFinderMob.isDeadOrDying()));
    }
    public void removePathFinderMob()
    {
        this.removePathFinderMobMethodCallCounter++;
    }
    /*
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
