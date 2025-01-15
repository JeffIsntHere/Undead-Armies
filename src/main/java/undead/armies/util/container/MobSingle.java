package undead.armies.util.container;

import net.minecraft.nbt.CompoundTag;

public class MobSingle implements SaveInCompoundTag
{
    public static final int defaultActionCooldown = 30;
    public int actionCooldown = MobSingle.defaultActionCooldown;
    public int mobGroup = -1;
    public MobSingle(final CompoundTag compoundTag)
    {
        this.actionCooldown = compoundTag.getInt("actionCooldown");
        if(this.actionCooldown == 0)
        {
            this.actionCooldown = MobSingle.defaultActionCooldown;
        }
    }
    public MobSingle(final int actionCooldown, final int group)
    {
        if(actionCooldown != 0)
        {
            this.actionCooldown = actionCooldown;
        }
    }
    public MobSingle() {}
    public void saveInCompoundTag(final CompoundTag compoundTag)
    {
        compoundTag.putInt("actionCooldown", this.actionCooldown);
    }
}
