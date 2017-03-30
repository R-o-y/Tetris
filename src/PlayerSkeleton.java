import java.util.Arrays;

public class PlayerSkeleton {
    
    NextState ns;
    
    public FeatureAnalyzer fa;
    private double[] weights;

    
    public PlayerSkeleton() {
        ns = new NextState();
        fa = new FeatureAnalyzer();
        weights = new double[] { 
                -99999, // WEIGHT_GAME_END
                100000, // WEIGHT_ROW_CLEAR
                -50000, // WEIGHT_AVG_COLUMN_HEIGHT
                -40000, // WEIGHT_ADJACENT_COLUMN_HEIGHT_DIFFERENCE
                -20000, // WEIGHT_MAX_COLUMN_HEIGHT
                -25000, // WEIGHT_MAX_MIN_COLUMN_DIFFERENCE
                -5000, // WEIGHT_NUMBER_WALL_HOLES
                -5000 // WEIGHT_NUMBER_TOP_BLOCKED
        };
    }
    
    public PlayerSkeleton(double weights[]) {
        ns = new NextState();
        fa = new FeatureAnalyzer();
        this.weights = weights;
    }

    // implement this function to have a working system
    public int pickMove(State s, int[][] legalMoves) {
        
        int bestMove = 0;
        double bestValue = Double.MIN_VALUE;
        
        for (int currentMove = 0; currentMove <legalMoves.length; currentMove++) {
            ns.copyState(s);
            ns.makeMove(currentMove);
            
            double[] featuresArr = fa.computeFeaturesArray(ns);
            double currentValue = fa.valueOfState(featuresArr, weights);
            
            if (currentValue > bestValue) {
                bestMove = currentMove;
                bestValue = currentValue;
            }
        }
        return bestMove;
    }

    public static void main(String[] args) {
        State s = new State();
        new TFrame(s);
        PlayerSkeleton p = new PlayerSkeleton();
        while (!s.hasLost()) {
            s.makeMove(p.pickMove(s, s.legalMoves()));
            s.draw();
            s.drawNext(0, 0);
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("You have completed " + s.getRowsCleared() + " rows.");
    }

}


/**
 *  FeatureAnalyzer reads a given board state and analyzes the predetermined feature values.
 */
class FeatureAnalyzer {
    
    double valueOfState(double[] featuresOfStateArr,  double[] weights) {
        double value = 0;
        for (int i=0; i<featuresOfStateArr.length; i++) {
            value += featuresOfStateArr[i] * weights[i];
        }
        return value;
    }
    
    double[] computeFeaturesArray(NextState s) {
        double[] featuresArr = new double[8];
        featuresArr[0] = evaluateGameEnd(s);
        featuresArr[1] = evaluateRowsCleared(s);
        featuresArr[2] = evaluateAvgColumnHeight(s);
        featuresArr[3] = evaluateAdjColumnHeightDiff(s);
        featuresArr[4] = evaluateMaxColHeight(s);
        featuresArr[5] = evaluateMaxMinHeightDiff(s);
        featuresArr[6] = evaluateHoles(s);
        featuresArr[7] = evaluateTopBlocked(s);
        
        return featuresArr;
    }
    
    int evaluateGameEnd(State s) {
        return s.hasLost() ? 1 : 0;
    }
    
    int evaluateRowsCleared(NextState ns) {
        return ns.getRowsCleared() - ns.originalState.getRowsCleared() + 1;
    }
    
    double evaluateAvgColumnHeight(State s) {
        int sum = 0;
        int[] top = s.getTop();
        for (int i=0; i<State.COLS; i++) {
            sum += top[i];
        }
        return sum / (double) State.COLS;
    }
        
    int evaluateAdjColumnHeightDiff(State s) {
        int[] top = s.getTop();
        int diffSum = 0;
        for (int i=0; i<State.COLS-1; i++) {
            diffSum += Math.abs(top[i] - top[i+1]);
        }
        return diffSum;
    }
    
    int evaluateMaxColHeight(State s) {
        int max = Integer.MIN_VALUE;
        int[] top = s.getTop();

        for (int i=0;i<State.COLS;i++) {
            max = Math.max(max, top[i]);
        }
        return max;
    }
    
    int evaluateMaxMinHeightDiff(State s) {
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        int[] top = s.getTop();

        for (int i=0; i<State.COLS; i++) {
            max = Math.max(max, top[i]);
            min = Math.min(min, top[i]);

        }
        
        return Math.abs(max - min);
    }
    
    int evaluateHoles(State s) {
        int numHoles = 0;
        int[][] field = s.getField();
        for (int i=0; i<field.length; i++) {
            for (int j=0; j<field[i].length; j++) {
                if (isHole(i, j, s)) numHoles++;
            }
        }
        return numHoles;
    }
    
    private boolean isHole(int row, int col, State s) {
        int[][] field = s.getField();
        
        if (field[row][col] != 0) {
            return false;
        }
        
        int numNeighbours = 0;
        for (int r = row-1; r < row+1; r++) {
            for (int c=col-1; c<col+1; c++) {
                if (r == row && c == col) continue;
                if (isValidRow(r) && isValidCol(c) && field[r][c] != 0) numNeighbours++;
            }
        }
        
        return numNeighbours == 8;
    }
    
    private boolean isValidRow(int r) {
        return r >= 0 && r < 21;
    }
    
    private boolean isValidCol(int c) {
        return c >= 0 && c < 10;
    }
    
    int evaluateTopBlocked(State s) {
        int numTopBlocked = 0;
        int[][] field = s.getField();
        
        for (int i=0; i<field.length; i++) {
            for (int j=0; j<field[i].length; j++) {
                if (isValidRow(i-1) && field[i][j] == 0 && field[i-1][j] != 0) numTopBlocked++;
            }
        }
        return numTopBlocked;
    }

}

class NextState extends State {
    
    State originalState;

    // current turn
    private int turn = 0;
    private int cleared = 0;
    
    //each square in the grid - int means empty - other values mean the turn it was placed
    private int[][] field = new int[ROWS][COLS];
    private int[] top = new int[COLS];
    
    // the next several arrays define the piece vocabulary in detail
    // width of the pieces [piece ID][orientation]
    protected static int[][] pWidth = { { 2 }, { 1, 4 }, { 2, 3, 2, 3 }, { 2, 3, 2, 3 }, { 2, 3, 2, 3 },
            { 3, 2 }, { 3, 2 } };
    // height of the pieces [piece ID][orientation]
    private static int[][] pHeight = { { 2 }, { 4, 1 }, { 3, 2, 3, 2 }, { 3, 2, 3, 2 }, { 3, 2, 3, 2 },
            { 2, 3 }, { 2, 3 } };
    private static int[][][] pBottom = { { { 0, 0 } }, { { 0 }, { 0, 0, 0, 0 } },
            { { 0, 0 }, { 0, 1, 1 }, { 2, 0 }, { 0, 0, 0 } },
            { { 0, 0 }, { 0, 0, 0 }, { 0, 2 }, { 1, 1, 0 } },
            { { 0, 1 }, { 1, 0, 1 }, { 1, 0 }, { 0, 0, 0 } }, { { 0, 0, 1 }, { 1, 0 } },
            { { 1, 0, 0 }, { 0, 1 } } };
    private static int[][][] pTop = { { { 2, 2 } }, { { 4 }, { 1, 1, 1, 1 } },
            { { 3, 1 }, { 2, 2, 2 }, { 3, 3 }, { 1, 1, 2 } },
            { { 1, 3 }, { 2, 1, 1 }, { 3, 3 }, { 2, 2, 2 } },
            { { 3, 2 }, { 2, 2, 2 }, { 2, 3 }, { 1, 2, 1 } }, { { 1, 2, 2 }, { 3, 2 } },
            { { 2, 2, 1 }, { 2, 3 } } };

    
    NextState() { }
    
    // To be updated (need to create a copy of existing attributes and then modify to get new state)
    NextState(State s) {
        copyState(s);
    }
    
    // random integer, returns 0-6
    private int randomPiece() {
        return (int) (Math.random() * N_PIECES);
    }
    
    public void copyState(State s) {
        originalState = s;
        this.nextPiece = s.getNextPiece();
        this.lost = s.lost;
        for (int i = 0; i < originalState.getField().length; i++) {
            field[i] = Arrays.copyOf(originalState.getField()[i], originalState.getField()[i].length);
        }

        top = Arrays.copyOf(originalState.getTop(), originalState.getTop().length);
        turn = originalState.getTurnNumber();
        cleared = originalState.getRowsCleared();
    }
    
 // make a move based on the move index - its order in the legalMoves list
    public void makeMove(int move) {
        makeMove(legalMoves[nextPiece][move]);
    }

    // make a move based on an array of orient and slot
    public void makeMove(int[] move) {
        makeMove(move[ORIENT], move[SLOT]);
    }

    // returns false if you lose - true otherwise
    public boolean makeMove(int orient, int slot) {
        turn++;
        // height if the first column makes contact
        int height = top[slot] - pBottom[nextPiece][orient][0];
        // for each column beyond the first in the piece
        for (int c = 1; c < pWidth[nextPiece][orient]; c++) {
            height = Math.max(height, top[slot + c] - pBottom[nextPiece][orient][c]);
        }

        // check if game ended
        if (height + pHeight[nextPiece][orient] >= ROWS) {
            lost = true;
            return false;
        }

        // for each column in the piece - fill in the appropriate blocks
        for (int i = 0; i < pWidth[nextPiece][orient]; i++) {

            // from bottom to top of brick
            for (int h = height + pBottom[nextPiece][orient][i]; h < height
                    + pTop[nextPiece][orient][i]; h++) {
                field[h][i + slot] = turn;
            }
        }

        // adjust top
        for (int c = 0; c < pWidth[nextPiece][orient]; c++) {
            top[slot + c] = height + pTop[nextPiece][orient][c];
        }

        int rowsCleared = 0;

        // check for full rows - starting at the top
        for (int r = height + pHeight[nextPiece][orient] - 1; r >= height; r--) {
            // check all columns in the row
            boolean full = true;
            for (int c = 0; c < COLS; c++) {
                if (field[r][c] == 0) {
                    full = false;
                    break;
                }
            }
            // if the row was full - remove it and slide above stuff down
            if (full) {
                rowsCleared++;
                cleared++;
                // for each column
                for (int c = 0; c < COLS; c++) {

                    // slide down all bricks
                    for (int i = r; i < top[c]; i++) {
                        field[i][c] = field[i + 1][c];
                    }
                    // lower the top
                    top[c]--;
                    while (top[c] >= 1 && field[top[c] - 1][c] == 0)
                        top[c]--;
                }
            }
        }

        // pick a new piece
        nextPiece = randomPiece();

        return true;
    }
    
}

