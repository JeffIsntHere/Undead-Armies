package undead.armies.behaviour;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import undead.armies.UndeadArmies;
import undead.armies.base.Resettable;
import undead.armies.behaviour.task.BaseTask;
import undead.armies.behaviour.task.TaskUtil;
import undead.armies.behaviour.type.BaseType;
import undead.armies.behaviour.type.TypeUtil;
import undead.armies.parser.loot.LootParser;

public class Single implements Resettable
{
    @NotNull
    public final PathfinderMob pathfinderMob;
    @NotNull
    public BaseType baseType;
    public boolean initialized = false;
    @NotNull
    public BaseTask currentTask;
    public int currentTaskLength;
    @NotNull
    public Vec3 lastPosition;
    @NotNull
    public Vec3 currentPosition;
    public void reset()
    {
        this.lastPosition = pathfinderMob.position();
        this.currentPosition = this.lastPosition;
    }
    protected boolean droppedLoot = false;
    public void tick()
    {
        if(this.pathfinderMob.level().isClientSide)
        {
            return;
        }
        if(this.pathfinderMob.isDeadOrDying())
        {
            if(!this.droppedLoot)
            {
                LootParser.instance.dropForPathfinderMob(this.pathfinderMob);
            }
            this.droppedLoot = true;
            return;
        }
        this.currentPosition = pathfinderMob.position();

        final int upperBound = this.currentTaskLength;
        for(int i = 0; i < upperBound; i++)
        {
            final boolean result = this.currentTask.handleTask(this);
            this.currentTask = this.currentTask.nextTask;
            if(result)
            {
                break;
            }
        }
        if(!this.initialized)
        {
            this.initialized = true;
            this.baseType.init(this);
        }
        this.baseType.additionalTick(this);

        this.lastPosition = this.currentPosition;
    }
    public void setMobType(final int id)
    {
        this.initialized = true;
        final BaseType typeFromId = TypeUtil.instance.getMobType(id);
        if(typeFromId == null)
        {
            this.baseType = TypeUtil.instance.getMobType(pathfinderMob.getRandom());
            this.baseType.init(this);
        }
        else
        {
            this.baseType = typeFromId;
        }
    }
    public Single(final PathfinderMob pathfinderMob)
    {
        this.pathfinderMob = pathfinderMob;
        this.baseType = TypeUtil.instance.getMobType(pathfinderMob.getRandom());
        this.lastPosition = pathfinderMob.position();
        this.currentPosition = this.lastPosition;
        final Pair<Integer, BaseTask> outputValue = TaskUtil.instance.getTask(this);
        this.currentTask = outputValue.getRight();
        this.currentTaskLength = outputValue.getLeft();
    }
}
