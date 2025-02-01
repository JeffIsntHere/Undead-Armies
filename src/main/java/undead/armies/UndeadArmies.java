package undead.armies;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;
import undead.armies.parser.config.Config;
import undead.armies.parser.config.ConfigParser;
import undead.armies.parser.config.type.BooleanType;
import undead.armies.parser.config.type.DecimalType;
import undead.armies.parser.config.type.NumberType;
import undead.armies.parser.config.type.TypeArgument;
import undead.armies.parser.loot.LootParser;

@Mod(UndeadArmies.modId)
public class UndeadArmies
{
    public static final String modId = "undead_armies";
    public static final Logger logger = LogUtils.getLogger();
    //most of the code is in the "behaviour" package.
    //the code responsible for reading the configs is in the "parser" package.
    //if you want to make for example, pillagers to be able to stack, break, etc. Then just mixin to pillager like in the "mixin" package and don't forget to add the class name in resources/mixin.json
    public UndeadArmies(IEventBus iEventBus)
    {
        NeoForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void registerCommandsEvent(RegisterCommandsEvent registerCommandsEvent)
    {
        Registry.registerCommands(registerCommandsEvent.getDispatcher());
    }
    @SubscribeEvent
    public void serverStartedEvent(ServerStartedEvent serverStartedEvent)
    {
        final long timeInNanoSeconds = LootParser.instance.reload();
        if(timeInNanoSeconds == -1)
        {
            serverStartedEvent.getServer().sendSystemMessage(Component.literal("failed to load loot!"));
        }
        serverStartedEvent.getServer().sendSystemMessage(Component.literal("successfully reloaded! took " + ((double)timeInNanoSeconds/1000000.0d) + "ms"));
        serverStartedEvent.getServer().sendSystemMessage(Component.literal("loaded: " + LootParser.instance.loots.size() + " items."));

        final Config config = ConfigParser.instance.loadConfig("test", 1,
                new TypeArgument(BooleanType::new, 'a', 'd'),
                new TypeArgument(DecimalType::new, 'b', 'u'),
                new TypeArgument(DecimalType::new, 'b', 'a'),
                new TypeArgument(NumberType::new, 'c'));
        UndeadArmies.logger.debug("loaded: \n" + config);
        UndeadArmies.logger.debug("reading val ad! " + config.get('a', 'd'));
        UndeadArmies.logger.debug("reading val ba! " + config.get('b', 'a'));
        UndeadArmies.logger.debug("reading val bu! " + config.get('b', 'u'));
        UndeadArmies.logger.debug("reading val c! " + config.get('c'));
    }
}
