package undead.armies.behaviour.task;

public class Argument
{
    /*
    stores common arguments or checks to an int. [number = bit position]
    0 = has target (1)
    1 = is moving (2)
    2 = on ground (4)
    3 = is passenger (8)
    4 = is vehicle (16)
     */
    public int value = 0;
}
