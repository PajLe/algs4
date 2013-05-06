import java.util.Comparator;
// You must use the MinPQ data type

public class Solver {
    private static final boolean DEBUG = false;
    private static final boolean PROFILE = true;
    private Comparator<SearchNode> MANORDER = new MinOrder();

    private Board current;
    private boolean     canSolve = false;
    private int         minMove = -1;
    private int moves = 0;
    private Queue<Board> result;
    private MinPQ<SearchNode> minpq;
    private MinPQ<SearchNode> twinq;

    public Solver(Board initial)
    // find a solution to the initial board (using the A* algorithm)
    {
        current = initial;
        canSolve = true;


        Stopwatch sw = new Stopwatch();

        //        int maxMove = initial.dimension() * initial.dimension();

        result = new Queue<Board>();
        minpq = new MinPQ<SearchNode>(MANORDER);

        twinq = new MinPQ<SearchNode>(MANORDER);

        if (current.hamming() == 0) {
            canSolve = true;
            minMove = moves;
            return;
        }

        SearchNode curSearchNode = new SearchNode(current, 0);
        SearchNode preSearchNode = new SearchNode(null, 0);

        SearchNode twinSearchNode = new SearchNode(current.twin(), 0);
        SearchNode preTwinSearchNode = new SearchNode(null, 0);

        result.enqueue(curSearchNode.board);
        
        while (true) {
            searchForNode(twinSearchNode, preTwinSearchNode, twinq);
            preTwinSearchNode = twinSearchNode;
            SearchNode twinN, n;
            do {
                twinN = twinq.delMin();
            } while (twinN.board.equals(preTwinSearchNode.board));
            twinSearchNode = twinN;
            if (twinN.board.isGoal()) {
                canSolve = false;
                minMove = -1;
                break;
            }

            searchForNode(curSearchNode, preSearchNode, minpq);
            preSearchNode = curSearchNode;
            do {
                n = minpq.delMin();
            } while (n.board.equals(preSearchNode.board));
            result.enqueue(n.board);
            curSearchNode = n;

            if (curSearchNode.board.isGoal()) {
                canSolve = true;
                minMove = curSearchNode.moves;
                break;
            }
        }

        minpq = null;
        twinq = null;

        if (PROFILE)
            StdOut.printf("time on solve :%f ms\n", sw.elapsedTime());
    }

    
    private void searchForNode(SearchNode node,
                               SearchNode preNode,
                               MinPQ<SearchNode> q)
    {
        for (Board n : node.board.neighbors()) {
            if (!n.equals(preNode.board)) {
                q.insert(new SearchNode(n, node.moves + 1));
            }
        }
    }

    private class SearchNode implements Comparable<SearchNode>
    {
        private int moves;
        private Board board;
        public SearchNode(Board b, int m)
            {
                board = b;
                moves = m;
            }

        public int compareTo(SearchNode that) {
            if (that == null)
                throw new RuntimeException();
            int m = this.board.manhattan() + this.board.hamming();
            m += this.moves;

            int t = that.board.manhattan() + that.board.hamming();
            t += that.moves;

            if (m > t)
                return 1;
            else if (m < t)
                return -1;
            else
                return 0;
                    
        }
    }
    private boolean solveOneBoard(Board oneBoard)
    {
        return true;
    }

    private class MinOrder implements Comparator<SearchNode> {
        public int compare(SearchNode a, SearchNode b) {
            return a.compareTo(b);
        }
    }
    
    public boolean isSolvable()             // is the initial board
    // solvable?
    {
        return canSolve;
    }
    public int moves()                      // min number of moves to
    // solve initial board; -1
    // if no solution
    {
        return minMove;
    }
    public Iterable<Board> solution()       // sequence of boards in a
    // shortest solution; null
    // if no solution
    {

        if (canSolve)
            return result;
        return null;
    }

    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);
        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution()) {
                if (DEBUG)
                    StdOut.printf("manhatan:%d hamming:%d moves:%d \n",
                                  board.manhattan(),
                                  board.hamming() /*, board.moves */);
                StdOut.println(board);
            }
        }
    }

}
