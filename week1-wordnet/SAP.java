// Exceptions: All methods should throw a
// java.lang.IndexOutOfBoundsException if one (or more) of the input
// arguments is not between 0 and G.V() - 1. You may assume that the
// iterable arguments contain at least one integer.

// All methods (and the constructor) should take time at most
// proportional to E + V in the worst case, where E and V are the
// number of edges and vertices in the digraph, respectively. Your
// data type should use space proportional to E + V.

// constructor takes a digraph (not necessarily a DAG)

import java.util.HashMap;

public class SAP {
    private static  class SAPResultUnit {
        private int length;
        private int ancestor;
        public SAPResultUnit(int len, int acc) {
            length = len;
            ancestor = acc;
        }
        int length() { return length; }
        int ancestor() { return ancestor; }
    };

    // acenstor faster...
    private HashMap<String, SAPResultUnit> resultCache;
    private int INTMAX = Integer.MAX_VALUE;
    private Digraph g;

    public SAP(Digraph gg) {
        g = gg;
        resultCache = new HashMap<String, SAPResultUnit>();
        // StdOut.println("dump the graph");
        // StdOut.println(g.toString());
    }

    private String getHash(int v, int w) {
        if (v > w)
            return Integer.toString(v) + "/" + Integer.toString(w);
        else
            return Integer.toString(w) + "/" + Integer.toString(v);
    }
    
    // length of shortest ancestral path between v and w; -1 if no
    // such path
    public int length(int v, int w)
    {
        if (v == w)
            return 0;
        return lengthAncestor(v, w).length();
    }

    // a common ancestor of v and w that participates in a shortest
    // ancestral path; -1 if no such path
    public int ancestor(int v, int w)
    {
        return lengthAncestor(v, w).ancestor();
    }

    
    private SAPResultUnit lengthAncestor(int v, int w)
    {
        int cur = w;
        int vdlength = INTMAX;
        int wdlength = INTMAX;
        int ancenstor = -1;
        int accl     = 0;
        int findlength = INTMAX;

        if (max(v, w) > g.V() - 1 || min(v, w) < 0)
            throw new IndexOutOfBoundsException("out of index");

        SAPResultUnit cache;
        cache = resultCache.get(getHash(v, w));
        if (cache != null) {
            return cache;
        }
        // V + E
        DeluxeBFS vbfs =
            new DeluxeBFS(g, v);
        // V + E
        DeluxeBFS wbfs =
            new DeluxeBFS(g, w);

        // E 
        if (vbfs.hasPathTo(w))
            vdlength = vbfs.pathToCount(w);

        // E 
        if (wbfs.hasPathTo(v))
            wdlength = wbfs.pathToCount(v);


        do {
            if (vbfs.hasPathTo(cur)) {
                int l = accl - 1
                    + vbfs.pathToCount(cur);
                if (l < findlength) {
                    findlength = l;
                    ancenstor = cur;
                } else if (l >= findlength) {
                    break;
                }
            }
            if (lengthOfIterable(g.adj(cur)) <= 0)
                break;
            accl++;
            cur = firstOfIterable(g.adj(cur));
            if (accl > g.E())           // workaround... for
                break;
        } while (cur != INTMAX && cur != w);

        // StdOut.printf("length: vdlength: " + vdlength
        //               + " wdlength:" + wdlength +
        //               " find length: "
        //               + findlength + "\n");
        int t = min(min(vdlength, wdlength), findlength);

        // wdlength -1 means, the edge number, not vertex number.
        if (t == INTMAX)
            cache =  new SAPResultUnit(-1, -1);
        else if (t == vdlength)
            cache = new SAPResultUnit(vdlength - 1, w);
        else if (t == wdlength)
            cache = new SAPResultUnit(wdlength - 1, v);
        else
            cache = new SAPResultUnit(findlength, ancenstor);
        resultCache.put(getHash(w, v), cache);
        return cache;
    }

    private int lengthOfIterable(Iterable<Integer> a) {
        int cnt = 0;
        for (int v : a)
            cnt++;
        return cnt;
    }

    private int firstOfIterable(Iterable<Integer> a) {
        for (int v : a)
            return v;

        return INTMAX;
    }


    private int max(int a, int b) { if (a > b) return a;
        else return b;
    }
    private int min(int a, int b) { if (a < b) return a; 
        else return b;
    }
    // length of shortest ancestral path between any vertex in v and
    // any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w)
    {
        return multiLengthAncestor(v, w).length();
    }
    // a common ancestor that participates in shortest ancestral path;
    // -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w)
    {
        return multiLengthAncestor(v, w).ancestor();
    }

    private SAPResultUnit multiLengthAncestor(Iterable<Integer> v,
                                               Iterable<Integer> w) {
        int cur;
        int vdlength = INTMAX;
        int wdlength = INTMAX;
        int ancenstor = -1;
        int accl     = 0;
        int findlength = INTMAX;

        int vvv = -1;
        int www = -1;

        for (int ww : w)
            if (ww > g.V() - 1 || ww < 0)
                throw new IndexOutOfBoundsException("out of index");
        for (int vv : v)
            if (vv > g.V() - 1 || vv < 0)
                throw new IndexOutOfBoundsException("out of index");

        // V + E
        DeluxeBFS vbfs =
            new DeluxeBFS(g, v);
        // V + E
        DeluxeBFS wbfs =
            new DeluxeBFS(g, w);

        // bE
        for (int vv : v) {
            try {
                if (wbfs.hasPathTo(vv))
                    wdlength = min(wdlength, wbfs.pathToCount(vv));
            } catch (NullPointerException e) {
                StdOut.printf(" v: length: %d w.length:%d connects:%b",
                              lengthOfIterable(v),
                              lengthOfIterable(w),
                              wbfs.hasPathTo(vv));
            }
        }

        for (int ww : w) {
            cur = ww;
            do {
                if (vbfs.hasPathTo(cur)) {
                    int newlen = accl - 1
                        + vbfs.pathToCount(cur);
                    if (newlen < findlength) {
                        ancenstor = cur;
                        findlength = newlen;
                    }
                    break;
                }
                if (lengthOfIterable(g.adj(cur)) <= 0)
                    break;
                accl++;
                cur = firstOfIterable(g.adj(cur));
            } while (cur != INTMAX);
        }

        int t = min(min(vdlength, wdlength), findlength);

        if (t == INTMAX)
            return new SAPResultUnit(-1, -1);
        else
            return new SAPResultUnit(findlength, ancenstor);
    }

    // for unit testing of this class (such as the one below)
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {

            //            Bag<Integer> ba = new Bag<Integer>();
            //            Bag<Integer> bb = new Bag<Integer>();
            int v = StdIn.readInt();
            //            ba.add(v);
            //            v = StdIn.readInt();
            //            ba.add(v);
            int w = StdIn.readInt();
            //            bb.add(w);
            //            w = StdIn.readInt();
            //            bb.add(w);

            //            int length = sap.length(ba, bb);
            //            int ancestor = sap.ancestor(ba, bb);
            Stopwatch w1 = new Stopwatch();
            int i = 1000;
            int length = -1, ancestor = -1;
            while (--i > 0) {
                length   = sap.length(v, w);
                ancestor = sap.ancestor(v, w);
            }
            double a1 = w1.elapsedTime();
            StdOut.printf("length = %d, ancestor = %d\n",
                          length, ancestor);
            StdOut.printf("first time: %g \n",
                          a1);
        }
    }

}
