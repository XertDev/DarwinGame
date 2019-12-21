package agh.cs.engine;

import agh.cs.engine.entities.Animal;
import agh.cs.engine.entities.Grass;
import agh.cs.engine.utils.MapDirection;
import agh.cs.engine.utils.Vector2D;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

public class WorldMap implements IPositionChangeObserver {
    private final Vector2D leftBottomWorldCorner;
    private final Vector2D rightTopWorldCorner;

    private final Vector2D leftBottomJungleCorner;
    private final Vector2D rightTopJungleCorner;

    private List<Vector2D> emptyJungleFields = new ArrayList<>();
    private List<Vector2D> emptySavannaFields = new ArrayList<>();

    private Random generator = new Random();

    private Map<Vector2D, Grass> grassFields = new HashMap<>();
    private Map<Vector2D, List<Animal>> animalMap = new HashMap<>();

    public WorldMap(int mapWidth, int mapHeight, float jungleRatio) {
        int jungleWidth = (int)Math.ceil(mapWidth * jungleRatio);
        int jungleHeight = (int)Math.ceil(mapHeight * jungleRatio);

        leftBottomWorldCorner = new Vector2D(0,0);
        rightTopWorldCorner =  new Vector2D(mapWidth-1, mapHeight-1);
        leftBottomJungleCorner = new Vector2D(0, 0);
        rightTopJungleCorner = leftBottomJungleCorner.add(new Vector2D(jungleWidth-1, jungleHeight-1));

        for(int i = 0; i < jungleHeight; ++i) {
            for(int j = 0; j < jungleWidth; ++j) {
                emptyJungleFields.add(new Vector2D(j, i));
            }
        }

        for(int i = 0; i < mapHeight; ++i) {
            for(int j = 0; j < mapWidth; ++j) {
                if(i >= jungleHeight || j >= jungleWidth) {
                    emptySavannaFields.add(new Vector2D(j, i));
                }
            }
        }
    }

    private boolean fieldEmpty(Vector2D position) {
        return !(grassFields.containsKey(position) || animalMap.containsKey(position));
    }

    private void placeGrassInArea(List<Vector2D> availablePos) {
        int availableSpace = availablePos.size();
        if(availableSpace > 0) {
            int fieldIndex = generator.nextInt(availableSpace);

            Vector2D newGrassPos = availablePos.get(fieldIndex);
            grassFields.put(newGrassPos, new Grass(newGrassPos));
            availablePos.remove(fieldIndex);
        }
    }

    void generateSavannaGrass() {
        placeGrassInArea(emptySavannaFields);
    }

    void generateJungleGrass() {
        placeGrassInArea(emptyJungleFields);
    }

    public Vector2D getJungleSize() {
        return rightTopJungleCorner.add(new Vector2D(1, 1));
    }

    public Vector2D getMapSize() {
        return rightTopWorldCorner.add(new Vector2D(1,1));
    }

    void placeAnimal(Animal animal) {
        boolean entityFieldEmpty = isFieldEmpty(animal.getPosition());
        Vector2D pos = animal.getPosition();
        if(pos.follows(leftBottomWorldCorner) && pos.precedes(rightTopWorldCorner)) {
            animalMap.putIfAbsent(pos, new ArrayList<>());
            animalMap.get(pos).add(animal);

            if(entityFieldEmpty){
                setFieldOccupied(pos);
            }

            animal.addPositionChangeObserver(this);
        } else {
            throw new IllegalArgumentException("Animal position out of map boundary");
        }
    }

    private boolean isFieldEmpty(Vector2D pos) {
        if(animalMap.containsKey(pos)) {
            return animalMap.get(pos).size() == 0 && !grassFields.containsKey(pos);
        }
        return !grassFields.containsKey(pos);
    }

    private boolean isFieldWithoutAnimals(Vector2D pos) {
        if(animalMap.containsKey(pos)) {
            return animalMap.get(pos).size() ==0;
        }
        return true;
    }

    private boolean isJunglePosition(Vector2D pos) {
        return pos.precedes(rightTopJungleCorner) && pos.follows(leftBottomJungleCorner);
    }

    private void setFieldEmpty(Vector2D pos) {
        if(isJunglePosition(pos)) {
            emptyJungleFields.add(pos);
        } else {
            emptySavannaFields.add(pos);
        }
    }

    private void setFieldOccupied(Vector2D pos) {
        if(isJunglePosition(pos)) {
            emptyJungleFields.remove(pos);
        } else {
            emptySavannaFields.remove(pos);
        }
    }

    @Override
    public void positionChanged(Animal animal, Vector2D oldPos, Vector2D newPos) {
        List<Animal> animals = animalMap.get(oldPos);
        animals.remove(animal);
        if(animals.isEmpty()){
            animalMap.remove(oldPos);
        }
        if(isFieldEmpty(oldPos)) {
            setFieldEmpty(oldPos);
        }

        boolean targetFieldEmpty = isFieldEmpty(newPos);
        animalMap.putIfAbsent(newPos, new ArrayList<>());
        animalMap.get(newPos).add(animal);

        if(targetFieldEmpty) {
            setFieldOccupied(newPos);
        }
    }

    public void grassEated(Vector2D pos) {
        grassFields.remove(pos);
        if(isFieldEmpty(pos)) {
            setFieldEmpty(pos);
        }
    }

    public List<List<Animal>> getGroupedAnimals() {
        return new ArrayList<>(animalMap.values());
    }

    public Set<Map.Entry<Vector2D, Grass>> getGrassFields() {
        return grassFields.entrySet();
    }

    public Optional<Vector2D> findRandomEmptyField() {
        if(emptyJungleFields.isEmpty() && emptySavannaFields.isEmpty()) {
                return Optional.empty();
        }
        if(!emptyJungleFields.isEmpty() && !emptySavannaFields.isEmpty()) {
            if(generator.nextBoolean()) {
                return Optional.of(emptyJungleFields.get(generator.nextInt(emptyJungleFields.size())));
            }
            return Optional.of(emptySavannaFields.get(generator.nextInt(emptySavannaFields.size())));
        }
        if(emptyJungleFields.isEmpty()) {
            return Optional.of(emptySavannaFields.get(generator.nextInt(emptySavannaFields.size())));
        } else {
            return Optional.of(emptyJungleFields.get(generator.nextInt(emptyJungleFields.size())));

        }
    }

    public boolean isGrassField(Vector2D pos) {
        return grassFields.containsKey(pos);
    }

    public Vector2D nextFieldInDir(Vector2D pos, MapDirection dir) {
        Vector2D nextField = pos.add(dir.toUnitVector());
        int mapHeight = rightTopWorldCorner.y + 1;
        int mapWidth = rightTopWorldCorner.x + 1;
        int newX = (nextField.x + mapWidth) % mapWidth;
        int newY = (nextField.y + mapHeight) % mapHeight;

        return new Vector2D(newX, newY);
    }

    void removeAnimal(Animal animal) {
        Vector2D pos = animal.getPosition();
        List<Animal> animalsOnField = animalMap.get(pos);
        animalsOnField.remove(animal);
        if(animalsOnField.isEmpty()) {
            animalMap.remove(pos);
        }
        if(isFieldEmpty(pos)) {
            setFieldEmpty(pos);
        }

    }

    public Optional<Vector2D> findEmptyNeighboringField(Vector2D pos) {
        Vector2D[] emptyFields = IntStream.rangeClosed(0, 7).boxed()
                .map(i -> nextFieldInDir(pos, MapDirection.NORTH.rotate(i))
                )
                .filter(this::isFieldWithoutAnimals).toArray(Vector2D[]::new);
        if(emptyFields.length == 0) {
            return Optional.empty();
        }
        int chosenFieldIndex = generator.nextInt(emptyFields.length);
        return Optional.of(emptyFields[chosenFieldIndex]);
    }

}
