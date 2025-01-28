package undead.armies.parser;

import undead.armies.UndeadArmies;

import java.io.*;
import java.util.ArrayList;

//base class for all classes in this the "parser" folder.
public abstract class Parser
{
    public static final int bufferSize = 4096;
    protected int workingParentCount = 0;
    protected int workingIndex = 0;
    //workingIndex-- is not supported!
    protected int workingAmountOfCharToRead = 0;
    protected final char[] workingReaderArray = new char[Parser.bufferSize];
    //returns number of characters read.
    //if returns negative, there was an error and the return will not be accurate on errors.
    abstract protected void process();
    protected final void parseFromInput(Reader reader)
    {
        this.workingParentCount = 0;
        final BufferedReader bufferedReader = new BufferedReader(reader, Parser.bufferSize);
        while(true)
        {
            try
            {
                boolean terminate = false;
                this.workingAmountOfCharToRead = bufferedReader.read(this.workingReaderArray, 0, Parser.bufferSize);
                if(this.workingAmountOfCharToRead == -1)
                {
                    // Main.logger.debug("Attempting to find EOF.");
                    terminate = true;
                    for(int i = 0; i < Parser.bufferSize; i++)
                    {
                        if(this.workingReaderArray[i] == -1)
                        {
                            //Main.logger.debug("Found at index " + String.valueOf(i));
                            this.workingAmountOfCharToRead = i;
                            break;
                        }
                    }
                }
                for(this.workingIndex = 0; this.workingIndex < this.workingAmountOfCharToRead;)
                {
                    this.process();
                }
                if(terminate)
                {
                    break;
                }
            }
            catch(IOException e)
            {
                UndeadArmies.logger.error(e.getMessage(),e.getCause());
                break;
            }
        }
    }
    protected final String getKeyUntilOpen()
    {
        final ArrayList<Character> tempCharacterArrayList = new ArrayList<>();
        for(;this.workingIndex < this.workingAmountOfCharToRead; this.workingIndex++)
        {
            final char tempChar = this.workingReaderArray[this.workingIndex];
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
                this.workingIndex++;
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
        for(;this.workingIndex < this.workingAmountOfCharToRead; this.workingIndex++)
        {
            final char tempChar = this.workingReaderArray[this.workingIndex];
            if (!Character.isWhitespace(tempChar))
            {
                this.workingIndex++;
                return tempChar;
            }
        }
        return ' ';
    }
    protected final void spinUntilOpen()
    {
        for(;this.workingIndex < this.workingAmountOfCharToRead; this.workingIndex++)
        {
            final char tempChar = this.workingReaderArray[this.workingIndex];
            if(Character.isWhitespace(tempChar))
            {
                continue;
            }
            if(tempChar == '{')
            {
                this.workingIndex++;
                return;
            }
        }
    }
    protected final void spinUntilClose()
    {
        for(;this.workingIndex < this.workingAmountOfCharToRead; this.workingIndex++)
        {
            final char tempChar = this.workingReaderArray[this.workingIndex];
            if(Character.isWhitespace(tempChar))
            {
                continue;
            }
            if(tempChar == '}')
            {
                this.workingIndex++;
                return;
            }
        }
    }
    protected final boolean spinUntilEntryOrOpen()
    {
        for(;this.workingIndex < this.workingAmountOfCharToRead; this.workingIndex++)
        {
            final char tempChar = this.workingReaderArray[this.workingIndex];
            if(Character.isWhitespace(tempChar))
            {
                continue;
            }
            if(tempChar == '.')
            {
                this.workingIndex++;
                return true;
            }
            else if(tempChar == '{')
            {
                this.workingIndex++;
                return false;
            }
        }
        return false;
    }
    protected final boolean spinUntilEntryOrClose()
    {
        for(;this.workingIndex < this.workingAmountOfCharToRead; this.workingIndex++)
        {
            final char tempChar = this.workingReaderArray[this.workingIndex];
            if(Character.isWhitespace(tempChar))
            {
                continue;
            }
            if(tempChar == '.')
            {
                this.workingIndex++;
                return true;
            }
            else if(tempChar == '}')
            {
                this.workingIndex++;
                return false;
            }
        }
        return false;
    }
    protected final byte spinUntilEntryOrOpenAndClose()
    {
        for(;this.workingIndex < this.workingAmountOfCharToRead; this.workingIndex++)
        {
            final char tempChar = this.workingReaderArray[this.workingIndex];
            if(Character.isWhitespace(tempChar))
            {
                continue;
            }
            if(tempChar == '.')
            {
                this.workingIndex++;
                return 0;
            }
            else if(tempChar == '{')
            {
                this.workingIndex++;
                return -2;
            }
            else if(tempChar == '}')
            {
                this.workingIndex++;
                return -3;
            }
        }
        return -1;
    }
    protected final boolean spinUntilCharOrOpen(final char filter)
    {
        for(;this.workingIndex < this.workingAmountOfCharToRead; this.workingIndex++)
        {
            final char tempChar = this.workingReaderArray[this.workingIndex];
            if(Character.isWhitespace(tempChar))
            {
                continue;
            }
            if(tempChar == filter)
            {
                this.workingIndex++;
                return true;
            }
            else if(tempChar == '{')
            {
                this.workingIndex++;
                return false;
            }
        }
        return false;
    }
    protected final boolean spinUntilCharOrClose(final char filter)
    {
        for(;this.workingIndex < this.workingAmountOfCharToRead; this.workingIndex++)
        {
            final char tempChar = this.workingReaderArray[this.workingIndex];
            if(Character.isWhitespace(tempChar))
            {
                continue;
            }
            if(tempChar == filter)
            {
                this.workingIndex++;
                return true;
            }
            else if(tempChar == '}')
            {
                this.workingIndex++;
                return false;
            }
        }
        return false;
    }
    protected final boolean spinUntilOpenOrClose()
    {
        for(;this.workingIndex < this.workingAmountOfCharToRead; this.workingIndex++)
        {
            final char tempChar = this.workingReaderArray[this.workingIndex];
            if(Character.isWhitespace(tempChar))
            {
                continue;
            }
            if(tempChar == '{')
            {
                this.workingIndex++;
                return true;
            }
            if(tempChar == '}')
            {
                this.workingIndex++;
                return false;
            }
        }
        return false;
    }
    protected final String parseValueToKey()
    {
        final ArrayList<Character> tempCharacterArrayList = new ArrayList<>();
        final int stopWhenParentIs = this.workingParentCount;
        for(;this.workingIndex < this.workingAmountOfCharToRead; this.workingIndex++)
        {
            final char tempChar = this.workingReaderArray[this.workingIndex];
            if(Character.isWhitespace(tempChar))
            {
                continue;
            }
            if(tempChar == '{')
            {
                tempCharacterArrayList.add(tempChar);
                this.workingParentCount++;
            }
            if(tempChar == '}')
            {
                if(this.workingParentCount == stopWhenParentIs)
                {
                    this.workingIndex++;
                    break;
                }
                else
                {
                    this.workingParentCount--;
                    tempCharacterArrayList.add(tempChar);
                }
            }
            else
            {
                tempCharacterArrayList.add(tempChar);
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
        final int stopWhenParentIs = this.workingParentCount;
        tempCharacterArrayList.add(firstChar);
        for(;this.workingIndex < this.workingAmountOfCharToRead; this.workingIndex++)
        {
            final char tempChar = this.workingReaderArray[this.workingIndex];
            if(Character.isWhitespace(tempChar))
            {
                continue;
            }
            if(tempChar == '{')
            {
                tempCharacterArrayList.add(tempChar);
                this.workingParentCount++;
            }
            if(tempChar == '}')
            {
                if(this.workingParentCount == stopWhenParentIs)
                {
                    this.workingIndex++;
                    break;
                }
                else
                {
                    this.workingParentCount--;
                    tempCharacterArrayList.add(tempChar);
                }
            }
            else
            {
                tempCharacterArrayList.add(tempChar);
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
        this.workingParentCount++;
        this.spinUntilCloseForCurrentOpen();
    }
    protected final void spinUntilCloseForCurrentOpen()
    {
        final char tempChar = this.spinUntilNotWhitespace();
        final int stopWhenParentIs = this.workingParentCount - 1;
        while(stopWhenParentIs != this.workingParentCount)
        {
            if(this.spinUntilOpenOrClose())
            {
                this.workingParentCount++;
            }
            else
            {
                this.workingParentCount--;
            }
        }
    }
}
