package Genetic;
import java.util.Arrays;

import LSPI.NextState;

//Essentially the same as State.java. Reproduced here so that we can carry
// out our local search across all possible resulting states given the
// current state.
public class TestState extends NextState {
    int[][] field;
    int[] top;
    int turn;
    int rowsCleared;
    boolean lost = false;

    public TestState(State s) {
        this.field = cloneField(s.getField());
        this.top = Arrays.copyOf(s.getTop(), s.getTop().length);
        this.turn = s.getTurnNumber();
        this.rowsCleared = s.getRowsCleared();
    }

    public TestState(TestState s) {
        this.field = cloneField(s.field);
        this.top = Arrays.copyOf(s.top, s.top.length);
        this.turn = s.turn;
        this.rowsCleared = s.rowsCleared;
    }

    private int[][] cloneField(int[][] field) {
        int[][] newField = new int[field.length][];
        for (int i = 0; i < newField.length; i++) {
            newField[i] = Arrays.copyOf(field[i], field[i].length);
        }
        return newField;
    }

    // returns false if you lose - true otherwise
    public boolean makeMove(int piece, int orient, int slot) {
        // height if the first column makes contact
        int height = top[slot] - pBottom[piece][orient][0];
        // for each column beyond the first in the piece
        for (int c = 1; c < pWidth[piece][orient]; c++) {
            height = Math.max(height, top[slot + c] - pBottom[piece][orient][c]);
        }

        // check if game ended
        if (height + pHeight[piece][orient] >= ROWS) {
            lost = true;
            return false;
        }

        // for each column in the piece - fill in the appropriate blocks
        for (int i = 0; i < pWidth[piece][orient]; i++) {

            // from bottom to top of brick
            for (int h = height + pBottom[piece][orient][i]; h < height + pTop[piece][orient][i]; h++) {
                field[h][i + slot] = turn;
            }
        }

        // adjust top
        for (int c = 0; c < pWidth[piece][orient]; c++) {
            top[slot + c] = height + pTop[piece][orient][c];
        }

        // check for full rows - starting at the top
        for (int r = height + pHeight[piece][orient] - 1; r >= height; r--) {
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
        return true;
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
    // height of the pieces [piece ID][orientation]
    private static int[][] pHeight = { { 2 }, // square
            { 4, 1 }, // vertical piece
            { 3, 2, 3, 2 }, // L
            { 3, 2, 3, 2 }, //
            { 3, 2, 3, 2 }, // T
            { 2, 3 }, { 2, 3 } };
    private static int[][][] pBottom = { { { 0, 0 } }, { { 0 }, { 0, 0, 0, 0 } },
            { { 0, 0 }, { 0, 1, 1 }, { 2, 0 }, { 0, 0, 0 } }, // L,
            { { 0, 0 }, { 0, 0, 0 }, { 0, 2 }, { 1, 1, 0 } }, { { 0, 1 }, { 1, 0, 1 }, { 1, 0 }, { 0, 0, 0 } },
            { { 0, 0, 1 }, { 1, 0 } }, { { 1, 0, 0 }, { 0, 1 } } };
    private static int[][][] pTop = { { { 2, 2 } }, { { 4 }, { 1, 1, 1, 1 } },
            { { 3, 1 }, { 2, 2, 2 }, { 3, 3 }, { 1, 1, 2 } }, { { 1, 3 }, { 2, 1, 1 }, { 3, 3 }, { 2, 2, 2 } },
            { { 3, 2 }, { 2, 2, 2 }, { 2, 3 }, { 1, 2, 1 } }, { { 1, 2, 2 }, { 3, 2 } }, { { 2, 2, 1 }, { 2, 3 } } };

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

