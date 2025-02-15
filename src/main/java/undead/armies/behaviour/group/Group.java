package undead.armies.behaviour.group;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import undead.armies.base.GetSingle;
import undead.armies.behaviour.task.Argument;
import undead.armies.behaviour.Single;
import undead.armies.parser.config.type.DecimalType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public static DecimalType recruitChance = new DecimalType("recruitChance", "chance for an undead mob to recruit other undead mobs to attack a target.", 0.2d);
    public Group parentGroup = this;
    public Group mergeWith = null;
    public HashMap<GroupMember, GroupMember> members = new HashMap<>();
    public final LivingEntity target;
    public void add(@NotNull final Single single)
    {
        final GroupMember groupMember = new GroupMember(single);
        this.members.put(groupMember, groupMember);
    }
    public void remove(@NotNull final Single single)
    {
        final GroupMember groupMember = new GroupMember(single);
        if(this.members.keySet().size() < 2) //set this to a key number.
        {
            this.recruit(single);
        }
        this.members.remove(groupMember);
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
    public void split(@NotNull final Single single, final LivingEntity target)
    {
        this.remove(single);
        single.setGroup(new Group(target));
        single.group.parentGroup = this.parentGroup;
    }
    public boolean trySplit(@NotNull final Single single, final LivingEntity target)
    {
        if(target == null || target.is(this.target))
        {
            return false;
        }
        this.remove(single);
        single.setGroup(new Group(target));
        single.group.parentGroup = this.parentGroup;
        return true;
    }
    public void tick(@NotNull final Single single, final Argument argument)
    {
        if(this.recruitCooldown == 0)
        {
            if(this.recruit)
            {
                this.recruit(single);
                this.recruit = false;
            }
        }
        else
        {
            this.recruitCooldown--;
        }
        if(this.recruitAndSetTargetsCooldown == 0)
        {
            if(this.recruitAndSetTargets)
            {
                this.recruitAndSetTargets(single);
                this.recruitAndSetTargets = false;
            }
        }
        else
        {
            this.recruitAndSetTargetsCooldown--;
        }
        if(this.mergeWith != null)
        {
            single.setGroup(this.mergeWith);
            return;
        }
        if(GroupUtil.instance.isInvalidTarget(this.target))
        {
            if(!this.target.is(this.parentGroup.target))
            {
                single.setGroup(parentGroup);
            }
            else
            {
                single.group = null;
                this.members.remove(new GroupMember(single));
            }
            return;
        }
        if((argument.value & 1) == 0)
        {
            single.pathfinderMob.setTarget(this.target);
        }
        else if(single.pathfinderMob.getTarget().is(this.target))
        {
            this.trySplit(single, single.pathfinderMob.getTarget());
        }
    }
    public void hit(@NotNull final Single single, @NotNull final LivingDamageEvent.Pre damageEvent)
    {
        final Entity causer = damageEvent.getSource().getDirectEntity();
        if(!(causer instanceof LivingEntity livingEntity) || GroupUtil.instance.isInvalidTarget(livingEntity))
        {
            return;
        }
        if(!causer.is(this.target))
        {
            this.split(single, (LivingEntity) causer);
            single.group.recruitAndSetTargets = true;
            return;
        }
        if(this.members.keySet().size() < 2) //set this to a key number.
        {
            this.recruit = true;
        }
        else if(single.pathfinderMob.getRandom().nextDouble() > Group.recruitChance.value)
        {
            this.recruitAndSetTargets = true;
        }
    }
    public int recruitCooldown = 0;
    public boolean recruit = false;
    public void recruit(@NotNull final Single single)
    {
        this.recruitCooldown = 20;
        final List<Entity> entities = single.getNearbyEntities();
        for(Entity entity : entities)
        {
            if(entity instanceof GetSingle getSingle)
            {
                final Single entitySingle = getSingle.getSingle();
                if(!Single.sameType(entitySingle, single) && !Single.targetCompatible(entitySingle, this.target))
                {
                    continue;
                }
                if(entitySingle.group == null)
                {
                    entitySingle.group = this;
                    entitySingle.pathfinderMob.setTarget(this.target);
                    this.add(entitySingle);
                    continue;
                }
                if(!single.group.tryMerge(this) && GroupUtil.instance.shouldJoin(entitySingle, target))
                {
                    entitySingle.group.remove(entitySingle);
                    entitySingle.group = this;
                    entitySingle.pathfinderMob.setTarget(this.target);
                    this.add(entitySingle);
                }
            }
        }
    }
    public int recruitAndSetTargetsCooldown = 0;
    public boolean recruitAndSetTargets = false;
    public void recruitAndSetTargets(@NotNull final Single single)
    {
        this.recruitAndSetTargetsCooldown = 20;
        final List<Entity> entities = single.getNearbyEntities();
        final ArrayList<Target> targets = new ArrayList<>();
        for(Entity entity : entities)
        {
            if(entity instanceof Mob mob && this.members.get(mob.getTarget()) != null)
            {
                targets.add(new Target(mob));
                continue;
            }
            if(entity instanceof GetSingle getSingle)
            {
                final Single entitySingle = getSingle.getSingle();
                if(!Single.sameType(entitySingle, single) && !Single.targetCompatible(entitySingle, this.target))
                {
                    continue;
                }
                if(entitySingle.group == null)
                {
                    entitySingle.group = this;
                    this.add(entitySingle);
                    continue;
                }
                if(!single.group.tryMerge(this) && GroupUtil.instance.shouldJoin(entitySingle, target))
                {
                    entitySingle.group.remove(entitySingle);
                    entitySingle.group = this;
                    this.add(entitySingle);
                }
            }
        }
        for(GroupMember groupMember : this.members.keySet())
        {
            final double currentPriority = GroupUtil.instance.getTargetPriority(groupMember.member);
            boolean found = false;
            for(Target target : targets)
            {
                if(target.requiredCapability <= 0)
                {
                    continue;
                }
                final double newPriority = target.capability / groupMember.member.position().distanceTo(target.mob.position());
                if(newPriority > currentPriority)
                {
                    this.trySplit(groupMember.member, target.mob);
                    target.requiredCapability -= GroupUtil.instance.getCapability(groupMember.member.pathfinderMob);
                    found = true;
                    break;
                }
            }
            if(!found && groupMember.member.pathfinderMob.getTarget() == null)
            {
                groupMember.member.pathfinderMob.setTarget(this.target);
            }
        }
    }
    public Group(@NotNull LivingEntity livingEntity)
    {
        this.target = livingEntity;
    }
}
