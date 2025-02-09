package undead.armies.misc;

//a class aimed at including extra math functions. EMath for Extended Math
public final class EMath
{
    public static int max(int... args)
    {
        int highest = args[0];
        for(int i : args)
        {
            if(i > highest)
            {
                highest = i;
            }
        }
        return highest;
    }
    public static float max(float... args)
    {
        float highest = args[0];
        for(float i : args)
        {
            if(i > highest)
            {
                highest = i;
            }
        }
        return highest;
    }
    public static double max(double... args)
    {
        double highest = args[0];
        for(double i : args)
        {
            if(i > highest)
            {
                highest = i;
            }
        }
        return highest;
    }
    public static int min(int... args)
    {
        int lowest = args[0];
        for(int i : args)
        {
            if(lowest > i)
            {
                lowest = i;
            }
        }
        return lowest;
    }
    public static float min(float... args)
    {
        float lowest = args[0];
        for(float i : args)
        {
            if(lowest > i)
            {
                lowest = i;
            }
        }
        return lowest;
    }
    public static double min(double... args)
    {
        double lowest = args[0];
        for(double i : args)
        {
            if(lowest > i)
            {
                lowest = i;
            }
        }
        return lowest;
    }
}
