package undead.armies.behaviour.type;

import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import undead.armies.parser.config.type.BooleanType;

import java.util.ArrayList;
import java.util.List;


public final class TypeUtil
{
    public static final TypeUtil instance = new TypeUtil();
    private TypeUtil(){}
    //if you are trying to add another type, please use mixins to override this method.
    public BooleanType enableEngineer = new BooleanType("enable", true);
    public BooleanType enableGiant = new BooleanType("enable", true);
    public BaseType[] getAllMobTypes()
    {
        final ArrayList<BaseType> types = new ArrayList<>();
        types.add(Normal.normal);
        if(this.enableEngineer.value)
        {
            types.add(Engineer.engineer);
        }
        if(this.enableGiant.value)
        {
            types.add(Giant.giant);
        }
        return (BaseType[]) types.toArray();
    }
    public BaseType defaultMobType()
    {
        return Normal.normal;
    }
    public BaseType getMobType(RandomSource randomSource)
    {
        final BaseType[] baseTypes = this.getAllMobTypes();
        final int mobTypesLength = baseTypes.length;
        final float[] chances = new float[mobTypesLength];
        float divisor = 0.0f;
        for(int i = 0; i < mobTypesLength; i++)
        {
            chances[i] = baseTypes[i].chance();
            divisor += chances[i];
        }
        //normalizing the chances.
        final float randomResult = randomSource.nextFloat();
        float cumulative = 0.0f;
        for(int i = 0; i < mobTypesLength; i++)
        {
            cumulative += chances[i]/divisor;
            if(cumulative >= randomResult)
            {
                return baseTypes[i];
            }
        }
        return this.defaultMobType();
    }
    @Nullable
    public BaseType getMobType(int id)
    {
        if(id != 0)
        {
            final List<BaseType> mobList = List.of(this.getAllMobTypes());
            for(BaseType baseType : mobList)
            {
                if(baseType.getId() == id)
                {
                    return baseType;
                }
            }
        }
        return null;
    }
}
