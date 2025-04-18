package undead.armies.behaviour;

import undead.armies.behaviour.task.Argument;
import undead.armies.behaviour.task.BaseTask;

public class Strategy
{
    BaseTask currentTask;
    int currentTaskLength;
    public boolean doStrategy(final Single single, final Argument argument)
    {
        if(this.currentTask == null)
        {
            return false;
        }
        return currentTask.handleTask(single, argument);
    }
    //in the future all tasks should return a score based off how good they are for the situation
    //the undead mob is in, then depending on the intellect of the undead mob
    //it can then choose the best strategies for the current situation.
    public void nextStrategy()
    {
        this.currentTask = currentTask.nextTask;
    }
}
