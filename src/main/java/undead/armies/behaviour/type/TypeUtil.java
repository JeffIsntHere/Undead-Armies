package undead.armies.behaviour.type;

import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public final class TypeUtil
{
    public static final TypeUtil instance = new TypeUtil();
    private TypeUtil(){}
    //if you are trying to add another type, please use mixins to override this method.
    public BaseType[] getAllMobTypes()
    {
        return new BaseType[]{/*Engineer.engineer, */Giant.giant/*, Normal.normal*/};
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
