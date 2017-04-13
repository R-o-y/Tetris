package Genetic;


// Features being used are:
// 1. Height sum
// 2. Number of holes
// 3. Completed lines
// 4. Height variation (between adjacent columns)
// 5. Terminal i.e. lost state
public class PlayerSkeletonOneLayer {

    // public static double HEIGHT_SUM_WEIGHT = 0.51f;
    public static double NUM_HOLES_WEIGHT;
    public static double COMPLETE_LINES_WEIGHT;
    public static double HEIGHT_VAR_WEIGHT;
    public static double LOST_WEIGHT;
    public static double MAX_HEIGHT_WEIGHT;
    public static double PIT_DEPTH_WEIGHT;
    public static double MEAN_HEIGHT_DIFF_WEIGHT;

    // implement this function to have a working system
    public int pickMove(State s, int[][] legalMoves) {
        // Explore legalMoves.length new states
        // legalMoves: an array of n total possible moves
        // each one of n moves contain orientation as index 0 and slot as index
        // 1
        double bestValueSoFar = -1;
        TestState bestStateSoFar = null;
        int bestMoveSoFar = 0;
        for (int i = 0; i < legalMoves.length; i++) {
            TestState state = new TestState(s);
            state.makeMove(s.nextPiece, legalMoves[i][ORIENT], legalMoves[i][SLOT]);
            double value = evaluateOneLevelLower(state);
            if (value > bestValueSoFar || bestStateSoFar == null) {
                bestStateSoFar = state;
                bestValueSoFar = value;
                bestMoveSoFar = i;
            }

        }
        return bestMoveSoFar;
    }

    private double evaluateOneLevelLower(TestState state) {
        double h = -FeatureFunction.numHoles(state) * NUM_HOLES_WEIGHT + FeatureFunction.numRowsCleared(state) * COMPLETE_LINES_WEIGHT
                + -FeatureFunction.heightVariationSum(state) * HEIGHT_VAR_WEIGHT + FeatureFunction.lostStateValue(state) * LOST_WEIGHT
                + -FeatureFunction.maxHeight(state) * MAX_HEIGHT_WEIGHT + -FeatureFunction.pitDepthValue(state) * PIT_DEPTH_WEIGHT
                + -FeatureFunction.meanHeightDiffValue(state) * MEAN_HEIGHT_DIFF_WEIGHT;
        return h;
    }

    // Depth of pits, a pit is a column with adjacent columns higher by at least
    // two blocks and the pit depth
    // is defined as the difference between the height of the pit column and the
    // shortest adjacent column.
    public double pitDepthValue(TestState s) {
        int[] top = s.top;
        int sumOfPitDepths = 0;

        int pitHeight;
        int leftOfPitHeight;
        int rightOfPitHeight;

        // pit depth of first column
        pitHeight = top[0];
        rightOfPitHeight = top[1];
        int diff = rightOfPitHeight - pitHeight;
        if (diff > 2) {
            sumOfPitDepths += diff;
        }

        for (int col = 0; col < State.COLS - 2; col++) {
            leftOfPitHeight = top[col];
            pitHeight = top[col + 1];
            rightOfPitHeight = top[col + 2];

            int leftDiff = leftOfPitHeight - pitHeight;
            int rightDiff = rightOfPitHeight - pitHeight;
            int minDiff = leftDiff < rightDiff ? leftDiff : rightDiff;

            if (minDiff > 2) {
                sumOfPitDepths += minDiff;
            }
        }

        // pit depth of last column
        pitHeight = top[State.COLS - 1];
        leftOfPitHeight = top[State.COLS - 2];
        diff = leftOfPitHeight - pitHeight;
        if (diff > 2) {
            sumOfPitDepths += diff;
        }

        return sumOfPitDepths;

    }

    // Mean height difference, the average of the difference between the height
    // of each column and the mean height of the state.
    public double meanHeightDiffValue(TestState s) {
        int[] top = s.top;

        int sum = 0;
        for (int height : top) {
            sum += height;
        }

        float meanHeight = (float) sum / top.length;

        float avgDiff = 0;
        for (int height : top) {
            avgDiff += Math.abs(meanHeight - height);
        }

        return avgDiff / top.length;
    }

    public static void main(String[] args) {

        for (int k = 0; k < 8; k++) {
            State s = new State();
            double[] weights = {

                    1.0673515084146694,
                    0.5518373660872153,
                    0.13114119880147435,
                    1.6682687332623332,
                    0.013608946139451739,
                    0.3436325190123959,
                    0.3589287624556017

            };
            PlayerSkeletonOneLayer p = new PlayerSkeletonOneLayer(weights);
            while (!s.lost) {
                s.makeMove(p.pickMove(s, s.legalMoves()));
            }

            System.out.println("You have completed " + s.getRowsCleared() + " rows.");
        }
    }

    public PlayerSkeletonOneLayer(double[] weights) {
        NUM_HOLES_WEIGHT = weights[0];
        COMPLETE_LINES_WEIGHT = weights[1];
        HEIGHT_VAR_WEIGHT = weights[2];
        LOST_WEIGHT = weights[3];
        MAX_HEIGHT_WEIGHT = weights[4];
        PIT_DEPTH_WEIGHT = weights[5];
        MEAN_HEIGHT_DIFF_WEIGHT = weights[6];
    }

    public PlayerSkeletonOneLayer() {
    }

    public int run() {
        State s = new State();
        while (!s.lost) {
            s.makeMove(pickMove(s, s.legalMoves()));
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
