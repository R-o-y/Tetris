import java.util.ArrayList;
import java.util.Random;

public class Learner {
    
    static Random random = new Random();
    static int MAX_FAILED_GENERATIONS = 10;
    double bestScore = Double.MIN_VALUE;
    
    int[][] sequences;
    int populationSize = 100;
    double crossOverRate = 0.6;
    double mutationRate = 0.01;
    
    int failedGenerations = 0;

    public void run() {
        

        
        // Create initial population
        ArrayList<Chromosome> population = new ArrayList<Chromosome>();
        for (int i=0; i<populationSize; i++) {
            population.add(new Chromosome(generateRandomWeightsArray()));
        }
        
        ArrayList<Chromosome> currentGeneration = population;
        ArrayList<Chromosome> nextGeneration = new ArrayList<Chromosome>();
        do {
            // Generate fixed set of sequences to test each candidate
            generatePieceSequences();
            
            // Do the fitness and algo shit.
            evaluateFitness(currentGeneration);
            
            // Do the genetic stuff based on the fitness.
            double totalFitness = 0;
            for (Chromosome c : currentGeneration) totalFitness += c.fitness;
            
            nextGeneration = new ArrayList<Chromosome>();
            
            // keep producing offspring while population is smaller than our desired
            while (nextGeneration.size() < populationSize) {
                // Randomly choose 2 parents taking into account their fitness
                Chromosome parent1;
                Chromosome parent2;
                
                // from the 2 selected
                // either crossover at a random point
                // or simply add to the next generation
            }
            
            // randomly mutate chromosomes 
            
            
        } while (!shouldEndLearner(nextGeneration));
        
        Chromosome bestChromosome = null;
        double bestScore = Integer.MIN_VALUE;
        
        for (int i=0; i<nextGeneration.size(); i++) {
            if (nextGeneration.get(i).fitness > bestScore) {
                bestScore = nextGeneration.get(i).fitness;
                bestChromosome = nextGeneration.get(i);
            }
        }
        
        System.out.println("best weights: ");
        printArray(bestChromosome.weights);
        
        return;
    }
    
    private void evaluateFitness(ArrayList<Chromosome> currentGeneration) {
        int sum = 0;
        for (Chromosome chromosome : currentGeneration) {
            // run the simulation 5times and get the avg fitness score
            for (int i=0; i<5; i++) 
                sum += runSimulation(chromosome);
            
            // set the chromosome's fitness
            chromosome.fitness = sum / 5.0;
        }
    }
    
    private int runSimulation(Chromosome chromosome) {
        State s = new State();
        PlayerSkeleton p = new PlayerSkeleton(chromosome.weights);
        while (!s.hasLost()) {
            s.makeMove(p.pickMove(s, s.legalMoves()));
            s.draw();
            s.drawNext(0, 0);
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return s.getRowsCleared();
        // return num ccleared
    }
    
    private static void printArray(double[] weights) {
        for (int i=0; i<weights.length; i++) {
            System.out.print(weights[i] + " ");
        }
        System.out.println();
    }
    
    private static double generateRandomWeight() {
        return random.nextFloat() * 1000;
    }
    
    private static double[] generateRandomWeightsArray() {
        double[] weightArr = new double[8];
        for (int i=0; i<weightArr.length; i++) {
            weightArr[i] = generateRandomWeight();
        }
        return weightArr;
    }
    
    private boolean shouldEndLearner(ArrayList<Chromosome> currentGeneration) {
        int maxScore = Integer.MIN_VALUE;
        
        for (int i=0; i<currentGeneration.size(); i++) {
            double score = currentGeneration.get(i).fitness;
            if (score > maxScore) {
                score = maxScore;
            }
        }
        
        // maxScore is this gen's bet score
        if (maxScore > bestScore) {
            // a good gen
            bestScore = maxScore;
            failedGenerations = 0;
            return false;
            
        } else {
            // failed gen
            ++failedGenerations;
            return failedGenerations >= MAX_FAILED_GENERATIONS;
        }
    }
    
    private void generatePieceSequences() {
        sequences = new int[5][100000];
        for (int i=0; i<5; i++) {
            for (int j=0; j<100000; j++) {
                sequences[i][j] = random.nextInt(State.N_PIECES);
            }
        }
    }

}

class Chromosome {
    double fitness;
    double[] weights;
    
    public Chromosome(double[] weights) {
        fitness = -1;
        this.weights = weights;
    }
}
