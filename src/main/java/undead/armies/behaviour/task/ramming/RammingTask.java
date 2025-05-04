package undead.armies.behaviour.task.ramming;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import undead.armies.UndeadArmies;
import undead.armies.behaviour.Single;
import undead.armies.behaviour.Strategy;
import undead.armies.behaviour.task.mine.MineTask;
import undead.armies.misc.Util;
import undead.armies.misc.blockcast.BlockRayCast;
import undead.armies.parser.config.type.DecimalType;

import java.util.HashMap;

public class RammingTask
{
    public static final DecimalType unbreakable = new DecimalType("unbreakable", "any blocks with block hp over this value will be regarded as unbreakable by ramming.", 72.0d);
    public static final DecimalType successGoal = new DecimalType("successGoal", "the number of blocks broken over the total number of target blocks to consider ramming as successful.", 0.5);
    public static final DecimalType baseDamage = new DecimalType("baseDamage", "base block damage for an undead mob.", 2.5);
    public static final DecimalType armorDamage = new DecimalType("armorDamage", "how much 1 armor point contributes to the block damage, ex: if it was 1 then 1 armor hp = +1 block damage", 0.2);
    protected Single leader = null;
    protected Strategy leaderStrategy = null;
    protected LivingEntity target = null;
    protected Level level = null;
    public boolean success = true;
    public BlockRayCast direction = null;
    public final HashMap<BlockPos, RammingProgress> targetsCache = new HashMap<>();
    public final HashMap<Long, BlockPos> cache = new HashMap<>();
    protected void ram(final Single single)
    {
        BlockPos blockPos = single.pathfinderMob.blockPosition();
        Long key = (((long)blockPos.getX()) << 32) | (blockPos.getY() & 0xffffffffL);
        BlockPos targetBlock = this.cache.get(key);
        if(targetBlock == null)
        {
            this.direction.reset();
            this.direction.current = blockPos;
            this.direction.stopWhenHit();
            targetBlock = this.direction.current;
            this.cache.put(key, targetBlock);
            if(this.targetsCache.get(targetBlock) == null)
            {
                this.targetsCache.put(targetBlock, new RammingProgress());
            }
        }
        RammingProgress rammingProgress = this.targetsCache.get(this.cache.get(key));
        UndeadArmies.logger.debug("Target! " + this.cache.get(key));
        rammingProgress.cumulativeDamage += single.pathfinderMob.getAttribute(Attributes.ARMOR).getValue() * RammingTask.armorDamage.value + RammingTask.baseDamage.value;
        Util.makeEntityLookAtBlockPos(single.pathfinderMob, targetBlock);
        single.pathfinderMob.setDeltaMovement(Util.getThrowVelocity(single.position(), new Vec3(this.target.getX() + 0.5d, this.target.getY() + 0.5d, this.target.getZ() + 0.5d), 60.0f, 0.0f));
    }
    public boolean breakBlockPos(final Single single, final RammingProgress rammingProgress, final BlockPos blockPos)
    {
        final BlockState blockState = this.level.getBlockState(blockPos);
        if(blockState.isAir())
        {
            return false;
        }
        final double blockHp = MineTask.getBlockHp(blockState);
        if(blockHp > RammingTask.unbreakable.value)
        {
            return false;
        }
        if(rammingProgress.cumulativeDamage > blockHp)
        {
            UndeadArmies.logger.debug("cumul Damage: " + rammingProgress.cumulativeDamage);
            Block.dropResources(blockState, level, blockPos);
            level.playSound(null, blockPos, blockState.getSoundType(level, blockPos, single.pathfinderMob).getBreakSound(), SoundSource.BLOCKS, 3.0f, 1.0f);
            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
            return true;
        }
        else
        {
            level.playSound(null, blockPos, blockState.getSoundType(level, blockPos, single.pathfinderMob).getBreakSound(), SoundSource.BLOCKS, 2.0f, 1.0f);
            return false;
        }
    }
    protected int triggerAfter = 0;
    public boolean handle(final Single single, final RammingWrapper rammingWrapper)
    {
        if(this.target != null && this.target.isDeadOrDying())
        {
            rammingWrapper.rammingTask = null;
            return true;
        }
        if(this.leader == null || this.leader.pathfinderMob.isDeadOrDying() || !(this.leaderStrategy.getCurrentTask() instanceof RammingWrapper leaderWrapper) || leaderWrapper.rammingTask != this)
        {
            this.leader = single;
            this.leaderStrategy = single.getStrategyByName("pursue");
            this.level = this.leader.pathfinderMob.level();
            this.target = single.pathfinderMob.getTarget();
        }
        if(this.direction != null)
        {
            this.ram(single);
        }
        else
        {
            //go back.
        }
        if(!this.leader.equals(single))
        {
            return this.success;
        }
        if(this.direction != null)
        {
            UndeadArmies.logger.debug("finalizing! ");
            double successCounter = 0;
            double attemptCounter = 0;
            this.direction = null;
            for(BlockPos blockPos : this.targetsCache.keySet())
            {
                final RammingProgress rammingProgress = this.targetsCache.get(blockPos);
                if(this.breakBlockPos(single, rammingProgress, blockPos))
                {
                    successCounter++;
                }
                if(this.breakBlockPos(single, rammingProgress, blockPos.above()))
                {
                    successCounter++;
                }
                attemptCounter+=2;
            }
            this.targetsCache.clear();
            this.cache.clear();
            if(successCounter / attemptCounter < successGoal.value)
            {
                this.success = false;
            }
        }
        else
        {
            triggerAfter--;
            if(triggerAfter > -1)
            {
                return true;
            }
            this.triggerAfter = (RammingWrapper.rammingCooldown.value + RammingWrapper.cooldown.value - 1)/RammingWrapper.cooldown.value;
            UndeadArmies.logger.debug("Starting ramming from ! " + this.leader.pathfinderMob.blockPosition());
            this.direction = new BlockRayCast(this.level, this.leader.pathfinderMob.blockPosition(), this.leader.pathfinderMob.getTarget().blockPosition());
            this.targetsCache.clear();
            this.cache.clear();
            this.success = true;
        }
        return this.success;
    }
}
