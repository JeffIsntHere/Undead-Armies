package undead.armies.parser.config.type;

public class StringType extends BaseType
{
    public String value;

    @Override
    public void save(final String string)
    {
        this.value = string;
    }

    public String toString()
    {
        return super.toString() + " : " + this.value;
    }

    public StringType(final String name, final String desc, final String value)
    {
        super(name, desc);
        this.value = value;
    }

    public StringType(final String name, final String value)
    {
        super(name);
        this.value = value;
    }
}
