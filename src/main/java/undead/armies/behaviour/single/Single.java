package undead.armies.behaviour.single;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import undead.armies.UndeadArmies;
import undead.armies.base.Resettable;
import undead.armies.behaviour.group.GroupStorage;
import undead.armies.behaviour.group.Group;
import undead.armies.behaviour.type.BaseType;
import undead.armies.behaviour.type.TypeUtil;

public class Single implements Resettable
{
    public final PathfinderMob pathfinderMob;
    public final BaseType baseType;
    public GroupStorage groupStorage = null;
    public static final int maxDismountChecks = 10;
    public void attemptDismount()
    {
        if(!this.pathfinderMob.isPassenger())
        {
            return;
        }
        final AttributeInstance passengerSpeed = this.pathfinderMob.getAttribute(Attributes.MOVEMENT_SPEED);
        if(passengerSpeed == null)
        {
            return;
        }
        final Entity vehicle = this.pathfinderMob.getVehicle();
        if(vehicle.fallDistance != 0)
        {
            return;
        }
        if(vehicle.getVehicle() != null)
        {
            return;
        }
        if(!(vehicle instanceof LivingEntity))
        {
            return;
        }
        final AttributeInstance vehicleSpeed = ((LivingEntity) vehicle).getAttribute(Attributes.MOVEMENT_SPEED);
        if(vehicleSpeed == null)
        {
            return;
        }
        if(vehicleSpeed.getBaseValue() > passengerSpeed.getBaseValue())
        {
            return;
        }
        this.pathfinderMob.stopRiding();
    }
    public void reset()
    {
        UndeadArmies.logger.debug("reset single!");
        this.groupStorage = null;
    }
    public void doTick()
    {
        if(this.pathfinderMob.level().isClientSide)
        {
            return;
        }
        if(this.pathfinderMob.tickCount % this.baseType.actionCooldown() != 0)
        {
            return;
        }
        if(this.groupStorage == null || !this.groupStorage.group.target.is(this.pathfinderMob.getTarget()))
        {
            this.attemptDismount();
            this.groupStorage = Group.getGroupStorageThatAttacks(this.pathfinderMob.getTarget());
        }
        else if(this.pathfinderMob.getTarget() == null)
        {
            this.pathfinderMob.setTarget(this.groupStorage.group.target);
        }
        if(this.groupStorage != null)
        {
            this.groupStorage.group.doGroupTask(this);
        }
        this.baseType.additionalTick(this);
    }
    public Single(final PathfinderMob pathfinderMob)
    {
        this.pathfinderMob = pathfinderMob;
        this.baseType = TypeUtil.instance.getMobType(pathfinderMob.getRandom());
    }
}
