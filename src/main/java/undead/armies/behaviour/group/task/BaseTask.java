package undead.armies.behaviour.group.task;

import net.minecraft.world.entity.LivingEntity;
import undead.armies.behaviour.single.Single;

public abstract class BaseTask
{
    public Single starter;
    //where this task is stored in Group class.
    public BaseTask(final Single starter)
    {
        this.starter = starter;
    }
    public abstract void handleTask(final Single single, final LivingEntity target);
}
