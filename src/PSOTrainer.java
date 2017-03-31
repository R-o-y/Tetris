import java.util.Random;

import net.sourceforge.jswarm_pso.FitnessFunction;
import net.sourceforge.jswarm_pso.Particle;
import net.sourceforge.jswarm_pso.Swarm;

public class PSOTrainer extends FitnessFunction {
    public double evaluate(double position[]) {
        State s = new State();
        TetrisPlayer p = new TetrisPlayer(s.getField()[0].length, position);
        int count = 0;
        while (!s.hasLost()) {
            s.makeMove(p.pickMove(s, s.legalMoves()));
            count++;
        }
        return (double) count;
    }
    
    
    
    public static void main(String[] args) {
        Swarm swarm = new Swarm(800
                , new TetrisParticle()
                , new PSOTrainer());
        // Set position (and velocity) constraints. 
        // i.e.: where to look for solutions
        swarm.setMaxPosition(1);
        swarm.setMinPosition(-1);
        swarm.setInertia(0.38);
        // Optimize a few times
        for( int i = 0; i < 180000; i++ ) {
            swarm.evolve();
            System.out.println(swarm.toStringStats());
        }
    }
    
   
    
    
    
//    double w = 0.729844;
//    double c1 = 1.49618;
//    double c2 = 1.49618;
//    
//    static void train() {
//        
//    }
//    
//    static double[] getRamdomeVector() {
//        Random r = new Random();
//        double[] randomVector = new double[25];
//        for (int i = 0; i < 25; i++) {
//            randomVector[i] = r.nextDouble();
//        }
//        return randomVector;
//    }
}












