package undead.armies.behaviour.group.task.selector;

import net.minecraft.world.entity.LivingEntity;
import undead.armies.behaviour.group.task.BaseTask;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;

public abstract class BaseTaskSelector
{
    public BaseTask getSuitableTask(final ArrayList<BaseTask> tasks, final Single single, final LivingEntity target)
    {
        if(tasks.size() > 0)
        {
            return tasks.get(0);
        }
        return null;
    }
}
