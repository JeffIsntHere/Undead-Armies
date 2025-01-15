package undead.armies.behaviour.type;

public class Giant extends BaseType
{
    public static final Giant giant = new Giant();
    @Override
    public float chance()
    {
        return 0.1f;
    }
    @Override
    public int actionCooldown()
    {
        return 45;
    }
    @Override
    public boolean canHoldItems()
    {
        return false;
    }
}
