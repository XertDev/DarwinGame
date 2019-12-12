package agh.cs;

import java.util.*;

public class WorldMap implements IPositionChangeObserver {
    private final Vector2D leftBottomWorldCorner;
    private final Vector2D rightTopWorldCorner;

    private final Vector2D leftBottomJungleCorner;
    private final Vector2D rightTopJungleCorner;

    private final int mapHeight;
    private final int mapWidth;

    private int steppGrassCount;
    private int jungleGrassCount;

    private Random generator = new Random();

    Map<Vector2D, Grass> grassFields;
    Map<Vector2D, List<IEntity>> entitiesMap = new HashMap<>();
    List<IEntity> entities = new ArrayList<>();

    public WorldMap(int mapWidth, int mapHeight, int jungleWidth, int jungleHeight) {
        final int centerWidthOffset = (mapWidth-1)/2;
        final int centerHeightOffset = (mapHeight-1)/2;

        jungleGrassCount = 0;
        steppGrassCount = 0;

        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;

        leftBottomWorldCorner = new Vector2D(0,0);
        rightTopWorldCorner =  new Vector2D(mapWidth-1, mapHeight-1);
        leftBottomJungleCorner = new Vector2D(centerWidthOffset - jungleWidth, centerHeightOffset - jungleHeight);
        rightTopJungleCorner = leftBottomJungleCorner.add(new Vector2D(jungleWidth, jungleHeight));
    }

    private boolean fieldEmpty(Vector2D position) {
        return !(grassFields.containsKey(position) || entitiesMap.containsKey(position));
    }

    private void generateJungleGrass() {
        boolean generated = false;
        final Vector2D jungleSize = getJungleSize();
        final int jungleWidth = jungleSize.x + 1;
        final int jungleHeight = jungleSize.y + 1;

        while(!generated) {
            final int posX = generator.nextInt(jungleWidth);
            final int posY = generator.nextInt(jungleHeight);
            final Vector2D newPos = new Vector2D(posX, posY).add(leftBottomJungleCorner);

            if(fieldEmpty(newPos)) {
                grassFields.put(newPos, new Grass(newPos));
                generated = true;
            }
        }
    }

    public Vector2D getJungleSize() {
        return rightTopJungleCorner.subtract(leftBottomJungleCorner);
    }

    private void generateSteppeGrass() {
        boolean generated = false;
        final Vector2D jungleSize = getJungleSize();
        final int jungleWidth = jungleSize.x + 1;
        final int jungleHeight = jungleSize.y + 1;

        while(!generated) {
            final int posX = generator.nextInt(mapHeight);
            final int posY = generator.nextInt(jungleHeight + 1);
            final Vector2D newPos = new Vector2D(posX, posY);

            if(!(newPos.follows(leftBottomJungleCorner) && newPos.precedes(rightTopJungleCorner)) && fieldEmpty(newPos)) {
                grassFields.put(newPos, new Grass(newPos));
                generated = true;
            }
        }
    }

    public void placeAnimal(Animal animal) {
        Vector2D pos = animal.getPosition();
        if(pos.follows(leftBottomWorldCorner) && pos.precedes(rightTopWorldCorner)) {
            entitiesMap.putIfAbsent(pos, new ArrayList<>());
            entitiesMap.get(pos).add(animal);
            entities.add(animal);
        } else {
            throw new IllegalArgumentException("Animal position out of map boundary");
        }
    }

    @Override
    public void positionChanged(Animal animal, Vector2D oldPos, Vector2D newPos) {
        entitiesMap.get(oldPos).remove(animal);
        entitiesMap.putIfAbsent(newPos, new ArrayList<>());
        entitiesMap.get(newPos).add(animal);
    }
}
