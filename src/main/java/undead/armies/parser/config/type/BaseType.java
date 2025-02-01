package undead.armies.parser.config.type;

public abstract class BaseType
{
    public final String name;
    public boolean isDefault = true;
    protected BaseType(final String name)
    {
        this.name = name;
    }
    public abstract void save(final String string);
    public String toString()
    {
        return this.name + " : " + ((isDefault) ? "unSet" : "set");
    }
}
