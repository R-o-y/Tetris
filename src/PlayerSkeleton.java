
public class PlayerSkeleton {

    static double[] allWeights = { 
//        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
//        -1, -1, -1, -1, -1, -1, -1, -1, -1, 
//        -1, 
//        -1,
//        -1,
//        -1,
//        -1,
//        -1,
//        1

            -0.7716696730964099, -0.5530568344402516, -0.12731819399500188, -0.2582750399233058, -0.9109628564309602, 0.5474920129248295, -0.5297500245546283, -0.6400550575898043, -0.05766951725913416
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
            
//            try {
//                Thread.sleep(0);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
        System.out.println("You have completed " + s.getRowsCleared() + " rows.");
    }
}
