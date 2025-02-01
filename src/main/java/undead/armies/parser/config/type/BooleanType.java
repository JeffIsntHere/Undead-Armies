package undead.armies.parser.config.type;

public class BooleanType extends BaseType
{
    public boolean value = true;

    @Override
    public void save(final String string)
    {
        this.value = Boolean.parseBoolean(string);
    }

    public BooleanType(final String name, final boolean value)
    {
        super(name);
        this.value = value;
        super.isDefault = false;
    }

    public BooleanType(final String name)
    {
        super(name);
        super.isDefault = false;
    }

    public String toString()
    {
        return super.toString() + " : " + this.value;
    }
}
