/*----------------------------------------------------------------
 *  Author:        Jason Zhang
 *  Written:       8/15/2012
 *  Last updated:  8/15/2012
 *
 *  Compilation:   javac PercolationStats.java
 *  Execution:     % java PercolationStats
 *
 *  Do the stat of Percolation stat of Monte Carlo simulation.
 *
 *----------------------------------------------------------------*/

public class PercolationStats {

    private static final boolean ENABLE_TIME_PROFILE = false;
    private static final boolean VERB_TIME_PROFILE = false;
    private static final boolean USE_RANDOM_ARRAY = true;

    private int         arrayNum;
    private double[]    results;        // save the every time 's result tor

    // perform T independent computational experiments on an N-by-N grid
   public PercolationStats(int N, int T)
    {
       if (N <= 0 || T <= 0)
            throw new IllegalArgumentException("must be >0");
        arrayNum = N;
        results = new double[T];

        startTest();
    }

    private void percOpen(int index, Percolation  p)
    {
        int i, j;

        j = index / arrayNum;
        i = index % arrayNum;

        i++;
        j++;

        p.open(i, j);
    }

    private boolean percIsFull(int index, Percolation p)
    {
        int i, j;
        j = index / arrayNum;
        i = index % arrayNum;

        i++;
        j++;

        return p.isFull(i, j);
    }

    private boolean percIsOpen(int index, Percolation  p)
    {
        int i, j;

        j = index / arrayNum;
        i = index % arrayNum;

        i++;
        j++;

        return p.isOpen(i, j);
    }


    private void startTest()
    {
        int totalSize = arrayNum * arrayNum;
        int[] randomArray = new int[totalSize];
        for (int i = 0; i < totalSize; i++)
            randomArray[i] = i;

        for (int round = 0; round < results.length; round++) {
            int t = 0;
            Stopwatch ns, rs, opens;

            if (USE_RANDOM_ARRAY) {
                Percolation perc = new Percolation(arrayNum);

                // Setup random
                StdRandom.shuffle(randomArray);

                if (ENABLE_TIME_PROFILE)
                    opens = new Stopwatch();
                for (int i = 0; i < totalSize; i++) {
                    if (ENABLE_TIME_PROFILE && VERB_TIME_PROFILE)
                        ns = new Stopwatch();
                    percOpen(randomArray[i], perc);
                    if (ENABLE_TIME_PROFILE && VERB_TIME_PROFILE)
                        StdOut.printf("open:%g msec\n", ns.elapsedTime());

                    if (ENABLE_TIME_PROFILE && VERB_TIME_PROFILE)
                        ns = new Stopwatch();

                    if (!percIsFull(randomArray[i], perc))
                        continue;
                    if (perc.percolates()) {
                        double pers = i;
                        pers /= totalSize;
                        results[round] = pers;
                        break;
                    }
                    if (ENABLE_TIME_PROFILE && VERB_TIME_PROFILE)
                        StdOut.printf("percolates:%g msec\n", ns.elapsedTime());
                }
            

                if (ENABLE_TIME_PROFILE)
                    StdOut.printf("mseconds on open sites: %g\n",
                                  opens.elapsedTime());
            } else {
                Percolation perc = new Percolation(arrayNum);
                int count = 0;

                if (ENABLE_TIME_PROFILE)
                    opens = new Stopwatch();                

                do {
                    int rand = StdRandom.uniform(totalSize);
                    if (!percIsOpen(rand, perc)) {
                        percOpen(rand, perc);
                        count++;
                    }
                } while (!perc.percolates());
                double percetage = count;
                percetage /= totalSize;
                results[round] = percetage;
                if (ENABLE_TIME_PROFILE)
                    StdOut.printf("mseconds on open sites: %g\n",
                                  opens.elapsedTime());

                
            }
        }
    }
    // sample mean of percolation threshold
    public double mean()
    {
        return StdStats.mean(results);
    }

    // sample standard deviation of percolation threshold
    public double stddev()
    {
        return StdStats.stddev(results);
    }

    private double[] confidence95()
    {
        double []ret = new double[2];

        ret[0] = mean() - ((1.96 * Math.sqrt(stddev())) / Math.sqrt(results.length));
        ret[1] = mean() + ((1.96 * Math.sqrt(stddev())) / Math.sqrt(results.length));
        return ret;
    }

    // test client, described below
   public static void main(String[] args)
    {

        if (args.length < 2) {
            usage();
            return;
        }

        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);

        PercolationStats percstat = new PercolationStats(n, t);
        StdOut.printf("mean\t\t\t\t = %g\n", percstat.mean());
        StdOut.printf("stddev\t\t\t\t = %g\n", percstat.stddev());
        double [] confidence = percstat.confidence95();
        StdOut.printf("95%% confidence interval \t = %g, %g\n",
                      confidence[0], confidence[1]);
    }

    private static void usage()
    {
        StdOut.printf("Usage: java PercolationStats N T\n");
    }
}
