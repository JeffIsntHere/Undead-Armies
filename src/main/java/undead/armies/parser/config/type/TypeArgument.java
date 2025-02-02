package undead.armies.parser.config.type;

public class TypeArgument
{
    public final char[] query;
    public final BaseType type;
    public TypeArgument(final BaseType type, final String name, final int length)
    {
        this.type = type;
        this.query = new char[length];
        for(int i = 0; i < length; i++)
        {
            this.query[i] = name.charAt(i);
        }
    }
    public boolean compare(final String string)
    {
        final int length = this.query.length;
        if(string.length() < length)
        {
            return false;
        }
        for(int i = 0; i < length; i++)
        {
            if(this.query[i] != string.charAt(i))
            {
                return false;
            }
        }
        return true;
    }
}
