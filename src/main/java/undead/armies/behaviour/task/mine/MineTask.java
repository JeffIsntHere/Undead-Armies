package undead.armies.behaviour.task.mine;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import undead.armies.UndeadArmies;
import undead.armies.base.GetSingle;
import undead.armies.behaviour.Single;
import undead.armies.behaviour.task.BaseTask;
import undead.armies.misc.blockcast.offset.*;
import undead.armies.parser.config.type.DecimalType;
import undead.armies.parser.config.type.StringType;

import java.util.HashMap;
import java.util.List;

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
                    {XMinus.instance, YMinus.instance, YMinus.instance}
            };
    public static double getBlockHp(final BlockState blockState)
    {
        if(blockState.isAir())
        {
            return 0;
        }
        final HashMap<BlockStateBlockPair, Double> specific = MineParser.instance.getData(MineTask.specific.value);
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
    protected BlockState currentBlockState = null;
    protected double remainingHp = 0;
    protected int offsetIndex = -1;
    protected int offsetIndexIndex = 1;
    public void init(final Single single)
    {
        final LivingEntity target = single.pathfinderMob.getTarget();
        final List<Entity> entityList = single.getNearbyEntities();
        for(Entity entity : entityList)
        {
            if(entity instanceof GetSingle getSingle)
            {
                final Single buffer = getSingle.getSingle();
                if(Single.targetCompatible(buffer, target))
                {
                    BaseTask currentTask = buffer.currentTask;
                    for(int i = 0; i < buffer.currentTaskLength; i++)
                    {
                        if(currentTask instanceof MineWrapper mineWrapper)
                        {
                            mineWrapper.mineTask = this;
                            break;
                        }
                        currentTask = currentTask.nextTask;
                    }
                }
            }
        }
        this.startingPoint = single.pathfinderMob.blockPosition().above();
        final Vec3 buffer = single.pathfinderMob.getTarget().position().subtract(single.position());
        if(buffer.y >= -0.5f && buffer.y <= 0.5f)
        {
            if(Math.abs(buffer.z) > Math.abs(buffer.x))
            {
                if(buffer.z < 0)
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
                if(buffer.x < 0)
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
                if(buffer.z < 0)
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
                if(buffer.x < 0)
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
                if(buffer.z < 0)
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
                if(buffer.x < 0)
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
        this.currentBlockState = this.level.getBlockState(this.currentBlockPos);
        this.remainingHp = MineTask.getBlockHp(this.currentBlockState);
        UndeadArmies.logger.debug("Init HP" + this.remainingHp + " BlockPos " + this.currentBlockPos + " Buffer " + buffer);
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
        if(!blockState.equals(this.currentBlockState))
        {
            this.remainingHp = MineTask.getBlockHp(blockState);
        }
        if(this.remainingHp > MineTask.unbreakable.value)
        {
            UndeadArmies.logger.debug("rotating, Starting point is: " + this.startingPoint);
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
            this.currentBlockState = this.level.getBlockState(this.currentBlockPos);
            this.remainingHp = MineTask.getBlockHp(this.currentBlockState);
            this.offsetIndexIndex = 1;
        }
        UndeadArmies.logger.debug("Currently Breaking " + this.currentBlockPos);
        this.remainingHp--;
        single.pathfinderMob.swing(InteractionHand.MAIN_HAND);
        if(this.remainingHp <= 0)
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
            this.currentBlockState = this.level.getBlockState(this.currentBlockPos);
            this.remainingHp = MineTask.getBlockHp(this.currentBlockState);
        }
        else
        {
            this.level.playSound(null, this.currentBlockPos, blockState.getSoundType(this.level, this.currentBlockPos, single.pathfinderMob).getHitSound(), SoundSource.BLOCKS, 2.0f / (float)this.remainingHp, 1.0f);
        }
        return true;
    }
}
