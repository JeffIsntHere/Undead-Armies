package undead.armies.misc.blockcast.offset;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

public class ZPlus implements Base
{
    public static final Base instance = new ZPlus();
    @Override
    public BlockPos offset(@NotNull BlockPos blockPos)
    {
        return new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ() + 1);
    }
}
