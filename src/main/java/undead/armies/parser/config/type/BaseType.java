package undead.armies.parser.config.type;

import java.util.Objects;

public abstract class BaseType
{
    public final String name;
    public final String desc;
    protected BaseType(final String name, final String desc)
    {
        this.name = name;
        this.desc = desc;
    }
    protected BaseType(final String name)
    {
        this.name = name;
        this.desc = "";
    }
    public abstract void save(final String string);
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    @Override
    public boolean equals(Object other)
    {
        if(!(other instanceof BaseType))
        {
            return false;
        }
        return ((BaseType) other).name.equals(this.name);
    }
    public String toString()
    {
        return this.name;
    }
}
