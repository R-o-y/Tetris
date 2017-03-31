import java.util.Arrays;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

public class TetrisFitnessFunction extends FitnessFunction {
    @Override
    // Evaluation of subject's fitness
    protected double evaluate(IChromosome subject) {
        double[] weights = Arrays.stream(subject.getGenes()).mapToDouble(gene -> (double) gene.getAllele()).toArray();
        State s = new State();
        TetrisPlayer p = new TetrisPlayer(s.getField()[0].length, weights);
        int count = 0;
        while (!s.hasLost()) {
            s.makeMove(p.pickMove(s, s.legalMoves()));
            count++;
        }
        return (double) count;
    }
}
