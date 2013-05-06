

public class MyQuickSort
{


    public static void sort3(Comparable[] a, int lo, int hi)
    {
        int lt = lo, gt = hi, i = lo;

        if (gt <= lt)
            return;

        Comparable v = a[lo];

        while (i <= gt) {
            int cmp = a[i].compareTo(v);

            if (cmp < 0)  exchange(a, i++, lt++);
            if (cmp > 0)  exchange(a, i,   gt--);
            else                 i++;
        }

        sort3(a, lo, lt - 1);
        sort3(a, gt + 1, hi);
    }

    
    public static void sort(Comparable[] a)
    {
        // first , parttiion.
        StdRandom.shuffle(a);
            
        sort3 (a, 0, a.length -1);
    }

    private static boolean less(Comparable v, Comparable w) {
        return (v.compareTo(w) < 0);
    }
        
    // exchange a[i] and a[j]
    private static void exchange(Object[] a, int i, int j) {
        Object swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    
    private static void sort(Comparable[] a, int lo, int hi)
    {
        if (lo >= hi)
            return;
        int j = partition(a, lo, hi);

        sort(a, lo, j - 1);
        sort(a, j+1, hi);
    }

    private static int partition(Comparable []a, int lo, int hi)
    {
        // i = 0, and j = hi+1

        int i = 0, j = hi + 1;


        while (true) {

            // loop start
            // from the left, find the left one is equal or bigger than [lo], notice if the i == hi, stop,
            while (less(a[++i], a[lo]))
                if (i == hi) break;
        

            // from the right, find the one is equal or smaller then [lo], if j == lo, break;
            while (less(a[lo], a[--j]))
                if (j == lo) break;

            // if j cross the line, break

            if (i >= j)
                break;

            // exchange the i, and j
            exchange(a, i, j);
        }
        // loop finish

        exchange(a, lo, j);
        return j;
        // after j cross the line, exchange the lo and j.
        // it will  put the [lo] to the middle.

        // return the j, the smallest one.
    
    }

    // print array to standard output
    private static void show(Comparable[] a) {
        for (int i = 0; i < a.length; i++) {
            StdOut.println(a[i]);
        }
    }


    // Read strings from standard input, sort them, and print.
    public static void main(String[] args) {
        int[] a = StdIn.readInts();

        Integer[] newArray = new Integer[a.length];
        int i = 0;
        for (int value : a) {
            newArray[i++] = Integer.valueOf(value);
        }

        long detailProfile = System.nanoTime();
        Quick.sort(newArray);
        long t = System.nanoTime() - detailProfile;
        StdOut.printf("time : %d ns\n", t);
        //        show(newArray);

        // // display results again using select
        // StdOut.println();
        // for (int i = 0; i < a.length; i++) {
        //     String ith = (String) Quick.select(a, i);
        //     StdOut.println(ith);
        // }
    }


    
}
