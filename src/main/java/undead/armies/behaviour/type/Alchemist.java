package undead.armies.behaviour.type;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.AABB;
import undead.armies.Util;
import undead.armies.base.GetSingle;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;
import java.util.List;

public class Alchemist extends BaseType
{
    public static final Alchemist alchemist = new Alchemist();
    public static final AABB throwingBox = new AABB(-10.0,-10.0,-10.0,10.0,10.0,10.0);
    public static final int throwingCooldown = Alchemist.alchemist.actionCooldown() * 6;
    @Override
    public float chance()
    {
        return 0.2f;
    }
    @Override
    public int actionCooldown()
    {
        return 20;
    }
    @Override
    public void additionalTick(final Single single)
    {
        /*if(single.pathfinderMob.tickCount % Alchemist.throwingCooldown != 0)
        {
            return;
        }
        final List<Entity> entitiesInThrowingRange = single.pathfinderMob.level().getEntities(single.pathfinderMob, Alchemist.throwingBox);
        LivingEntity currentTarget = single.pathfinderMob;
        for(Entity entity : entitiesInThrowingRange)
        {
            if(entity instanceof LivingEntity && entity instanceof GetSingle)
            {
                currentTarget = (LivingEntity) entity;
            }
        }
        if(currentTarget.getMaxHealth() == currentTarget.getHealth())
        {

        }
        LivingEntity currentTarget = single.pathfinderMob;
        float highestTargetWeight = 1.0f/single.pathfinderMob.getHealth();
        for(LivingEntity livingEntity : targets)
        {
            final float currentTargetWeight = 1.0f/livingEntity.getHealth();
            if(currentTargetWeight >= highestTargetWeight)
            {
                highestTargetWeight = currentTargetWeight;
                currentTarget = livingEntity;
            }
        }
        if(currentTarget.getHealth() == currentTarget.getMaxHealth() && single.groupStorage != null)
        {
            currentTarget = single.groupStorage.group.target;
        }
        Util.throwPotion(single.pathfinderMob, currentTarget, PotionContents.createItemStack(Items.SPLASH_POTION, Potions.HARMING), 0.75f, 1.2f);
    */}
}
