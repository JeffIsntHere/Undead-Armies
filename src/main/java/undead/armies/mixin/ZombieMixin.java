package undead.armies.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import undead.armies.behaviour.single.Single;

@Mixin(Zombie.class)
public class ZombieMixin extends Monster
{
    protected ZombieMixin(EntityType<? extends Monster> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }
    @Unique
    private final Single single = new Single(this);
    @Inject(method="tick",at=@At("RETURN"))
    public void additionalTick(CallbackInfo callbackInfo)
    {
        single.tick();
    }
}
