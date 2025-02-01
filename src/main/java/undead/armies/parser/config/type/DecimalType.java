package undead.armies.parser.config.type;

public class DecimalType extends BaseType
{
    public double value = 0.0;

    @Override
    public void save(String string)
    {
        try
        {
            this.value = Double.valueOf(string);
            super.isDefault = false;
        }
        catch (NumberFormatException e)
        {
            this.value = 0.0;
            super.isDefault = true;
        }
    }

    public String toString()
    {
        return super.toString() + " : " + this.value;
    }

    public DecimalType(final String name, final double value)
    {
        super(name);
        this.value = value;
        super.isDefault = false;
    }
    public DecimalType(final String name)
    {
        super(name);
    }
}
