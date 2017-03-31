import java.util.Arrays;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;

public class GeneticTrainer {
    public static final int MAX_EVOLUTION_PERIOD = 40;
    public static final int MAX_EVOLUTION_CYCLES = 5;
    public static final int POPULATION_SIZE = 100;
    
    public static void main(String[] args) throws Exception {
        
        Configuration rootConf = new DefaultConfiguration();
        
        Gene[] sampleGenes = new Gene[26];
        
        for (int i  = 0; i < sampleGenes.length; i++) {
            sampleGenes[i] = new DoubleGene(rootConf, -1, 1);
        }
        
        Chromosome sampleChromosome = new Chromosome(rootConf, sampleGenes);
        rootConf.setSampleChromosome(sampleChromosome);
        rootConf.setPopulationSize(POPULATION_SIZE);
        rootConf.setFitnessFunction(new TetrisFitnessFunction());
        
        Genotype population = Genotype.randomInitialGenotype(rootConf);
        
        for (int i = 0; i < 1000; i++) {
            
            population.evolve();
            
            IChromosome bestSolutionSoFar = population.getFittestChromosome();
            
            double[] weights = Arrays.stream(bestSolutionSoFar.getGenes()).mapToDouble(gene -> (double) gene.getAllele()).toArray();
            for (double weight: weights) {
                System.out.print(weight + ", ");
            }
            System.out.println();
            System.out.println(bestSolutionSoFar.getFitnessValueDirectly());
            
            System.out.println();
        }

    }
}
