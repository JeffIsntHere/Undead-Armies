package undead.armies.behaviour;

import com.mojang.realmsclient.client.Request;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import undead.armies.base.GetSingle;
import undead.armies.base.GetTargetType;
import undead.armies.base.Resettable;
import undead.armies.behaviour.group.Group;
import undead.armies.behaviour.group.GroupUtil;
import undead.armies.behaviour.task.TaskUtil;
import undead.armies.behaviour.task.argument.Argument;
import undead.armies.behaviour.task.argument.Situation;
import undead.armies.misc.BlockUtil;
import undead.armies.misc.Util;
import undead.armies.misc.blockcast.offset.XMinus;
import undead.armies.misc.blockcast.offset.XPlus;
import undead.armies.misc.blockcast.offset.ZMinus;
import undead.armies.misc.blockcast.offset.ZPlus;
import undead.armies.parser.config.type.DecimalType;

import java.util.ArrayList;
import java.util.List;

public class Single implements Resettable
{
    public static DecimalType boxLength = new DecimalType("length", "length of the horizontal side of the recruitment box.", 20.0d);
    public static DecimalType boxHeight = new DecimalType("height", "height and depth of the recruitment box", 3.0d);
    public static boolean sameType(@NotNull final Single left, @NotNull final Single right)
    {
        return left.pathfinderMob.getClass() == right.pathfinderMob.getClass();
    }
    public static boolean targetCompatible(@NotNull final Single single, @NotNull final LivingEntity target)
    {
        final GoalSelector targetSelector = single.pathfinderMob.targetSelector;
        for(WrappedGoal wrappedGoal : targetSelector.getAvailableGoals())
        {
            if(wrappedGoal.getGoal() instanceof GetTargetType<?> getTargetType && getTargetType.targetType().isAssignableFrom(target.getClass()))
            {
                return true;
            }
        }
        return false;
    }
    public ArrayList<Single> getNearbySingles(@NotNull final LivingEntity target)
    {
        final ArrayList<Single> output = new ArrayList<>();
        for(Entity entity : this.getNearbyEntities())
        {
            if(entity instanceof GetSingle getSingle && Single.targetCompatible(getSingle.getSingle(), target))
            {
                output.add(getSingle.getSingle());
            }
        }
        return output;
    }
    @NotNull
    public final ArrayList<Strategy> strategies = new ArrayList<>();
    @NotNull
    public final PathfinderMob pathfinderMob;
    @NotNull
    public Vec3 lastPosition;
    public Group group = null;
    public void reset()
    {
        this.lastPosition = pathfinderMob.position();
    }
    public Argument argument = new Argument();
    public Strategy getStrategyByName(final String string)
    {
        for(Strategy strategy : this.strategies)
        {
            if(strategy.name == string)
            {
                return strategy;
            }
        }
        return null;
    }
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
        if(this.pathfinderMob.isPathFinding())
        {
            this.argument.value |= 32;
        }
        else if((this.argument.value & 1) == 1)
        {
            Vec3 direction = this.pathfinderMob.getTarget().position().subtract(this.position());
            BlockPos blockPos = this.pathfinderMob.blockPosition().below();
            final Level level = this.pathfinderMob.level();
            if(Math.abs(direction.x) > Math.abs(direction.z))
            {
                if(direction.x > 0)
                {
                    blockPos = XPlus.instance.offset(blockPos);
                }
                else
                {
                    blockPos = XMinus.instance.offset(blockPos);
                }
            }
            else
            {
                if(direction.z > 0)
                {
                    blockPos = ZPlus.instance.offset(blockPos);
                }
                else
                {
                    blockPos = ZMinus.instance.offset(blockPos);
                }
            }
            if(BlockUtil.blockIsSolid(level.getBlockState(blockPos)) || BlockUtil.blockIsSolid(level.getBlockState(blockPos.above())) || BlockUtil.blockIsSolid(level.getBlockState(blockPos.above().above())))
            {
                this.argument.value |= 64;
            }
            else
            {
                this.argument.value |= 128;
            }
        }
    }
    protected Situation getSituation()
    {
        final Situation situation = new Situation();
        situation.value = this.argument.value;
        for(Entity entity : this.getNearbyEntities())
        {
            if(entity instanceof GetSingle)
            {
                situation.nearbyEntitiesWithGetSingle++;
            }
        }
        if((situation.value & 1) == 1)
        {
            final Vec3 direction = this.pathfinderMob.getTarget().position().subtract(this.position());
            situation.targetDistance = direction.length();
            situation.targetYDifference = direction.y;
        }
        return situation;
    }
    public Vec3 position()
    {
        return this.pathfinderMob.position();
    }
    public void setGroup(@NotNull final Group group)
    {
        if(this.group != null)
        {
            if(this.group != group)
            {
                this.group.remove(this);
            }
            else
            {
                return;
            }
        }
        this.group = group;
        this.group.add(this);
        this.pathfinderMob.setTarget(this.group.target);
    }
    public List<Entity> getNearbyEntities()
    {
        final double x = this.position().x;
        final double y = this.position().y;
        final double z = this.position().z;
        final double lengthDiv2 = Single.boxLength.value / 2.0;
        final double heightDiv2 = Single.boxHeight.value / 2.0;
        final AABB checkingBox = new AABB(x - lengthDiv2, y - heightDiv2, z - lengthDiv2, x + lengthDiv2, y + heightDiv2, z + lengthDiv2);
        return this.pathfinderMob.level().getEntities(this.pathfinderMob, checkingBox);
    }
    double patience = 0.0d;
    protected int strategyIndex = 0;
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
        for(int i = 0; i < strategies.size(); i++, this.strategyIndex = ((this.strategyIndex + 1) % this.strategies.size()))
        {
            final Strategy strategy = this.strategies.get(this.strategyIndex);
            if(this.patience <= 0)
            {
                strategy.searchOtherStrategies(this, this.getSituation());
                this.patience = this.pathfinderMob.getRandom().nextDouble() * 400;
            }
            if(strategy.doStrategy(this,this.argument))
            {
                break;
            }
            final LivingEntity target = this.pathfinderMob.getTarget();
            //this is to prevent the mob from searching other strategies while having no targets.
            if(target != null && !target.isDeadOrDying())
            {
                this.patience -= 10.0d;
            }
        }
        this.lastPosition = pathfinderMob.position();
    }
    public void hit(final LivingDamageEvent.Pre damageEvent)
    {
        if(this.group != null)
        {
            this.group.hit(this, damageEvent);
        }
        else if(damageEvent.getSource().getDirectEntity() instanceof LivingEntity livingEntity && GroupUtil.instance.isValidTarget(livingEntity))
        {
            this.group = new Group(livingEntity);
            this.group.add(this);
            this.group.recruitAndSetTargets = true;
        }
    }
    public Single(final PathfinderMob pathfinderMob)
    {
        this.pathfinderMob = pathfinderMob;
        this.lastPosition = pathfinderMob.position();
        TaskUtil.instance.setStrategies(this);
    }
}
