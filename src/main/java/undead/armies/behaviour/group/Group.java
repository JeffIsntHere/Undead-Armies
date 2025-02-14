package undead.armies.behaviour.group;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.task.Argument;
import undead.armies.behaviour.Single;
import undead.armies.parser.config.type.DecimalType;

import java.util.ArrayList;
import java.util.HashMap;

//this class is for advanced targeting.
/*
TODO:
Multi targeting:
Somehow detect which entities are supporting the main target, then attack those entities.

Zig Zag Marching:
If the main target is attacking with range, then march into the target with a zigzag pattern.
 */
public class Group
{
    public static DecimalType setTargetChance = new DecimalType("setTargetChance", "chance for an undead mob to recruit other undead mobs to attack a target.", 0.8d);
    public Group parentGroup = this;
    public Group mergeWith = null;
    public final LivingEntity target;
    public final HashMap<TargetWrapper, TargetWrapper> subTargets = new HashMap<>();
    public void setTarget(@NotNull final Single single, final Argument argument)
    {
        double highestWeight = GroupUtil.instance.getTargetPriority(single, this.target);
        LivingEntity bestTarget = this.target;
        final ArrayList<TargetWrapper> removal = new ArrayList<>();
        for(TargetWrapper targetWrapper : this.subTargets.keySet())
        {
            if(GroupUtil.instance.isInvalidTarget(targetWrapper.target))
            {
                removal.add(targetWrapper);
                continue;
            }
            final double currentWeight = GroupUtil.instance.getTargetPriority(single, targetWrapper.target);
            if(currentWeight > highestWeight)
            {
                highestWeight = currentWeight;
                bestTarget = targetWrapper.target;
            }
        }
        for(TargetWrapper targetWrapper : removal)
        {
            this.subTargets.remove(targetWrapper);
        }
        if(GroupUtil.instance.isInvalidTarget(bestTarget))
        {
            return;
        }
        single.pathfinderMob.setTarget(bestTarget);
        argument.value |= 1;
    }
    public void updateTarget(@NotNull final Single single, final Argument argument)
    {
        final LivingEntity target = single.pathfinderMob.getTarget();
        if((argument.value & 1) == 1)
        {
            if(GroupUtil.instance.isInvalidTarget(target))
            {
                this.setTarget(single, argument);
                return;
            }
            if(target.is(this.target) || subTargets.get(target) != null)
            {
                return;
            }
        }
        final double random = single.pathfinderMob.getRandom().nextDouble();
        if(random <= Group.setTargetChance.value)
        {
            this.setTarget(single, argument);
        }
    }
    public void tick(@NotNull final Single single, final Argument argument)
    {
        if(GroupUtil.instance.isInvalidTarget(this.target))
        {
            single.group = null;
            if(this.mergeWith != null)
            {
                single.group = this.mergeWith;
                return;
            }
            if(single.group != this.parentGroup)
            {
                single.group = parentGroup;
            }
        }
        this.updateTarget(single, argument);
    }
    public boolean tryMerge(@NotNull final Group group)
    {
        if(!group.target.is(this.target) || group.mergeWith == group)
        {
            return false;
        }
        group.mergeWith = this;
        return true;
    }
    public boolean trySplit(@NotNull final Single single, final LivingEntity target)
    {
        if(target == null || target.is(this.target))
        {
            return false;
        }
        single.setGroup(new Group(target));
        single.group.parentGroup = this.parentGroup;
        single.recruit();
        return true;
    }
    public void hit(@NotNull final Single single, final LivingDamageEvent.Pre damageEvent)
    {
        if(damageEvent.getSource().getDirectEntity() instanceof LivingEntity livingEntity)
        {
            if(GroupUtil.instance.isInvalidTarget(livingEntity))
            {
                return;
            }
            this.trySplit(single, livingEntity);
        }
    }
    public Group(@NotNull LivingEntity livingEntity)
    {
        this.target = livingEntity;
    }
}
