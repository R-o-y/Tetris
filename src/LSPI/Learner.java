package LSPI;

import java.io.*;
import java.util.*;

public class Learner {

    private double[] weights;
    public static final int K = 8;
    private static final String NEWFILENAME = "newWeight.txt";

    public Learner(String[] args) {
        readWeightFile(args[1]);
    }

    public void learn() {
        LSPI lspi = new LSPI(weights);
        weights = lspi.learn();
        writeWeightFile();
    }

    private void readWeightFile(String fileName) {
        weights = new double[K];
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            Scanner sc = new Scanner(br);
            int i = 0;
            while (sc.hasNext()) {
                weights[i] = Double.parseDouble(sc.nextLine());
                i++;
            }
        } catch (IOException e) {
            System.out.println("Invalid Filename." + "1. Re-enter file name 2. generate random weight");
            Scanner sc = new Scanner(System.in);
            if (Integer.valueOf(sc.nextLine()) == 1) {
                System.out.println("Original weight file:");
                readWeightFile(sc.nextLine());
            } else {
                for (int i = 0; i < K; i++) {
                    weights[i] = new Random().nextInt();
                }
            }
        }
    }

    private void writeWeightFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(NEWFILENAME))) {
            for (Double weight : weights) {
                bw.write(weight.toString());
                bw.newLine();
            }
            System.out.println("final weight written to " + NEWFILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class LSPI {
    private static double[] weights;

    FeatureFunction ff = new FeatureFunction();

    int limit = 100000;
    public static final int COLS = 10;
    public static final int ROWS = 21;
    public static final int N_PIECES = 7;
    public static final int K = 8;
    private static int LOST_REWARD = -1000000;
    private static final double GAMMA = 0.9; // can be changed
    private static final double EPS = 0.0005;
    private static final double P = 1.0 / N_PIECES;
    private static final String PROCESS = "/20";

    public LSPI(double[] w) {
        weights = Arrays.copyOf(w, w.length);
    }

    private int pickMove(NextState s, double[] w) {

        int bestMove = 0, currentMove;
        double bestValue = Double.NEGATIVE_INFINITY, currentValue = 0.0;
        NextState ns = new NextState();

        for (currentMove = 0; currentMove < s.legalMoves().length; currentMove++) {
            ns.copyState(s);
            ns.makeMove(currentMove);

            if (ns.hasLost())
                continue;

            currentValue = ff.computeValueOfState(ns, w);

            if (currentValue > bestValue) {
                bestMove = currentMove;
                bestValue = currentValue;
            }
        }
        return bestMove;

    }

    private double getR(NextState ns, NextState nns, int action) {
        if (nns.hasLost())
            return P * LOST_REWARD;
        else
            return P * (nns.getRowsCleared() - ns.getRowsCleared());
    }

    public double[] learn() {

        NextState s = new NextState();
        double[] prevWeight = Arrays.copyOf(weights, weights.length);
        NextState ns = new NextState();
        NextState nns = new NextState();

        int count = 0;

        // stop when weights converge or count reduce to 0
        // don't need to compare for the first iteration
        while ((diff(weights, prevWeight) > EPS || count == 0) && count < 20) {
            prevWeight = Arrays.copyOf(weights, weights.length);
            System.out.println(Arrays.toString(weights));
            System.out.println(count + PROCESS); // print out current stage
            weights = updateWeights(s, weights, ns, nns);
            count++;
        }
        for (int i = 0; i < K; i++) {
            weights[i] = weights[i] < 0 ? weights[i] : -weights[i];
        }
        weights[1] = -weights[1];
        System.out.println("Final Weight: " + Arrays.toString(weights));
        return weights;
    }

    private double[] updateWeights(NextState s, double[] w, NextState ns, NextState nns) {
        double reward = 0;
        double[][] A = new double[K][K];

        for (int j = 0; j < K; j++) {
            A[j][j] = 1.0 / 100000; // corresponding to the number of state
        }
        double[][] B = new double[K][1];
        Generator gen = new Generator();

        for (int i = 0; i < limit; i++) {

            do {
                s = Generator.decodeState(gen.generateUniqueState());
            } while (s == null);

            // to get summation of all the possible action and nextStates
            for (int action = 0; action < s.legalMoves().length; action++) {

                ns.copyState(s);
                ns.makeMove(action);

                if (!ns.hasLost()) {
                    reward = 0;
                    double[][] phi1 = new double[K][1];
                    double[][] phi2 = new double[K][1];
                    double[][] phiSum = new double[K][1];
                    phi1 = Matrix.convertToColumnVector(ff.computeFeatureVector(ns));

                    // calculate summation of all the possibilities
                    for (int piece = 0; piece < N_PIECES; piece++) {
                        ns.setNextPiece(piece);
                        nns.copyState(ns);
                        nns.makeMove(pickMove(nns, w));

                        phi2 = Matrix.convertToColumnVector(ff.computeFeatureVector(nns));
                        phiSum = Matrix.matrixAdd(phiSum, phi2);
                        reward += getR(ns, nns, action);
                    }

                    // find numerator
                    // As both GAMMA and P is constant
                    double[][] tempSum = Matrix.multiplyByConstant(phiSum, GAMMA * P);
                    double[][] transposed = Matrix.transpose(Matrix.matrixSub(phi1, tempSum));
                    double[][] numerator = Matrix.matrixMulti(A, phi1);
                    numerator = Matrix.matrixMulti(numerator, transposed);
                    numerator = Matrix.matrixMulti(numerator, A);

                    // find denominator
                    double[][] temp = Matrix.matrixMulti(transposed, A);
                    temp = Matrix.matrixMulti(temp, phi1);
                    // temp is a 1*1 array
                    double denominator = 1.0 + temp[0][0];

                    A = Matrix.matrixSub(A, Matrix.multiplyByConstant(numerator, 1.0 / denominator));
                    B = Matrix.matrixAdd(B, Matrix.multiplyByConstant(phi1, reward));
                }
            }
        }

        w = Matrix.convertToArray(Matrix.matrixMulti(A, B));
        return w;
    }

    private double diff(double[] w, double[] prevWeight) {
        int diff = 0;
        for (int i = 0; i < w.length; i++) {
            diff += (w[i] - prevWeight[i]) * (w[i] - prevWeight[i]);
        }

        return diff;
    }
}
