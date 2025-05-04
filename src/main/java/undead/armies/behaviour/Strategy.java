package undead.armies.behaviour;

import net.minecraft.util.RandomSource;
import undead.armies.behaviour.task.argument.Argument;
import undead.armies.behaviour.task.BaseTask;
import undead.armies.behaviour.task.argument.Situation;
import undead.armies.parser.config.type.DecimalType;

import java.util.ArrayList;

//a strategy is simply a collection of tasks.
public class Strategy
{
    public static final DecimalType biasOnKeepingFirstTask = new DecimalType("biasOnKeepingFirstTask", "basically when 2 tasks have the same score (both have equal performance for the specific situation) how likely should an undead mob swap it's first choice with the next.", 0.5);
    public final String name;
    BaseTask currentTask;
    int currentTaskLength;
    protected int cooldown = 0;
    public int triggerAfter = 0;
    public BaseTask getCurrentTask()
    {
        return this.currentTask;
    }
    public <T extends BaseTask> T findTask(Class<T> instance)
    {
        BaseTask buffer = this.currentTask;
        for(int i = 0; i < this.currentTaskLength; i++)
        {
            if(buffer.getClass().isAssignableFrom(instance))
            {
                return (T) buffer;
            }
            buffer = buffer.nextTask;
        }
        return null;
    }
    protected void setTask(final BaseTask baseTask, final Single single)
    {
        this.currentTask = baseTask;
        this.cooldown = baseTask.getCooldown(single);
    }
    //returns true if strategy's task was successfully set to selected data type.
    public <T extends BaseTask> boolean setTask(Class<T> instance, final Single single)
    {
        BaseTask buffer = this.currentTask;
        for(int i = 0; i < this.currentTaskLength; i++)
        {
            if(buffer.getClass().isAssignableFrom(instance))
            {
                this.setTask(buffer, single);
                return true;
            }
            buffer = buffer.nextTask;
        }
        return false;
    }
    public boolean doStrategy(final Single single, final Argument argument)
    {
        if(this.currentTask == null)
        {
            return false;
        }
        this.triggerAfter--;
        if(this.triggerAfter > 0)
        {
            return true;
        }
        this.triggerAfter = cooldown;
        return this.currentTask.handleTask(single, argument);
    }
    //in the future all tasks should return a score based off how good they are for the situation
    //the undead mob is in, then depending on the intellect of the undead mob
    //it can then choose the best strategies for the current situation.

    //for now next strategy picks the best task out of the next 2 strategies.
    //each task must return a score
    //for now the score is like this:
    /*
    good in groups
    good when player above
    good when player far
     */
    public int getCurrentScore(final Single single, final Situation situation)
    {
        return this.currentTask.situationScore(single, situation);
    }
    public void searchOtherStrategies(final Single single, final Situation situation)
    {
        if(this.currentTaskLength < 2)
        {
            return;
        }
        BaseTask bestTask = this.currentTask.nextTask;
        int bestTaskScore = bestTask.situationScore(single, situation);
        BaseTask nextTask = bestTask.nextTask;
        final RandomSource randomSource = single.pathfinderMob.getRandom();
        for(int i = 2; i < this.currentTaskLength; i++)
        {
            int score = nextTask.situationScore(single, situation);
            if(score > bestTaskScore || (Strategy.biasOnKeepingFirstTask.value != 0 && score == bestTaskScore && randomSource.nextDouble() <= Strategy.biasOnKeepingFirstTask.value))
            {
                bestTaskScore = score;
                bestTask = nextTask;
            }
            nextTask = nextTask.nextTask;
        }
        this.setTask(bestTask, single);
    }
    public void setStrategy(final ArrayList<BaseTask> tasks)
    {
        if(tasks.isEmpty())
        {
            this.currentTask = null;
            this.currentTaskLength = 0;
            return;
        }
        this.currentTask = tasks.getFirst();
        this.currentTaskLength = tasks.size();
        for(int i = 1; i < this.currentTaskLength; i++)
        {
            this.currentTask.append(tasks.get(i));
        }
    }
    public Strategy(String name, final ArrayList<BaseTask> tasks)
    {
        this.name = name;
        this.setStrategy(tasks);
    }
    public Strategy(String name)
    {
        this.name = name;
        this.currentTask = null;
        this.currentTaskLength = 0;
    }
}
