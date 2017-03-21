
public class PlayerSkeleton {

    // implement this function to have a working system
    public int pickMove(State s, int[][] legalMoves) {

        return 0;
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
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("You have completed " + s.getRowsCleared() + " rows.");
    }

}

class Evaluator {
    
    // To be finalized
    static final int WEIGHT_GAME_END = -1;
    static final int WEIGHT_ROW_CLEAR = -1;
    static final int WEIGHT_AVG_COLUMN_HEIGHT = -1;             // 
    static final int WEIGHT_ADJACENT_COLUMN_HEIGHT_DIFFERENCE = -1;    // 
    static final int WEIGHT_MAX_COLUMN_HEIGHT = -1;         // 
    static final int WEIGHT_MAX_MIN_COLUMN_DIFFERENCE = -1;
    static final int WEIGHT_JAGGEDNESS = -1;
    static final int WEIGHT_ROW_TRANSITIONS = -1;
    static final int WEIGHT_COL_TRANSITIONS = -1;
    
    static final int WEIGHT_NUMBER_WALL_HOLES = -1;         // 
    static final int WEIGHT_NUMBER_TOP_BLOCKED = -1;
    static final int WEIGHT_NUMBER_WELL_WIDTH_ONE = -1;
    static final int WEIGHT_NUMBER_WELL_WIDTH_TWO = -1;
    static final int WEIGHT_WELL_DEPTH = -1;
    static final int WEIGHT_WELL_ADJACENCY = -1;
    static final int WEIGHT_CUMULATIVE_ADJACENT_SPACES = -1;
    
    // Further considerations
    // 1. Row dependencies
    // 2. Classification of gap (determines what can fill it)
}


/**
 *  FeatureAnalyzer reads a given board state and analyzes the predetermined feature values.
 */
class FeatureAnalyzer {
    
    int evaluateGameEnd(State s) {
        return s.hasLost() ? 1 : 0;
    }
    
    int evaluateRowsCleared(NextState ns) {
        return ns.getRowsCleared() - ns.originalState.getRowsCleared() + 1;
    }
    
    /**
     * Evaluates the following features in one pass for better efficiency:
     * 1. Average column height
     * 2. Adjacent column height difference
     * 3. Max column height
     * 4. Max-min column height difference
     * 
     * @param s
     * @return a double[] array representing the feature values in the order of the features listed
     */
    double[] evaluateColumnFeatures(State s) {
        
        /**
         * 1. Average column height
         * 2. Adjacent column height difference
         * 3. Max column height
         * 4. Max-min column height difference
         */
        double[] features = new double[4];
        int[] top = s.getTop();
        
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        int sum = 0;
        int diffSum = 0;
        
        for (int i=0; i<State.COLS; i++) {
            // 1. Average column height
            sum += top[i];
            
            // 2. Adjacent column height difference
            if (i < State.COLS - 1) diffSum += Math.abs(top[i] - top[i+1]);
            
            // 3. Max column height
            max = Math.max(max, top[i]);
            
            // 4. Max-min column height difference (update min)
            min = Math.min(min, top[i]);
        }
        
        features[0] = sum / (double) State.COLS;
        features[1] = diffSum;
        features[2] = max;
        features[3] = max - min;
        return features;
    }  
}

class NextState extends State {
    
    State originalState;
    
    // To be updated (need to create a copy of existing attributes and then modify to get new state)
    NextState(State s) {
        originalState = s;
    }
    
}