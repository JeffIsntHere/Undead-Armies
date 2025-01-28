package undead.armies.parser.loot;

import net.minecraft.commands.arguments.item.ItemParser;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
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
    protected double chance = 1.0d;
    protected int lowerBound = 0;
    protected int upperBound = 1;
    @Override
    protected void process()
    {
        if(super.workingParentCount == 0)
        {
            this.item = null;
            this.chance = 1.0d;
            this.lowerBound = 0;
            this.upperBound = 1;
            super.spinUntilOpen();
            super.workingParentCount++;
        }
        else if(super.workingParentCount == 1)
        {
            final char key = super.spinUntilNotWhitespace();
            if(key == '}' || !super.spinUntilOpenOrClose())
            {
                if(this.item != null)
                {
                    this.loots.add(new Loot(this.itemParser, this.item, this.chance, this.lowerBound, this.upperBound));
                }
                super.workingParentCount--;
            }
            else if(key == 'i')
            {
                this.item = super.parseValueToKey();
            }
            else if(key == 'c')
            {
                try
                {
                    this.chance = Double.valueOf(super.parseValueToKey());
                }
                catch(NumberFormatException e)
                {
                    this.chance = 1.0d;
                }
            }
            else if(key == 'l')
            {
                try
                {
                    this.lowerBound = Integer.valueOf(super.parseValueToKey());
                }
                catch(NumberFormatException e)
                {
                    this.lowerBound = 0;
                }
            }
            else if(key == 'u')
            {
                try
                {
                    this.upperBound = Integer.valueOf(super.parseValueToKey());
                }
                catch(NumberFormatException e)
                {
                    this.upperBound = 1;
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
        return true;
    }
    private LootParser(){}
}
