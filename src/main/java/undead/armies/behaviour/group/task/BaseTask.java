package undead.armies.behaviour.group.task;

import net.minecraft.world.entity.LivingEntity;
import undead.armies.behaviour.group.Task;
import undead.armies.behaviour.single.Single;

public abstract class BaseTask
{
    public Single starter;
    public BaseTask(final Single starter)
    {
        this.starter = starter;
    }
    public abstract void handleTask(final Single single, final LivingEntity target);
    //when overriding this, make sure to do these 4 things below.
    public void handleCleanUp(final Single single, final LivingEntity target)
    {
        single.group = null;
        single.baseTask = null;
        single.taskStorage = Task.nothing;
        single.currentTask = Task.nothing;
    }
}
