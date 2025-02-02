package undead.armies.parser.config.type;

public class BooleanType extends BaseType
{
    public boolean value = true;

    @Override
    public void save(final String string)
    {
        if(string.length() == 0)
        {
            return;
        }
        final char firstChar = string.charAt(0);
        if(firstChar == 't' || firstChar == 'y')
        {
            this.value = true;
            this.set = true;
        }
        else if(firstChar == 'f' || firstChar == 'n')
        {
            this.value = false;
            this.set = true;
        }
    }

    public BooleanType(final String name, final boolean value)
    {
        super(name);
        this.value = value;
        super.set = true;
    }

    public String toString()
    {
        return super.toString() + " : " + this.value;
    }
}
