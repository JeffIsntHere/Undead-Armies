package undead.armies.behaviour.task.mine;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import undead.armies.UndeadArmies;
import undead.armies.parser.Parser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MineParser extends Parser
{
    public static final MineParser instance = new MineParser();
    public boolean cacheIsValid = false;
    protected HashMap<BlockStateBlockPair, Double> cache = new HashMap<BlockStateBlockPair, Double>();
    protected HolderLookup<Block> lookup = null;
    @Override
    protected void process()
    {
        final ArrayList<String> data = new ArrayList<>();
        super.spinUntilOpen();
        super.parseValueArrayToKeyArrayList(data);
        if(data.size() < 2)
        {
            return;
        }
        BlockStateParser.BlockResult blockResult;
        try
        {
            blockResult = BlockStateParser.parseForBlock(this.lookup, data.getFirst(), true);
        }
        catch (CommandSyntaxException e)
        {
            throw new RuntimeException(e);
        }
        final BlockState blockState = new BlockInput(blockResult.blockState(), blockResult.properties().keySet(), blockResult.nbt()).getState();
        Double value;
        try
        {
            value = Double.valueOf(data.get(1));
        }
        catch (NumberFormatException e)
        {
            value = -1.0d;
        }
        UndeadArmies.logger.debug("Block: " + blockState + " hp: " + value);
        if(data.size() == 2)
        {
            this.cache.put(new BlockStateBlockPair(null, blockState.getBlock()), value);
        }
        else
        {
            this.cache.put(new BlockStateBlockPair(blockState, null), value);
        }
    }
    public HashMap<BlockStateBlockPair, Double> getData(final String string)
    {
        if(this.lookup == null)
        {
            this.lookup = ServerLifecycleHooks.getCurrentServer().registryAccess().lookupOrThrow(Registries.BLOCK);
        }
        if(!cacheIsValid)
        {
            this.cache.clear();
            super.parseFromInput(new StringReader(string));
        }
        this.cacheIsValid = true;
        return this.cache;
    }
    private MineParser() {}
}
