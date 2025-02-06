package undead.armies.behaviour.type;

public class Normal extends BaseType
{
    public static final Normal normal = new Normal();
    @Override
    public float chance()
    {
        return 0.7f;
    }

    @Override
    public int getId()
    {
        return 3;
    }

}
