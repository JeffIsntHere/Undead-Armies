package undead.armies.misc.blockcast.offset;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

public interface Base
{
    BlockPos offset(@NotNull BlockPos blockPos);
}
