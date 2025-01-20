package undead.armies.behaviour.group.task.selector;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.group.task.BaseTask;

import java.util.ArrayList;

public interface TickableTaskSelector
{
    void tick(@NotNull final ArrayList<BaseTask> tasks, @NotNull final LivingEntity target);
}
