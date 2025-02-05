package undead.armies.behaviour.type;

import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.group.task.selector.BaseTaskSelector;
import undead.armies.behaviour.single.Single;
import undead.armies.behaviour.task.BaseTask;

public abstract class BaseType
{
    public abstract float chance();
    public abstract int actionCooldown();
    public abstract int getId(); //returns the id.
    public int getHitPower(){return 1;}
    public void init(final @NotNull Single single){} //called once every time a mob's type is selected.
    public boolean canHoldItems() {return true;} //can it hold pickaxes? swords? etc...
    public boolean canWearArmor() {return true;} //can it wear leather helmet? iron chestplate? etc...
    public boolean canDoGroupTask(Class<? extends BaseTaskSelector> baseTaskSelector) {return true;} //can it do this group task?
    public boolean canDoTask(Class<? extends BaseTask> baseTask) {return true;} //can it do this single task?
    public void additionalTick(final Single single){}
}
