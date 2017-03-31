
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

          0.00501545649951135, -0.6560903394943809, -0.2210859168018138, -0.028887613773096732, 0.43416420222827756, -0.169217462125951, 0.6018524951123382, -0.18632521892675502, -0.07204677747566637, 0.09766436983028705, -0.1547522824713426, 0.11801576046744204, -0.9968325608906321, -0.45091677069845015, 0.2662712903743433, -0.22223860989151595, 0.1209790834056952, -0.00829136381994558, 0.02137506014925039, -1.0, -0.0739063615830785, -0.957348477511844, 0.047041855634560696, -0.7867476583891074, -1.0, 0.598323387122444
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
