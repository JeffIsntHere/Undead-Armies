package undead.armies.parser.loot;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Loot
{
    @NotNull
    public final ItemStack item;
    public final double quota;
    public final int minimum;
    public Loot(final ItemParser itemParser, final String item, final double quota, final int minimum)
    {
        this.minimum = minimum;
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
        this.quota = quota;
    }
    public void dropAtLocation(final Level level, final Vec3 location, final double power)
    {
        final RandomSource randomSource = level.getRandom();
        int amount = this.minimum;
        double quota = this.quota * power;
        while(quota > 0.01)
        {
            quota -= Math.max(randomSource.nextDouble(), 0.01d);
            amount++;
        }
        level.addFreshEntity(new ItemEntity(level, location.x, location.y, location.z, this.item.copyWithCount(amount)));
    }
}
