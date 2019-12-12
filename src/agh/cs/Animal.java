package agh.cs;

import java.util.*;
import java.util.stream.IntStream;

public class Animal implements IEntity {
    private static int geneCount = 32;
    private static int geneTypeCount = 8;

    private int energy;
    private Vector2D position;
    private MapDirection facingDirection;
    private List<Integer> genes = Arrays.asList(new Integer[geneCount]);

    private static Random generator = new Random();
    private Set<IPositionChangeObserver> observers= new HashSet<>();
    private WorldMap world;

    public MapDirection getFacingDirection() {
        return facingDirection;
    }

    public Vector2D getPosition() {
        return position;
    }

    public Animal(Vector2D position, MapDirection facingDirection, int initialEnergy) {
        this(position, initialEnergy);
        this.facingDirection = facingDirection;
    }

    public Animal(Vector2D position, int initialEnergy) {
        this.position = position;
        this.energy = initialEnergy;
        randomizeGenes();
        fixGenes();
    }

    private Animal(Vector2D position, MapDirection facingDirection, List<Integer> genes) {
        this.position = position;
        this.facingDirection = facingDirection;
        if(genes.size() != geneCount) {
            throw new IllegalArgumentException("Genes count not equals to " + geneCount);
        }
        this.genes = genes;
        fixGenes();
    }

    private void positionChanged(Vector2D oldPos, Vector2D newPos) {
        observers.forEach((observer)->observer.positionChanged(this ,oldPos, newPos));
    }

    public void addObserver(IPositionChangeObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer) {
        observers.remove(observer);
    }


    private void randomizeGenes() {
        IntStream.range(0,32).forEach((index) ->{
            genes.set(index, generator.nextInt(8));
        });
        genes.sort(Integer::compareTo);
    }

    private void fixGenes() {
        HashMap<Integer, Integer> genePopularity = new HashMap<>();
        IntStream.rangeClosed(0, 7).forEach((index) -> genePopularity.put(index, 0));
        genes.forEach((gene) -> {
           genePopularity.replace(gene,genePopularity.get(gene)+1);
        });

        int mostPopular = 0;
        int mostPopularCount = 0;
        for(Map.Entry<Integer, Integer> entry: genePopularity.entrySet()) {
            if(entry.getValue() > mostPopularCount) {
                mostPopular = entry.getKey();
                mostPopularCount = entry.getValue();
            }
        }

        for(int i = 0; i < geneTypeCount; ++i) {
            if(genePopularity.get(i) == 0){
                genePopularity.replace(mostPopular, genePopularity.get(mostPopular)-1);
                genePopularity.replace(i, 1);
            }
        }

        List<Integer> fixedGenes = new LinkedList<>();
        for(int i = 0; i < geneTypeCount; ++i) {
            final int popularity = genePopularity.get(i);
            for(int j=0; j < popularity; ++j) {
                fixedGenes.add(i);
            }
        }

        genes = fixedGenes;
    }

    public String getGenes() {
        return genes.toString();
    }
}
