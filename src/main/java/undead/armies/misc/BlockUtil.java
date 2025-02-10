package undead.armies.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.LavaFluid;

public final class BlockUtil
{
    public static boolean blockIsLava(final BlockState blockState)
    {
        return (blockState.getBlock() instanceof LiquidBlock liquidBlock && liquidBlock.fluid instanceof LavaFluid);
    }
    public static boolean blockIsNotLava(final BlockState blockState)
    {
        return (!(blockState.getBlock() instanceof LiquidBlock liquidBlock) || !(liquidBlock.fluid instanceof LavaFluid));
    }
    public static boolean blockIsGood(final BlockPos blockPos, final Level level)
    {
        final BlockState blockState = level.getBlockState(blockPos);
        return !blockState.isAir() && BlockUtil.blockIsNotLava(blockState);
    }
    public static boolean blockIsGood(final BlockState blockState)
    {
        return !blockState.isAir() && BlockUtil.blockIsNotLava(blockState);
    }
    public static boolean blockIsAirOrNotLava(final BlockPos blockPos, final Level level)
    {
        final BlockState blockState = level.getBlockState(blockPos);
        return blockState.isAir() || BlockUtil.blockIsNotLava(blockState);
    }
    public static boolean blockIsAirOrNotLava(final BlockState blockState)
    {
        return blockState.isAir() || BlockUtil.blockIsNotLava(blockState);
    }
}
