package undead.armies.behaviour.type;

import undead.armies.behaviour.single.Single;

public abstract class BaseType
{
    public abstract float chance();
    public abstract int actionCooldown();
    public boolean canHoldItems() {return true;} //can it hold pickaxes? swords? etc...
    public boolean canWearArmor() {return true;} //can it wear leather helmet? iron chestplate? etc...
    public void additionalTick(final Single single){}
}
