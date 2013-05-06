import java.util.Arrays;

public class Board {

    private static final boolean DEBUG = false;

    private int N;
    private int rowSize;
    private int columnSize;
    private int emptyIndex;     // index of empty array
    //    public int moves;

    //    public int moves = 0;

    private int[][] currentBoard;

    private int manVal;
    private boolean needsCalcMan;

    private int hamVal;
    private boolean needsCalcHam;
    
    public Board(int[][] blocks)
    // construct a board from an N-by-N array of blocks
    // (where blocks[i][j] = block in row i, column j)
    {
        needsCalcMan = true;
        needsCalcHam = true;
        if (blocks == null)
            throw new RuntimeException();
        
        N = blocks[0].length * blocks[0].length;
        columnSize = blocks[0].length;
        rowSize = N / columnSize;

        currentBoard = dupArray(blocks);

        if (DEBUG)
            StdOut.printf("construct a Board with %d x %d empty is:%d\n",
                          columnSize, rowSize, emptyIndex);

        // Construct the target board of this board.
        emptyIndex = findEmptyIndex(currentBoard);
    }

    private Board()
    {
    }

    private void setUpBoard()
    {
        needsCalcMan = true;
        needsCalcHam = true;
        if (currentBoard == null)
            throw new RuntimeException();
        
        N = currentBoard[0].length * currentBoard[0].length;
        columnSize = currentBoard[0].length;
        rowSize = N / columnSize;
        
        if (DEBUG)
            StdOut.printf("construct a Board with %d x %d empty is:%d\n",
                          columnSize, rowSize, emptyIndex);

        // Construct the target board of this board.
        emptyIndex = findEmptyIndex(currentBoard);
    }

    private int findEmptyIndex(int [][] board)
    {
        for (int i = 0; i < N; ++i)
            if (board[i / columnSize][i % columnSize] == 0)
                return i;
        throw new RuntimeException("Board have no empty index");
    }
    public int dimension()                 // board dimension N
    {
        return rowSize;
    }
    public int hamming()                   // number of blocks out of place
    {
        if (!needsCalcHam)
            return hamVal;
        int outOfOrder = 0;
        for (int i = 0; i < N - 1; ++i) {
            int v = currentBoard[i / columnSize][i % columnSize];
            // if (v == i + 1 /* && v != 0 */)
            if (v != i + 1)
                ++outOfOrder;
        }
        needsCalcHam = false;
        hamVal = outOfOrder;
        return outOfOrder;
    }

    private static int abs(int n)
    {
        if (n < 0)
            return -n;
        else
            return n;
    }

    public int manhattan()
    // sum of Manhattan distances between blocks and goal
    {
        if (!needsCalcMan)
            return manVal;
        int sumDistance = 0;

        for (int i = 0; i < N; ++i) {
            int row, column, val;
            row = i / rowSize;
            column = i % rowSize;
            val = currentBoard[row][column];
            if (val == 0)
                continue;
            --val;              // decrease to align the index of our
                                // array.
            sumDistance += (abs(row - (val / rowSize))
                            + abs(column - (val % rowSize)));

            if (DEBUG)
                StdOut.printf("manhattan: %d %d val:%d --> %d, %d %d\n",
                              row, column, val,
                              (abs(row - (val / rowSize)))
                              + abs(column - (val % rowSize)),
                              abs(row - (val / rowSize)),
                              abs(column - (val % rowSize)));
        }
        manVal = sumDistance;
        needsCalcMan = false;
        return sumDistance;
    }
    public boolean isGoal()                // is this board the goal board?
    {
        if (emptyIndex == N - 1 && hamming() == 0)
            return true;
        return
            false;
    }

    // duplicate the current array.
    private int [][] dupArray(int [][] old)
    {
        int [][] board = new int[rowSize][rowSize];
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < columnSize; j++)
                board[i][j] = old[i][j];
        }
        return board;
    }

    // a board obtained by exchanging two adjacent blocks in the same row
    public Board twin()            
    {
        int [][] twinBoard = dupArray(currentBoard);

        for (int i = 0; i < rowSize; i++) {
            if (twinBoard[i][0] != 0 && twinBoard[i][1] != 0) {
                int t = twinBoard[i][0];
                twinBoard[i][0] = twinBoard[i][1];
                twinBoard[i][1] = t;
                break;
            }
        }

        return new Board(twinBoard);
    }
    public boolean equals(Object y)        // does this board equal y?
    {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;

        Board that = (Board) y;

        if (that.rowSize != this.rowSize) return false;

        for (int i = 0; i < rowSize; i++) {
            if (!Arrays.equals(this.currentBoard[i], that.currentBoard[i]))
                return false;
        }
        return true;
    }

    private void swapIndexOfArray(int[][] tiles, int width,
                                  int index1, int index2)
    {
        int t = tiles[index1 / width][index1 % width];
        tiles[index1 / width][index1 % width] =
            tiles[index2 / width][index2 % width];
        tiles[index2 / width][index2 % width] = t;
    }
    
    public Iterable<Board> neighbors()     // all neighboring boards
    {
        Queue result = new Queue<Board>();

        // 0 no edge, 1, up/left edge, 2, bottom, right edge
        int verticalEdge = 0;
        int horizontalEdge = 0;

        if (emptyIndex % columnSize == 0)
            horizontalEdge = 1;
        else if (emptyIndex % columnSize == (columnSize - 1))
            horizontalEdge = 2;

        if (emptyIndex / rowSize == 0)
            verticalEdge = 1;
        else if (emptyIndex / rowSize == (rowSize - 1))
            verticalEdge = 2;

        if (horizontalEdge != 1) {
            int [][] tboard = dupArray(currentBoard);
            swapIndexOfArray(tboard, rowSize, emptyIndex, emptyIndex - 1);
            Board t = new Board();
            t.currentBoard = tboard;
            t.setUpBoard();
            //            t.moves = this.moves + 1;
            result.enqueue(t);
        }
        
        if (horizontalEdge != 2) {
            int [][] tboard = dupArray(currentBoard);
            swapIndexOfArray(tboard, rowSize, emptyIndex, emptyIndex + 1);
            Board t = new Board();
            t.currentBoard = tboard;
            t.setUpBoard();
            //            t.moves = this.moves + 1;
            result.enqueue(t);
        }

        if (verticalEdge != 1) {
            int [][] tboard = dupArray(currentBoard);
            swapIndexOfArray(tboard, rowSize,
                             emptyIndex, emptyIndex - rowSize);
            
            Board t = new Board();
            t.currentBoard = tboard;
            t.setUpBoard();
            result.enqueue(t);
        }

        if (verticalEdge != 2) {
            int [][] tboard = dupArray(currentBoard);
            swapIndexOfArray(tboard, rowSize,
                             emptyIndex, emptyIndex + rowSize);

            Board t = new Board();
            t.currentBoard = tboard;
            t.setUpBoard();
            result.enqueue(t);
        }
        return result;
    }
    // string representation of the board (in the output format specified below)
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        s.append(rowSize + "\n");
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < rowSize; j++) {
                s.append(String.format("%2d ", currentBoard[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }
}
