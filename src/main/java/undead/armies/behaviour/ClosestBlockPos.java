package undead.armies.behaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ClosestBlockPos
{
    public final Vec3 position;
    public BlockPos closest = null;
    public double distance = Double.MAX_VALUE;
    public ClosestBlockPos(@NotNull final Vec3 position)
    {
        this.position = position;
    }
    public void add(@NotNull final BlockPos blockPos)
    {
        final double blockPosDistance = this.position.distanceTo(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        if(blockPosDistance > this.distance)
        {
            return;
        }
        this.distance = blockPosDistance;
        this.closest = blockPos;
    }
}
