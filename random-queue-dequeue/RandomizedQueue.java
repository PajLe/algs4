// Randomized queue. A randomized queue is similar to a stack or
// queue, except that the item removed is chosen uniformly at random
// from items in the data structure. Create a generic data type
// RandomizedQueue that implements the following API:

// public class RandomizedQueue<Item> implements Iterable<Item> {
//    public RandomizedQueue() // construct an empty randomized queue
//    public boolean isEmpty() // is the queue empty?  public int
//    size() // return the number of items on the queue public void
//    enqueue(Item item) // add the item public Item dequeue() //
//    delete and return a random item public Item sample() // return
//    (but do not delete) a random item public Iterator<Item>
//    iterator() // return an independent iterator over items in
//    random order } Throw a java.lang.NullPointerException if the
//    client attempts to add a null item; throw a
//    java.util.NoSuchElementException if the client attempts to
//    sample or dequeue an item from an empty randomized queue; throw
//    a java.lang.UnsupportedOperationException if the client calls
//    the remove() method in the iterator.

// Your randomized queue implementation should support each randomized
// queue operation (besides creating an iterator) in constant
// amortized time and use space proportional to the number of items
// currently in the queue. That is, any sequence of M randomized queue
// operations (starting from an empty queue) should take at most cM
// steps in the worst case, for some constant c. Additionally, your
// iterator implementation should support construction in time linear
// in the number of items and it should support the operations next()
// and hasNext() in constant worst-case time; you may use a linear
// amount of extra memory per iterator. The order of two iterators to
// the same randomized queue should be independent; each iterator must
// maintain its own random order.



// basic idea, when enqueu, first put the item into last, and exchange
// with one of items in the list, use a random generater.

// when dequeu, sample, generate a random number.

// when interator, just from first to last ?

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {

   private Item[] q;
   private int N;
   private int first;
   private int last;
    
   public RandomizedQueue()           // construct an empty randomized
                                      // queue
       {
           q = (Item[]) new Object[4];
       }

       // resize the underlying array
    private void resize(int max) {
        assert max >= N;
        Item[] temp = (Item[]) new Object[max];
        for (int i = 0; i < N; i++) {
            temp[i] = q[(first + i) % q.length];
        }
        q = temp;
        first = 0;
        last  = N;
    }

   public boolean isEmpty()           // is the queue empty?
   {
       return N == 0;
   }
   
   public int size()                  // return the number of items on
                                      // the queue
   {
       return N;
   }


   private void swap(int i, int j)
   {
       Item t = q[j];
       q[j] = q[i];
       q[i] = t;
   }
   
   public void enqueue(Item item)     // add the item
   {
       // double size of array if necessary and recopy to front of array

       if (item == null)
            throw new NullPointerException();
       
       if (N == q.length) resize(2*q.length);   // double size of array if necessary
       q[last++] = item;                        // add item
       if (last == q.length) last = 0;          // wrap-around
       N++;

       // swap the choosen on with the last one.
       if (N > 2) {
           int choosen = StdRandom.uniform(N);

           int index = (first + choosen) % q.length;

           //   StdOut.printf("swap between :last:%d index:%d first:%d
           //   choosen:%d\n", last, index, first, choosen);

           if (last == 0)
               swap(q.length - 1, index);
           else
               swap(last - 1, index);
       }
   }

   // public void dump()
   //     {
   //         StdOut.printf("----------------------\n");
   //      for (Item i : q)
   //          StdOut.printf("ARRAY: %s\n", i);
   //  }


   public Item dequeue()              // delete and return a random item
   {
       if (isEmpty())
            throw new NoSuchElementException();

       Item item = q[first];
        q[first] = null;                            // to avoid loitering
        N--;
        first++;
        if (first == q.length) first = 0;           // wrap-around
        // shrink size of array if necessary
        if (N > 0 && N == q.length/4) resize(q.length/2); 
        return item;
   }
   public Item sample()               // return (but do not delete) a
                                      // random item
   {
       int choosen = StdRandom.uniform(N);
       int index = (first + choosen) % q.length;
       return q[index];
   }

   public Iterator<Item> iterator() { return new RandomQueueIterator(); }
    // an iterator, doesn't implement remove() since it's optional
    private class RandomQueueIterator  implements Iterator<Item> {
        private int i;

        public boolean hasNext()  { return i < N; }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = q[(i + first) % q.length];
            i++;
            return item;
        }
    }

}
