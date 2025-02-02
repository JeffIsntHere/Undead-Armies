package undead.armies.parser.config.type;

public class DecimalType extends BaseType
{
    public double value = 0.0;

    @Override
    public void save(String string)
    {
        final double oldValue = this.value;
        try
        {
            this.value = Double.valueOf(string);
            super.set = true;
        }
        catch (NumberFormatException e)
        {
            this.value = oldValue;
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
        super.set = false;
    }
}
