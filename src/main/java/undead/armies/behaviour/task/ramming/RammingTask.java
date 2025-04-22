package undead.armies.behaviour.task.ramming;

import undead.armies.behaviour.Single;
import undead.armies.parser.config.type.NumberType;

import java.util.ArrayList;
import java.util.LinkedList;

public class RammingTask
{
    public static final NumberType minimumRammerPerBlock = new NumberType("minimumRammerPerBlock", "minimum amount of undead mobs required to attempt ramming a single block.", 2);
    public static final NumberType maxRammerPerBlock = new NumberType("maxRammerPerBlock", "the max amount of undead mobs that can attempt to ram a single block.",4);
    protected Single leader = null;
    protected int currentDirective = 0;
    final protected LinkedList<Single> availableSingles = new LinkedList<>();
    final protected ArrayList<RammingSubGroup> rammingSubGroups = new ArrayList<>();
    /*
    0 = standby
    1 = ram
    2 = get back
     */
    public boolean handle(final Single single, final RammingWrapper rammingWrapper)
    {
        if(this.leader == null || this.leader.pathfinderMob.isDeadOrDying())
        {
            this.leader = single;
        }
        if(!this.leader.equals(single))
        {
            if(rammingWrapper.rammingSubGroup == null)
            {
                this.availableSingles.add(single);
            }
            return true;
        }
        this.rammingSubGroups.removeIf(rammingSubGroup -> {
            rammingSubGroup.clean();
            while(rammingSubGroup.members.size() < RammingTask.minimumRammerPerBlock.value && !this.availableSingles.isEmpty())
            {
                rammingSubGroup.members.add(this.availableSingles.getFirst());
                this.availableSingles.removeFirst();
            }
            return rammingSubGroup.members.size() < RammingTask.minimumRammerPerBlock.value ;
        });
        for(RammingSubGroup rammingSubGroup : this.rammingSubGroups)
        {
            if(this.availableSingles.isEmpty())
            {
                break;
            }
            while(rammingSubGroup.members.size() < RammingTask.maxRammerPerBlock.value && !this.availableSingles.isEmpty())
            {
                rammingSubGroup.members.add(this.availableSingles.getFirst());
                this.availableSingles.removeFirst();
            }
        }
        if(this.availableSingles.isEmpty())
        {
            //create subgroups.
        }
        return true;
    }
}
