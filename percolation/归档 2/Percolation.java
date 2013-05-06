/*----------------------------------------------------------------
 *  Author:        Jason Zhang
 *  Written:       8/15/2012
 *  Last updated:  8/15/2012
 *
 *  Compilation:   javac Percolation
 *  Execution:     Library function.
 *
 *  Calculate the Percolation threshold via Monte Carlo simulation.
 *
 *  open() function to open a site.
 *  percolates() function return is already percolates.
 *
 *----------------------------------------------------------------*/

public class Percolation {

    private static final int STATE_OPEN = 1;
    private static final int STATE_FULL = 2;
    private static final boolean LOG = false;

    private int arrayWidth;
    private int sitesOpened;

    private WeightedQuickUnionUF quickUF;
    private int[][] sites;
    private int[]       topOpen;
    private int[]       bottomOpen;
    private int[]       topCache;
    private int[]       bottomCache;
    private int[]       connectToTopRecord;
    private int         topOpenNum;
    private int         bottomOpenNum;



    // create N-by-N grid, with all sites blocked
    public Percolation(int N)
    {
        if (N <= 0)
            throw new IllegalArgumentException("must be >0");
        sites = new int[N][N];
        quickUF = new WeightedQuickUnionUF(N * N);
        
        arrayWidth = N;
        topOpen = new int[N];
        bottomOpen = new int[N];

        topCache = new int[N];
        bottomCache = new int[N];

        for (int i = 0; i < N; i++) {
            topCache[i] = -1;
            bottomCache[i] = -1;
        }
    }

    private int siteIndexToUFIndex(int i, int j)
    {
        return (j * arrayWidth) + i;
    }

    private void tryConnect(int srci, int srcj, int desti, int destj)
    {
        if (desti >= 0
            && desti < arrayWidth
            && destj < arrayWidth
            && destj >= 0
            && isOpenInternal(desti, destj)) {
            quickUF.union(siteIndexToUFIndex(srci, srcj),
                          siteIndexToUFIndex(desti, destj));
        }
    }

    // open site (row i, column j) if it is not already
    public void open(int i, int j)
    {
        int row = i - 1;
        int col = j - 1;

        if (LOG)
            StdOut.printf("open : row:%d col:%d\n", row + 1, col + 1);


        if (row < 0 || row >= arrayWidth)
            throw new IndexOutOfBoundsException("invaled input");
        if (col <  0 || col >= arrayWidth)
            throw new IndexOutOfBoundsException("invalde input");

        if (isOpenInternal(row, col))
            return;

        openSiteInternal(row, col);

        // needs to open related 4 cells.
        tryConnect(row, col, row - 1, col);
        tryConnect(row, col, row + 1, col);
        tryConnect(row, col, row, col - 1);
        tryConnect(row, col, row, col + 1);

        // deal with top and bottom open,
        // for optmize the percolate time.
        if (row == 0) {
            topOpen[topOpenNum++] = col;
        }

        if (row == arrayWidth - 1) {
            bottomOpen[bottomOpenNum++] = col;
        }

        //if (sites[row - 1, col] == STATE_OPEN
        for (int top = 0; top < topOpenNum; top++) {
            topCache[top] = quickUF.find(siteIndexToUFIndex(0, topOpen[top]));
        }


        sitesOpened++;
    }

    // is site (row i, column j) open?
    public boolean isOpen(int i, int j) 
    {
        int row = i - 1;
        int col = j - 1;

        if (row < 0 || row >= arrayWidth)
            throw new IndexOutOfBoundsException("invaled input");
        if (col <  0 || col >= arrayWidth)
            throw new IndexOutOfBoundsException("invalde input");

        return isOpenInternal(row, col);
    }

    private boolean isOpenInternal(int i, int j) 
    {
        return (sites[i][j] & STATE_OPEN) != 0;
    }

    private void openSiteInternal(int i, int j)
    {
        sites[i][j] |= STATE_OPEN;
    }

    private boolean isFullCache(int i, int j)
    {
        return (sites[i][j] & STATE_FULL) != 0;
    }

    private void setFullCache(int i, int j)
    {
        sites[i][j] |= STATE_FULL;
    }
    
    public boolean isFull(int i, int j)
    {
        // For all input from API, -- i, and --j, to align with
        // internal count, start from 0.
        int row = i - 1;
        int col = j - 1;

        return isFullInternal(row, col);
    }

    private boolean isFullInternal(int row, int col)
    {
        if (row < 0 || row >= arrayWidth)
            throw new IndexOutOfBoundsException("invaled input");
        if (col <  0 || col >= arrayWidth)
            throw new IndexOutOfBoundsException("invalde input");

        if (sitesOpened == 0)
            return false;

        if (isFullCache(row, col))
            return true;

        int root = quickUF.find(siteIndexToUFIndex(row, col));
        for (int c = 0; c < topOpenNum; c++) {
            if (LOG)
                StdOut.printf("row:%d col:%d topCache[%d]=%d, root:%d connect:%b\n",
                              row, col,
                              c, topCache[c], root,
                              quickUF.connected(siteIndexToUFIndex(row, col),
                                                siteIndexToUFIndex(0, c)));
            if (topCache[c] == root) {
                setFullCache(row, col);
                return true;
            }
        }

        return false;
    }

    public boolean percolates()            // does the system percolate?
    {
        if (sitesOpened == 0)
            return false;

        if (topOpenNum == 0 || bottomOpenNum == 0)
            return false;


        for (int bottom = 0; bottom < bottomOpenNum; bottom++) {
            if (isFullInternal(arrayWidth - 1, bottomOpen[bottom]))
                return true;
        }

        /*
        for (int bottom = 0; bottom < bottomOpenNum; bottom++) {
            bottomCache[bottom] =
                quickUF.find(siteIndexToUFIndex(arrayWidth - 1,
                                                bottomOpen[bottom]));
        }

        for (int top = 0; top < topOpenNum; top++) {
            for (int bottom = 0; bottom < bottomOpenNum; bottom++) {
                if (topCache[top] == -1 || bottomCache[bottom] == -1)
                    continue;
                if (topCache[top] == bottomCache[bottom])
                    return true;
            }
        }
        */

        /*
        for (int top = 0; top < topOpenNum; top++) {
            for (int bottom = 0; bottom < bottomOpenNum; bottom++) {
                if (quickUF.connected(siteIndexToUFIndex(0, topOpen[top]),
                                      siteIndexToUFIndex(arrayWidth - 1,
                                                         bottomOpen[bottom])))
                    return true;
            }
        }
        */
        return false;
    }
}
