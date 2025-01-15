package undead.armies.behaviour.type;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.AABB;
import undead.armies.behaviour.single.Single;
import undead.armies.Util;

import java.util.ArrayList;
import java.util.List;

public class Alchemist extends BaseType
{
    public static final Alchemist alchemist = new Alchemist();
    public final ArrayList<LivingEntity> targets = new ArrayList<>();
    public static final AABB throwingBox = new AABB(-10.0,-10.0,-10.0,10.0,10.0,10.0);
    public static final int throwingCooldown = Alchemist.alchemist.actionCooldown() * 3;
    @Override
    public float chance()
    {
        return 0.1f;
    }
    @Override
    public int actionCooldown()
    {
        return 30;
    }
    @Override
    public void additionalTick(final Single single)
    {
        if(single.pathfinderMob.tickCount % Alchemist.throwingCooldown == 0)
        {
            final List<Entity> entitiesInThrowingRange = single.pathfinderMob.level().getEntities(single.pathfinderMob, Alchemist.throwingBox);
            targets.clear();
            for(Entity entity : entitiesInThrowingRange)
            {
                if(entity instanceof LivingEntity livingEntity)
                {
                    this.targets.add(livingEntity);
                }
            }
        }
        LivingEntity currentTarget = single.pathfinderMob;
        float highestTargetWeight = 1.0f/single.pathfinderMob.getHealth();
        for(LivingEntity livingEntity : this.targets)
        {
            final float currentTargetWeight = 1.0f/livingEntity.getHealth();
            if(currentTargetWeight > highestTargetWeight)
            {
                highestTargetWeight = currentTargetWeight;
                currentTarget = livingEntity;
            }
        }
        Util.throwPotion(single.pathfinderMob, currentTarget, PotionContents.createItemStack(Items.SPLASH_POTION, Potions.HARMING));
    }
}
