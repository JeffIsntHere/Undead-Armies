package undead.armies.parser.config.type;

public class NumberType extends BaseType
{
    public int value;

    @Override
    public void save(String string)
    {
        final int oldValue = this.value;
        try
        {
            this.value = Integer.valueOf(string);
        }
        catch(NumberFormatException e)
        {
            this.value = oldValue;
        }
    }

    public String toString()
    {
        return super.toString() + " : " + this.value;
    }

    public NumberType(final String name, final String desc, final int value)
    {
        super(name, desc);
        this.value = value;
    }

    public NumberType(final String name, final int value)
    {
        super(name);
        this.value = value;
    }
}
