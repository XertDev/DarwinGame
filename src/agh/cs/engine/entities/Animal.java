package agh.cs.engine.entities;

import agh.cs.engine.utils.MapDirection;
import agh.cs.engine.utils.Vector2D;
import agh.cs.engine.IPositionChangeObserver;
import agh.cs.engine.WorldMap;

import java.util.*;

public class Animal {
    public static final Comparator<Animal> energyEntityComparator = Comparator.comparingDouble(Animal::getEnergy).reversed();

    private float energy;
    private final float maxEnergy;
    private Vector2D position;
    private MapDirection facingDirection = MapDirection.NORTH;
    private final Genome genome;
    private int age = 0;

    private final Set<IPositionChangeObserver> positionChangedObservers = new HashSet<>();
    private final WorldMap worldMap;

    public MapDirection getFacingDirection() {
        return facingDirection;
    }

    public Vector2D getPosition() {
        return position;
    }

    public float getEnergy() {
        return energy;
    }

    public int getAge() {
        return age;
    }

    public Genome getGenome() {
        return genome;
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

    public boolean isDead() {
        return energy < 0;
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
        if(usedEnergy < 0) {
            throw new IllegalArgumentException("usedEnergy must be a positive integer");
        }
        energy -= usedEnergy;
    }

    public void run() {
        ++age;
        int rotation = genome.getRandomGene();
        facingDirection = facingDirection.rotate(rotation);

        Vector2D oldPos = position;
        position = worldMap.nextFieldInDir(position, facingDirection);

        positionChange(oldPos, position);
    }
}
