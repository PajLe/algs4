import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item>
{
    private int N = 0;          // number of total emelements.
    private int first;            // 
    private int last;
    private Item[] a;
    
    public Deque()                     // construct an empty deque
        {
            a = (Item[]) new Object[4];
            // Let's start at middle of the array.
        }
    public boolean isEmpty()           // is the deque empty?
    {
        return N == 0;
    }
    public int size()                  // return the number of items on the deque
    {
        return N;
    }
    public void addFirst(Item item)    // insert the item at the front
    {
        if (item == null)
            throw new NullPointerException();
        if (N == a.length) resize(2 * a.length);

        // warp
        if (N != 0) {
            if (first == 0)
                first = a.length - 1;
            else
                first--;
        }

        a[first] = item;
        N++;

    }

    public void addLast(Item item)     // insert the item at the end
    {
        if (item == null)
            throw new NullPointerException();

        if (N == a.length) resize(2 * a.length);

        if (N != 0)
            last = nextLast();
        N++;

        a[last] = item;
    }
        
    public Item removeFirst()          // delete and return the item
    // at the front
    {

        // @TODO needs strike the array when small.

        if (isEmpty())
            throw new NoSuchElementException();

        Item t = a[first];

        a[first] = null;
        if (first == a.length - 1)
            first = 0;
        else
            first++;
        N--;

        if (N > 0 && N == a.length/4) resize(a.length/2);

        return t;
    }
        
    public Item removeLast()           // delete and return the item at the end
    {

        // @TODO needs strike the array when smaller than 1/4
        if (isEmpty())
            throw new NoSuchElementException();


        Item t = a[last];

        a[last] = null;

        if (last == 0)
            last = a.length - 1;
        else
            last--;

        N--;

        if (N > 0 && N == a.length/4) resize(a.length/2);
        return t;
    }

    // public void dump()
    // {
    //     StdOut.printf("ARRAY: first %d last %d\n", first, last);
    //     for (Item i : a)
    //         StdOut.printf("ARRAY: %s\n", i);
    // }

    private int nextLast()
    {
        if (last == a.length - 1)       return 0;
            else                            return last + 1;
    }
    
    public Iterator<Item> iterator()   // return an iterator over
                                       // items in order from front to
                                       // end
    {
        return new DequeArrayInterator();
    }

    private class DequeArrayInterator implements Iterator<Item> {
        private int i = first;
        public boolean hasNext() {   return i != nextLast();   }
        public void remove()      { throw new UnsupportedOperationException();  }
        public Item next() {
            if (!hasNext())
                throw new NoSuchElementException();
            
            Item ret;
            ret = a[i];
            if (i == a.length - 1)      i = 0;
            else                        i++;

            return ret;
        }
    }

    // Resize, bigger: 2x when size is not enough. smaller: 1/2 when
    // 4/1 size only...

    private int increaseIndexWrap(int i) {
        if (i == a.length - 1)
            return 0;
        else
            return i+1;
    }

    
    private void resize(int max) {

        assert max >= N;
        Item[] temp = (Item[]) new Object[max];
        for (int i = 0; i < N; i++) {
            temp[i] = a[(first + i) % a.length];
        }
        a = temp;
        first = 0;
        last  = N - 1;
        
        // assert capacity >= N;

        // Item[] temp = (Item[]) new Object[capacity];
        // int j = 0;

        // for (Item it : a) {
        //     temp[j++] = it;
        // }
        
        // first = 0;
        // last = a.length - 1;

        // a = temp;
    }

}
