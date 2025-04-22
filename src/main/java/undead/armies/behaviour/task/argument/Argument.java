package undead.armies.behaviour.task.argument;

public class Argument
{
    /*
    stores common arguments or checks to an int. [number = bit position]
    0 = has target (1)
    1 = is moving (2)
    2 = on ground (4)
    3 = is passenger (8)
    4 = is vehicle (16)
    5 = is pathfinding (32)
    6 = unable to pathfind due to obstructed path (64)
    7 = unable to pathfind due to hole in path (128)
     */
    public int value = 0;
}
