package Genetic;

import LSPI.NextState;

import LSPI.FeatureFunction;

// Features being used are:
// 1. Height sum
// 2. Number of holes
// 3. Completed lines
// 4. Height variation (between adjacent columns)
// 5. Terminal i.e. lost state
public class PlayerSkeletonOneLayer {

    // public static double HEIGHT_SUM_WEIGHT = 0.51f;
    public static double F1;
    public static double F2;
    public static double F3;
    public static double F4;
    public static double F5;
    public static double F6;
    public static double F7;
    public static double F8;
    
    public FeatureFunction ff = new FeatureFunction();
    public NextState state = new NextState();

    // implement this function to have a working system
    public int pickMove(State s, int[][] legalMoves) {
        // Explore legalMoves.length new states
        // legalMoves: an array of n total possible moves
        // each one of n moves contain orientation as index 0 and slot as index
        // 1
        double bestValueSoFar = -1;
        NextState bestStateSoFar = null;
        int bestMoveSoFar = 0;
        for (int i = 0; i < legalMoves.length; i++) {
            state.copyState(s);
            state.makeMove(i);
            double value = evaluateOneLevelLower(state);
            if (state.hasLost()) 
                continue;
            if (value > bestValueSoFar || bestStateSoFar == null) {
                bestStateSoFar = state;
                bestValueSoFar = value;
                bestMoveSoFar = i;
            }
        }
        return bestMoveSoFar;
    }

    private double evaluateOneLevelLower(NextState state) {
        double[] v = ff.computeFeatureVector(state);
        double h = 
                v[0] * F1 + 
                v[1] * F2 +  
                v[2] * F3 +
                v[3] * F4 + 
                v[4] * F5 + 
                v[5] * F6 +
                v[6] * F7 + 
                v[7] * F8;
        return h;
    }

    public static void main(String[] args) {

        for (int k = 0; k < 8; k++) {
            State s = new State();
            double[] weights = {

                    -18632.774652174616, 6448.762504425676, -29076.013395444257,
                    -36689.271441668505, -16894.091937650956, -8720.173920864327, 
                    -49926.16836221889, -47198.39106032252

            };
            PlayerSkeletonOneLayer p = new PlayerSkeletonOneLayer(weights);
            while (!s.lost) {
                s.makeMove(p.pickMove(s, s.legalMoves()));
                if (s.getRowsCleared() % 10000 == 0) {
                    System.out.println(s.getRowsCleared());
                }
            }

            System.out.println("You have completed " + s.getRowsCleared() + " rows.");
        }
    }

    public PlayerSkeletonOneLayer(double[] weights) {
        F1 = weights[0];
        F2 = weights[1];
        F3 = weights[2];
        F4 = weights[3];
        F5 = weights[4];
        F6 = weights[5];
        F7 = weights[6];
        F8 = weights[7];
    }

    public PlayerSkeletonOneLayer() {
    }

    public int run() {
        State s = new State();
        while (!s.lost) {
            s.makeMove(pickMove(s, s.legalMoves()));
//            s.draw();
//            try {
//            Thread.sleep(0);}catch(Exception e){}
        }
        int rowCleared = s.getRowsCleared();
        if (rowCleared > 1000000) {
            System.out.println("------------------------million-------------------------");
        }
        System.out.println("OneLayerPlayer has completed " + rowCleared + " rows.");
        return s.getRowsCleared();
    }

    public static final int COLS = State.COLS;
    public static final int ROWS = State.ROWS;
    public static final int N_PIECES = State.N_PIECES;
    // all legal moves - first index is piece type - then a list of 2-length
    // arrays
    protected static int[][][] legalMoves = new int[N_PIECES][][];

    // indices for legalMoves
    public static final int ORIENT = 0;
    public static final int SLOT = 1;

    // possible orientations for a given piece type
    protected static int[] pOrients = { 1, 2, 4, 4, 4, 2, 2 };

    // the next several arrays define the piece vocabulary in detail
    // width of the pieces [piece ID][orientation]
    protected static int[][] pWidth = { { 2 }, { 1, 4 }, { 2, 3, 2, 3 }, { 2, 3, 2, 3 }, { 2, 3, 2, 3 }, { 3, 2 },
            { 3, 2 } };

    // initialize legalMoves
    // legalMoves[piece type][num legal moves][tuple of orient and slot]
    {
        // for each piece type
        for (int i = 0; i < N_PIECES; i++) {
            // figure number of legal moves
            int n = 0;
            for (int j = 0; j < pOrients[i]; j++) {
                // number of locations in this orientation
                n += COLS + 1 - pWidth[i][j];
            }
            // allocate space
            legalMoves[i] = new int[n][2];
            // for each orientation
            n = 0;
            for (int j = 0; j < pOrients[i]; j++) {
                // for each slot
                for (int k = 0; k < COLS + 1 - pWidth[i][j]; k++) {
                    legalMoves[i][n][ORIENT] = j;
                    legalMoves[i][n][SLOT] = k;
                    n++;
                }
            }
        }
    }

}
