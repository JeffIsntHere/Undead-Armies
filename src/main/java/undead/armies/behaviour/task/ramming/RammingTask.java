package undead.armies.behaviour.task.ramming;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import undead.armies.behaviour.Single;
import undead.armies.behaviour.Strategy;
import undead.armies.parser.config.type.DecimalType;
import undead.armies.parser.config.type.NumberType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class RammingTask
{
    public static final NumberType minimumRammerPerBlock = new NumberType("minimumRammer", "minimum amount of undead mobs required to do ramming (which is basically mining on a group level.)", 2);
    public static final NumberType maxRammerPerBlock = new NumberType("maxRammerPerBlock", "the max amount of undead mobs that can attempt to ram a single block.",4);
    public static final DecimalType successPercentage = new DecimalType("successPercentage", "Basically how much ramming that succeeds is needed for the entire operation to be counted as a success, in percentage. (50% means 50% needs to succeed, 20% means 20% needs to succeed.)", 0.5);
    protected Single leader = null;
    protected LivingEntity target = null;
    protected int currentDirective = 0;
    protected Level level = null;
    public boolean success = true;
    final protected LinkedList<Single> availableSingles = new LinkedList<>();
    final protected ArrayList<RammingSubGroup> rammingSubGroups = new ArrayList<>();
    /*
    1 = ram
    2 = get back
     */
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
    public ArrayList<BlockPos> getTargets(final int amount)
    {
        final ArrayList<BlockPos> output = new ArrayList<>();
        Vec3 direction = this.target.position().subtract(this.leader.position());
        double lineX = -direction.z;
        double lineZ = direction.x;
        BlockPos start = this.leader.pathfinderMob.blockPosition();

        for(int i = 0; i < amount; i++)
        {

        }
        return output;
    }
    public boolean handle(final Single single, final RammingWrapper rammingWrapper)
    {
        if(this.target.isDeadOrDying())
        {
            rammingWrapper.rammingTask = null;
            rammingWrapper.rammingSubGroup = null;
            return true;
        }
        if(rammingWrapper.rammingSubGroup == null)
        {
            this.availableSingles.add(single);
        }
        if(this.leader == null || this.leader.pathfinderMob.isDeadOrDying() || !(this.leader.getStrategyByName("pursue").getCurrentTask() instanceof RammingWrapper leaderWrapper) || leaderWrapper.rammingTask != this)
        {
            this.leader = single;
            this.level = this.leader.pathfinderMob.level();
            this.target = single.pathfinderMob.getTarget();
        }
        if(!this.leader.equals(single))
        {
            return this.success;
        }
        this.rammingSubGroups.removeIf(rammingSubGroup -> {
            this.clean(rammingSubGroup.members);
            while(rammingSubGroup.members.size() < RammingTask.minimumRammerPerBlock.value && !this.availableSingles.isEmpty())
            {
                rammingSubGroup.add(this.availableSingles.getFirst(), this);
                this.availableSingles.removeFirst();
            }
            if(rammingSubGroup.members.size() < RammingTask.minimumRammerPerBlock.value)
            {
                this.availableSingles.addAll(rammingSubGroup.members);
                return true;
            }
            return false;
        });
        for(RammingSubGroup rammingSubGroup : this.rammingSubGroups)
        {
            if(this.availableSingles.isEmpty())
            {
                break;
            }
            while(rammingSubGroup.members.size() < RammingTask.maxRammerPerBlock.value && !this.availableSingles.isEmpty())
            {
                rammingSubGroup.add(this.availableSingles.getFirst(), this);
                this.availableSingles.removeFirst();
            }
        }
        while(!this.availableSingles.isEmpty() && this.availableSingles.size() >= RammingTask.minimumRammerPerBlock.value)
        {
            final RammingSubGroup rammingSubGroup = new RammingSubGroup();
            while(rammingSubGroup.members.size() < RammingTask.maxRammerPerBlock.value && !this.availableSingles.isEmpty())
            {
                rammingSubGroup.add(this.availableSingles.getFirst(), this);
                this.availableSingles.removeFirst();
            }
            //set the target block here.
        }
        switch(this.currentDirective)
        {
            case 0:
            {
                double successGoal = this.rammingSubGroups.size() * RammingTask.successPercentage.value;
                for(RammingSubGroup rammingSubGroup : this.rammingSubGroups)
                {
                    if(rammingSubGroup.ram(this.level))
                    {
                        successGoal--;
                    }
                    //target recallibration
                }
                this.success = (successGoal <= 0);
                this.currentDirective = 1;
                break;
            }
            case 1:
            {
                for(RammingSubGroup rammingSubGroup : this.rammingSubGroups)
                {
                    rammingSubGroup.gather();
                }
                this.currentDirective = 0;
                break;
            }
        }
        return this.success;
    }
}
