package undead.armies.parser.config;

import undead.armies.parser.GetByChar;
import undead.armies.parser.config.type.BaseType;

public class Config extends GetByChar<BaseType>
{
    public final String name;
    public Config(final String name)
    {
        this.name = name;
    }
    public String toString()
    {
        return "==== Config name : " + name + " ====\n" + super.toString() + "==== End ====\n";
    }
}
