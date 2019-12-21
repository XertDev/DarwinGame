package agh.cs.engine.entities;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class Genome {
    private final static int geneCount = 32;
    private final static int geneTypeCount = 8;

    private final int[] genes;

    private static Random generator = new Random();

    public Genome() {
        genes = new int[geneCount];
        IntStream.range(0, geneTypeCount).forEach((index) -> {
            genes[index] = index;
        });
        IntStream.range(geneTypeCount,geneCount).forEach((index) -> {
            genes[index]  = generator.nextInt(geneTypeCount);
        });
    }

    private Genome(int[] genes) {
        this.genes = genes;
    }

    public Genome mixGenes(Genome other) {
        int[] genes1 = this.genes;
        int[] genes2 = other.genes;

        if(generator.nextBoolean()) {
            int[] temp = genes1;
            genes1 = genes2;
            genes2 = temp;
        }

        int[] newGenes = new int[geneCount];
        IntStream.range(0, geneTypeCount).forEach((gene) -> {
            newGenes[gene] = gene;
        });

        int[] splitPoint = generator.ints(geneTypeCount, geneCount).distinct().limit(2).toArray();
        Arrays.sort(splitPoint);

        for(int i=geneTypeCount; i <= splitPoint[0]; ++i) {
            newGenes[i] = genes1[i];
        }
        for(int i = splitPoint[0]+1; i <= splitPoint[1]; ++i) {
            newGenes[i] = genes2[i];
        }
        for(int i = splitPoint[1] +1; i <= geneCount-1; ++i) {
            newGenes[i] = genes1[i];
        }
        return new Genome(newGenes);
    }

    int getRandomGene() {
        int genePos = generator.nextInt(geneCount);
        return genes[genePos];
    }

}
