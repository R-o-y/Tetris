package LSPI;

import java.util.Scanner;
import Genetic.State;

public class PlayerSkeleton implements Runnable {
    private FeatureFunction featureFunction;
    private static double[] weights = new double[] { -18632.774652174616, 6448.762504425676, -29076.013395444257,
            -36689.271441668505, -16894.091937650956, -8720.173920864327, -49926.16836221889, -47198.39106032252

            // 8 feature functions
    };
    private static int NUMBEROFLEARNER = 4;

    private NextState nextstate;

    public PlayerSkeleton() {
        featureFunction = new FeatureFunction();
        nextstate = new NextState();
    }

    public int pickMove(State s, int[][] legalMoves) {

        int bestMove = 0, currentMove;
        double bestValue = Double.NEGATIVE_INFINITY, currentValue = 0.0;

        for (currentMove = 0; currentMove < legalMoves.length; currentMove++) {
            nextstate.copyState(s);
            nextstate.makeMove(currentMove);
            currentValue = featureFunction.computeValueOfState(nextstate, weights);

            if (nextstate.hasLost())
                continue;

            if (currentValue > bestValue) {
                bestMove = currentMove;
                bestValue = currentValue;
            }
        }
        return bestMove;
    }

    public static void main(String[] args) {

        System.out.println("Choose: 1. Play; 2. Learn");
        Scanner sc = new Scanner(System.in);

        if (Integer.valueOf(sc.nextLine()) == 1) {
            Thread[] threads = new Thread[NUMBEROFLEARNER];
            for (int i = 0; i < NUMBEROFLEARNER; i++) {
                threads[i] = new Thread(new PlayerSkeleton());
                threads[i].start();
            }
            try {
                for (Thread t : threads) {
                    t.join();
                }
            } catch (InterruptedException e) {
            }
        }

        else {
            System.out.println("Key in number of threads and file name of the orginal weights");
            System.out.println("eg.4 weights.txt");
            String input = sc.nextLine();
            Learner learner = new Learner(input.split(" "));
            learner.learn();
        }
    }

    public void run() {
        State s = new State();
        PlayerSkeleton p = new PlayerSkeleton();
        while (!s.hasLost()) {
            s.makeMove(p.pickMove(s, s.legalMoves()));
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("You have completed " + s.getRowsCleared() + " rows.");
    }

}
