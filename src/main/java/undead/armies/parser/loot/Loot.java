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
    public final double reducer;
    public final double minimumPower;
    public Loot(final ItemParser itemParser, final String item, final double quota, final double reducer, final double minimumPower)
    {
        this.reducer = reducer;
        this.minimumPower = minimumPower;
        if(item != null)
        {
            ItemStack itemStackFromString;
            try
            {
                final ItemParser.ItemResult itemResult = itemParser.parse(new StringReader(item));
                itemStackFromString = new ItemInput(itemResult.item(), itemResult.components()).createItemStack(1, false);
            }
            catch (CommandSyntaxException e)
            {
                e.printStackTrace();
                itemStackFromString = ItemStack.EMPTY;
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
        //quota = chance * 1/required power
        if(power < this.minimumPower)
        {
            return;
        }
        final RandomSource randomSource = level.getRandom();
        int amount = 0;
        double quota = this.quota * power;
        while(true)
        {
            final double nextDouble = randomSource.nextDouble();
            if(quota < nextDouble)
            {
                break;
            }
            else
            {
                quota -= (nextDouble + this.reducer);
                amount++;
            }
        }
        final int stackSize = this.item.getMaxStackSize();
        int loopCount = amount / stackSize;
        for(int i = 0; i < loopCount; i++)
        {
            level.addFreshEntity(new ItemEntity(level, location.x, location.y, location.z, this.item.copyWithCount(stackSize)));
        }
        loopCount = amount - loopCount * stackSize;
        if(loopCount > 0)
        {
            level.addFreshEntity(new ItemEntity(level, location.x, location.y, location.z, this.item.copyWithCount(loopCount)));
        }
    }
}
