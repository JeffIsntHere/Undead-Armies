package undead.armies.behaviour.single;

import net.minecraft.world.entity.PathfinderMob;
import undead.armies.UndeadArmies;
import undead.armies.behaviour.group.Task;
import undead.armies.behaviour.group.Group;
import undead.armies.behaviour.group.task.BaseTask;
import undead.armies.behaviour.type.BaseType;
import undead.armies.behaviour.type.TypeUtil;

public class Single
{
    public final PathfinderMob pathfinderMob;
    public final BaseType baseType;
    public BaseTask baseTask = null;
    public int currentTask = Task.nothing;
    public int taskStorage = Task.nothing;
    public Group group = null;
    public Single(final PathfinderMob pathfinderMob)
    {
        UndeadArmies.logger.debug("created Single! " + pathfinderMob.getUUID());
        this.pathfinderMob = pathfinderMob;
        this.baseType = TypeUtil.instance.getMobType(pathfinderMob.getRandom());
    }
    public void doTick()
    {
        if(this.pathfinderMob.level().isClientSide)
        {
            return;
        }
        if(this.pathfinderMob.tickCount % this.baseType.actionCooldown() != 0)
        {
            return;
        }
        this.baseType.additionalTick(this);
        if(this.group == null)
        {
            this.group = Group.getGroupThatAttacks(this.pathfinderMob.getTarget());
        }
        else
        {
            this.group.doGroupTask(this);
        }
    }
}
