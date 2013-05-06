import java.util.Arrays;

public class BurrowsWheeler {

    private static final int R = 256;
    private static final boolean DEBUG = false;
    // apply Burrows-Wheeler encoding, reading from standard input and
    // writing to standard output
    public static void encode()
    {
        String s = BinaryStdIn.readString();
        char[] input = s.toCharArray();

        if (DEBUG) StdOut.printf("1\n");
        CircularSuffixArray csa = new CircularSuffixArray(s);

        if (DEBUG) StdOut.printf("a\n");
        for (int i = 0; i < csa.length(); i++)
            if (csa.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }

        if (DEBUG) StdOut.printf("b\n");
        for (int i = 0; i < s.length(); i++) {
            int idx = (csa.index(i) + csa.length() - 1) % csa.length();
            BinaryStdOut.write(input[idx], 8);
        }

        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler decoding, reading from standard input and
    // writing to standard output
    public static void decode()
    {
        int first = BinaryStdIn.readInt();
        String s = BinaryStdIn.readString();
        char[] input = s.toCharArray();
        char[] sorted = new char[input.length];

        for (int i = 0; i < input.length; i++)
            sorted[i] = input[i];
        
        Arrays.sort(sorted);
        
        int []baseIndex = new int[R];
        int []next = new int[input.length];

        // First, construct the next array...
        for (int i = 0; i < input.length; i++) {
            next[i] = getNextForChar(sorted[i], input, baseIndex);
        }

        if (DEBUG)
            for (int i = 0; i < next.length; i++)
                StdOut.printf("Next: i: %d   %d\n", i, next[i]);

        // show the string.
        int i, ptr;
        for (i = 0, ptr = first; i < next.length; i++, ptr = next[ptr]) {
            BinaryStdOut.write(sorted[ptr], 8); 
        }
            
        BinaryStdOut.close();
        
    }

    private static int getNextForChar(char c, char[] input, int []baseIndex)
    {
        for (int i = baseIndex[c]; i < input.length; i++) {
            if (input[i] == c) {
                baseIndex[c] = i+1;
                return i;
            }
        }

        // should not reach here....
        assert false;
        return 0;
    }

    // if args[0] is '-', apply Burrows-Wheeler encoding
    // if args[0] is '+', apply Burrows-Wheeler decoding
    public static void main(String[] args)
    {
        if      (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
    }
}
