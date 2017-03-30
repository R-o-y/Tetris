
public class PlayerSkeleton {

    static double[] allWeights = { 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, 
        -1,
        1
    };

    public static void main(String[] args) {
        State s = new State();
        new TFrame(s);
        TetrisPlayer p = new TetrisPlayer(s.getField()[0].length, allWeights);
        while (!s.hasLost()) {
            s.makeMove(p.pickMove(s, s.legalMoves()));
            s.draw();
            s.drawNext(0, 0);
//            double[] v = FeatureUtil.getFeatureVector(s);
//            for (double i : v) {
//            	System.out.print(i + " ");
//            }
//            System.out.println();
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("You have completed " + s.getRowsCleared() + " rows.");
    }
}
