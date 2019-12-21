package agh.cs.engine;

import agh.cs.engine.utils.Vector2D;
import agh.cs.engine.entities.Animal;

import java.util.*;
import java.util.stream.Collectors;

public class World {
    private Random generator = new Random();
    private List<Animal> animals = new ArrayList<>();
    private WorldMap map;
    private double grassEnergy;
    private double dailyEnergyDepletion;
    private double initialAnimalEnergy;

    public World(float grassEnergy, float dailyEnergyDepletion, float initialAnimalEnergy, WorldMap map) {
        this.map = map;
        this.grassEnergy = grassEnergy;
        this.dailyEnergyDepletion = dailyEnergyDepletion;
        this.initialAnimalEnergy = initialAnimalEnergy;

    }

    public void placeAnimal(Animal animal) {
        map.placeAnimal(animal);
        animals.add(animal);
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
        tempAnimals.stream().filter(animal -> (animal.getEnergy() <=0)).forEach(animal -> {
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

            Animal[] breedingPair = generator.ints( 0,strongestAnimals.length)
                    .distinct()
                    .limit(2)
                    .boxed()
                    .map(integer -> strongestAnimals[integer])
                    .toArray(Animal[]::new);

            Animal child = breedingPair[0].breed(breedingPair[1], childPos);
            placeAnimal(child);
        }
    }

    private void growGrass() {
        map.generateJungleGrass();
        map.generateSteppeGrass();
    }

    public void runEpoch() {
        updateAnimalEnergy();
        updateAnimalsPosition();
        feedAnimals();
        breedAnimals();
        growGrass();
    }
}
