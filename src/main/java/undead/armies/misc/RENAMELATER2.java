package undead.armies.misc;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

public class RENAMELATER2
{
    public BlockPos current;
    public final int length;
    protected final int adder;
    protected int x = 0;
    protected int y = 0;
    protected int z = 0;
    protected final int xDir;
    protected final int yDir;
    protected final int zDir;
    protected final int xToMove;
    protected final int yToMove;
    protected final int zToMove;
    public int renameLater2(int number)
    {
        return (number < 0) ? -1 : (number == 0) ? 0 : 1;
    }
    public RENAMELATER2(@NotNull final BlockPos start, @NotNull final BlockPos end)
    {
        this.current = start;
        final BlockPos direction = end.subtract(start);
        this.xDir = this.renameLater2(direction.getX());
        this.yDir = this.renameLater2(direction.getY());
        this.zDir = this.renameLater2(direction.getZ());
        this.xToMove = Math.abs(direction.getX());
        this.yToMove = Math.abs(direction.getY());
        this.zToMove = Math.abs(direction.getZ());
        this.adder = Math.min(Math.min(this.xToMove, this.yToMove), this.zToMove);
        this.length = Math.max(Math.max(this.xToMove, this.yToMove), this.zToMove);
    }
    public void traverse()
    {
        this.x += this.adder;
        this.y += this.adder;
        this.z += this.adder;
        int offsetX = 0;
        int offsetY = 0;
        int offsetZ = 0;
        if(this.x >= this.xToMove)
        {
            this.x -= this.xToMove;
            offsetX = this.xDir;
        }
        if(this.y >= this.yToMove)
        {
            this.y -= this.yToMove;
            offsetY = this.yDir;
        }
        if(this.z >= this.zToMove)
        {
            this.z -= this.zToMove;
            offsetZ = this.zDir;
        }
        this.current = this.current.offset(offsetX, offsetY, offsetZ);
    }
}
