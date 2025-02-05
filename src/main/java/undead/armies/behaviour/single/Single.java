package undead.armies.behaviour.single;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
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
    public final BaseType baseType;
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
        if(this.pathfinderMob.tickCount % this.baseType.actionCooldown() != 0)
        {
            final int upperBound = this.currentTaskLength;
            for(int i = 0; i < upperBound; i++)
            {
                if(this.currentTask.handleTask(this))
                {
                    break;
                }
                this.currentTask = this.currentTask.nextTask;
            }
            return;
        }
        this.baseType.additionalTick(this);
        this.lastPosition = this.currentPosition;
    }
    public Single(final PathfinderMob pathfinderMob)
    {
        this.pathfinderMob = pathfinderMob;
        final BaseType typeFromId;
        {
            final CompoundTag data = new CompoundTag();
            pathfinderMob.readAdditionalSaveData(data);
            typeFromId = TypeUtil.instance.getMobType(data.getInt("MobType"));
        }
        if(typeFromId == null)
        {
            this.baseType = TypeUtil.instance.getMobType(pathfinderMob.getRandom());
            this.baseType.init(this);
        }
        else
        {
            this.baseType = typeFromId;
        }
        this.lastPosition = pathfinderMob.position();
        this.currentPosition = this.lastPosition;
        final Pair<Integer, BaseTask> outputValue = TaskUtil.instance.getTask(this);
        this.currentTask = outputValue.getRight();
        this.currentTaskLength = outputValue.getLeft();
    }
}
