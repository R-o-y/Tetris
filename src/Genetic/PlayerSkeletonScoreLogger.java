package Genetic;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;


public class PlayerSkeletonScoreLogger implements Runnable {

    // Number of runs to play the game
    private static final int ROUNDS = 1;
    
    static double[] weights = {
            1.0673515084146694,
            0.5518373660872153,
            0.13114119880147435,
            1.6682687332623332,
            0.013608946139451739,
            0.3436325190123959,
            0.3589287624556017
    };
    
    
	public void run() {
        State s = new State();
        // The optimal set of weights found after 20 evolutions
       
        double start = System.currentTimeMillis();
    	PlayerSkeleton p = new PlayerSkeleton(weights);
        while (!s.lost)
        	s.makeMove(p.pickMove(s, s.legalMoves()));

        //time in seconds
        double runtime = (System.currentTimeMillis() - start) / 1000.0;

        System.out.println(s.getRowsCleared() + "," + runtime);
	}

	public static void main(String[] args) throws FileNotFoundException {
		PrintStream out = new PrintStream(new FileOutputStream("time.csv"));
		System.setOut(out);

        for (int i = 0; i < ROUNDS; i++) {
            PlayerSkeletonScoreLogger logger = new PlayerSkeletonScoreLogger();
            logger.run();
        }
        System.exit(0);
	}
}
