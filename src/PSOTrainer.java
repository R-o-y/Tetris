
import net.sourceforge.jswarm_pso.FitnessFunction;
import net.sourceforge.jswarm_pso.Swarm;

public class PSOTrainer extends FitnessFunction {
    // return the evaluation result of the given position (weight vector in this case)
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

    // main training method
    public static void main(String[] args) {
        Swarm swarm = new Swarm(800,  // number of particles 
                                new TetrisParticle(),  // class representing a particle
                                new PSOTrainer());  // class that calculates fitness function for particles
        
        // Set position (and velocity) constraints. 
        // i.e.: where to look for solutions
        swarm.setMaxPosition(1);
        swarm.setMinPosition(-1);
        
        // main loop
        for( int i = 0; i < 180000; i++ ) {
            swarm.evolve();
            System.out.println(swarm.toStringStats());
        }
    }
}












