package undead.armies.behaviour.group.task;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.single.Single;

public abstract class BaseTask
{
    public Single starter;
    public boolean deleted = false;
    protected final int taskSelectorIndex;
    //where this task is stored in Group class.
    public BaseTask(@NotNull final Single starter, final int taskSelectorIndex)
    {
        this.starter = starter;
        this.taskSelectorIndex = taskSelectorIndex;
    }
    protected void addBackToGroup()
    {
        if(this.starter.groupStorage != null)
        {
            this.starter.groupStorage.group.addTask(this, this.taskSelectorIndex);
            this.deleted = false;
        }
    }
    public abstract void handleTask(@NotNull final Single single, @NotNull final LivingEntity target);
}
