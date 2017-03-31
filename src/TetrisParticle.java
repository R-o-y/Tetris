import net.sourceforge.jswarm_pso.Particle;

/**
 * This class represents the weight vector of heuristics in the Tetris problem
 *
 */
public class TetrisParticle extends Particle {
    public TetrisParticle() {
        super(26);  // The input number is the dimension of the weight vector
    }
}