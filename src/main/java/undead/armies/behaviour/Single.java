package undead.armies.behaviour;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import undead.armies.UndeadArmies;
import undead.armies.base.GetSingle;
import undead.armies.base.Resettable;
import undead.armies.behaviour.group.Group;
import undead.armies.behaviour.group.GroupUtil;
import undead.armies.behaviour.task.BaseTask;
import undead.armies.behaviour.task.TaskUtil;
import undead.armies.behaviour.task.Argument;
import undead.armies.misc.Util;
import undead.armies.parser.config.type.DecimalType;

import java.util.List;

public class Single implements Resettable
{
    public static DecimalType recruitChance = new DecimalType("recruitChance", "chance for an undead mob to recruit other undead mobs to attack a target.", 0.2d);
    @NotNull
    public final PathfinderMob pathfinderMob;
    @NotNull
    public BaseTask currentTask;
    public int currentTaskLength;
    @NotNull
    public Vec3 lastPosition;
    public Group group = null;
    public void reset()
    {
        this.lastPosition = pathfinderMob.position();
    }
    public Argument argument = new Argument();
    //mix into this to add your own arguments
    public void updateArguments()
    {
        this.argument.value = 0;
        if(this.pathfinderMob.getTarget() != null)
        {
            this.argument.value |= 1;
        }
        if(Util.isMoving(this))
        {
            this.argument.value |= 2;
        }
        if(this.pathfinderMob.onGround())
        {
            this.argument.value |= 4;
        }
        if(this.pathfinderMob.isPassenger())
        {
            this.argument.value |= 8;
        }
        if(this.pathfinderMob.isVehicle())
        {
            this.argument.value |= 16;
        }
    }
    public Vec3 position()
    {
        return this.pathfinderMob.position();
    }
    public void setGroup(@NotNull final Group group)
    {
        this.group = group;
        this.pathfinderMob.setTarget(group.target);
    }
    public void beingTargetBy(@NotNull final LivingEntity livingEntity)
    {
        if(GroupUtil.instance.isInvalidTarget(livingEntity))
        {
            return;
        }
        if(this.pathfinderMob.getTarget() == null)
        {
            this.pathfinderMob.setTarget(livingEntity);
            return;
        }
        if(this.group == null)
        {
            this.group = new Group(livingEntity);
            this.recruit();
        }
        else
        {
            this.group.trySplit(this, livingEntity);
        }
    }
    public void recruit()
    {
        final double x = this.position().x;
        final double y = this.position().y;
        final double z = this.position().z;
        final AABB checkingBox = new AABB(x - 10.0d, y - 3.0d, z - 10.0d, x + 10.0d, y + 3.0d, z + 10.0d);
        final List<Entity> entities = this.pathfinderMob.level().getEntities(this.pathfinderMob, checkingBox);
        final LivingEntity target = this.pathfinderMob.getTarget();
        double requiredCapability = GroupUtil.instance.getCapability(target) - GroupUtil.instance.getCapability(this.pathfinderMob);
        if(requiredCapability <= 0)
        {
            return;
        }
        for(Entity entity : entities)
        {
            if(entity instanceof GetSingle getSingle)
            {
                final Single single = getSingle.getSingle();
                final double power = GroupUtil.instance.getCapability(single.pathfinderMob);
                if(single.group == null)
                {
                    single.setGroup(this.group);
                    requiredCapability -= power;
                }
                else if(!single.group.tryMerge(this.group) && GroupUtil.instance.shouldJoin(single, target))
                {
                    single.setGroup(this.group);
                    requiredCapability -= power;
                }
                if(requiredCapability <= 0)
                {
                    return;
                }
            }
        }
    }
    public void tick()
    {
        if(this.pathfinderMob.level().isClientSide)
        {
            return;
        }
        this.updateArguments();
        if(this.group != null)
        {
            this.group.tick(this, this.argument);
        }
        final int upperBound = this.currentTaskLength;
        for(int i = 0; i < upperBound; i++)
        {
            final boolean result = this.currentTask.handleTask(this, this.argument);
            this.currentTask = this.currentTask.nextTask;
            if(result)
            {
                break;
            }
        }
        this.lastPosition = pathfinderMob.position();
    }
    public void hit(final LivingDamageEvent.Pre damageEvent)
    {
        if(this.group != null)
        {
            if(this.pathfinderMob.getRandom().nextDouble() < Single.recruitChance.value)
            {
                this.recruit();
            }
            this.group.hit(this, damageEvent);
        }
        else if(damageEvent.getSource().getDirectEntity() instanceof LivingEntity livingEntity && GroupUtil.instance.isValidTarget(livingEntity))
        {
            this.group = new Group(livingEntity);
            this.recruit();
        }
    }
    public Single(final PathfinderMob pathfinderMob)
    {
        this.pathfinderMob = pathfinderMob;
        this.lastPosition = pathfinderMob.position();
        final Pair<Integer, BaseTask> outputValue = TaskUtil.instance.getTask(this);
        this.currentTask = outputValue.getRight();
        this.currentTaskLength = outputValue.getLeft();
    }
}
