
public class FeatureUtil {
	public static double[] getFeatureVector(State s) {
		double[] featureVector = joinArray(
				columnHeightHeuristic(s), 
				colHeightDifferenceHeuristic(s),
				new double[]{maxColHeightHeuristic(s)},
				new double[]{maxHeightDefferenceHeuristic(s)},
				new double[]{numHolesHeuristic(s)},
				new double[]{numTrapsHeuristic(s)},
				new double[]{numHorzTransitionHeuristic(s)},
				new double[]{numVertTransitionHeuristic(s)}
				);
		return featureVector;
	}

    static double[] joinArray(double[]... arrays) {
        int length = 0;
        for (double[] array : arrays) {
            length += array.length;
        }

        final double[] result = new double[length];

        int offset = 0;
        for (double[] array : arrays) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }

        return result;
    }

    static double[] columnHeightHeuristic(State s) {
        int[] colHeights = s.getTop();
        
        double[] featureVector = new double[colHeights.length];
        for (int colIndex = 0; colIndex < colHeights.length; colIndex++)
        	featureVector[colIndex] = colHeights[colIndex];
        return featureVector;
    }

    static double[] colHeightDifferenceHeuristic(State s) {
        int[] colHeights = s.getTop();

        double[] featureVector = new double[colHeights.length - 1];
        for (int colIndex = 0; colIndex < colHeights.length - 1; colIndex++)
        	featureVector[colIndex] = Math.abs(colHeights[colIndex] - colHeights[colIndex + 1]);
        return featureVector;
    }

    static double maxColHeightHeuristic(State s) {
        int[] colHeights = s.getTop();

        int maxColHeight = 0;
        for (int colIndex = 0; colIndex < colHeights.length; colIndex++)
            if (colHeights[colIndex] > maxColHeight)
                maxColHeight = colHeights[colIndex];
        return maxColHeight;
    }

    static double numHolesHeuristic(State s) {
    	int[] colHeights = s.getTop();
    	
    	int count = 0;
        for (int colIndex = 0; colIndex < colHeights.length; colIndex++) {
            // count the number of holes in this column 	
        	for (int rowIndex = 0; rowIndex < colHeights[colIndex]; rowIndex++) {
        		if (s.getField()[rowIndex][colIndex] == 0)
        			count++;		
        	}
        }
        return count;
    }

    static double numTrapsHeuristic(State s) {
        boolean[][] breathable = getBreathable(s);
        int countHoles = 0;
        for (int i = 0; i < breathable.length; i++)
            for (int j = 0; j < breathable[i].length; j++)
                if (breathable[i][j] == false)
                    countHoles++;
        return countHoles;
    }
    
    static double numHorzTransitionHeuristic(State s) {
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
        return count;
    }
    
    static double numVertTransitionHeuristic(State s) {
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
        return count;
    }

    static double maxHeightDefferenceHeuristic(State s) {
        int[] colHeights = s.getTop();
        int max = 0;
        int min = Integer.MAX_VALUE;
        for (int height : colHeights) {
            if (height > max)
                max = height;
            else if (height < min)
                min = height;
        }
        return (max - min);
    }

    // this method is to help calculate numTrapsHeuristic
    static boolean[][] getBreathable(State s) {
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
    static void exploreFrom(boolean[][] breathable, int rowIndex, int colIndex) {
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
}
