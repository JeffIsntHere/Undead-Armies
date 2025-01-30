package undead.armies.parser.loot;

import net.minecraft.commands.arguments.item.ItemParser;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import undead.armies.UndeadArmies;
import undead.armies.parser.File;
import undead.armies.parser.Parser;

import java.io.Reader;
import java.util.ArrayList;

public class LootParser extends Parser
{
    public static final LootParser instance = new LootParser();
    public ArrayList<Loot> loots = new ArrayList<>();
    protected ItemParser itemParser = null;
    protected String item = null;
    protected double quota = 0.0d;
    protected int minimum = 0;
    @Override
    protected void process()
    {
        if(super.workingParentCount == 0)
        {
            this.item = null;
            this.quota = 1.0d;
            this.minimum = 0;
            super.spinUntilOpen();
            super.workingParentCount++;
        }
        else if(super.workingParentCount == 1)
        {
            final char key = super.spinUntilNotWhitespace();
            if(key == '}' || !super.spinUntilOpenOrClose())
            {
                UndeadArmies.logger.debug("adding loot!");
                if(this.item != null)
                {
                    this.loots.add(new Loot(this.itemParser, this.item, this.quota, this.minimum));
                }
                super.workingParentCount--;
            }
            else if(key == 'i')
            {
                this.item = super.parseValueToKey();
            }
            else if(key == 'q')
            {
                try
                {
                    this.quota = Double.valueOf(super.parseValueToKey());
                }
                catch(NumberFormatException e)
                {
                    this.quota = 1.0d;
                }
            }
            else if(key == 'm')
            {
                try
                {
                    this.minimum = Integer.valueOf(super.parseValueToKey());
                }
                catch(NumberFormatException e)
                {
                    this.minimum = 0;
                }
            }
        }
    }
    public boolean reload()
    {
        if(ServerLifecycleHooks.getCurrentServer() == null)
        {
            return false;
        }
        loots.clear();
        this.itemParser = new ItemParser(ServerLifecycleHooks.getCurrentServer().registryAccess());
        final File file = new File();
        final Reader reader = file.getFileReader("loot");
        super.parseFromInput(reader);
        File.closeReader(reader);
        this.itemParser = null;
        //this.loots.removeIf(loot -> loot.item.isEmpty());
        return true;
    }
    private LootParser(){}
}
