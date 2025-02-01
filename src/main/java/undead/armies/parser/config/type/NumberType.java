package undead.armies.parser.config.type;

public class NumberType extends BaseType
{
    public int value = 0;

    @Override
    public void save(String string)
    {
        try
        {
            this.value = Integer.valueOf(string);
            super.isDefault = false;
        }
        catch(NumberFormatException e)
        {
            this.value = 0;
            super.isDefault = true;
        }
    }

    public String toString()
    {
        return super.toString() + " : " + this.value;
    }

    public NumberType(final String name, final int value)
    {
        super(name);
        this.value = value;
        super.isDefault = false;
    }
    public NumberType(final String name)
    {
        super(name);
    }
}
