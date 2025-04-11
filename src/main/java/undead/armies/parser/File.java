package undead.armies.parser;

import undead.armies.UndeadArmies;

import java.io.*;

//excellent explanation of File readers https://stackoverflow.com/questions/9648811/specific-difference-between-bufferedreader-and-filereader
public class File
{
    protected final java.io.File directory = new java.io.File("config/" + UndeadArmies.modId);

    public File()
    {
        this.directory.mkdir();
    }

    public static void closeReader(Reader reader)
    {
        try
        {
            reader.close();
        }
        catch(IOException e)
        {
            UndeadArmies.logger.error(e.getMessage(),e.getCause());
        }
    }

    public boolean fileExists(String fileName)
    {
        return new java.io.File(this.directory, fileName).exists();
    }

    public Reader getFileReader(String fileName)
    {
        try
        {
            java.io.File file = new java.io.File(this.directory, fileName);
            file.createNewFile();
            Reader reader = new FileReader(file);
            return reader;
        }
        catch(SecurityException e)
        {
            UndeadArmies.logger.error(e.getMessage(),e.getCause());
        }
        catch(FileNotFoundException e)
        {
            UndeadArmies.logger.error(e.getMessage(),e.getCause());
        }
        catch(IOException e)
        {
            UndeadArmies.logger.error(e.getMessage(),e.getCause());
        }
        return null;
    }

    public PrintStream getFilePrinter(String fileName)
    {
        try
        {
            java.io.File file = new java.io.File(this.directory, fileName);
            file.createNewFile();
            PrintStream printStream = new PrintStream(file);
            return printStream;
        }
        catch(SecurityException e)
        {
            UndeadArmies.logger.error(e.getMessage(),e.getCause());
        }
        catch(FileNotFoundException e)
        {
            UndeadArmies.logger.error(e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }
}
