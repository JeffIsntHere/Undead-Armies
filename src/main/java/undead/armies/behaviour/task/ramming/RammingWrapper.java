package undead.armies.behaviour.task.ramming;

import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.Single;
import undead.armies.behaviour.task.BaseTask;
import undead.armies.behaviour.task.argument.Argument;
import undead.armies.behaviour.task.argument.Situation;

public class RammingWrapper extends BaseTask
{
    protected RammingTask rammingTask = new RammingTask();
    protected RammingSubGroup rammingSubGroup = null;
    @Override
    public boolean handleTask(@NotNull Single single, Argument argument)
    {

        return false;
    }

    @Override
    public int situationScore(@NotNull Single single, Situation situation)
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
        if(Math.abs(situation.targetYDifference) < 0.6d)
        {
            score++;
        }
        if(situation.nearbyEntitiesWithGetSingle + 1 >= RammingTask.minimumRammerPerBlock.value)
        {
            score++;
        }
        return score;
    }
}
