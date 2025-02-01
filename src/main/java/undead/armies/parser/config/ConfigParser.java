package undead.armies.parser.config;

import org.jetbrains.annotations.NotNull;
import undead.armies.parser.File;
import undead.armies.parser.Parser;
import undead.armies.parser.config.type.BaseType;
import undead.armies.parser.config.type.TypeArgument;

import java.io.Reader;

public class ConfigParser extends Parser
{
    public static final ConfigParser instance = new ConfigParser();
    protected Config config = null;
    protected ConfigArgument name = null;
    protected TypeArgument[] arguments = null;
    private ConfigParser(){}
    @Override
    protected void process()
    {
        if(super.parentCount == 0)
        {
            final String string = super.getKeyUntilOpen();
            if(this.name.compare(string))
            {
                super.parentCount++;
            }
        }
        else if(super.parentCount == 1)
        {
            final String string = super.getKeyUntilOpenOrClose();
            if(super.bufferedReaderWrapper.character == '}')
            {
                super.terminate = true;
                return;
            }
            final String key = super.parseValueToKey();
            for(TypeArgument typeArgument : this.arguments)
            {
                if(typeArgument.compare(string))
                {
                    final BaseType baseType = typeArgument.type.apply(string);
                    baseType.save(key);
                    this.config.set(baseType, typeArgument.query);
                }
            }
        }
    }
    public Config loadConfig(@NotNull final String name, final int scanLen, @NotNull final TypeArgument... arguments)
    {
        final File file = new File();
        final Reader reader = file.getFileReader("config");
        this.config = new Config(name);
        this.name = new ConfigArgument(name, scanLen);
        this.arguments = arguments;
        super.parseFromInput(reader);
        File.closeReader(reader);
        final Config config = this.config;
        this.config = null;
        this.name = null;
        this.arguments = null;
        return config;
    }
}
