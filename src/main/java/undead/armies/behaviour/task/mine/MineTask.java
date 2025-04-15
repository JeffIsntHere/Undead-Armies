package undead.armies.behaviour.task.mine;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import undead.armies.behaviour.Single;
import undead.armies.misc.blockcast.offset.*;

public class MineTask
{
    public static final Base[][] offsets =
            {
                    {YPlus.instance, ZPlus.instance, YMinus.instance},
                    {YPlus.instance, XPlus.instance, YMinus.instance},
                    {YPlus.instance, ZMinus.instance, YMinus.instance},
                    {YPlus.instance, YMinus.instance, YMinus.instance},

                    {ZPlus.instance, YMinus.instance, NoOffset.instance},
                    {XPlus.instance, YMinus.instance, NoOffset.instance},
                    {ZMinus.instance, YMinus.instance, NoOffset.instance},
                    {YMinus.instance, YMinus.instance, NoOffset.instance},

                    {ZPlus.instance, YMinus.instance, YMinus.instance},
                    {XPlus.instance, YMinus.instance, YMinus.instance},
                    {ZMinus.instance, YMinus.instance, YMinus.instance},
                    {YMinus.instance, YMinus.instance, YMinus.instance}
            };
    public static int getBlockHp(final Block block)
    {
        return 0;
    }
    protected BlockPos currentBlockPos = null;
    protected Level level = null;
    protected Block currentBlock = null;
    protected int remainingHp = 0;
    protected int offsetIndex = 0;
    protected int offsetIndexIndex = 1;
    public boolean handle(Single single)
    {
        if(this.level != single.pathfinderMob.level())
        {
            return false;
        }
        this.currentBlockPos = single.pathfinderMob.blockPosition().above();
        this.offsetIndexIndex = 1;
        final Vec3 buffer = single.pathfinderMob.getTarget().position().subtract(single.position());
        if(buffer.y >= -0.5f && buffer.y <= 0.5f)
        {
            if(Math.abs(buffer.z) > Math.abs(buffer.x))
            {
                if(buffer.z > 0)
                {
                    this.offsetIndex = 4;
                }
                else
                {
                    this.offsetIndex = 6;
                }
            }
            else
            {
                if(buffer.x > 0)
                {
                    this.offsetIndex = 5;
                }
                else
                {
                    this.offsetIndex = 7;
                }
            }
        }
        else if(buffer.y > 0)
        {
            if(Math.abs(buffer.z) > Math.abs(buffer.x))
            {
                if(buffer.z > 0)
                {
                    this.offsetIndex = 0;
                }
                else
                {
                    this.offsetIndex = 2;
                }
            }
            else
            {
                if(buffer.x > 0)
                {
                    this.offsetIndex = 1;
                }
                else
                {
                    this.offsetIndex = 3;
                }
            }
        }
        else
        {
            if(Math.abs(buffer.z) > Math.abs(buffer.x))
            {
                if(buffer.z > 0)
                {
                    this.offsetIndex = 8;
                }
                else
                {
                    this.offsetIndex = 10;
                }
            }
            else
            {
                if(buffer.x > 0)
                {
                    this.offsetIndex = 9;
                }
                else
                {
                    this.offsetIndex = 11;
                }
            }
        }
        this.level = single.pathfinderMob.level();
        this.currentBlockPos = MineTask.offsets[this.offsetIndex][this.offsetIndexIndex].offset(this.currentBlockPos);
        this.currentBlock = this.level.getBlockState(this.currentBlockPos).getBlock();
        if(!this.level.getBlockState(this.currentBlockPos).is(this.currentBlock))
        {
            this.remainingHp = MineTask.getBlockHp(this.currentBlock);
        }
        if(this.remainingHp < 1)
        {
            final BlockState blockState = this.level.getBlockState(this.currentBlockPos);
            if(!blockState.isAir())
            {
                Block.dropResources(blockState, this.level, this.currentBlockPos);
                this.level.playSound(null, this.currentBlockPos, blockState.getSoundType(this.level, this.currentBlockPos, single.pathfinderMob).getBreakSound(), SoundSource.BLOCKS, 3.0f, 1.0f);
                this.level.setBlock(this.currentBlockPos, Blocks.AIR.defaultBlockState(), 3);
            }
            if(this.offsetIndexIndex == 3)
            {
                return false;
            }
            this.currentBlockPos = MineTask.offsets[this.offsetIndex][this.offsetIndexIndex].offset(this.currentBlockPos);
            this.offsetIndexIndex++;
            this.currentBlock = this.level.getBlockState(this.currentBlockPos).getBlock();
            this.remainingHp = MineTask.getBlockHp(this.currentBlock);
        }
        return true;
    }
}
