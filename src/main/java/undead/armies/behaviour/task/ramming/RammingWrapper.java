package undead.armies.behaviour.task.ramming;

import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.Single;
import undead.armies.behaviour.task.BaseTask;
import undead.armies.behaviour.task.argument.Argument;
import undead.armies.behaviour.task.argument.Situation;

public class RammingWrapper extends BaseTask
{
    protected RammingTask rammingTask = new RammingTask();
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
        return score;
    }
}
