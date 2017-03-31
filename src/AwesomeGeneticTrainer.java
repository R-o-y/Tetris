import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.Population;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.job.SimplePopulationSplitter;

// Multi-threaded version of GeneticTrainer
public class AwesomeGeneticTrainer {

    public static final int NUM_THREADS = 4;
    public static final int MAX_EVOLUTION_PERIOD = 40;
    public static final int MAX_EVOLUTION_CYCLES = 5;
    public static final int POPULATION_SIZE = 10;
    
    public static void main(String[] args) throws Exception {
        
        // Splitter to split the population
        SimplePopulationSplitter splitter = new SimplePopulationSplitter(NUM_THREADS);
        
        // Default configurations
        Configuration rootConf = new DefaultConfiguration();
        
        // Create sample genes and sample chromosomes
        Gene[] sampleGenes = new Gene[26];
        for (int i  = 0; i < sampleGenes.length; i++) {
            sampleGenes[i] = new DoubleGene(rootConf, -1, 1);
        }
        Chromosome sampleChromosome = new Chromosome(rootConf, sampleGenes);
        
        // Set population size, fitness functions and sample chromosome
        rootConf.setSampleChromosome(sampleChromosome);
        rootConf.setPopulationSize(POPULATION_SIZE);
        rootConf.setFitnessFunction(new TetrisFitnessFunction());
        
        // Generate random genotype (initial chromosome population) according to configuration
        Genotype gt = Genotype.randomInitialGenotype(rootConf);
        Population[] populations = splitter.split(gt.getPopulation());
        
        // Evolve population by splitting and merging up to evolution limit
        for (int i=0; i<MAX_EVOLUTION_CYCLES; i++) {
            // Evolve each population that was split
            populations = splitEvolve(populations);

            // Merge populations according to fitness
            
            
        }
    }
    
    public static Population[] splitEvolve(Population[] populations) {
        return populations;
    }
    
    public static Population splitMerge(Population[] population) {
        return population[0];
    }
}
