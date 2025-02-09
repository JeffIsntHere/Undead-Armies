package undead.armies.misc.blockcast.offset;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

public class XPlus implements Base
{
    public static final Base instance = new XPlus();
    @Override
    public BlockPos offset(@NotNull BlockPos blockPos)
    {
        return new BlockPos(blockPos.getX() + 1, blockPos.getY(), blockPos.getZ());
    }
}
