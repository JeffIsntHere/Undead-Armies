package undead.armies.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import undead.armies.base.GetSingle;
import undead.armies.behaviour.Single;

@Mixin(Skeleton.class)
public class SkeletonMixin extends Monster implements GetSingle
{
    protected SkeletonMixin(EntityType<? extends Monster> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }
    @Unique
    private final Single single = new Single(this);
    @Inject(method="tick",at=@At("HEAD"))
    public void additionalTick(CallbackInfo callbackInfo)
    {
        this.single.tick();
    }
    @Override
    public Single getSingle()
    {
        return this.single;
    }
}
