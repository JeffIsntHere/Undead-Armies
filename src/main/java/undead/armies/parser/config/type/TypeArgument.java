package undead.armies.parser.config.type;

import undead.armies.parser.config.ConfigArgument;

import java.util.function.Function;
import java.util.function.Supplier;

public class TypeArgument extends ConfigArgument
{
    public final Function<String, BaseType> type;
    public TypeArgument(final Function<String, BaseType> type, final String name, final int length)
    {
        super(name, length);
        this.type = type;
    }
    public TypeArgument(final Function<String, BaseType> type, char... query)
    {
        super(query);
        this.type = type;
    }
}
