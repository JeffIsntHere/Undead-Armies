package undead.armies.behaviour.group;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import undead.armies.UndeadArmies;
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
    public Group parentGroup = this;
    public Group mergeWith = null;
    public HashMap<GroupMember, GroupMember> members = new HashMap<>();
    public final LivingEntity target;
    public void tick(@NotNull final Single single, final Argument argument)
    {
        if(this.mergeWith != null)
        {
            single.setGroup(this.mergeWith);
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
