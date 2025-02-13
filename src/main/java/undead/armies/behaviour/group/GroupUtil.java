package undead.armies.behaviour.group;

import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;

public class GroupUtil
{
    public static final GroupUtil instance = new GroupUtil();
    protected GroupUtil(){};
    //mix into this to replace groupUtil with your own implementation.
    public GroupUtil getInstance()
    {
        return GroupUtil.instance;
    }
    public final HashMap<Group,Group> groups = new HashMap<>();
    public Group getGroupThatAttacks(final LivingEntity livingEntity)
    {
        final Group tempGroup = new Group(livingEntity);
        final Group groupFromGroups = this.groups.get(tempGroup);
        if(groupFromGroups == null)
        {
            this.groups.put(tempGroup, tempGroup);
            return tempGroup;
        }
        return groupFromGroups;
    }
}
