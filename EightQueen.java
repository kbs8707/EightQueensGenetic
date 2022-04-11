import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class EightQueen {
    static final int MAXSCORE = 57;
    static final int NUM_PER_GENERATION = 10;
    static final double MUTATION_RATE = 0.05;
    static final int CHOROSOMES = 8;
    
    public static void main(String[] args) {
        Random rand = new Random();
        
        //Generate first generation
        ArrayList<int[]> root = new ArrayList<int[]>();
        for (int i=0; i<NUM_PER_GENERATION; i++) {
            int[] gene = new int[CHOROSOMES];
            for (int j=0; j<CHOROSOMES; j++) {
                gene[j] = rand.nextInt(8) + 1;
            }
            root.add(gene);
        }
        
        EightQueen eq = new EightQueen();
        MaxScore ms = eq.maxScore(root);
        
        int iteration = 0;

        //loop until a solution is found
        while (ms.getMaxScore() < MAXSCORE) {
            ArrayList<int[]> selection = eq.selection(root);    //prepare for selection, higher scoring gene will be duplicated
            ArrayList<int[]> crossover = eq.crossover(selection);   //prepare for crossover, every pair of genes will swap half of their chromosomes
            ArrayList<int[]> mutation = new ArrayList<int[]>(); //prepare for mutation, every chromosome has a chance of mutation
            for (int i=0; i<crossover.size(); i++) {
                mutation.add(eq.mutation(crossover.get(i)));
            }
            ms = eq.maxScore(mutation); //obtain the highest scoring gene within the generation
            iteration++;
        }
        
        System.out.println("Solution board is: " + Arrays.toString(ms.getMaxSequence()));
        System.out.println("Iteration: " + iteration);
    }
    
    private int fitness(int[] sequence) {
        int score = 1;  //base score of 1 to prevent divide by zero error
        
        for(int row=0; row<sequence.length; row++) {
            int col = sequence[row];
            for(int i=0; i<sequence.length; i++) {
                if(i==row) continue;    //exclude self
                if(sequence[i] == col) continue;    //column check
                if(i + sequence[i] == row + col) continue;  //diagonal check
                if(i - sequence[i] == row - col) continue;  //diagonal check
                score++;    //score increments if all conditions are satisfied
            }
        }
        
        return score;
    }
    
    private ArrayList<int[]> crossover(ArrayList<int[]> parent) {
        ArrayList<int[]> children = new ArrayList<int[]>();
        for(int i=0; i<NUM_PER_GENERATION/2; i++) { //iterates the genes in pairs, thus it is divided by 2
            int[] a = new int[CHOROSOMES];
            int[] b = new int[CHOROSOMES];
            for(int j=0; j<CHOROSOMES/2; j++) { //iterates half of the chromosomes to swap with the other half
                a[j] = parent.get(i)[j];
                a[(CHOROSOMES/2) + j] = parent.get(NUM_PER_GENERATION/2 + i)[CHOROSOMES/2 + j];
                b[j] = parent.get(i)[CHOROSOMES/2 + j];
                b[(CHOROSOMES/2) + j] = parent.get(NUM_PER_GENERATION/2 + i)[j];
            }
            children.add(a);
            children.add(b);
        }
        
        return children;
    }
    
    private int[] mutation(int[] sequence) {
        int[] res = new int[CHOROSOMES];
        Random rand = new Random();
        
        for (int i=0; i<sequence.length; i++) {
            double roll = rand.nextDouble();
            if (roll < MUTATION_RATE) { //roll for mutation
                res[i] = rand.nextInt(8) + 1;   //random value from 1-8
            }
            else {
                res[i] = sequence[i];
            }
        }
        
        return res;
    }
    
    private ArrayList<int[]> selection(ArrayList<int[]> parent) {
        int[] fitness = new int[NUM_PER_GENERATION];
        ArrayList<int[]> selectionList = new ArrayList<int[]>();
        
        for (int i=0; i<parent.size(); i++) {
            fitness[i] = fitness(parent.get(i));    //obtain fitness score for every genes
        }
        
        int sum = fitnessSum(fitness);  //obtain sum to execute weighted random selection of genes
        
        for (int i=0; i<parent.size(); i++) {
            selectionList.add(weightedRandom(parent, fitness, sum));
        }
        
        return selectionList;
    }
    
    private int fitnessSum(int[] fitness) {
        int sum = 0;
        for (int i=0; i<fitness.length; i++) {
            sum+=fitness[i];
        }
        return sum;
    }
    
    private int[] weightedRandom(ArrayList<int[]> parent, int[] fitness, int sum) {
        Random rand = new Random();
        int weight = 0;
        int roll = rand.nextInt(sum);
        
        for (int i=0; i<parent.size(); i++) {   //weighed random selection
            weight+=fitness[i];
            if (roll <= weight) return parent.get(i);   //higher scoring gene has a higher chance of selection
        }
        return parent.get(0);
    } 
    
    private MaxScore maxScore(ArrayList<int[]> generation) {
        MaxScore ms = new MaxScore();
        int maxScore = 0;
        for (int i=0; i<generation.size(); i++) {
            if (fitness(generation.get(i)) > maxScore) {
                ms.setMaxScore(fitness(generation.get(i)));
                ms.setMaxSequence(generation.get(i));
                maxScore = fitness(generation.get(i));
            }
        }
        return ms;
    }
}