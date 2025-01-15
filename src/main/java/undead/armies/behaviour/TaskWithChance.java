package undead.armies.behaviour;

public class TaskWithChance
{
    public final int task;
    public float chance;
    public TaskWithChance(final int task, final float chance)
    {
        this.task = task;
        this.chance = chance;
    }
}
