package undead.armies.misc.blockcast.offset;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

public class YPlus implements Base
{
    public static final Base instance = new YPlus();
    @Override
    public BlockPos offset(@NotNull BlockPos blockPos)
    {
        return new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
    }
}
