package Genetic;

import LSPI.FeatureFunction;
import LSPI.NextState;

/**
 * This class is an implementation of a goal based Tetris playing agent. It
 * makes use of 2-layer local search to determine the best move to make next
 * given the current state (defined by the falling piece and the blocks already
 * placed on the board). The agent makes use of a heuristic function to
 * determine which states are better than others. This heuristic function makes
 * use of the following features: 1. The number of holes present 2. The number
 * of rows cleared so far 3. The maximum column height 4. The mean height
 * difference of every column 5. The sum of adjacent height variances 6. The sum
 * of pits 7. Is the state a lost state or not In order to determine the weights
 * to be given to each of these features, we ran the AI through a genetic
 * algorithm-based trainer, treating the set of seven weights as one chromosome
 * (with each allele corresponding to one of the seven weights) and the total
 * number of lines cleared until losing as the fitness function for the
 * chromosomes. After many evolutions on a population of 100 random chromosomes,
 * the chromosome with the best results was used as the weights for the
 * features.
 *
 */
public class PlayerSkeleton {

    public static double F1;
    public static double F2;
    public static double F3;
    public static double F4;
    public static double F5;
    public static double F6;
    public static double F7;
    public static double F8;

    public FeatureFunction ff = new FeatureFunction();
    public NextState ns = new NextState();
    
    public int threshold = 12;

    // implement this function to have a working system
    public int pickMove(State s, int[][] legalMoves) {
        double bestValueSoFar = Double.MIN_VALUE;
        NextState bestStateSoFar = null;
        int bestMoveSoFar = 0;
        
        for (int i = 0; i < legalMoves.length; i++) {
            ns.copyState(s);
            ns.makeMove(i);

            if (ns.hasLost()) continue;
            
            //---------------------------------------------------------------
            // there is no need to do 2-layer look-ahead even for good case
            // Therefore, I add one more condition, only when the max height is larger than a certain threshold,
            // do 2-layer look-ahead, this will significantly speed up the player while still keep the performance
            
            double value = 0;
            if (FeatureFunctionObsolete.maxHeight(s) > threshold) {
                value = evaluateState(ns);
            } else {
                value = evaluateOneLevelLower(ns);
            }
            
            
            //----------------------- end ------------------------------------
            
            
            if (value > bestValueSoFar || bestStateSoFar == null) {
                bestStateSoFar = ns;
                bestValueSoFar = value;
                bestMoveSoFar = i;
            }

        }
        return bestMoveSoFar;
    }

    // Evaluate the value of the given state by going one layer deeper.
    // Given the board position, for each of the N_PIECES of tetrominos,
    // consider all
    // possible placements and rotations, and find the highest heuristic value
    // of all these resultant states of the particular tetromino. Find the
    // average max heuristic value across all N_PIECES tetrominos: this will be
    // the evaluation value for the state
    private double evaluateState(NextState state) {
        double sumLowerLevel = 0;
        for (int i = 0; i < N_PIECES; i++) {
            double maxSoFar = Integer.MIN_VALUE;
            
            state.setNextPiece(i);
            
            for (int j = 0; j < legalMoves[i].length; j++) {
                NextState lowerState = new NextState(state);
                lowerState.makeMove(j);
                maxSoFar = Math.max(maxSoFar, evaluateOneLevelLower(lowerState));
            }
            sumLowerLevel += maxSoFar;
        }

        return sumLowerLevel / N_PIECES;
    }

    // Evaluate the state given features to be tested and weights. Apply
    // heuristic function.
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

        State s = new State();
        // The optimal set of weights found after 20 evolutions
        double[] weights = {

                // a sub-optimal weight for testing whether 2layer work with LSPI
                -8389.920140025264, 0, 0, -28604.42300521227, 0, -4911.003209270886, -45526.66822160344, 0

        };
        PlayerSkeleton p = new PlayerSkeleton(weights);
        while (!s.lost) {
            s.makeMove(p.pickMove(s, s.legalMoves()));

            if (s.getRowsCleared() % 1000 == 0) {
                System.out.println(s.getRowsCleared());
            }

        }
        System.out.println("player have completed " + s.getRowsCleared() + " rows.");
    }

    public PlayerSkeleton(double[] weights) {
        F1 = weights[0];
        F2 = weights[1];
        F3 = weights[2];
        F4 = weights[3];
        F5 = weights[4];
        F6 = weights[5];
        F7 = weights[6];
        F8 = weights[7];
    }

    // This method is used to train the agent via a genetic algorithm
    public int run() {

        State s = new State();
        while (!s.lost) {
            s.makeMove(pickMove(s, s.legalMoves()));
        }
        System.out.println("train have completed " + s.getRowsCleared() + " rows.");

        return s.getRowsCleared();
    }

    /*
     * =============== Random info copied from State.java ===============
     */
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
