package undead.armies;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import undead.armies.parser.config.Config;
import undead.armies.parser.config.ConfigParser;
import undead.armies.parser.config.type.TypeArgument;

import java.util.ArrayList;

public class Registry
{
    public static int reloadConfig(CommandContext<CommandSourceStack> commandContext)
    {
        ConfigParser.instance.reload();
        commandContext.getSource().sendSuccess(() -> Component.translatable("successfully reloaded!"), true);
        return 1;
    }
    public static int dumpConfig(CommandContext<CommandSourceStack> commandContext)
    {
        final Entity sender = commandContext.getSource().getEntity();
        final ArrayList<Config> configs = ConfigParser.instance.getConfigCache();
        if(sender == null)
        {
            UndeadArmies.logger.info("");
            for(Config config : configs)
            {
                UndeadArmies.logger.info("---> Config: " + config);
                for(TypeArgument typeArgument : config.typeArguments)
                {
                    UndeadArmies.logger.info(">" + typeArgument);
                    if(!typeArgument.type.desc.isEmpty())
                    {
                        sender.sendSystemMessage(Component.literal(">" + typeArgument.type.desc));
                    }
                    UndeadArmies.logger.info("");
                }
            }
        }
        else
        {
            sender.sendSystemMessage(Component.literal(""));
            for(Config config : configs)
            {
                sender.sendSystemMessage(Component.literal("§7---> Config: §f" + config));
                for(TypeArgument typeArgument : config.typeArguments)
                {
                    sender.sendSystemMessage(Component.literal("§7>§f" + typeArgument));
                    if(!typeArgument.type.desc.isEmpty())
                    {
                        sender.sendSystemMessage(Component.literal("§7>" + typeArgument.type.desc));
                    }
                    sender.sendSystemMessage(Component.literal(""));
                }
            }
        }
        return 1;
    }
    public static void registerCommands(final CommandDispatcher<CommandSourceStack> commandDispatcher)
    {
        commandDispatcher.register(Commands.literal("undeadArmies")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_ADMINS))
                .then(Commands.literal("reloadConfig").executes(Registry::reloadConfig))
                .then(Commands.literal("dumpConfig").executes(Registry::dumpConfig))
        );
    }
}
