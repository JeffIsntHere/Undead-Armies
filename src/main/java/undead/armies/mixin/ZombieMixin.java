package undead.armies.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import undead.armies.base.GetSingle;
import undead.armies.behaviour.Single;

@Mixin(Zombie.class)
public abstract class ZombieMixin extends Monster implements GetSingle
{
    protected ZombieMixin(EntityType<? extends Monster> pEntityType, Level pLevel)
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
    @Inject(method="addAdditionalSaveData",at=@At("HEAD"))
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo callbackInfo)
    {
        compoundTag.putInt("MobType", this.single.baseType.getId());
    }
    @Override
    public Single getSingle()
    {
        return this.single;
    }
}
