package Genetic;

public class FeatureFunctionObsolete {

    /*
     * ===================== Features calculations =====================
     */

    // By default, set the lost state value as -10
    public static int lostStateValue(TestState state) {
        return hasLost(state) ? -10 : 0;
    }

    // The highest column in the board
    public static int maxHeight(TestState s) {
        int[] top = s.top;
        int maxSoFar = -1;
        for (int i : top) {
            maxSoFar = Math.max(maxSoFar, i);
        }

        return maxSoFar;
    }

    // Holes are defined as all empty cells that are below the top of each
    // column.
    public static int numHoles(TestState s) {
        int[][] field = s.field;
        int sumHoles = 0;
        for (int col = 0; col < State.COLS; col++) {
            for (int row = 0; row < s.top[col] - 1; row++) {
                if (field[row][col] == 0) {
                    sumHoles++;
                }
            }
        }
        return sumHoles;
    }

    public static int numRowsCleared(TestState s) {
        return s.rowsCleared;
    }

    // summing up the differences of adjacent column heights
    public static int heightVariationSum(TestState s) {
        int[] top = s.top;
        int varSum = 0;
        for (int i = 0; i < top.length - 1; i++) {
            varSum += Math.abs(top[i] - top[i + 1]);
        }

        return varSum;
    }

    public static boolean hasLost(TestState s) {
        return s.lost;
    }

    // The sum of all pit depths. A pit is defined as the difference in height
    // between a column and its two adjacent columns, with a minimum difference
    // of 3.
    public static double pitDepthValue(TestState s) {
        int[] top = s.top;
        int pitDepthSum = 0;

        int pitColHeight;
        int leftColHeight;
        int rightColHeight;

        // pit depth of first column
        pitColHeight = top[0];
        rightColHeight = top[1];
        int diff = rightColHeight - pitColHeight;
        if (diff > 2) {
            pitDepthSum += diff;
        }

        for (int col = 0; col < State.COLS - 2; col++) {
            leftColHeight = top[col];
            pitColHeight = top[col + 1];
            rightColHeight = top[col + 2];

            int leftDiff = leftColHeight - pitColHeight;
            int rightDiff = rightColHeight - pitColHeight;
            int minDiff = Math.min(leftDiff, rightDiff);
            if (minDiff > 2) {
                pitDepthSum += minDiff;
            }
        }

        // pit depth of last column
        pitColHeight = top[State.COLS - 1];
        leftColHeight = top[State.COLS - 2];
        diff = leftColHeight - pitColHeight;
        if (diff > 2) {
            pitDepthSum += diff;
        }

        return pitDepthSum;

    }

    // The mean height difference is the average of all height differences
    // between each adjacent columns
    public static double meanHeightDiffValue(TestState s) {
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
}
