// constructor takes a WordNet object
public class Outcast {
    private WordNet wordnet;
    public Outcast(WordNet w) {
        wordnet = w;
    }
    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int max = 0;
        String outword = null;
        for (String w : nouns) {
            int dsum = 0;
            for (String ww : nouns) {
                // if (ww == w)
                //     continue;
                dsum += wordnet.distance(w, ww);
                // StdOut.printf("\tdistance %s -->  %s: %d\n", w,
                // ww, wordnet_.distance(w, ww));
            }

            // StdOut.printf("\t %s distance to each other:%d\n",w, dsum);
            if (dsum > max) {
                max = dsum;
                outword = w;
            }
        }
        return outword;
    }

    // for unit testing of this class (such as the one below)
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            String[] nouns = In.readStrings(args[t]);
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
