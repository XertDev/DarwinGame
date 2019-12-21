package agh.cs.engine.entities;

import agh.cs.engine.utils.MapDirection;
import agh.cs.engine.utils.Vector2D;
import agh.cs.engine.IPositionChangeObserver;
import agh.cs.engine.WorldMap;

import java.util.*;
import java.util.stream.IntStream;

public class Animal {
    public static final Comparator<Animal> energyEntityComparator = Comparator.comparingDouble(Animal::getEnergy).reversed();

    private float energy;
    private final float maxEnergy;
    private Vector2D position;
    private MapDirection facingDirection = MapDirection.NORTH;
    private Genome genome;

    private static Random generator = new Random();
    private Set<IPositionChangeObserver> positionChangedObservers = new HashSet<>();
    private WorldMap worldMap;

    public MapDirection getFacingDirection() {
        return facingDirection;
    }

    public Vector2D getPosition() {
        return position;
    }

    public float getEnergy() {
        return energy;
    }

    public Animal(WorldMap map, Vector2D position, float initialEnergy, float maxEnergy) {
        this.position = position;
        this.energy = initialEnergy;
        this.maxEnergy = maxEnergy;
        this.worldMap = map;
        this.genome = new Genome();
    }

    private Animal(WorldMap map, Vector2D position, float initialEnergy, float maxEnergy, Genome genome) {
        this.position = position;
        this.energy = initialEnergy;
        this.maxEnergy = maxEnergy;
        this.worldMap = map;
        this.genome = genome;
    }

    private void positionChange(Vector2D oldPos, Vector2D newPos) {
        positionChangedObservers.forEach((observer)->observer.positionChanged(this ,oldPos, newPos));
    }

    public void addPositionChangeObserver(IPositionChangeObserver observer) {
        positionChangedObservers.add(observer);
    }

    public void removePositionChangeObserver(IPositionChangeObserver observer) {
        positionChangedObservers.remove(observer);
    }

    public void eat(double foodEnergy) {
        energy += foodEnergy;
        if(energy > maxEnergy) {
            energy = maxEnergy;
        }
    }

    public Animal breed(Animal mate, Vector2D childPos, float childMaxEnergy) {
        float childEnergy = energy/4 + mate.energy/4;
        energy -= energy/4;
        mate.energy -= mate.energy/4;

        Genome childGenome = genome.mixGenes(mate.genome);

        return new Animal(worldMap, childPos, childEnergy, childMaxEnergy, childGenome);
    }

    public void depleteEnergy(double usedEnergy) {
        energy -= usedEnergy;
    }

    public void run() {
        int rotation = genome.getRandomGene();
        facingDirection = facingDirection.rotate(rotation);

        Vector2D oldPos = position;
        position = worldMap.nextFieldInDir(position, facingDirection);

        positionChange(oldPos, position);
    }

    public String getGenes() {
        return "A";
    }
}
