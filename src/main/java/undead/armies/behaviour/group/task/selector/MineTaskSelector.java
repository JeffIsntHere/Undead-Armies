package undead.armies.behaviour.group.task.selector;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import undead.armies.behaviour.group.task.BaseTask;
import undead.armies.behaviour.group.task.Mine;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;

public class MineTaskSelector extends BaseTaskSelector
{
    public static final MineTaskSelector instance = new MineTaskSelector();
    public static final double maxDistanceFromTask = 12.0d;
    @Override
    public BaseTask getSuitableTask(final ArrayList<BaseTask> tasks, final Single single, final LivingEntity target, final int taskSelectorIndex)
    {
        if(!single.pathfinderMob.getNavigation().isStuck() && !single.pathfinderMob.getNavigation().isDone())
        {
            return null;
        }
        single.groupStorage.assignedTask = Mine.mineAdd;
        tasks.removeIf(baseTask ->
        {
            if(baseTask.starter == null)
            {
                return true;
            }
            if(baseTask.starter.pathfinderMob.isDeadOrDying())
            {
                baseTask.deleted = true;
                return true;
            }
            return false;
        });
        final Vec3 position = single.pathfinderMob.position();
        for(BaseTask baseTask : tasks)
        {
            if(baseTask instanceof Mine mine)
            {
                if(position.distanceTo(new Vec3(mine.mineTarget.getX(), mine.mineTarget.getY(), mine.mineTarget.getZ())) <= MineTaskSelector.maxDistanceFromTask)
                {
                    return baseTask;
                }
            }
        }
        return null;
    }
}
