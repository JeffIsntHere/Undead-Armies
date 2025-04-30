package undead.armies;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import org.slf4j.Logger;
import undead.armies.base.GetSingle;
import undead.armies.behaviour.Single;
import undead.armies.behaviour.Strategy;
import undead.armies.behaviour.group.Group;
import undead.armies.behaviour.task.*;
import undead.armies.behaviour.task.mine.MineTask;
import undead.armies.behaviour.task.mine.MineWrapper;
import undead.armies.behaviour.task.ramming.RammingTask;
import undead.armies.behaviour.task.ramming.RammingWrapper;
import undead.armies.parser.config.ConfigParser;
import undead.armies.parser.config.type.TypeArgument;

@Mod(UndeadArmies.modId)
public class UndeadArmies
{
    public static final String modId = "undead_armies";
    public static final Logger logger = LogUtils.getLogger();
    //most of the code is in the "behaviour" package.
    //the code responsible for reading the configs is in the "parser" package.
    //if you want to make for example, pillagers to be able to stack, break, etc. Then just mixin to pillager like in the "mixin" package and don't forget to add the class name in resources/mixin.json
    public UndeadArmies(IEventBus iEventBus)
    {
        NeoForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void registerCommandsEvent(RegisterCommandsEvent registerCommandsEvent)
    {
        Registry.instance.getInstance().registerCommands(registerCommandsEvent.getDispatcher(), registerCommandsEvent.getBuildContext());
    }
    @SubscribeEvent
    public void livingDamageEventPre(LivingDamageEvent.Pre damageEvent)
    {
        if(damageEvent.getEntity() instanceof GetSingle getSingle)
        {
            getSingle.getSingle().hit(damageEvent);
        }
    }
    @SubscribeEvent
    public void serverAboutToStartEvent(ServerAboutToStartEvent serverAboutToStartEvent)
    {
        ConfigParser.instance.getInstance().registerConfig("mining",
                new TypeArgument(TaskUtil.instance.mineTaskChance),
                new TypeArgument(MineWrapper.maxMiningDistance),
                new TypeArgument(MineTask.blockHealthMultiplier),
                new TypeArgument(MineTask.specific),
                new TypeArgument(MineTask.unbreakable),
                new TypeArgument(MineWrapper.cooldown));
        ConfigParser.instance.getInstance().registerConfig("dismount",
                new TypeArgument(DismountTask.cooldown));
        ConfigParser.instance.getInstance().registerConfig("grab",
                new TypeArgument(TaskUtil.instance.grabTaskChance),
                new TypeArgument(GrabTask.grabDistance),
                new TypeArgument(GrabTask.maxSlowdown));
        ConfigParser.instance.getInstance().registerConfig("jumping",
                new TypeArgument(TaskUtil.instance.jumpTaskChance),
                new TypeArgument(JumpTask.cooldown),
                new TypeArgument(JumpTask.maxMemorySize),
                new TypeArgument(JumpTask.disableMovementCheck));
        ConfigParser.instance.getInstance().registerConfig("sprinting",
                new TypeArgument(TaskUtil.instance.sprintTaskChance),
                new TypeArgument(SprintTask.sprintDistance),
                new TypeArgument(SprintTask.alwaysSprintWhenDistanceIsThisFar),
                new TypeArgument(SprintTask.cooldown),
                new TypeArgument(SprintTask.duration),
                new TypeArgument(SprintTask.amplifier));
        ConfigParser.instance.getInstance().registerConfig("stacking",
                new TypeArgument(TaskUtil.instance.stackTaskChance),
                new TypeArgument(StackTask.cooldown));
        ConfigParser.instance.getInstance().registerConfig("ramming",
                new TypeArgument(TaskUtil.instance.ramTaskChance),
                new TypeArgument(RammingWrapper.cooldown),
                new TypeArgument(RammingTask.armorDamage),
                new TypeArgument(RammingTask.baseDamage),
                new TypeArgument(RammingTask.successGoal));
        ConfigParser.instance.getInstance().registerConfig("misc",
                new TypeArgument(Group.recruitChance),
                new TypeArgument(Single.boxLength),
                new TypeArgument(Single.boxHeight),
                new TypeArgument(Strategy.biasOnKeepingFirstTask));
        ConfigParser.instance.getInstance().reload();
    }
}