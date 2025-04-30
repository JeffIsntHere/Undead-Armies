package undead.armies.behaviour.task.mine;

import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.Single;
import undead.armies.behaviour.task.argument.Argument;
import undead.armies.behaviour.task.BaseTask;
import undead.armies.behaviour.task.argument.Situation;
import undead.armies.misc.PathfindingTracker;
import undead.armies.parser.config.type.DecimalType;
import undead.armies.parser.config.type.NumberType;

public class MineWrapper extends BaseTask
{
    public static final NumberType cooldown = new NumberType("cooldown", 20);
    public static final DecimalType maxMiningDistance = new DecimalType("maxMiningDistance", 5.0d);
    protected PathfindingTracker pathfindingTracker = new PathfindingTracker(30);
    protected MineTask mineTask = new MineTask();
    @Override
    public boolean handleTask(@NotNull Single single, final Argument argument)
    {
        this.pathfindingTracker.tick();
        if((argument.value & 1) == 0 || (argument.value & 2) == 2)
        {
            return false;
        }
        if(!this.pathfindingTracker.tick(single))
        {
            return false;
        }
        boolean returnValue = true;
        if(!this.mineTask.handle(single))
        {
            if(this.mineTask.blocksBroken == 0)
            {
                returnValue = false;
            }
            this.mineTask = new MineTask();
        }
        return returnValue;
    }
    @Override
    public int situationScore(@NotNull Single single, final Situation situation)
    {
        int score = 0;
        if((situation.value & 1) == 1)
        {
            score++;
        }
        if((situation.value & 2) == 0)
        {
            score++;
        }
        if((situation.value & 64) == 64)
        {
            score++;
        }
        if(situation.targetYDifference < 2)
        {
            score++;
        }
        return score;
    }
    @Override
    public int getCooldown(@NotNull final Single single)
    {
        return MineWrapper.cooldown.value;
    }
}
