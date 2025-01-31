package undead.armies;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import undead.armies.parser.loot.Loot;
import undead.armies.parser.loot.LootParser;

public class Registry
{
    public static int reloadLoot(CommandContext<CommandSourceStack> commandContext)
    {
        final long timeInNanoSeconds = LootParser.instance.reload();
        if(timeInNanoSeconds == -1)
        {
            commandContext.getSource().sendFailure(Component.literal("this command only works when the server has started!"));
        }
        commandContext.getSource().sendSuccess(() -> Component.translatable("successfully reloaded! took " + ((double)timeInNanoSeconds/1000000.0d) + "ms"), true);
        commandContext.getSource().sendSuccess(() -> Component.translatable("loaded: " + LootParser.instance.loots.size() + " items."), true);
        return 1;
    }
    public static int dumpLoot(CommandContext<CommandSourceStack> commandContext, boolean dumpData)
    {
        final Entity entity = commandContext.getSource().getEntity();
        if(entity == null)
        {
            UndeadArmies.logger.info("");
            for(Loot loot : LootParser.instance.loots)
            {
                UndeadArmies.logger.info("Item type: " + loot.item.getItem());
                if(dumpData)
                {
                    UndeadArmies.logger.info(">Item data: " + loot.item.getComponents());
                }
                UndeadArmies.logger.info(">Quota: " + loot.quota);
                UndeadArmies.logger.info(">Reducer: " + loot.reducer);
                UndeadArmies.logger.info(">MinimumPower: " + loot.minimumPower);
                UndeadArmies.logger.info("");
            }
        }
        else if(entity instanceof ServerPlayer serverPlayer)
        {
            serverPlayer.sendSystemMessage(Component.literal(""));
            for(Loot loot : LootParser.instance.loots)
            {
                serverPlayer.sendSystemMessage(Component.literal("§7Item type: §f" + loot.item.getItem()));
                if(dumpData)
                {
                    serverPlayer.sendSystemMessage(Component.literal("§7>Item data: §f" + loot.item.getComponents()));
                }
                serverPlayer.sendSystemMessage(Component.literal("§7>Quota: §f" + loot.quota));
                serverPlayer.sendSystemMessage(Component.literal("§7>Reducer: §f" + loot.reducer));
                serverPlayer.sendSystemMessage(Component.literal("§7>MinimumPower: §f" + loot.minimumPower));
                serverPlayer.sendSystemMessage(Component.literal(""));
            }
        }
        return 1;
    }
    public static int dropAllLoot(CommandContext<CommandSourceStack> commandContext)
    {
        final Entity entity = commandContext.getSource().getEntity();
        if(entity == null)
        {
            commandContext.getSource().sendFailure(Component.literal("command must be ran by an entity!"));
        }
        else
        {
            final Level level = entity.level();
            final Vec3 location = entity.position();
            for(Loot loot : LootParser.instance.loots)
            {
                level.addFreshEntity(new ItemEntity(level, location.x, location.y, location.z, loot.item.copyWithCount(1)));
            }
        }
        return 1;
    }
    public static void registerCommands(final CommandDispatcher<CommandSourceStack> commandDispatcher)
    {
        commandDispatcher.register(Commands.literal("undeadArmies")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_ADMINS))
                .then(Commands.literal("reloadLoot").executes(Registry::reloadLoot))
                .then(Commands.literal("dumpLoot").executes(commandContext -> Registry.dumpLoot(commandContext, false))
                        .then(Commands.argument("dumpData", BoolArgumentType.bool())
                                .executes(commandContext -> Registry.dumpLoot(commandContext, BoolArgumentType.getBool(commandContext, "dumpData")))))
                .then(Commands.literal("dropAllLoot").executes(Registry::dropAllLoot))
        );
    }
}
