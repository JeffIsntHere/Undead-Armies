package undead.armies.behaviour.group;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.Single;

import java.util.UUID;

public class GroupMember
{
    public final Single member;
    public final UUID uuid;
    public GroupMember(final @NotNull Single member)
    {
        this.member = member;
        this.uuid = member.pathfinderMob.getUUID();
    }
    @Override
    public int hashCode()
    {
        return this.uuid.hashCode();
    }
    @Override
    public boolean equals(Object other)
    {
        if(!(other instanceof LivingEntity))
        {
            return false;
        }
        return ((LivingEntity) other).getUUID().equals(this.uuid);
    }
}
