package undead.armies.behaviour;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import undead.armies.UndeadArmies;
import undead.armies.base.GetSingle;
import undead.armies.behaviour.task.argument.Argument;
import undead.armies.behaviour.task.BaseTask;
import undead.armies.behaviour.task.argument.Situation;

import java.util.ArrayList;

//a strategy is simply a collection of tasks.
public class Strategy
{
    public final String name;
    BaseTask currentTask;
    int currentTaskLength;
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
    public boolean doStrategy(final Single single, final Argument argument)
    {
        if(this.currentTask == null)
        {
            return false;
        }
        //UndeadArmies.logger.debug("doing task: ! " + this.currentTask.getClass().getCanonicalName());
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
        for(int i = 2; i < this.currentTaskLength; i++)
        {
            int score = nextTask.situationScore(single, situation);
            if(score > bestTaskScore)
            {
                bestTaskScore = score;
                bestTask = nextTask;
            }
            nextTask = nextTask.nextTask;
        }
        this.currentTask = bestTask;
        UndeadArmies.logger.debug("found another strat! " + this.currentTask.getClass().getCanonicalName());
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
