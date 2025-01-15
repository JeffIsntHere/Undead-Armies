package undead.armies.behaviour.type;

public class Engineer extends BaseType
{
    public static final Engineer engineer = new Engineer();
    @Override
    public float chance()
    {
        return 0.1f;
    }
    @Override
    public int actionCooldown()
    {
        return 15;
    }
}
