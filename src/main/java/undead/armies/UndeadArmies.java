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
import undead.armies.behaviour.task.*;
import undead.armies.behaviour.task.mine.MineTask;
import undead.armies.behaviour.type.TypeUtil;
import undead.armies.parser.config.ConfigParser;
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
        ConfigParser.instance.registerConfig("mining",
                new TypeArgument(TaskUtil.instance.enableMineTask),
                new TypeArgument(MineTask.maxMiningDistance),
                new TypeArgument(MineTask.blockHealthMultiplier));
        ConfigParser.instance.registerConfig("dismount",
                new TypeArgument(TaskUtil.instance.enableDismountTask),
                new TypeArgument(DismountTask.cooldown));
        ConfigParser.instance.registerConfig("grab",
                new TypeArgument(TaskUtil.instance.enableGrabTask),
                new TypeArgument(GrabTask.grabDistance),
                new TypeArgument(GrabTask.maxSlowdown));
        ConfigParser.instance.registerConfig("jumping",
                new TypeArgument(TaskUtil.instance.enableJumpTask),
                new TypeArgument(JumpTask.cooldown),
                new TypeArgument(JumpTask.maxMemorySize),
                new TypeArgument(JumpTask.disableMovementCheck));
        ConfigParser.instance.registerConfig("sprinting",
                new TypeArgument(TaskUtil.instance.enableSprintTask),
                new TypeArgument(SprintTask.sprintDistance),
                new TypeArgument(SprintTask.alwaysSprintWhenDistanceIsThisFar),
                new TypeArgument(SprintTask.cooldown));
        ConfigParser.instance.registerConfig("stacking",
                new TypeArgument(TaskUtil.instance.enableStackTask),
                new TypeArgument(StackTask.cooldown));
        ConfigParser.instance.registerConfig("engineer", new TypeArgument(TypeUtil.instance.enableEngineer));
        ConfigParser.instance.registerConfig("giant", new TypeArgument(TypeUtil.instance.enableGiant));
        ConfigParser.instance.reload();
    }
    @SubscribeEvent
    public void serverStartedEvent(ServerStartedEvent serverStartedEvent)
    {
        LootParser.instance.reload();
        serverStartedEvent.getServer().sendSystemMessage(Component.literal("successfully reloaded!"));
        serverStartedEvent.getServer().sendSystemMessage(Component.literal("loaded: " + LootParser.instance.loots.size() + " items."));
    }
}