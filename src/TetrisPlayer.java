import java.util.Arrays;

public class TetrisPlayer {
    double[] allWeights;
    double[] columnHeightHeuristicWeights;
    double[] colHeightDifferenceHeuristicWeights;
    double maxColHeightHeuristicWeight;
    double numHolesHeuristicWeight;
    double rowClearedWeight;
    
    public TetrisPlayer(int colNum, double[] allWeights) {
        this.allWeights = allWeights;
        columnHeightHeuristicWeights = Arrays.copyOfRange(allWeights, 0, colNum);
        colHeightDifferenceHeuristicWeights = Arrays.copyOfRange(allWeights, colNum, colNum + colNum - 1);
        maxColHeightHeuristicWeight = allWeights[colNum + colNum - 1];
        numHolesHeuristicWeight = allWeights[colNum + colNum];
        rowClearedWeight = allWeights[colNum + colNum + 1];
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
    
    private double calculateUtility(State s, int[][] legalMoves, int moveIndex) {
        State simulatedState = s.clone();
        int previousClearedCount = simulatedState.getRowsCleared();
        simulatedState.makeMove(moveIndex);
        int newClearedCount = simulatedState.getRowsCleared();
        
        double accHeuristic = columnHeightHeuristic(simulatedState, columnHeightHeuristicWeights)
                + colHeightDifferenceHeuristic(simulatedState, colHeightDifferenceHeuristicWeights)
                + maxColHeightHeuristic(simulatedState, maxColHeightHeuristicWeight) + numHolesHeuristic(s, numHolesHeuristicWeight);

        return accHeuristic + (newClearedCount - previousClearedCount) * rowClearedWeight;
    }

    private double columnHeightHeuristic(State s, double[] weights) {
        int[] colHeights = s.getTop();

        int accumulatedUtility = 0;
        for (int colIndex = 0; colIndex < weights.length; colIndex++) {
            accumulatedUtility += colHeights[colIndex] * weights[colIndex];
        }
        return accumulatedUtility;
    }

    private double colHeightDifferenceHeuristic(State s, double[] weights) {
        int[] colHeights = s.getTop();

        int accumulatedUtility = 0;
        for (int colIndex = 0; colIndex < weights.length - 1; colIndex++) {
            accumulatedUtility += Math.abs(colHeights[colIndex] - colHeights[colIndex + 1]) * weights[colIndex];
        }
        return accumulatedUtility;
    }

    private double maxColHeightHeuristic(State s, double weight) {
        int[] colHeights = s.getTop();

        int maxColHeight = 0;
        for (int colIndex = 0; colIndex < colHeights.length; colIndex++) {
            if (colHeights[colIndex] > maxColHeight) {
                maxColHeight = colHeights[colIndex];
            }
        }
        return maxColHeight * weight;
    }

    private double numHolesHeuristic(State s, double weight) {
        boolean[][] breathable = getBreathable(s);
        int countHoles = 0;
        for (int i = 0; i < breathable.length; i++) {
            for (int j = 0; j < breathable[i].length; j++) {
                if (breathable[i][j] == false) {
                    countHoles++;
                }
            }
        }
        return weight * countHoles;
    }

    private boolean[][] getBreathable(State s) {
        int[][] field = s.getField();

        int rowNum = field.length;
        int colNum = field[0].length;
        boolean[][] breathable = new boolean[rowNum][colNum]; // initially, all
                                                              // false

        // wall is breathable by definition
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                if (field[i][j] != 0) {
                    breathable[i][j] = true;
                }
            }
        }
        for (int i = 0; i < colNum; i++) {
            if (field[rowNum - 1][i] == 0) {
                exploreFrom(breathable, rowNum - 1, i);
            }
        }
        return breathable;
    }

    private void exploreFrom(boolean[][] breathable, int rowIndex, int colIndex) {
        int rowNum = breathable.length;
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

    // this method is for testing purpose, to check whether all heuristics are calculated correctly
    public void printHeuristics(State s) {
        System.out.println(columnHeightHeuristic(s, columnHeightHeuristicWeights));
        System.out.println(colHeightDifferenceHeuristic(s, colHeightDifferenceHeuristicWeights));
        System.out.println(maxColHeightHeuristic(s, maxColHeightHeuristicWeight));
        System.out.println(numHolesHeuristic(s, numHolesHeuristicWeight));
        System.out.println();
    }
}
