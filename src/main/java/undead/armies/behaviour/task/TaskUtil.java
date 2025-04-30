package undead.armies.behaviour.task;

import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.Single;
import undead.armies.behaviour.Strategy;
import undead.armies.behaviour.task.mine.MineWrapper;
import undead.armies.behaviour.task.ramming.RammingWrapper;
import undead.armies.parser.config.type.DecimalType;

import java.util.ArrayList;

public final class TaskUtil
{
    public final static TaskUtil instance = new TaskUtil();
    public DecimalType sprintTaskChance = new DecimalType("enableChance", 1.0d);
    public DecimalType grabTaskChance = new DecimalType("enableChance", 1.0d);
    public DecimalType jumpTaskChance = new DecimalType("enableChance", 1.0d);
    public DecimalType stackTaskChance = new DecimalType("enableChance", "enabling this enables dismountTask, if this is disabled dismountTask will also be disabled.",1.0d);
    public DecimalType mineTaskChance = new DecimalType("enableChance", 1.0d);
    public DecimalType ramTaskChance = new DecimalType("enableChance", 1.0d);
    private final ArrayList<BaseTask> taskPool = new ArrayList<>();
    public void setPursueTaskPool(@NotNull final RandomSource randomSource)
    {
        this.taskPool.clear();
        if(TaskUtil.instance.stackTaskChance.value != 0 && TaskUtil.instance.stackTaskChance.value >= randomSource.nextDouble())
        {
            this.taskPool.add(new StackTask());
            this.taskPool.add(new DismountTask());//if this was removed, undead mobs wont be able to dismount after climbing each other.
        }
        if(TaskUtil.instance.mineTaskChance.value != 0 && TaskUtil.instance.mineTaskChance.value >= randomSource.nextDouble())
        {
            this.taskPool.add(new MineWrapper());
        }
        if(TaskUtil.instance.jumpTaskChance.value != 0 && TaskUtil.instance.jumpTaskChance.value >= randomSource.nextDouble())
        {
            this.taskPool.add(new JumpTask());
        }
        if(TaskUtil.instance.sprintTaskChance.value != 0 && TaskUtil.instance.sprintTaskChance.value >= randomSource.nextDouble())
        {
            this.taskPool.add(new SprintTask());
        }
        if(TaskUtil.instance.ramTaskChance.value != 0 && TaskUtil.instance.ramTaskChance.value >= randomSource.nextDouble())
        {
            this.taskPool.add(new RammingWrapper());
        }
    }
    public void setKillTaskPool(@NotNull final RandomSource randomSource)
    {
        this.taskPool.clear();
        if(TaskUtil.instance.grabTaskChance.value != 0 && TaskUtil.instance.grabTaskChance.value >= randomSource.nextDouble())
        {
            this.taskPool.add(new GrabTask());
        }
    }
    public void setEradicationTaskPool(@NotNull final RandomSource randomSource)
    {
        //todo: move multiTargeting to here.
    }
    public void setStrategies(@NotNull final Single single)
    {
        final RandomSource randomSource = single.pathfinderMob.getRandom();
        single.strategies.clear();

        this.setPursueTaskPool(randomSource);
        single.strategies.add(new Strategy("pursue", this.taskPool));

        this.setKillTaskPool(randomSource);
        single.strategies.add(new Strategy("kill", this.taskPool));

        this.taskPool.clear();
    }
    private TaskUtil(){};
}
