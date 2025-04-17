package undead.armies;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.world.entity.Entity;
import undead.armies.behaviour.task.mine.MineParser;
import undead.armies.behaviour.task.mine.MineTask;
import undead.armies.parser.config.Config;
import undead.armies.parser.config.ConfigParser;
import undead.armies.parser.config.type.TypeArgument;

import java.util.ArrayList;

public class Registry
{
    public static final Registry instance = new Registry();
    protected Registry(){}
    //mix into this to replace groupUtil with your own implementation.
    public Registry getInstance()
    {
        return Registry.instance;
    }
    public int reloadConfig(CommandContext<CommandSourceStack> commandContext)
    {
        ConfigParser.instance.getInstance().reload();
        MineParser.instance.cacheIsValid = false;
        commandContext.getSource().sendSuccess(() -> Component.translatable("successfully reloaded!"), true);
        return 1;
    }
    public int getBlockHp(CommandContext<CommandSourceStack> commandContext, BlockInput blockInput)
    {
        commandContext.getSource().sendSuccess(() -> Component.translatable("BlockHp: " + MineTask.getBlockHp(blockInput.getState())), true);
        return 1;
    }
    public int dumpConfig(CommandContext<CommandSourceStack> commandContext)
    {
        final Entity sender = commandContext.getSource().getEntity();
        final ArrayList<Config> configs = ConfigParser.instance.getInstance().getConfigCache();
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
    public void registerCommands(final CommandDispatcher<CommandSourceStack> commandDispatcher, final CommandBuildContext commandBuildContext)
    {
        commandDispatcher.register(Commands.literal("undeadArmies")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_ADMINS))
                .then(Commands.literal("reloadConfig").executes(this::reloadConfig))
                .then(Commands.literal("dumpConfig").executes(this::dumpConfig))
                .then(Commands.literal("getBlockHp")
                        .then(Commands.argument("block", BlockStateArgument.block(commandBuildContext))
                                .executes(context -> getBlockHp(context, BlockStateArgument.getBlock(context, "block")))))
        );
    }
}
