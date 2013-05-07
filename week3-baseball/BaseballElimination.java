import java.util.HashMap;

public class BaseballElimination {
    private static final boolean DEBUG = false;
    private int teamNumber;
    private int[][] gameMatrix;         // schedule of left games matrix.
    private int[]   wins;               // array that every team wins.
    private int[]   lose;               // array that team lost..
    private int[]   remains;              // game left number of each team
    private String[] teamsName;
    private HashMap<String, Integer> nameIndex;
    private boolean[] elimited;
    private boolean[] simpleLimited;
    private HashMap<Integer, Stack<String>> elimitedList;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) 
    {
        In in = new In(filename);

        teamNumber = in.readInt();
        
        teamsName = new String[teamNumber];
        gameMatrix = new int[teamNumber][teamNumber];
        wins       = new int[teamNumber];
        lose       = new int[teamNumber];
        remains     = new int[teamNumber];
        elimited   = new boolean[teamNumber];
        simpleLimited = new boolean[teamNumber];
        elimitedList = new HashMap<Integer, Stack<String>>();
        nameIndex = new HashMap<String, Integer>();
        
        for (int i = 0; i < teamNumber; i++) {
            teamsName[i] = in.readString();
            wins[i] = in.readInt();
            lose[i] = in.readInt();
            remains[i] = in.readInt();
            nameIndex.put(teamsName[i], i);

            for (int j = 0; j < teamNumber; j++) {
                gameMatrix[i][j] = in.readInt();
            }
        }

        // Basic setup is done, now start doing the graph setup.

        // First, check the trival elimited

        for (int i = 0; i < teamNumber; i++)
            checkElimitedofTeamTrival(i);

        for (int i = 0; i < teamNumber; i++)
            checkElimitedOfTeam(i);

        // Then Cacl the flow network elimited.
        
    }

    private void checkElimitedofTeamTrival(int team)
    {

        for (int i = 0; i < teamNumber; i++) {
            if (i == team) continue;
            if (wins[team] + remains[team] < wins[i]) {
                elimited[team] = true;
                simpleLimited[team] = true;
                addTeamElimitedList(team, i);
            }
        }
    }

    private void checkElimitedOfTeam(int team) {
        FlowNetwork flowNetwork;
        int matchs = getMatchNumbers(teamNumber);
        // we want match number without x
        int vnum = matchs + 1 + 1 + teamNumber; // s + v + matchs + teams
        int tnode = vnum - 1;

        if (simpleLimited[team])
            return;
        flowNetwork = new FlowNetwork(vnum);
        int count = 1;
        for (int i = 0; i < teamNumber; i++) {
            for (int j = i; j < teamNumber; j++) {
                if (i == j)
                    continue;
                if (i == team || j == team)
                    continue;
                FlowEdge e = new FlowEdge(0, count, gameMatrix[i][j]);
                flowNetwork.addEdge(e);
                flowNetwork.addEdge(new FlowEdge(count, matchs + i,
                                                 Double.POSITIVE_INFINITY));
                flowNetwork.addEdge(new FlowEdge(count, matchs + j,
                                                 Double.POSITIVE_INFINITY));
                count++;    
            }
        }

        if (DEBUG) {
            for (int i = 0; i < teamNumber; i++) {
                StdOut.printf("TeamName: %s node: %d\n", teamsName[i],  matchs + i);
            }
        }

        for (int i = 0; i < teamNumber; i++) {
            if (i == team)
                continue;
            int ew = wins[team] + remains[team] - wins[i];
            if (ew < 0)
                ew = 0;
            flowNetwork.addEdge(new FlowEdge(count + i, tnode, ew));
        }

        FordFulkerson maxflow = new FordFulkerson(flowNetwork, 0, tnode);

        int t = tnode;
        FlowNetwork G = flowNetwork;
        Bag<FlowEdge> finishLine = new Bag<FlowEdge>();

        if (DEBUG) {
            StdOut.printf("Team: %s got :%g \n",
                          teamsName[team], maxflow.value());
            StdOut.println("Graph:" + G);
        }
        //        StdOut.println("Max flow from " + s + " to " + t);

        for (int v = 0; v < G.V(); v++) {
            for (FlowEdge e : G.adj(v)) {
                if ((v == e.from()) && e.flow() > 0) {
                    //                    StdOut.println("   " + e);
                    if (e.to() == tnode)
                        finishLine.add(e);
                }
                
            }
        }

        if (DEBUG) {
            for (int i = 0; i <= tnode; i++)
                StdOut.printf(" %d: %b ", i, maxflow.inCut(i));
            StdOut.println("");
        }

        
        for (FlowEdge e : finishLine)
            if (e.flow() == e.capacity())
                elimited[team] = true;

        if (DEBUG) {
            StdOut.println("matchs: " + matchs);
        }
        for (int i = 0; i < teamNumber; i++) {
            if (maxflow.inCut(matchs + i) && !simpleLimited[i]) {
                if (DEBUG) { StdOut.println("add team:  to list "
                                            + teamsName[i]); }
                addTeamElimitedList(team, i);
            }
        }
    }

    private void addTeamElimitedList(int team, int i) {
        if (elimitedList.get(team) == null) {
            Stack<String> b = new Stack<String>();
            b.push(teamsName[i]);
            elimitedList.put(team, b);
        } else {
            Stack<String> b = elimitedList.get(team);
            b.push(teamsName[i]);
        }
    }

    private int getMatchNumbers(int num)
    {
        int n = 0;
        for (int i = 0; i < num; i++)
            for (int j = i; j < num; j++)
                if (i != j)
                    n++;
        return n;
    }
    public              int numberOfTeams() { return teamNumber; }
    public Iterable<String> teams()                                // all teams
    {
        Bag<String> b = new Bag<String>();
        for (int i = 0; i < teamNumber; i++)
            b.add(teamsName[i]);
        return b;
    }
    // number of wins for given team
    public              int wins(String team)
    {
        if (!nameIndex.containsKey(team))
            throw new IllegalArgumentException("");
        
        int idx = nameIndex.get(team);
        return wins[idx];
    }

    // number of losses for given team
    public int losses(String team)
    {
        if (!nameIndex.containsKey(team))
            throw new IllegalArgumentException("");

        int idx = nameIndex.get(team);
        return lose[idx];
    }

    // number of remaining games for given team
    public              int remaining(String team)
    {
        if (!nameIndex.containsKey(team))
            throw new IllegalArgumentException("");

        int idx = nameIndex.get(team);
        return remains[idx];
    }

    // number of remaining games between team1 and team2
    public              int against(String team1, String team2)
    {

        if (!nameIndex.containsKey(team1) || !nameIndex.containsKey(team2))
            throw new IllegalArgumentException("");        
        int idx1, idx2;

        idx1 = nameIndex.get(team1);
        idx2 = nameIndex.get(team2);

        return gameMatrix[idx1][idx2];
    }
    // is given team eliminated?
    public          boolean isEliminated(String team)
    {
        if (!nameIndex.containsKey(team))
            throw new IllegalArgumentException("");

        return elimited[nameIndex.get(team)] || simpleLimited[nameIndex.get(team)];
    }
    
    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team)  
    {
        if (!nameIndex.containsKey(team))
            throw new IllegalArgumentException("");

        int idx = nameIndex.get(team);
        
        if (!elimited[idx])
            return new Bag<String>();
        else {
            return elimitedList.get(idx);
        }
    }
    
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team))
                    StdOut.print(t + " ");
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

}
