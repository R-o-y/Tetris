
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
            0.008438801461880252, -0.6276436142237138, -0.1640272770275105, -0.049686129308627924, 0.4469280143859468, -0.20625413646132926, 0.6142795109067636, -0.25304895483484785, -0.08169342966561954, 0.10131862547720882, 0.17999260933152594, 0.2146358462037513, -1.0, -0.44994146304495325, -0.023662839244034034, -0.12322682174318347, 0.14427604849386355, 0.04096533818497916, -0.012918322899952817, -1.0, -0.0647225419086956, -0.9623171232602709, 0.024732270195320677, -0.7612546489569885, -1.0, 0.6003194162535427
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
