package agh.cs.engine;

import agh.cs.engine.entities.Genome;
import agh.cs.engine.utils.Vector2D;
import agh.cs.engine.entities.Animal;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class World {
    private Random generator = new Random();
    private List<Animal> animals = new ArrayList<>();
    private WorldMap map;
    private long epoch = 0;
    private float grassEnergy;
    private float dailyEnergyDepletion;
    private float initialAnimalEnergy;
    private float maxEnergy;
    private int deathCounter = 0;
    private long deathAgeSum = 0;

    public World(
            float grassEnergy,
            float dailyEnergyDepletion,
            float initialAnimalEnergy,
            float maxEnergy,
            int initialGrassFieldCount,
            WorldMap map
    ) {
        this.map = map;
        this.grassEnergy = grassEnergy;
        this.maxEnergy = maxEnergy;
        this.dailyEnergyDepletion = dailyEnergyDepletion;
        this.initialAnimalEnergy = initialAnimalEnergy;
        IntStream.range(0, initialGrassFieldCount).forEach((i) -> map.generateJungleGrass());
        IntStream.range(0, initialGrassFieldCount).forEach((i) -> map.generateSavannaGrass());
    }

    public void placeAnimal(Animal animal) {
        map.placeAnimal(animal);
        animals.add(animal);
    }

    public void placeRandomAnimals(int count) {
        for(int i = 0; i < count; ++i) {
            Vector2D pos = map.findRandomEmptyField().orElseThrow(
                    () -> new RuntimeException("Cannot place new animal. Empty field not found")
            );
            Animal animal = new Animal(map, pos, initialAnimalEnergy, maxEnergy);
            placeAnimal(animal);
        }
    }

    public WorldMap getMap() {
        return map;
    }

    public long getEpoch()
    {
        return epoch;
    }

    public float getAverageDeathAge() {
        if(deathCounter == 0) {
            return 0;
        } else {
            return deathAgeSum/deathCounter;
        }
    }

    public int getDeathCounter() {
        return deathCounter;
    }

    private void updateAnimalsPosition() {
        animals.forEach(Animal::run);
    }

    private void feedAnimals() {
        for(List<Animal> groupedAnimals: map.getGroupedAnimals()) {
            Vector2D pos = groupedAnimals.get(0).getPosition();
            groupedAnimals.sort(Animal.energyEntityComparator);
            if(map.isGrassField(pos)) {
                groupedAnimals.sort(Animal.energyEntityComparator);
                double highestEnergy = groupedAnimals.get(0).getEnergy();

                List<Animal> strongestAnimals = groupedAnimals.stream()
                        .filter((animal) -> (animal.getEnergy() == highestEnergy))
                        .collect(Collectors.toList());
                double foodEnergyPerAnimal = grassEnergy / strongestAnimals.size();
                strongestAnimals.forEach((animal -> animal.eat(foodEnergyPerAnimal)));
                map.grassEated(pos);
            }
        }
    }

    private void updateAnimalEnergy() {
        animals.forEach((animal -> animal.depleteEnergy(dailyEnergyDepletion)));
        List<Animal> tempAnimals = new ArrayList<>(animals);
        tempAnimals.stream().filter(Animal::isDead).forEach(animal -> {
            ++deathCounter;
            deathAgeSum += animal.getAge();
            map.removeAnimal(animal);
            animals.remove(animal);
        });
    }

    private void breedAnimals() {
        for(List<Animal> groupedAnimals: map.getGroupedAnimals()){
            Vector2D pos = groupedAnimals.get(0).getPosition();
            if(groupedAnimals.size() < 2) continue;

            Vector2D childPos = map.findEmptyNeighboringField(pos).orElse(pos);

            Animal[] strongestAnimals = groupedAnimals.stream()
                    .filter(animal -> animal.getEnergy() >= initialAnimalEnergy * 0.5)
                    .limit(2).toArray(Animal[]::new);

            if(strongestAnimals.length < 2) continue;

            Arrays.sort(strongestAnimals, Animal.energyEntityComparator);

            Animal child = strongestAnimals[0].breed(strongestAnimals[1], childPos, maxEnergy);
            placeAnimal(child);
        }
    }

    public int getAnimalCount() {
        return animals.size();
    }

    public int getGrassCount() {
        return map.getGrassFields().size();
    }

    private void growGrass() {
        map.generateJungleGrass();
        map.generateSavannaGrass();
    }

    public float getAverageLivingAnimalsAge() {
        return (float)animals.stream().mapToInt(Animal::getAge).average().orElse(0);
    }

    public void runEpoch() {
        updateAnimalEnergy();
        updateAnimalsPosition();
        feedAnimals();
        breedAnimals();
        growGrass();
        ++epoch;
    }
}
