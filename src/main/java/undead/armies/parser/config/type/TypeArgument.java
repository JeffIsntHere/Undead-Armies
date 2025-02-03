package undead.armies.parser.config.type;

public class TypeArgument
{
    public final char[] query;
    public final BaseType type;
    public TypeArgument(final BaseType type, final String name)
    {
        this.type = type;
        this.query = name.toCharArray();
    }
    public boolean compare(final String string)
    {
        final int upperBound = Math.min(this.query.length, string.length());
        for(int i = 0; i < upperBound; i++)
        {
            if(this.query[i] != string.charAt(i))
            {
                return false;
            }
        }
        return true;
    }
}
