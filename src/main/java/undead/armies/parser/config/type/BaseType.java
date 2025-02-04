package undead.armies.parser.config.type;

import undead.armies.parser.config.Config;

public abstract class BaseType
{
    public final String name;
    protected BaseType(final String name)
    {
        this.name = name;
    }
    public abstract void save(final String string);
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
