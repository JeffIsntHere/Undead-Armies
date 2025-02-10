package undead.armies.parser.config.type;

public class BooleanType extends BaseType
{
    public boolean value;

    @Override
    public void save(final String string)
    {
        if(string.isEmpty())
        {
            return;
        }
        final char firstChar = string.charAt(0);
        if(firstChar == 't' || firstChar == 'y')
        {
            this.value = true;
        }
        else if(firstChar == 'f' || firstChar == 'n')
        {
            this.value = false;
        }
    }

    public String toString()
    {
        return super.toString() + " : " + this.value;
    }

    public BooleanType(final String name, final String desc, final boolean value)
    {
        super(name, desc);
        this.value = value;
    }

    public BooleanType(final String name, final boolean value)
    {
        super(name);
        this.value = value;
    }
}
