package undead.armies.behaviour.task.ramming;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import undead.armies.behaviour.Single;
import undead.armies.behaviour.Strategy;
import undead.armies.misc.blockcast.BlockRayCast;
import undead.armies.parser.config.type.DecimalType;

import java.util.Collection;
import java.util.HashMap;

public class RammingTask
{
    public static final DecimalType baseDamage = new DecimalType("baseDamage", "base block damage for an undead mob.", 2);
    public static final DecimalType armorDamage = new DecimalType("armorDamage", "how much 1 armor point contributes to the block damage, ex: if it was 1 then 1 armor hp = +1 block damage", 0.2);
    protected Single leader = null;
    protected Strategy leaderStrategy = null;
    protected LivingEntity target = null;
    protected Level level = null;
    public boolean success = true;
    public final HashMap<Integer, BlockPos> middle = new HashMap<>();
    /*
    1 = ram
    2 = get back
     */
    public void setTargets(int Amount)
    {
        BlockPos start = this.leader.pathfinderMob.blockPosition().above();
        BlockPos end;
        {
            BlockPos targetBlockPos = this.leader.pathfinderMob.getTarget().blockPosition().above();
            end = new BlockPos(targetBlockPos.getX(), start.getY(), targetBlockPos.getZ());
        }
        final BlockRayCast blockRayCast = new BlockRayCast(this.level, start, end);
        if(Math.abs(blockRayCast.xAdder) < Math.abs(blockRayCast.zAdder))
        {

        }
    }
    public void clean(final Collection<Single> collection)
    {
        collection.removeIf(member -> {
            if(member.pathfinderMob.isDeadOrDying() || !member.pathfinderMob.level().equals(this.level) || member.pathfinderMob.getTarget() != this.target)
            {
                return true;
            }
            final Strategy strategy = member.getStrategyByName("pursue");
            if(strategy.getCurrentTask() instanceof RammingWrapper rammingWrapper && rammingWrapper.rammingTask == this)
            {
                return false;
            }
            return true;
        });
    }
    public boolean handle(final Single single, final RammingWrapper rammingWrapper)
    {
        if(this.target.isDeadOrDying())
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
        if(!this.leader.equals(single))
        {
            return this.success;
        }
        return this.success;
    }
}
