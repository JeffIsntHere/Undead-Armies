package undead.armies.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import undead.armies.util.container.MobSingle;

@Mixin(Zombie.class)
public class ZombieMixin extends Monster
{
    protected ZombieMixin(EntityType<? extends Monster> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }
    @Unique
    private MobSingle mobSingle = null;
}
