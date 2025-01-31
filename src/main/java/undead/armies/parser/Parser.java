package undead.armies.parser;

import java.io.*;
import java.util.ArrayList;

/*
This parser was made for speed
 */

//base class for all classes in this the "parser" folder.
public abstract class Parser
{
    protected int parentCount = 0;
    protected BufferedReaderWrapper bufferedReaderWrapper = null;
    protected abstract void process();
    protected final void parseFromInput(Reader reader)
    {
        this.parentCount = 0;
        this.bufferedReaderWrapper = new BufferedReaderWrapper(reader);
        while(this.bufferedReaderWrapper.hasNext())
        {
            this.process();
        }
    }
    protected final String getKeyUntilOpen()
    {
        final ArrayList<Character> tempCharacterArrayList = new ArrayList<>();
        while(this.bufferedReaderWrapper.next())
        {
            final char tempChar = this.bufferedReaderWrapper.character;
            if(Character.isWhitespace(tempChar))
            {
                continue;
            }
            if(tempChar == '{')
            {
                //source : https://stackoverflow.com/a/6324852
                StringBuilder builder = new StringBuilder(tempCharacterArrayList.size());
                for(Character ch: tempCharacterArrayList)
                {
                    builder.append(ch);
                }
                return builder.toString();
            }
            else
            {
                tempCharacterArrayList.add(tempChar);
            }
        }
        return new String();
    }
    protected final char spinUntilNotWhitespace()
    {
        while(this.bufferedReaderWrapper.next())
        {
            if (!Character.isWhitespace(this.bufferedReaderWrapper.character))
            {
                return this.bufferedReaderWrapper.character;
            }
        }
        return ' ';
    }
    protected final void spinUntilOpen()
    {
        while(this.bufferedReaderWrapper.next())
        {
            if(Character.isWhitespace(this.bufferedReaderWrapper.character))
            {
                continue;
            }
            if(this.bufferedReaderWrapper.character == '{')
            {
                return;
            }
        }
    }
    protected final void spinUntilClose()
    {
        while(this.bufferedReaderWrapper.next())
        {
            if(Character.isWhitespace(this.bufferedReaderWrapper.character))
            {
                continue;
            }
            if(this.bufferedReaderWrapper.character == '}')
            {
                return;
            }
        }
    }
    protected final boolean spinUntilEntryOrOpen()
    {
        while(this.bufferedReaderWrapper.next())
        {
            if(Character.isWhitespace(this.bufferedReaderWrapper.character))
            {
                continue;
            }
            if(this.bufferedReaderWrapper.character == '.')
            {
                return true;
            }
            else if(this.bufferedReaderWrapper.character == '{')
            {
                return false;
            }
        }
        return false;
    }
    protected final boolean spinUntilEntryOrClose()
    {
        while(this.bufferedReaderWrapper.next())
        {
            if(Character.isWhitespace(this.bufferedReaderWrapper.character))
            {
                continue;
            }
            if(this.bufferedReaderWrapper.character == '.')
            {
                return true;
            }
            else if(this.bufferedReaderWrapper.character == '}')
            {
                return false;
            }
        }
        return false;
    }
    protected final byte spinUntilEntryOrOpenAndClose()
    {
        while(this.bufferedReaderWrapper.next())
        {
            if(Character.isWhitespace(this.bufferedReaderWrapper.character))
            {
                continue;
            }
            if(this.bufferedReaderWrapper.character == '.')
            {
                return 0;
            }
            else if(this.bufferedReaderWrapper.character == '{')
            {
                return -2;
            }
            else if(this.bufferedReaderWrapper.character == '}')
            {
                return -3;
            }
        }
        return -1;
    }
    protected final boolean spinUntilCharOrOpen(final char filter)
    {
        while(this.bufferedReaderWrapper.next())
        {
            if(Character.isWhitespace(this.bufferedReaderWrapper.character))
            {
                continue;
            }
            if(this.bufferedReaderWrapper.character == filter)
            {
                return true;
            }
            else if(this.bufferedReaderWrapper.character == '{')
            {
                return false;
            }
        }
        return false;
    }
    protected final boolean spinUntilCharOrClose(final char filter)
    {
        while(this.bufferedReaderWrapper.next())
        {
            if(Character.isWhitespace(this.bufferedReaderWrapper.character))
            {
                continue;
            }
            if(this.bufferedReaderWrapper.character == filter)
            {
                return true;
            }
            else if(this.bufferedReaderWrapper.character == '}')
            {
                return false;
            }
        }
        return false;
    }
    protected final boolean spinUntilOpenOrClose()
    {
        while(this.bufferedReaderWrapper.next())
        {
            if(Character.isWhitespace(this.bufferedReaderWrapper.character))
            {
                continue;
            }
            if(this.bufferedReaderWrapper.character == '{')
            {
                return true;
            }
            if(this.bufferedReaderWrapper.character == '}')
            {
                return false;
            }
        }
        return false;
    }
    protected final String parseValueToKey()
    {
        final ArrayList<Character> tempCharacterArrayList = new ArrayList<>();
        final int stopWhenParentIs = this.parentCount;
        while(this.bufferedReaderWrapper.next())
        {
            if(Character.isWhitespace(this.bufferedReaderWrapper.character))
            {
                continue;
            }
            if(this.bufferedReaderWrapper.character == '{')
            {
                tempCharacterArrayList.add(this.bufferedReaderWrapper.character);
                this.parentCount++;
            }
            else if(this.bufferedReaderWrapper.character == '}')
            {
                if(this.parentCount == stopWhenParentIs)
                {
                    break;
                }
                else
                {
                    this.parentCount--;
                    tempCharacterArrayList.add(this.bufferedReaderWrapper.character);
                }
            }
            else
            {
                tempCharacterArrayList.add(this.bufferedReaderWrapper.character);
            }
        }
        final StringBuilder builder = new StringBuilder(tempCharacterArrayList.size());
        for(Character ch: tempCharacterArrayList)
        {
            builder.append(ch);
        }
        return builder.toString();
    }
    protected final String parseValueToKey(final char firstChar)
    {
        final ArrayList<Character> tempCharacterArrayList = new ArrayList<>();
        final int stopWhenParentIs = this.parentCount;
        tempCharacterArrayList.add(firstChar);
        while(this.bufferedReaderWrapper.next())
        {
            if(Character.isWhitespace(this.bufferedReaderWrapper.character))
            {
                continue;
            }
            if(this.bufferedReaderWrapper.character == '{')
            {
                tempCharacterArrayList.add(this.bufferedReaderWrapper.character);
                this.parentCount++;
            }
            if(this.bufferedReaderWrapper.character == '}')
            {
                if(this.parentCount == stopWhenParentIs)
                {
                    break;
                }
                else
                {
                    this.parentCount--;
                    tempCharacterArrayList.add(this.bufferedReaderWrapper.character);
                }
            }
            else
            {
                tempCharacterArrayList.add(this.bufferedReaderWrapper.character);
            }
        }
        final StringBuilder builder = new StringBuilder(tempCharacterArrayList.size());
        for(Character ch: tempCharacterArrayList)
        {
            builder.append(ch);
        }
        return builder.toString();
    }
    protected final void parseValueArrayToKeyArrayList(ArrayList<String> output)
    {
        while(this.spinUntilOpenOrClose())
        {
            output.add(this.parseValueToKey());
        }
    }
    protected final void parseValueOrValueArrayToKeyArrayList(ArrayList<String> output)
    {
        final char tempChar = this.spinUntilNotWhitespace();
        if(tempChar == '{')
        {
            output.add(this.parseValueToKey());
            this.parseValueArrayToKeyArrayList(output);
        }
        else
        {
            output.add(this.parseValueToKey(tempChar));
        }
    }
    protected final void spinUntilCloseForNextOpen()
    {
        this.spinUntilOpen();
        this.parentCount++;
        this.spinUntilCloseForCurrentOpen();
    }
    protected final void spinUntilCloseForCurrentOpen()
    {
        final char tempChar = this.spinUntilNotWhitespace();
        final int stopWhenParentIs = this.parentCount - 1;
        while(stopWhenParentIs != this.parentCount)
        {
            if(this.spinUntilOpenOrClose())
            {
                this.parentCount++;
            }
            else
            {
                this.parentCount--;
            }
        }
    }
}
