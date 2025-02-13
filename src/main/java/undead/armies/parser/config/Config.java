package undead.armies.parser.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import undead.armies.parser.config.type.TypeArgument;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class Config
{
    public final String name;
    protected final ArrayList<StringPair> data = new ArrayList<>();
    public final HashSet<TypeArgument> typeArguments = new HashSet<>();
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    @Override
    public boolean equals(Object other)
    {
        if(!(other instanceof Config))
        {
            return false;
        }
        return ((Config) other).name.equals(this.name);
    }
    @Nullable
    public String get(@NotNull final String name)
    {
        final char[] chars = name.toCharArray();
        ArrayList<StringPair> past;
        ArrayList<StringPair> present = this.data;
        int currentIndex = 0;
        if(chars.length == 0)
        {
            return null;
        }
        do
        {
            past = present;
            present = new ArrayList<>();
            for(final StringPair stringPair : past)
            {
                if(currentIndex >= stringPair.right.length())
                {
                    continue;
                }
                if(stringPair.right.charAt(currentIndex) == chars[currentIndex])
                {
                    present.add(stringPair);
                }
            }
            currentIndex++;
        }
        while(!present.isEmpty() && (currentIndex < chars.length));
        return (present.isEmpty()) ? ((currentIndex < chars.length) ? null : past.getFirst().right) : present.getFirst().right;
    }
    public void process()
    {
        for(StringPair stringPair : this.data)
        {
            for(TypeArgument typeArgument : this.typeArguments)
            {
                if(typeArgument.compare(stringPair.left))
                {
                    typeArgument.type.save(stringPair.right);
                }
            }
        }
    }
    public void clear()
    {
        this.data.clear();
    }
    public void add(@NotNull final StringPair pair)
    {
        this.data.add(pair);
    }
    public Config(@NotNull final String name)
    {
        this.name = name;
    }
    public String toString()
    {
        return this.name;
    }
}
