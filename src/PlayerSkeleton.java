
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

        return 0;
    }
    
    private double columnHeightHeuristic(int[][] field, double[] weights) {
        assert field.length > 0;
        assert field[0].length > 0;
        assert field[0].length == weights.length;
        
        int accumulatedUtility = 0;
        for (int colIndex = 0; colIndex < weights.length; colIndex++) {
            accumulatedUtility += countColHeight(field, colIndex) * weights[colIndex];
        }
        return accumulatedUtility;
    }
    
    private double colHeightDifferenceHeuristic(int[][] field, double[] weights) {
        assert field.length > 0;
        assert field[0].length > 0;
        assert field[0].length - 1 == weights.length;
        
        int accumulatedUtility = 0;
        for (int colIndex = 0; colIndex < weights.length - 1; colIndex++) {
            accumulatedUtility += Math.abs(countColHeight(field, colIndex) - countColHeight(field, colIndex + 1)) * weights[colIndex];
        }
        return accumulatedUtility;
    }
    
    private double maxColHeightHeuristic(int[][] field, double weight) {
        assert field.length > 0;
        assert field[0].length > 0;
        
        int maxColHeight = 0;
        for (int colIndex = 0; colIndex < field[0].length; colIndex++) {
            if (countColHeight(field, colIndex) > maxColHeight) {
                maxColHeight =  countColHeight(field, colIndex);
            }
        }
        return maxColHeight * weight;
    }
    
    private double numHolesHeuristic(int[][] field, double weight) {
        assert field.length > 0;
        assert field[0].length > 0;
        
        int countHoles = 0;
        for (int rowIndex = 0; rowIndex < field.length; rowIndex++) {
            for (int colIndex = 0; colIndex < field[0].length; colIndex++) {
                if (checkIsHole(field, rowIndex, colIndex)) {
                    countHoles++;
                }
            }
        }
        return weight * countHoles;
    }
    
    // helper method to count the height of a column
    private int countColHeight(int[][] field, int colIndex) {
        int count = 0;
        for (int rowIndex = 0; rowIndex < field.length; rowIndex++) {
            count += field[colIndex][rowIndex];
        }
        return count;
    }
    
    // helper method to check whether the position specified by the row and col index is a hole
    private boolean checkIsHole(int[][] field, int rowIndex, int colIndex) {
        assert field.length > 0;
        assert field[0].length > 0;
        
        int rowNum = field.length;
        int colNum = field[0].length;
        
        if (colIndex - 1 >= 0 && field[rowIndex][colIndex - 1] == 0) {
            return false;
        } else if (colIndex + 1 <= colNum - 1 && field[rowIndex][colIndex + 1] == 0) {
            return false;
        } else if (rowIndex - 1 >= 0 && field[rowIndex - 1][colIndex] == 0) {
            return false;
        } else if (rowIndex + 1 <= rowNum - 1 && field[rowIndex + 1][colIndex] == 0) {
            return false;
        } else {
            return true;
        }
    }
}









