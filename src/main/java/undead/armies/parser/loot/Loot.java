package undead.armies.parser.loot;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Loot
{
    @NotNull
    public final ItemStack item;
    public final double chance;
    public final int lowerBound;
    public final int possibleExtra;
    public Loot(final ItemParser itemParser, final String item, final double chance, final int lowerBound, final int maximumCount)
    {
        this.lowerBound = lowerBound;
        this.possibleExtra = (lowerBound > maximumCount) ? 0 : maximumCount - lowerBound;
        if(item != null)
        {
            final ItemInput itemInput;
            ItemStack itemStackFromString = null;
            try
            {
                final ItemParser.ItemResult itemResult = itemParser.parse(new StringReader(item));
                itemStackFromString = new ItemInput(itemResult.item(), itemResult.components()).createItemStack(1, false);
            }
            catch (CommandSyntaxException e)
            {
                itemStackFromString = ItemStack.EMPTY;
                throw new RuntimeException(e);
            }
            if(itemStackFromString == null)
            {
                itemStackFromString = ItemStack.EMPTY;
            }
            this.item = itemStackFromString;
        }
        else
        {
            this.item = ItemStack.EMPTY;
        }
        this.chance = chance;
    }
    public void dropAtLocation(final Level level, final Vec3 location)
    {
        final ItemEntity itemEntity = new ItemEntity(level, location.x, location.y, location.z, this.item.copyWithCount(this.lowerBound + (int) (level.getRandom().nextFloat() * this.possibleExtra)));
        level.addFreshEntity(itemEntity);
    }
}
