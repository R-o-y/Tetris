import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Chromosome {
    
    public static final int NUM_OF_FEATURES = 12;
    private static final int MIN = 0;
    private static final int MAX = 2;
    
    public double[] weights = new double[NUM_OF_FEATURES];
    public double[] exponents = new double[NUM_OF_FEATURES];

    //gaussian weights
    public Chromosome(Random rand, int mean, int stdDev) {
        
        for (int i=0; i<NUM_OF_FEATURES; i++) {
            weights[i] = gaussianGenerator(rand, mean, stdDev);
            
        }

    }
    
    //random integer weights
    public Chromosome(int min, int max, Random rand, boolean exp) {
        
        for (int i=0; i<NUM_OF_FEATURES; i++) {
            weights[i] = randInt(min, max, rand);
            
        }
        
        if (exp) {
        	for (int i = 0; i<NUM_OF_FEATURES; i++) {
        		exponents[i] = doubleGenerator(rand, MIN, MAX);
        	}
        }

    }
    
    
    public Chromosome(double[] weights) {
        
        this.weights = Arrays.copyOf(weights, weights.length);
    }
    
    
    public Chromosome(double[] weights, double[] exponents) {
        
        this.weights = Arrays.copyOf(weights, weights.length);
        this.exponents = Arrays.copyOf(exponents, exponents.length);
    }
    
    public Chromosome() {
    	
	}


	public static int randInt(int min, int max, Random rand) {

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
    
    public static double gaussianGenerator(Random rand, int mean, int stdDev) {
        return rand.nextGaussian() * stdDev + mean ;
    }
    
    public static double doubleGenerator(Random rand, int max, int min) {
        return min + (rand.nextDouble() * (max - min)) ;
    }
    
    public void mutate(Random rand, int mean, int stdDev, boolean exp) {
		int toMutateIndex = rand.nextInt(NUM_OF_FEATURES);
        double mutation = gaussianGenerator(rand, mean, stdDev); 


    	if (exp) {
    		int toMutateExpIndex = rand.nextInt(NUM_OF_FEATURES);
    	    this.weights[toMutateIndex] = this.weights[toMutateIndex] * mutation;
    	    this.exponents[toMutateExpIndex] = this.exponents[toMutateExpIndex] * mutation;

    	} else {
    		this.weights[toMutateIndex] = this.weights[toMutateIndex] * mutation;
    	}
    }
     
    public void crossOver(Chromosome other, ArrayList<Chromosome> newPopulation, Random rand, boolean exp) {
        int weightsCrossOverSite = rand.nextInt(NUM_OF_FEATURES-1);
        
        Chromosome child1 = new Chromosome();
        Chromosome child2 = new Chromosome();

        for (int i=0; i<weightsCrossOverSite; i++) {
        	child1.weights[i] = other.weights[i];
        	child2.weights[i] = this.weights[i];

        }
        
        for (int i=weightsCrossOverSite; i<NUM_OF_FEATURES; i++) {
        	child1.weights[i] = this.weights[i];
        	child2.weights[i] = other.weights[i];

        }
        
        if (exp) {
            int expCrossOverSite = rand.nextInt(NUM_OF_FEATURES-1);
            
            for (int i=0; i<expCrossOverSite; i++) {
            	child1.exponents[i] = other.exponents[i];
            	child2.exponents[i] = this.exponents[i];

            }
            
            for (int i=expCrossOverSite; i<NUM_OF_FEATURES; i++) {
            	child1.exponents[i] = this.exponents[i];
            	child2.exponents[i] = other.exponents[i];

            }

        }
        
        newPopulation.add(child1);
        newPopulation.add(child2);
    }
    
    
    

    public static void main(String[] args) {
        

        
        
        
    }
}
