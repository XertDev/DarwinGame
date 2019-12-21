package agh.cs;

import agh.cs.engine.World;
import agh.cs.engine.WorldMap;
import agh.cs.engine.entities.Animal;
import agh.cs.engine.utils.Vector2D;
import agh.cs.visualizer.TextVisualizer;

import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        WorldMap map = new WorldMap(5, 5, 2, 2);
        World world = new World(30, 2, 50, map);
        Animal animal1 = new Animal(map, new Vector2D(3, 3), 50);
        Animal animal2 = new Animal(map, new Vector2D(3, 4), 50);

        world.placeAnimal(animal1);
        world.placeAnimal(animal2);

        TextVisualizer visualizer = new TextVisualizer(map);
        visualizer.show();

        IntStream.range(0, 50).forEach((i) -> {
            world.runEpoch();
            visualizer.show();
        });

    }
}
