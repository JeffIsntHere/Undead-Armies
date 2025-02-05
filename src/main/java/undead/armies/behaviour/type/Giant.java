package undead.armies.behaviour.type;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.Single;
import undead.armies.behaviour.task.BaseTask;
import undead.armies.behaviour.task.StackTask;

public class Giant extends BaseType
{
    public static final Giant giant = new Giant();
    @Override
    public float chance()
    {
        return 0.15f;
    }
    @Override
    public int actionCooldown()
    {
        return 30;
    }

    @Override
    public int getId()
    {
        return 2;
    }

    @Override
    public int getHitPower()
    {
        return 10;
    }

    @Override
    public void init(final @NotNull Single single)
    {
        final AttributeMap attributeMap = single.pathfinderMob.getAttributes();
        final AttributeInstance scaleAttribute = attributeMap.getInstance(Attributes.SCALE);
        final AttributeInstance attackAttribute = attributeMap.getInstance(Attributes.ATTACK_DAMAGE);
        final AttributeInstance healthAttribute = attributeMap.getInstance(Attributes.MAX_HEALTH);
        final AttributeInstance speedAttribute = attributeMap.getInstance(Attributes.MOVEMENT_SPEED);
        final AttributeInstance knockBackAttribute = attributeMap.getInstance(Attributes.ATTACK_KNOCKBACK);
        scaleAttribute.setBaseValue(scaleAttribute.getBaseValue() * 1.5d);
        attackAttribute.setBaseValue(attackAttribute.getBaseValue() * 2d);
        healthAttribute.setBaseValue(healthAttribute.getBaseValue() * 4d);
        speedAttribute.setBaseValue(speedAttribute.getBaseValue() * 1.5d);
        knockBackAttribute.setBaseValue(1.0d);
    }

    @Override
    public boolean canHoldItems()
    {
        return false;
    }

    @Override
    public boolean canDoTask(Class<? extends BaseTask> baseTaskSelector)
    {
        return !baseTaskSelector.isAssignableFrom(StackTask.class);
    }
}
