package undead.armies;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;
import undead.armies.behaviour.group.GroupUtil;
import undead.armies.behaviour.single.task.TaskUtil;
import undead.armies.parser.config.ConfigParser;
import undead.armies.parser.config.type.BooleanType;
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
    public void serverAboutToStartEvent(ServerAboutToStartEvent serverAboutToStartEvent)
    {
        ConfigParser.instance.registerConfig("stacking", new TypeArgument(GroupUtil.instance.enableStacking));
        ConfigParser.instance.registerConfig("mining", new TypeArgument(GroupUtil.instance.enableMining));
        ConfigParser.instance.registerConfig("sprinting", new TypeArgument(TaskUtil.instance.enableSprintTask));
        ConfigParser.instance.registerConfig("grab", new TypeArgument(TaskUtil.instance.enableGrabTask));
        ConfigParser.instance.registerConfig("jumping", new TypeArgument(TaskUtil.instance.enableJumpTask));
    }
    @SubscribeEvent
    public void serverStartedEvent(ServerStartedEvent serverStartedEvent)
    {
        LootParser.instance.reload();
        serverStartedEvent.getServer().sendSystemMessage(Component.literal("successfully reloaded!"));
        serverStartedEvent.getServer().sendSystemMessage(Component.literal("loaded: " + LootParser.instance.loots.size() + " items."));
        ConfigParser.instance.reload();
    }
}