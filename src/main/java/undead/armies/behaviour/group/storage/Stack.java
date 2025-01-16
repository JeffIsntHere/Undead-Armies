package undead.armies.behaviour.group.storage;

import undead.armies.behaviour.single.Single;

public class Stack
{
    public static final int hitCounterToMakeStackingMobsFall = 3;
    public static final float hitDamageToMakeStackingMobsFall = 3.0f;
    public static final float minimumDistanceToStack = 3.0f;
    //"legs" as in the legs of the stack, the one who moves an entire of undead mobs.
    public Single legs;
    public Stack(final Single legs)
    {
        this.legs = legs;
    }
}
