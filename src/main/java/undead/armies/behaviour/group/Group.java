package undead.armies.behaviour.group;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import undead.armies.UndeadArmies;
import undead.armies.base.GetSingle;
import undead.armies.behaviour.task.Argument;
import undead.armies.behaviour.Single;

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
        if(this.members.keySet().size() == 1)
        {
            single.recruit(GroupUtil.instance.getCapability(this.target) + GroupUtil.instance.getCapability(single.pathfinderMob));
        }
        this.members.remove(groupMember);
    }
    public void tick(@NotNull final Single single, final Argument argument)
    {
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
        this.remove(single);
        single.setGroup(new Group(target));
        single.group.parentGroup = this.parentGroup;
        single.recruit();
        return true;
    }
    public void hit(@NotNull final Single single, @NotNull final LivingEntity livingEntity)
    {
        if(GroupUtil.instance.isInvalidTarget(livingEntity))
        {
            return;
        }
        this.trySplit(single, livingEntity);
    }
    public void hit(@NotNull final Single single, final LivingDamageEvent.Pre damageEvent)
    {
        if(damageEvent.getSource().getDirectEntity() instanceof LivingEntity livingEntity)
        {
            this.hit(single, livingEntity);
        }
    }
    public void hit(@NotNull final Single single)
    {
        final List<Entity> entities = single.getNearbyEntities();
        for(Entity entity : entities)
        {
            if(entity instanceof Mob mob && mob.getTarget() instanceof GetSingle getSingle && getSingle.getSingle().pathfinderMob.is(single.pathfinderMob))
            {
                this.hit(single, mob);
            }
        }
    }
    public Group(@NotNull LivingEntity livingEntity)
    {
        this.target = livingEntity;
    }
}
