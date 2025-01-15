package undead.armies.util.container;

import net.minecraft.nbt.CompoundTag;

public class FlagWithCounter implements SaveInCompoundTag
{
    public final String prefix;
    public boolean flag;
    public int counter;
    public FlagWithCounter(final CompoundTag compoundTag, final String prefix)
    {
        this.flag = compoundTag.getBoolean(prefix + "Flag");
        this.counter = compoundTag.getInt(prefix + "Counter");
        this.prefix = prefix;
    }
    public FlagWithCounter(final boolean flag, final int counter, final String prefix)
    {
        this.flag = flag;
        this.counter = counter;
        this.prefix = prefix;
    }
    public FlagWithCounter(final boolean flag, final String prefix)
    {
        this.flag = flag;
        this.counter = 0;
        this.prefix = prefix;
    }
    public FlagWithCounter(final String prefix)
    {
        this.flag = false;
        this.counter = 0;
        this.prefix = prefix;
    }
    public void saveInCompoundTag(final CompoundTag compoundTag)
    {
        compoundTag.putBoolean(this.prefix + "Flag", this.flag);
        compoundTag.putInt(this.prefix + "Counter", this.counter);
    }
}
