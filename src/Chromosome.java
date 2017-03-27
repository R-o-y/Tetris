import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Chromosome {
    
    public static final int NUM_OF_FEATURES = 12;
    
    public double[] weights = new double[NUM_OF_FEATURES];

    //gaussian weights
    public Chromosome(Random rand, int mean, int stdDev) {
        
        for (int i=0; i<NUM_OF_FEATURES; i++) {
            weights[i] = gaussianGenerator(rand, mean, stdDev);
            
        }

    }
    
    //random integer weights
    public Chromosome(int min, int max, Random rand) {
        
        for (int i=0; i<NUM_OF_FEATURES; i++) {
            weights[i] = randInt(min, max, rand);
            
        }

    }
    
    
    public Chromosome(double[] weights) {
        
        this.weights = weights;
    }
    
    
    public static int randInt(int min, int max, Random rand) {

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
    
    public double gaussianGenerator(Random rand, int mean, int stdDev) {
        return rand.nextGaussian() * stdDev + mean ;
    }
    
    public void mutate(Random rand, int mean, int stdDev) {
        int toMutateIndex = rand.nextInt(NUM_OF_FEATURES);
        double mutation = gaussianGenerator(rand, mean, stdDev); 
        this.weights[toMutateIndex] = this.weights[toMutateIndex] * mutation;
    }
     
    public void crossOver(Chromosome other, ArrayList<Chromosome> newPopulation, Random rand) {
        int crossOverSite = rand.nextInt(NUM_OF_FEATURES-1);
        double[] copy = Arrays.copyOfRange(this.weights, 0, crossOverSite);
        
        for (int i=0; i<crossOverSite; i++) {
            this.weights[i] = other.weights[i];
        }
        
        for (int i=0; i<crossOverSite; i++) {
            other.weights[i] = copy[i];
        }
        
        newPopulation.add(this);
        newPopulation.add(other);
    }
    
    
    

    public static void main(String[] args) {
        

        
        
        
    }
}
