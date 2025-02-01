package undead.armies.parser;

import org.jetbrains.annotations.NotNull;
import undead.armies.UndeadArmies;

public class GetByChar<T>
{
    public static String dumpValidValues(final Object startingPoint)
    {
        if(startingPoint == null)
        {
            return "";
        }
        if(startingPoint instanceof Object[] objects)
        {
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0; i < 26; i++)
            {
                stringBuilder.append(GetByChar.dumpValidValues(objects[i]));
            }
            return stringBuilder.toString();
        }
        else
        {
            return startingPoint + "\n";
        }
    }
    public static int characterToNumber(final char character)
    {
        int number = 0;
        switch (character)
        {
            case 'b' -> number = 1;
            case 'c' -> number = 2;
            case 'd' -> number = 3;
            case 'e' -> number = 4;
            case 'f' -> number = 5;
            case 'g' -> number = 6;
            case 'h' -> number = 7;
            case 'i' -> number = 8;
            case 'j' -> number = 9;

            case 'k' -> number = 10;
            case 'l' -> number = 11;
            case 'm' -> number = 12;
            case 'n' -> number = 13;
            case 'o' -> number = 14;
            case 'p' -> number = 15;
            case 'q' -> number = 16;
            case 'r' -> number = 17;
            case 's' -> number = 18;
            case 't' -> number = 19;

            case 'u' -> number = 20;
            case 'v' -> number = 21;
            case 'w' -> number = 22;
            case 'x' -> number = 23;
            case 'y' -> number = 24;
            case 'z' -> number = 25;
        }
        return number;
    }
    protected Object data[] = new Object[26];
    public void set(@NotNull final T thing, char... characters)
    {
        if(characters.length == 0)
        {
            return;
        }
        Object[] currentData = this.data;
        final int length = characters.length - 1;
        for(int i = 0; i < length; i++)
        {
            final int index = GetByChar.characterToNumber(characters[i]);
            if(currentData[index] == null || !(currentData[index] instanceof Object[]))
            {
                currentData[index] = new Object[26];
            }
            currentData = (Object[]) currentData[index];
        }
        currentData[GetByChar.characterToNumber(characters[length])] = thing;
    }
    public T get(char... characters)
    {
        Object[] currentData = this.data;
        int index;
        final int length = characters.length - 1;
        for(int i = 0; i < length; i++)
        {
            index = GetByChar.characterToNumber(characters[i]);
            final Object testData = currentData[index];
            if(testData == null || !(testData instanceof Object[]))
            {
                return null;
            }
            currentData = (Object[]) testData;
        }
        index = GetByChar.characterToNumber(characters[length]);
        if(currentData[index] == null || currentData[index] instanceof Object[])
        {
            return null;
        }
        return (T) currentData[index];
    }

    @Override
    public String toString()
    {
        return GetByChar.dumpValidValues(this.data);
    }
}
