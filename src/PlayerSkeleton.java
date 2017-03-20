import java.util.Arrays;

public class PlayerSkeleton {
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

    // implement this function to have a working system
    public int pickMove(State s, int[][] legalMoves) {
        int colNum = s.getField()[0].length;
        
        double[] allWeights = {1,1,1,1,1,1,1,1,1,1,
                               1,1,1,1,1,1,1,1,1,
                               1,
                               1};
        
        double[] columnHeightHeuristicWeights = Arrays.copyOfRange(allWeights, 0, colNum);
        double[] colHeightDifferenceHeuristicWeights = Arrays.copyOfRange(allWeights, 
                                                                          colNum, 
                                                                          colNum + colNum - 1);
        double maxColHeightHeuristicWeight = allWeights[colNum + colNum - 1];
        double numHolesHeuristicWeight = allWeights[colNum + colNum];
        
        System.out.println(columnHeightHeuristic(s, columnHeightHeuristicWeights));
        System.out.println(colHeightDifferenceHeuristic(s, colHeightDifferenceHeuristicWeights));
        System.out.println(maxColHeightHeuristic(s, maxColHeightHeuristicWeight));
        System.out.println(numHolesHeuristic(s, numHolesHeuristicWeight));
        System.out.println();

        return 0;
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
                maxColHeight =  colHeights[colIndex];
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
        boolean[][] breathable = new boolean[rowNum][colNum];  // initially, all false
        
        // wall is breathable by definition
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                if (field[i][j] != 0) {
                    breathable[i][j] = true;
                }
            }
        }
        for (int i = 0; i < colNum; i++) {
            if (field[rowNum - 1][i] == 0){
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
}









