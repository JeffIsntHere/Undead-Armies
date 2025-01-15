package undead.armies.behaviour.single;

import net.minecraft.world.entity.PathfinderMob;
import undead.armies.behaviour.group.Task;
import undead.armies.behaviour.group.Group;
import undead.armies.behaviour.type.BaseType;
import undead.armies.behaviour.type.TypeUtil;

public class Single
{
    public final PathfinderMob pathfinderMob;
    public final BaseType baseType;
    public int currentTask = Task.nothing;
    public int taskStorage = 0;
    public Group group = null;
    public Single(final PathfinderMob pathfinderMob)
    {
        this.pathfinderMob = pathfinderMob;
        this.baseType = TypeUtil.instance.getMobType(pathfinderMob.getRandom());
    }
    public void tick()
    {
        if(this.pathfinderMob.tickCount % this.baseType.actionCooldown() != 0)
        {
            return;
        }
        this.group = Group.getGroupThatAttacks(this.pathfinderMob.getTarget());
        if(this.group != null)
        {
            this.group.doGroupTask(this);
        }
        this.baseType.additionalTick(this);
    }
}
