package undead.armies.misc;

public class LinkedList<T>
{
    public LinkedList next = this;
    public T data;
    public void expand(final T data)
    {
        final LinkedList<T> nextData = new LinkedList<>(data);
        nextData.next = this.next;
        this.next = nextData;
    }
    public LinkedList(final T data)
    {
        this.data = data;
    }
}
