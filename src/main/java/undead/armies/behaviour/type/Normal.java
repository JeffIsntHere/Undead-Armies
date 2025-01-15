package undead.armies.behaviour.type;

public class Normal extends BaseType
{
    public static final Normal normal = new Normal();
    @Override
    public float chance()
    {
        return 0.6f;
    }
    @Override
    public int actionCooldown()
    {
        return 30;
    }
}
