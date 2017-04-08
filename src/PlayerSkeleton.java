import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

public class PlayerSkeleton {
       
    private static final int GENERATION = 20;
    private static final int POPULATION_SIZE = 100;
    private static final int MEAN = 1;
    private static final int STD_DEV = 2;
    private static final long SEED = 12;
    private static final int CROSS_OVER= 60;
    private static final int MUTATION = 20;
    private static final int MIN = -1000;
    private static final int MAX = 1000;
    private static final boolean EXP = true;

    
    
    public static int utilityValue(Chromosome c, int currRowsCleared,  State s, boolean exp) {
        
        int[] colHeightList = new int[State.COLS];
        
        int max = 0;
        int min = State.ROWS;
        
        for (int col=0; col<10; col++) {
            int colHeight = s.getTop()[col];
            colHeightList[col] = colHeight;
            
            if (colHeight > max)
                max = colHeight;
            
            if (colHeight < min)
                min = colHeight;
        }
        
        int[] features = new int[Chromosome.NUM_OF_FEATURES];

              
        int uValue = 0;
        
        features[0] = maxHeight(max);
        features[1] = numHoles(s, colHeightList);
        features[2] = connectedHoles(s, colHeightList);
        features[3] = RemovedLines(s, currRowsCleared);
        features[4] = altitudeDifference(max, min);
        features[5] = maxWellDepth(s, colHeightList);
        features[6] = sumOfWells(colHeightList);
        features[7] = landingHeight(s);
        features[8] = numBlocks(s);
        features[9] = weightedBlocks(s);
        features[10] = rowTransitions(s);
        features[11] = columnTransitions(s);
        
        for (int k=0; k<Chromosome.NUM_OF_FEATURES; k++) 
        	if (exp) {
        		uValue += c.weights[k] * (Math.pow(features[k], c.exponents[k]));
        	} else {
                uValue += c.weights[k]*features[k];

        	}

        return uValue;
    }
        
    
    

    public static int maxHeight (int max) {

        return max;
    }
    
    
    public static int numHoles(State s, int[] colHeightList) {
        int numHoles = 0;
        
        int[][] field = s.getField();
        
        
        for (int col=0; col<10; col++) {
            int colHeight = colHeightList[col];
            
            for (int row=colHeight-1; row>=0; row--)
                if (field[row][col]==0)
                    numHoles+=1;
        }
        
        return numHoles;
    }
    
    public static int connectedHoles(State s, int[] colHeightList) {
        
        int numConnectedHoles = 0;
        
        int[][] field = s.getField();
  
        for (int col=0; col<10; col++) {
            int colHeight = colHeightList[col];
            
            for (int row=colHeight-1; row>=0; row--) {
                if (field[row][col]==0) {
                    numConnectedHoles+=1;
                    
                    while (row>0) {
                        if (field[row-1][col]==0) 
                            row--;
                        else
                            break;
                    }
                }
            }
        }
        
        return numConnectedHoles;
    }
    
    public static int RemovedLines(State s, int currRowsCleared) {
        return s.getRowsCleared() -  currRowsCleared;
    }
    
    public static int altitudeDifference(int max, int min) {
   
        return max - min;
    }
    
    public static int maxWellDepth(State s, int[] colHeightList) {
        int maxWell = 0;
        
                
        if (colHeightList[0] < colHeightList[1])
            maxWell = colHeightList[1] - colHeightList[0];

        if (colHeightList[State.COLS-2] > colHeightList[State.COLS-1]) 
            if (maxWell < colHeightList[State.COLS-2] - colHeightList[State.COLS-1]) 
                maxWell = colHeightList[State.COLS-2] - colHeightList[State.COLS-1];
            
            
        int currWell = 0;
        for (int col=1; col<9; col++) {
            
            if (colHeightList[col] < colHeightList[col-1] && colHeightList[col] < colHeightList[col+1] )
                currWell = Math.min(colHeightList[col-1], colHeightList[col+1]) - colHeightList[col];
                if (currWell > maxWell) 
                    maxWell = currWell;
        }
        
        return maxWell;
        
    }
    
    public static int sumOfWells(int[] colHeightList) {
        int well = 0;
        
        
        if (colHeightList[0] < colHeightList[1])
            well += (colHeightList[1] - colHeightList[0]);

        if (colHeightList[State.COLS-2] > colHeightList[State.COLS-1]) 
                well += (colHeightList[State.COLS-2] - colHeightList[State.COLS-1]);
            
            
        int currWell = 0;
        for (int col=1; col<9; col++) {
            
            if (colHeightList[col] < colHeightList[col-1] && colHeightList[col] < colHeightList[col+1] ) {
                currWell = Math.min(colHeightList[col-1], colHeightList[col+1]) - colHeightList[col];
                well += currWell;
            }
        }
        
        return well;
    }
        
    
    public static int landingHeight(State curr) {
        
        int turn = curr.getTurnNumber();
        int[][] field = curr.getField();
        int lHeight = 0;
        for (int row=State.ROWS-1; row>=0; row--) {
            for (int col=0; col<State.COLS; col++) {
                if (field[row][col] == turn) {
                    lHeight = row;
                    break;
                }
            }
        }
        
        return lHeight;
    }
    
    public static int numBlocks(State s) { 
        int[][] field = s.getField();
        int blocks = 0;
        for (int row=0; row<State.ROWS; row++) {
            for (int col=0; col<State.COLS; col++) {
                if (field[row][col] != 0) {
                    blocks+=1;
                }
            }
        }
        return blocks;
    }
    
    public static int weightedBlocks(State s) {
        
        int[][] field = s.getField();
        int wblocks = 0;
        for (int row=0; row<State.ROWS; row++) {
            for (int col=0; col<State.COLS; col++) {
                if (field[row][col] != 0) {
                    wblocks+=row+1;
                }
            }
        }
        return wblocks;
        
    }
    
    public static int rowTransitions(State s) {
        
        int[][] field = s.getField();
        int rTransition = 0;
        for (int row=0; row<State.ROWS; row++) {
            for (int col=0; col<State.COLS; col++) {
                
                if (col==0) {
                    if (field[row][col] == 0) {
                        rTransition+=1;
                        continue;
                    } else
                        continue;
                }
                
                if (col==State.COLS-1) {
                    if (field[row][col] == 0) {
                        rTransition+=1;
                        continue;
                    } else
                        continue;
                }
                
                if (field[row][col] == 0) {
                    if (field[row][col-1] != 0) {
                        rTransition+=1;
                        continue;
                    } else
                        continue;
                }
                
                if (field[row][col] != 0) {
                    if (field[row][col-1] == 0) {
                        rTransition+=1;
                        continue;
                    } else
                        continue;
                }
                
                
            }
        }
        
        return rTransition;
    }
    
    public static int columnTransitions(State s) {
        int[][] field = s.getField();
        int cTransition = 0;
        for (int row=0; row<State.ROWS; row++) {
            for (int col=0; col<State.COLS; col++) {
                
                if (row==0) {
                    if (field[row][col] == 0) {
                        cTransition+=1;
                        continue;
                    } else
                        continue;
                }
                
                if (row==State.ROWS-1) {
                    if (field[row][col] == 0) {
                        cTransition+=1;
                        continue;
                    } else
                        continue;
                }
                
                if (field[row][col] == 0) {
                    if (field[row-1][col] != 0) {
                        cTransition+=1;
                        continue;
                    } else
                        continue;
                }
                
                if (field[row][col] != 0) {
                    if (field[row-1][col] == 0) {
                        cTransition+=1;
                        continue;
                    } else
                        continue;
                }
                
                
            }
        }
        
        return cTransition;
    }
    
    



    
    //implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves, Chromosome c, Random rand) {
	    
        int numLegalMoves = s.legalMoves().length;
        int bestHeuValue = Integer.MIN_VALUE;
        int bestMove = rand.nextInt(numLegalMoves);
        int currRowsCleared = s.getRowsCleared();
        
        //clone the curr state to play and generate the next state for comparison 
        NextState ns = new NextState();
        ns.setToCurrentState(s);
        
        //play all possible legal moves
        for (int i = 0; i<numLegalMoves; i++) {
            

            
            ns.makeMove(i);

            //int reward = ns.getRowsCleared();
            int uValue = utilityValue(c, currRowsCleared, ns, EXP);
            int currHeuticValue = /*reward + */uValue;
            
            if (currHeuticValue > bestHeuValue) {
                bestHeuValue = currHeuticValue;
                bestMove = i;
                

            }

            ns.setToCurrentState(s);

        }
        
        return bestMove;
	}
	
	

	
	public static void main(String[] args) throws IOException {

	    Random rand = new Random();
	    rand.setSeed(SEED);
	    
        FileWriter fw = new FileWriter("stdout.txt");
        //BufferedWriter bw = new BufferedWriter(fw);
        
        //initialize a population 
        ArrayList<Chromosome> population = new ArrayList<Chromosome>();                        
        int[] populationIdentifier = new int[POPULATION_SIZE];
        
        int nonCrossOver = POPULATION_SIZE - CROSS_OVER;

        
  
        for (int i=0; i<POPULATION_SIZE; i++) {
            population.add(new Chromosome(MIN, MAX, rand, EXP));
            //double[] w = {-62709, -30271, 0, -48621, 35395, -12, -43810, 0, 0, -4041, -44262, -5832};
            //population.add(new Chromosome(w));

            populationIdentifier[i] = i;
        }
        
        double[] fitnessList = new double[POPULATION_SIZE];
        double[] selectionProb = new double[POPULATION_SIZE];
       
        int gen = 0;
        // change while condition to true to run infinite generation
	    while(gen<GENERATION) {
	        
	        for (int i=0; i<POPULATION_SIZE; i++) {
	            
	            Chromosome c = population.get(i);
                State s = new State();
                new TFrame(s);
                PlayerSkeleton p = new PlayerSkeleton();
                
                //int round = 0;
                while(!s.hasLost()) {
                    s.makeMove(p.pickMove(s,s.legalMoves(), c, rand));
                    //System.out.println(s.getTop()[s.getTop().length-1]);
                    s.draw();
                    s.drawNext(0,0);
                    
                    //round++;
            
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                
        /*
                
                fw.write("Gen" + gen + " chrom" + i + "\n");
                fw.write("rows cleared: " + s.getRowsCleared() + "\n");
                for (int j=0; j<Chromosome.NUM_OF_FEATURES; j++) {
                    fw.write("weights[" + j + "] = " + c.weights[j] + "\n");
                }
                fw.write("\n");
                fw.flush();
                
          */      
                fw.write(gen + "\t" + i + "\t" + s.getRowsCleared() + "\t");
                for (int j=0; j<Chromosome.NUM_OF_FEATURES; j++) {
                    fw.write(c.weights[j] + "\t");
                }
                
                if (EXP) {
	                for (int j=0; j<Chromosome.NUM_OF_FEATURES; j++) {
	                    fw.write(c.exponents[j] + "\t");
	                }
                }
                fw.write("\n");
                fw.flush();
                

                //System.out.println("You have completed "+s.getRowsCleared()+" rows.");
                
                
                // update fitness value for chromosome

                fitnessList[i] = s.getRowsCleared();    

	        }
	        
	        gen++;
	        
            ArrayList<Chromosome> newPopulation = new ArrayList<Chromosome>();                        
	        
	        // calculate selection probability for each chromosome
	        double fitnessSum = 0;
	        for (int i=0; i<POPULATION_SIZE; i++) {
	            fitnessSum += fitnessList[i];
	        }
	        
	        for (int i=0; i<POPULATION_SIZE; i++) {
	            selectionProb[i] = fitnessList[i]/fitnessSum;
	        }
	        
	        EnumeratedIntegerDistribution dist = new EnumeratedIntegerDistribution(populationIdentifier, selectionProb);
	        
	        // select non cross over chromosomes
	        for (int i=0; i<nonCrossOver ; i++) {
	            Chromosome toSelect = new Chromosome(population.get(dist.sample()).weights, population.get(dist.sample()).exponents);
	            newPopulation.add(toSelect);
	        }
	        

	        // select cross over chromosomes

            for (int i=0; i<CROSS_OVER/2; i++) {
                int index1 = dist.sample();
                int index2 = dist.sample();

                Chromosome c1 = population.get(index1);
                Chromosome c2 = population.get(index2);
                
                c1.crossOver(c2, newPopulation, rand, EXP);
            }
	                
	        
	        
	        HashSet<Integer> mutationList = new HashSet<Integer>();
	        
	        int mutateIter = 0;
	        while (mutateIter < MUTATION) {
	            int index = rand.nextInt(POPULATION_SIZE);
	            if (mutationList.contains(index))
	                continue;
	            mutationList.add(index);
	            newPopulation.get(index).mutate(rand, MEAN, STD_DEV, EXP);
	            mutateIter++;
	            
	        }
	        
	        population = newPopulation;
	        
	    } 
       
	        
	        
	    }
	
	

	
	
}



