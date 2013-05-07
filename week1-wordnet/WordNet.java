import java.util.HashMap;

// build a graph by id, the number,
// and build the map tween number and id  by HashMap,
// the key was name, and value was the id of that word.
public class WordNet {
    // constructor takes the name of the two input files
    // java.lang.IllegalArgumentException throw if input not have a root DAG

    private HashMap<String, Bag<Integer>> strintsmap;
    private HashMap<Integer, Bag<String>> intstrsmap;
    private Digraph    wordgraph;
    private SAP        sap;
    
    public WordNet(String synsets, String hypernyms)
    {
        In synin = new In(synsets);
        In hyin = new In(hypernyms);

        if (synin == null || hyin == null)
            throw new IllegalArgumentException("synset and hyin file not correct."
                                               + "synin:"+synin + " hyin: " + hyin);
        
        strintsmap = new HashMap<String, Bag<Integer>>();
        intstrsmap = new HashMap<Integer, Bag<String>>();
        int lines = 0;

        // Read all synset lines, and compose a hashmap.
        while (synin.hasNextLine()) {
            lines++;
            String line = synin.readLine();
            String [] parts = line.split(",");
            if (parts.length < 3)
                StdOut.println("error on parser synset lines");
            int id = Integer.parseInt(parts[0]);
            String [] words = parts[1].split(" ");

            for (String w : words) {
                Bag<Integer> b = strintsmap.get(w);
                if (b == null) {
                    b = new Bag<Integer>();
                    b.add(id);
                    strintsmap.put(w, b);
                } else {
                    b.add(id);
                }

                Bag<String> s = intstrsmap.get(id);
                if (s == null) {
                    s = new Bag<String>();
                    s.add(w);
                    intstrsmap.put(id, s);
                } else {
                    s.add(w);
                }
                
            }
        }

        //        StdOut.println("build a graph with: " +
        // lines + " vertexs");

        // start build the digraph.

        wordgraph = new Digraph(lines);
        while (hyin.hasNextLine()) {
            String l = hyin.readLine();
            String [] p = l.split(",");
            if (p.length < 2) {
                continue;
            }
            int a = Integer.parseInt(p[0]);
            for (int i = 1; i < p.length; i++) {
                wordgraph.addEdge(a, Integer.parseInt(p[i]));
            }
        }
        
        // StdOut.println("dump the graph");
        // StdOut.println(wordgraph.toString());

        DirectedCycle finder = new DirectedCycle(wordgraph);
        if (finder.hasCycle() || !checkGraphRootedDAG(wordgraph))
            throw new IllegalArgumentException("have cycle. or not one root");

        sap = new SAP(wordgraph);
    }

    private int lengthOfIterable(Iterable<Integer> a) {
        int cnt = 0;
        for (int v : a)
            cnt++;
        return cnt;
    }

    private boolean checkGraphRootedDAG(Digraph gr)
    {
        boolean haveroot = false;
        for (int v = 0; v < gr.V(); v++) {
            if (lengthOfIterable(gr.adj(v)) == 0 && haveroot) 
                return false;
            else if (lengthOfIterable(gr.adj(v)) == 0 && !haveroot)
                haveroot = true;
        }
        return true;
    }

    // returns all WordNet nouns
    public Iterable<String> nouns()
    {
        if (strintsmap == null)
            return null;
        return strintsmap.keySet();
    }

    // is the word a WordNet noun?
    // should run in log
    public boolean isNoun(String word)
    {
        return strintsmap.containsKey(word);
    }

    // distance between nounA and nounB (defined below) should throw a
    // java.lang.IllegalArgumentException unless both of the noun
    // arguments are WordNet nouns.  linera
    public int distance(String nounA, String nounB)
    {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("noun not in synmap");
        Bag<Integer> a = strintsmap.get(nounA);
        Bag<Integer> b = strintsmap.get(nounB);

        int aa = -1, bb = -1;

        for (int x : a) {
            aa = x;
            break;
        }
        for (int x : b) {
            bb = x;
            break;
        }
            
        if (a.size() > 1 || b.size() > 1) {
            //            StdOut.println("\t\t\tmulti sap still have
            //            bug: a.size():" + a.size() + " b.size: " +
            //            b.size());
            return sap.length(a, b);
        }
        return sap.length(aa, bb);
    }

    private String composeBag(Bag<String> b)
    {
        if (b == null)
            return null;
        String result = "";
        Stack<String> stack = new Stack<String>();

        for (String s : b)
            stack.push(s);

        while (!stack.isEmpty()) {
            result = result.concat(stack.pop());
            if (!stack.isEmpty())
                result = result.concat(" ");
        }

        return result;
    }

    // a synset (second field of synsets.txt) that is the common
    // ancestor of nounA and nounB in a shortest ancestral path
    // (defined below) should throw a
    // java.lang.IllegalArgumentException unless both of the noun
    // arguments are WordNet nouns.  linear.
    public String sap(String nounA, String nounB)
    {
        int aa = -1, bb = -1, ancestor = -1;
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("noun not in synmap");
        }

        Bag<Integer> a = strintsmap.get(nounA);
        Bag<Integer> b = strintsmap.get(nounB);

        if (a.size() > 1 || b.size() > 1) {
            StdOut.printf("ancestor: %d\n", ancestor);
            ancestor = sap.ancestor(a, b);
            return composeBag(intstrsmap.get(ancestor));
        } else {
            for (int x : a) {
                aa = x;
                break;
            }
            for (int x : b) {
                bb = x;
                break;
            }
            ancestor = sap.ancestor(aa, bb);
            return composeBag(intstrsmap.get(ancestor));
        }
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        while (!StdIn.isEmpty()) {
            String v = StdIn.readString();
            String w = StdIn.readString();

            int length = wordnet.distance(v, w);
            String sap = wordnet.sap(v, w);
            StdOut.printf("length:%d  sap: '%s'\n", length, sap);

        }
    }
}
