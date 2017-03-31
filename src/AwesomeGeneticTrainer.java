import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.audit.EvolutionMonitor;
import org.jgap.audit.IEvolutionMonitor;
import org.jgap.event.GeneticEvent;
import org.jgap.event.GeneticEventListener;
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
        rootConf.setPopulationSize(POPULATION_SIZE * NUM_THREADS);
        rootConf.setFitnessFunction(new TetrisFitnessFunction());
        
        // Generate random genotype (initial chromosome population) according to configuration
        Genotype gt = Genotype.randomInitialGenotype(rootConf);
        Population[] populations = splitter.split(gt.getPopulation());
        
        // Evolve population by splitting and merging up to evolution limit
        for (int i=0; i<MAX_EVOLUTION_CYCLES; i++) {
            // Evolve each population that was split
            populations = splitEvolve(populations);

            // Merge populations according to fitness
            Population p = splitMerge(populations);
            
            // Blah blah
        }
    }
    
    public static Population[] splitEvolve(Population[] populations) throws InvalidConfigurationException {
        
        // Prepare the semaphore and mutex for multi-threading evolution
        CountingSemaphore cs = new CountingSemaphore(NUM_THREADS);
        Mutex mutex = new Mutex();
        Population[] newPopulations = new Population[NUM_THREADS];
        
        for (int i=0; i<NUM_THREADS; i++) {
            
            // Config for a particular thread
            Configuration.reset("Thread " + i);
            Configuration config = new DefaultConfiguration("Thread " + i, "Thread " + i);
            
            // Create sample genes and chromosomes
            Gene[] genes = new Gene[26];
            for (int j = 0; j < genes.length; j++) {
                genes[i] = new DoubleGene(config, -1, 1);
            }
            Chromosome chromosome = new Chromosome(config, genes);
            config.setSampleChromosome(chromosome);

            // Population size for a single thread
            config.setPopulationSize(POPULATION_SIZE);
            config.setFitnessFunction(new TetrisFitnessFunction());
            
            // Init a genotype using the given population for this thread
            Genotype genotype = new Genotype(config, populations[i]);
            
            // Use monitor
            // IEvolutionMonitor monitor = new EvolutionMonitor();
            // genotype.setUseMonitor(true);
            // config.setMonitor(monitor);
            // genotype.setMonitor(monitor);
            
            // Create thread to run the genotype
            Thread thread = new Thread(genotype);
            
            // Listen for evolution limit
            GeneticEventListener listener = new GeneticEventListener() {

                @Override
                public void geneticEventFired(GeneticEvent arg0) {
                    
                }
                
            };
            config.getEventManager().addEventListener(GeneticEvent.GENOTYPE_EVOLVED_EVENT, listener);
            
            // Run the thread
            thread.start();
        }
        
        // Wait for all threads to finish
        while (cs.getCount() < NUM_THREADS);
        
        return populations;
    }
    
    public static Population splitMerge(Population[] population) {
        return population[0];
    }
}
