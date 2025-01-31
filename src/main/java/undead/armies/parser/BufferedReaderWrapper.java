package undead.armies.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class BufferedReaderWrapper
{
    public static final int bufferSize = 4096;
    protected final BufferedReader bufferedReader;
    protected int index = 0;
    protected int dataSize = 0;
    protected final char[] data = new char[BufferedReaderWrapper.bufferSize];
    protected boolean end = false;
    public BufferedReaderWrapper(final Reader reader)
    {
        this.bufferedReader = new BufferedReader(reader, BufferedReaderWrapper.bufferSize);
    }
    protected boolean read()
    {
        try
        {
            if(this.end)
            {
                this.bufferedReader.close();
                return false;
            }
            this.index = 0;
            this.dataSize = this.bufferedReader.read(this.data, 0, BufferedReaderWrapper.bufferSize);
            if(this.dataSize == -1)
            {
                this.end= true;
                for(int i = 0; i < BufferedReaderWrapper.bufferSize; i++)
                {
                    if(this.data[i] == -1)
                    {
                        this.dataSize = i;
                        break;
                    }
                }
            }
        }
        catch(IOException e)
        {
            try
            {
                this.bufferedReader.close();
            } 
            catch (IOException ex)
            {
                return false;
            }
            return false;
        }
        return true;
    }
    public char character = 0;
    public boolean next()
    {
        if(this.index >= this.dataSize && !this.read())
        {
            return false;
        }
        this.character = this.data[this.index];
        this.index++;
        return true;
    }
    public boolean hasNext()
    {
        return this.index < this.dataSize || this.read();
    }
}
