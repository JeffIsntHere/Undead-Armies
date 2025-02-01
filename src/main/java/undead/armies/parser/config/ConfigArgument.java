package undead.armies.parser.config;

public class ConfigArgument
{
    public final char[] query;
    public ConfigArgument(final String string, final int length)
    {
        this.query = new char[length];
        for(int i = 0; i < length; i++)
        {
            this.query[i] = string.charAt(i);
        }
    }
    public ConfigArgument(char... query)
    {
        this.query = query;
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
