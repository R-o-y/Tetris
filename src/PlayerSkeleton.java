
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

      -0.789699192573204, 0.15350553539908107, 0.6640382895889534, -0.3437484173083649, 0.3045269936038726, 0.4553456357989858, -0.6965695175250533, -0.14768588620428758, 0.25435351542050566, -0.3929285785943033, -0.45866898319188554, 0.3939799240955857, -0.4436971700436445, -0.4519189718514476, 0.3002001509876142, -0.9726713226576467, -0.0317592736847927, -0.47074781133509447, 0.3580112892416263, -0.6173020320210689, 0.16092244309577342, -0.7193563012907032, 0.7508500125760489, -0.8890296475474264, -0.7162040489483588, 0.8264555551725756
    };

    public static void main(String[] args) {
        State s = new State();
        new TFrame(s);
        TetrisPlayer p = new TetrisPlayer(s.getField()[0].length, allWeights);
        while (!s.hasLost()) {
            s.makeMove(p.pickMove(s, s.legalMoves()));
//            s.draw();
//            s.drawNext(0, 0);
            
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
