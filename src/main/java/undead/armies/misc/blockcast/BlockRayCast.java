package undead.armies.misc.blockcast;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import undead.armies.misc.EMath;
import undead.armies.misc.blockcast.offset.*;

public class BlockRayCast
{
    public BlockPos current;
    public int length;
    public Level level;
    public int stepCount = 0;
    public int x = 0;
    public int y = 0;
    public int z = 0;
    public Base xDir;
    public Base yDir;
    public Base zDir;
    public int xAdder;
    public int yAdder;
    public int zAdder;
    public BlockRayCast(@NotNull final Level level, @NotNull final BlockPos start, @NotNull final BlockPos end)
    {
        this.level = level;
        this.current = start;
        final BlockPos direction = end.subtract(start);
        this.xDir = (direction.getX() < 0) ? XMinus.instance : XPlus.instance;
        this.yDir = (direction.getY() < 0) ? YMinus.instance : YPlus.instance;
        this.zDir = (direction.getZ() < 0) ? ZMinus.instance : ZPlus.instance;
        this.xAdder = Math.abs(direction.getX());
        this.yAdder = Math.abs(direction.getY());
        this.zAdder = Math.abs(direction.getZ());
        this.length = EMath.max(this.xAdder, this.yAdder, this.zAdder);
    }
    public BlockState step()
    {
        this.stepCount++;
        this.x += this.xAdder;
        this.y += this.yAdder;
        this.z += this.zAdder;
        if(this.x >= this.length)
        {
            this.x -= this.length;
            this.current = xDir.offset(this.current);
            final BlockState temp = this.level.getBlockState(this.current);
            if(!temp.isEmpty())
            {
                return temp;
            }
        }
        if(this.y >= this.length)
        {
            this.y -= this.length;
            this.current = yDir.offset(this.current);
            final BlockState temp = this.level.getBlockState(this.current);
            if(!temp.isEmpty())
            {
                return temp;
            }
        }
        if(this.z >= this.length)
        {
            this.z -= this.length;
            this.current = zDir.offset(this.current);
            final BlockState temp = this.level.getBlockState(this.current);
            if(!temp.isEmpty())
            {
                return temp;
            }
        }
        return null;
    }
    public BlockState stopWhenHit()
    {
        BlockState blockState = null;
        while(blockState == null && this.stepCount < this.length)
        {
            blockState = this.step();
        }
        return blockState;
    }
}
