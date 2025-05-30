package undead.armies.behaviour.task.ramming;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.Single;
import undead.armies.behaviour.Strategy;
import undead.armies.behaviour.task.BaseTask;
import undead.armies.behaviour.task.argument.Argument;
import undead.armies.behaviour.task.argument.Situation;
import undead.armies.parser.config.type.NumberType;

public class RammingWrapper extends BaseTask
{
    public static final NumberType recruitDelay = new NumberType("recruitDelay", "how many ticks to delay recruiting task. The value will be rounded to the highest nearest multiple of cooldown", 20);
    public static final NumberType cooldown = new NumberType("cooldown", "cooldown between attempting ramming", 5);
    public static final NumberType rammingCooldown = new NumberType("rammingCooldown", "cooldown between ramming attempts. The value will be rounded to the highest nearest multiple of cooldown.", 160);
    protected RammingTask rammingTask = null;
    protected BlockPos blockPos = null;
    protected int recruitAfter = (RammingWrapper.recruitDelay.value + RammingWrapper.cooldown.value - 1)/RammingWrapper.cooldown.value;
    public int ramCount = 0;
    @Override
    public boolean handleTask(@NotNull Single single, Argument argument)
    {
        if((argument.value & 1) == 0)
        {
            return false;
        }
        if(this.rammingTask == null)
        {
            if(this.recruitAfter > 0)
            {
                this.recruitAfter--;
                return true;
            }
            this.rammingTask = new RammingTask();
            for(Single buffer : single.getNearbySingles(single.pathfinderMob.getTarget()))
            {
                Strategy bufferStrategy = buffer.getStrategyByName("pursue");
                if(bufferStrategy == null)
                {
                    continue;
                }
                if(bufferStrategy.setTask(RammingWrapper.class, single))
                {
                    ((RammingWrapper) bufferStrategy.getCurrentTask()).rammingTask = this.rammingTask;
                }
            }
        }
        return this.rammingTask.handle(single, this);
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
    @Override
    public int getCooldown(@NotNull final Single single)
    {
        return RammingWrapper.cooldown.value;
    }
}
