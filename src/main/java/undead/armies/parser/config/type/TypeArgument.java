package undead.armies.parser.config.type;

public class TypeArgument
{
    public final char[] query;
    public final BaseType type;
    public TypeArgument(final BaseType type)
    {
        this.type = type;
        this.query = type.name.toCharArray();
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
    @Override
    public int hashCode() {
        return this.type.hashCode();
    }
    @Override
    public boolean equals(Object other)
    {
        if(!(other instanceof TypeArgument))
        {
            return false;
        }
        return ((TypeArgument) other).type.equals(this.type);
    }
    public String toString()
    {
        return type.toString();
    }
}
