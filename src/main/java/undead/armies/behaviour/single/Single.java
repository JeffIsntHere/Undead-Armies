package undead.armies.behaviour.single;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import undead.armies.base.Resettable;
import undead.armies.behaviour.group.GroupUtil;
import undead.armies.behaviour.group.GroupStorage;
import undead.armies.behaviour.single.task.BaseTask;
import undead.armies.behaviour.single.task.TaskUtil;
import undead.armies.behaviour.type.BaseType;
import undead.armies.behaviour.type.TypeUtil;
import undead.armies.parser.loot.LootParser;

public class Single implements Resettable
{
    @NotNull
    public final PathfinderMob pathfinderMob;
    @NotNull
    public final BaseType baseType;
    public GroupStorage groupStorage = null;
    @NotNull
    public BaseTask currentTask;
    public int currentTaskLength;
    @NotNull
    public Vec3 lastPosition;
    @NotNull
    public Vec3 currentPosition;
    public void attemptDismount()
    {
        if(!this.pathfinderMob.isPassenger())
        {
            return;
        }
        final AttributeInstance passengerSpeed = this.pathfinderMob.getAttribute(Attributes.MOVEMENT_SPEED);
        if(passengerSpeed == null)
        {
            return;
        }
        final Entity vehicle = this.pathfinderMob.getVehicle();
        if(vehicle == null || !vehicle.onGround())
        {
            return;
        }
        if(!(vehicle instanceof LivingEntity))
        {
            return;
        }
        final AttributeInstance vehicleSpeed = ((LivingEntity) vehicle).getAttribute(Attributes.MOVEMENT_SPEED);
        if(vehicleSpeed == null)
        {
            return;
        }
        if(vehicleSpeed.getBaseValue() > passengerSpeed.getBaseValue())
        {
            return;
        }
        this.pathfinderMob.stopRiding();
    }
    public void reset()
    {
        this.groupStorage = null;
        this.lastPosition = pathfinderMob.position();
        this.currentPosition = this.lastPosition;
    }
    public void doTick()
    {
        if(this.groupStorage != null)
        {
            this.groupStorage.group.doGroupTask(this);
        }
        this.baseType.additionalTick(this);
    }
    public void tick()
    {
        if(this.pathfinderMob.level().isClientSide)
        {
            return;
        }
        if(this.pathfinderMob.isDeadOrDying())
        {
            LootParser.instance.dropForPathfinderMob(this.pathfinderMob);
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
        //Util.clearHeldItem(this.pathfinderMob);
        if(this.groupStorage == null || !this.groupStorage.group.target.is(this.pathfinderMob.getTarget()))
        {
            this.attemptDismount();
            this.groupStorage = GroupUtil.instance.getGroupStorageThatAttacks(this.pathfinderMob.getTarget(), this);
        }
        else if(this.pathfinderMob.getTarget() == null)
        {
            this.pathfinderMob.setTarget(this.groupStorage.group.target);
        }
        this.doTick();
        this.lastPosition = this.currentPosition;
    }
    public Single(final PathfinderMob pathfinderMob)
    {
        this.pathfinderMob = pathfinderMob;
        this.baseType = TypeUtil.instance.getMobType(pathfinderMob.getRandom());
        this.lastPosition = pathfinderMob.position();
        this.currentPosition = this.lastPosition;
        final Pair<Integer, BaseTask> outputValue = TaskUtil.instance.getTask();
        this.currentTask = outputValue.getRight();
        this.currentTaskLength = outputValue.getLeft();
    }
}
