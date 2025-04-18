package undead.armies.behaviour.task.mine;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import undead.armies.UndeadArmies;
import undead.armies.behaviour.Single;
import undead.armies.misc.blockcast.offset.*;
import undead.armies.parser.config.type.DecimalType;
import undead.armies.parser.config.type.StringType;

import java.util.HashMap;

public class MineTask
{
    public static final DecimalType unbreakable = new DecimalType("unbreakable", "any blocks with block hp over this value will be regarded as unbreakable.", 72.0d);
    public static final StringType specific = new StringType("specific", "this is used to manually specify block hp for any block.","");
    public static final DecimalType blockHealthMultiplier = new DecimalType("blockHealthMultiplier", "a block's hp is calculated using this: Blast resistance * blockHealthMultiplier. The result is how many hits is required to break the block.", 8.0d);
    public static final Base[][] offsets =
            {
                    {YPlus.instance, ZPlus.instance, YMinus.instance},
                    {YPlus.instance, XPlus.instance, YMinus.instance},
                    {YPlus.instance, ZMinus.instance, YMinus.instance},
                    {YPlus.instance, XMinus.instance, YMinus.instance},

                    {ZPlus.instance, YMinus.instance, NoOffset.instance},
                    {XPlus.instance, YMinus.instance, NoOffset.instance},
                    {ZMinus.instance, YMinus.instance, NoOffset.instance},
                    {YMinus.instance, YMinus.instance, NoOffset.instance},

                    {ZPlus.instance, YMinus.instance, YMinus.instance},
                    {XPlus.instance, YMinus.instance, YMinus.instance},
                    {ZMinus.instance, YMinus.instance, YMinus.instance},
                    {YMinus.instance, YMinus.instance, YMinus.instance}
            };
    public static double getBlockHp(final BlockState blockState)
    {
        final HashMap<BlockStateBlockPair, Double> specific = MineParser.instance.getData(MineTask.specific.value);
        for(BlockStateBlockPair blockStateBlockPair : specific.keySet())
        {
            UndeadArmies.logger.debug(blockStateBlockPair.equals(new BlockStateBlockPair(null, blockState.getBlock())) + "");
        }
        Double blockHp = specific.get(new BlockStateBlockPair(blockState, null));
        if(blockHp != null)
        {
            return blockHp;
        }
        blockHp = specific.get(new BlockStateBlockPair(null, blockState.getBlock()));
        if(blockHp != null)
        {
            return blockHp;
        }
        return blockState.getBlock().getExplosionResistance() * MineTask.blockHealthMultiplier.value;
    }
    protected BlockPos startingPoint = null;
    protected BlockPos currentBlockPos = null;
    protected Level level = null;
    protected Block currentBlock = null;
    protected double remainingHp = 0;
    protected int offsetIndex = -1;
    protected int offsetIndexIndex = 1;
    public void init(final Single single)
    {
        this.startingPoint = single.pathfinderMob.blockPosition().above();
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
        this.currentBlockPos = MineTask.offsets[this.offsetIndex][this.offsetIndexIndex].offset(this.startingPoint);
        this.currentBlock = this.level.getBlockState(this.currentBlockPos).getBlock();
    }
    public boolean handle(Single single)
    {
        if(this.offsetIndex == -1)
        {
            this.init(single);
        }
        else if(this.level != single.pathfinderMob.level())
        {
            return false;
        }
        final BlockState blockState = this.level.getBlockState(this.currentBlockPos);
        if(!blockState.is(this.currentBlock))
        {
            this.remainingHp = MineTask.getBlockHp(blockState);
        }
        if(this.remainingHp > MineTask.unbreakable.value)
        {
            this.offsetIndex = this.offsetIndex % 4;
            if(this.level.getRandom().nextBoolean())
            {
                //go left
                switch(this.offsetIndex)
                {
                    case 0:
                        offsetIndex = 3;
                        break;
                    case 1:
                        offsetIndex = 0;
                        break;
                    case 2:
                        offsetIndex = 1;
                        break;
                    case 3:
                        offsetIndex = 2;
                }
            }
            else
            {
                //go right
                switch(this.offsetIndex)
                {
                    case 0:
                        offsetIndex = 1;
                        break;
                    case 1:
                        offsetIndex = 2;
                        break;
                    case 2:
                        offsetIndex = 3;
                        break;
                    case 3:
                        offsetIndex = 0;
                }
            }
            final Vec3 buffer = single.pathfinderMob.getTarget().position().subtract(single.position());
            if(buffer.y >= -0.5f && buffer.y <= 0.5f)
            {
                offsetIndex += 4;
            }
            else if(buffer.y < 0)
            {
                offsetIndex += 8;
            }
            this.currentBlockPos = MineTask.offsets[this.offsetIndex][0].offset(this.startingPoint);
            this.currentBlock = this.level.getBlockState(this.currentBlockPos).getBlock();
            this.offsetIndexIndex = 1;
        }
        this.remainingHp--;
        if(this.remainingHp < 1)
        {
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
            this.remainingHp = MineTask.getBlockHp(this.level.getBlockState(this.currentBlockPos));
        }
        else
        {
            this.level.playSound(null, this.currentBlockPos, blockState.getSoundType(this.level, this.currentBlockPos, single.pathfinderMob).getHitSound(), SoundSource.BLOCKS, 2.0f / (float)this.remainingHp, 1.0f);
        }
        return true;
    }
}
