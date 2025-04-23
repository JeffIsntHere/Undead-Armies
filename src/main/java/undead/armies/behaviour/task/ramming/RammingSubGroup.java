package undead.armies.behaviour.task.ramming;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import undead.armies.behaviour.Single;
import undead.armies.behaviour.task.mine.MineTask;
import undead.armies.misc.Util;
import undead.armies.parser.config.type.DecimalType;

import java.util.ArrayList;

public class RammingSubGroup
{
    public static final DecimalType baseDamage = new DecimalType("baseDamage", "base block damage for an undead mob.", 2);
    public static final DecimalType armorDamage = new DecimalType("armorDamage", "how much 1 armor point contributes to the block damage, ex: if it was 1 then 1 armor hp = +1 block damage", 0.2);
    public BlockPos target = null;
    public final ArrayList<Single> members = new ArrayList<>();
    public void add(final Single single, final RammingTask rammingTask)
    {
        if(single.pathfinderMob.getTarget() == rammingTask.target && single.getStrategyByName("pursue").getCurrentTask() instanceof RammingWrapper rammingWrapper && rammingWrapper.rammingTask == rammingTask)
        {
            this.members.add(single);
        }
    }
    public void gather()
    {
        for(Single single : this.members)
        {
            single.pathfinderMob.getNavigation().moveTo(this.target.getX(), this.target.getY(), this.target.getZ(), 1.2);
        }
    }
    public boolean ram(final Level level)
    {
        if(this.members.isEmpty())
        {
            return false;
        }
        double totalDamage = 0;
        for(Single single : this.members)
        {
            Util.makeEntityLookAtBlockPos(single.pathfinderMob, this.target);
            single.pathfinderMob.setDeltaMovement(Util.getThrowVelocity(single.position(), new Vec3(this.target.getX() + 0.5d, this.target.getY() + 0.5d, this.target.getZ() + 0.5d), 5.0f, 0.5f));
            totalDamage += single.pathfinderMob.getAttribute(Attributes.ARMOR).getValue() * RammingSubGroup.armorDamage.value;
        }
        totalDamage += this.members.size() * RammingSubGroup.baseDamage.value;
        final BlockState blockState = level.getBlockState(this.target);
        if(totalDamage > MineTask.getBlockHp(blockState))
        {
            Block.dropResources(blockState, level, this.target);
            level.playSound(null, this.target, blockState.getSoundType(level, this.target, this.members.getFirst().pathfinderMob).getBreakSound(), SoundSource.BLOCKS, 3.0f, 1.0f);
            level.setBlock(this.target, Blocks.AIR.defaultBlockState(), 3);
            return true;
        }
        else
        {
            level.playSound(null, this.target, blockState.getSoundType(level, this.target, this.members.getFirst().pathfinderMob).getBreakSound(), SoundSource.BLOCKS, 2.0f, 1.0f);
            return false;
        }
    }
}
