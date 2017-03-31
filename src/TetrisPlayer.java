import java.util.Arrays;

public class TetrisPlayer {
    double[] columnHeightHeuristicWeights;
    double[] colHeightDifferenceHeuristicWeights;
    double maxColHeightHeuristicWeight;
    double maxHeightDifferenceHeuristicWeight;
    double numHolesHeuristicWeight;
    double numTrapsHeuristicWeight;
    double numHorzTransitionHeuristicWeight;
    double numVertTransitionHeuristicWeight;
    
    double rowClearedWeight;
    
    public TetrisPlayer(int colNum, double[] allWeights) {
        columnHeightHeuristicWeights = Arrays.copyOfRange(allWeights, 0, colNum);
        colHeightDifferenceHeuristicWeights = Arrays.copyOfRange(allWeights, colNum, colNum + colNum - 1);
        maxColHeightHeuristicWeight = allWeights[colNum + colNum - 1];
        maxHeightDifferenceHeuristicWeight = allWeights[colNum + colNum];
        numHolesHeuristicWeight = allWeights[colNum + colNum + 1];
        numTrapsHeuristicWeight = allWeights[colNum + colNum + 2];
        numHorzTransitionHeuristicWeight = allWeights[colNum + colNum + 3];
        numVertTransitionHeuristicWeight = allWeights[colNum + colNum + 4];
        rowClearedWeight = allWeights[colNum + colNum + 5];
    }
    
    // implement this function to have a working system
    public int pickMove(State s, int[][] legalMoves) {
        int bestUtilityMoveIndex = 0;
        double tempBestUtility = Double.NEGATIVE_INFINITY;
        
        for (int i = 0; i < legalMoves.length; i++) {
            double utility = calculateUtility(s, legalMoves, i);
            if (utility > tempBestUtility) {
                tempBestUtility = utility;
                bestUtilityMoveIndex = i;
            }
        }
        return bestUtilityMoveIndex;
    }
    
    /// calculate the utility of the input move, which equals to:
    /// sum of all the Heuristic of the next state by this move + 
    /// the number of rows cleared by this move
    private double calculateUtility(State s, int[][] legalMoves, int moveIndex) {
        State simulatedState = s.clone();
        int previousClearedCount = simulatedState.getRowsCleared();
        simulatedState.makeMove(moveIndex);
        int newClearedCount = simulatedState.getRowsCleared();
        
        double accHeuristic = columnHeightHeuristic(simulatedState, columnHeightHeuristicWeights)
                + colHeightDifferenceHeuristic(simulatedState, colHeightDifferenceHeuristicWeights)
                + maxColHeightHeuristic(simulatedState, maxColHeightHeuristicWeight) 
                + maxHeightDefferenceHeuristic(simulatedState, maxHeightDifferenceHeuristicWeight)
                + numHolesHeuristic(simulatedState, numHolesHeuristicWeight)
                + numTrapsHeuristic(s, numTrapsHeuristicWeight) 
                + numHorzTransitionHeuristic(simulatedState, numHorzTransitionHeuristicWeight)
                + numVertTransitionHeuristic(simulatedState, numVertTransitionHeuristicWeight);
        return accHeuristic + (newClearedCount - previousClearedCount) * rowClearedWeight;
    }

    /// Heuristic: sum of column height for each column
    private double columnHeightHeuristic(State s, double[] weights) {
        int[] colHeights = s.getTop();

        int accumulatedUtility = 0;
        for (int colIndex = 0; colIndex < weights.length; colIndex++)
            accumulatedUtility += colHeights[colIndex] * weights[colIndex];
        return accumulatedUtility;
    }

    /// Heuristic: sum of the absolute value of the difference of the heights of adjacent column
    private double colHeightDifferenceHeuristic(State s, double[] weights) {
        int[] colHeights = s.getTop();

        int accumulatedUtility = 0;
        for (int colIndex = 0; colIndex < weights.length; colIndex++)
            accumulatedUtility += Math.abs(colHeights[colIndex] - colHeights[colIndex + 1]) * weights[colIndex];
        return accumulatedUtility;
    }

    /// Heuristic: the maximum column height 
    private double maxColHeightHeuristic(State s, double weight) {
        int[] colHeights = s.getTop();

        int maxColHeight = 0;
        for (int colIndex = 0; colIndex < colHeights.length; colIndex++)
            if (colHeights[colIndex] > maxColHeight)
                maxColHeight = colHeights[colIndex];
        return maxColHeight * weight;
    }

    /// Heuristic: the total number of holes
    /// definition of "hole": a blank position where there is some full position above in the same column
    private double numHolesHeuristic(State s, double weight) {
    	int[] colHeights = s.getTop();
    	
    	int count = 0;
        for (int colIndex = 0; colIndex < colHeights.length; colIndex++) {
            // count the number of holes in this column 	
        	for (int rowIndex = 0; rowIndex < colHeights[colIndex]; rowIndex++) {
        		if (s.getField()[rowIndex][colIndex] == 0)
        			count++;		
        	}
        }
        return count * weight;
    }

    /// Heuristic: the total number of traps
    /// definition of "trap": a blank position, from where there is no path to the top
    private double numTrapsHeuristic(State s, double weight) {
        boolean[][] breathable = getBreathable(s);
        int countHoles = 0;
        for (int i = 0; i < breathable.length; i++)
            for (int j = 0; j < breathable[i].length; j++)
                if (breathable[i][j] == false)
                    countHoles++;
        return weight * countHoles;
    }
    
    /// Heuristic: the number of horizontal full/blank transitions
    /// wall is considered as full
    private double numHorzTransitionHeuristic(State s, double weight) {
        int count = 0;
        int[][] field = s.getField();
        for (int row = 0; row < field.length - 1; row++)
            for (int col = -1; col < field[0].length; col++) {
                if (col == -1) {  // left wall
                    if (field[row][col + 1] == 0)
                        count++;
                }
                else if (col == field[0].length - 1) {  // right wall
                    if (field[row][col] == 0)
                        count++;
                }
                else if (field[row][col] * field[row][col + 1] == 0 && 
                         field[row][col] + field[row][col + 1] != 0)
                    count++;
            }
        return count * weight;
    }
    
    /// Heuristic: the number of vertical full/blank transitions
    /// wall is considered as full
    private double numVertTransitionHeuristic(State s, double weight) {
        int count = 0;
        int[][] field = s.getField();
        for (int col = 0; col < field[0].length; col++)
            for (int row = -1; row < field.length - 1; row++) {
                if (row == -1) {  // left wall
                    if (field[row + 1][col] == 0)
                        count++;
                }
                else if (field[row][col] * field[row + 1][col] == 0 && 
                         field[row][col] + field[row + 1][col] != 0)
                    count++;
            }
        return count * weight;
    }
    
    /// Heuristic: the difference between the maximum and the minimum height
    private double maxHeightDefferenceHeuristic(State s, double weight) {
        int[] colHeights = s.getTop();
        int max = 0;
        int min = Integer.MAX_VALUE;
        for (int height : colHeights) {
            if (height > max)
                max = height;
            else if (height < min)
                min = height;
        }
        return (max - min) * weight;
    }

    // this method is to help calculate numTrapsHeuristic
    private boolean[][] getBreathable(State s) {
        int[][] field = s.getField();

        int rowNum = field.length - 1;
        int colNum = field[0].length;
        boolean[][] breathable = new boolean[rowNum][colNum]; // initially, all false

        // wall is breathable by definition
        for (int i = 0; i < rowNum; i++)
            for (int j = 0; j < colNum; j++)
                if (field[i][j] != 0)
                    breathable[i][j] = true;
        for (int i = 0; i < colNum; i++)
            if (field[rowNum - 1][i] == 0)
                exploreFrom(breathable, rowNum - 1, i);
        return breathable;
    }

    // this method is to help calculate numTrapsHeuristic
    private void exploreFrom(boolean[][] breathable, int rowIndex, int colIndex) {
        int rowNum = breathable.length - 1;
        int colNum = breathable[0].length;
        if (rowIndex - 1 >= 0 && !breathable[rowIndex - 1][colIndex]) {
            breathable[rowIndex - 1][colIndex] = true;
            exploreFrom(breathable, rowIndex - 1, colIndex);
        }
        if (rowIndex + 1 <= rowNum - 1 && !breathable[rowIndex + 1][colIndex]) {
            breathable[rowIndex + 1][colIndex] = true;
            exploreFrom(breathable, rowIndex + 1, colIndex);
        }
        if (colIndex - 1 >= 0 && !breathable[rowIndex][colIndex - 1]) {
            breathable[rowIndex][colIndex - 1] = true;
            exploreFrom(breathable, rowIndex, colIndex - 1);
        }
        if (colIndex + 1 <= colNum - 1 && !breathable[rowIndex][colIndex + 1]) {
            breathable[rowIndex][colIndex + 1] = true;
            exploreFrom(breathable, rowIndex, colIndex + 1);
        }
    }

    // this method is for testing purpose, to check whether all Heuristics are calculated correctly
    public void printHeuristics(State s) {
        System.out.println(columnHeightHeuristic(s, new double[]{1,1,1,1,1,1,1,1,1,1}));
        System.out.println(colHeightDifferenceHeuristic(s, new double[]{1,1,1,1,1,1,1,1,1}));
        
        System.out.println(maxColHeightHeuristic(s, 1));
        System.out.println(maxHeightDefferenceHeuristic(s, 1));
        
        System.out.println(numHolesHeuristic(s, 1));
        System.out.println(numTrapsHeuristic(s, 1));
        
        System.out.println(numHorzTransitionHeuristic(s, 1));
        System.out.println(numVertTransitionHeuristic(s, 1));
        System.out.println();
    }
}
