package agh.cs.visualizer;

import agh.cs.engine.WorldMap;
import agh.cs.engine.entities.Animal;
import agh.cs.engine.entities.Grass;
import agh.cs.engine.utils.Vector2D;

import java.util.*;

public class TextVisualizer {
    WorldMap map;

    public TextVisualizer(WorldMap map) {
        this.map = map;
    }

    public void show() {
        List<List<Animal>> groupedAnimals = map.getGroupedAnimals();
        Set<Map.Entry<Vector2D, Grass>> grassFields = map.getGrassFields();
        Vector2D mapSize = map.getMapSize();
        StringBuilder viewBuilder = new StringBuilder();

        char[][] view = new char[mapSize.y][mapSize.x];
        for(int i = 0; i < mapSize.y; ++i) {
            for(int j = 0; j < mapSize.x; ++j) {
                view[i][j] = 'M';
            }
        }

        grassFields.forEach(vector2DGrassEntry -> {
            Vector2D pos = vector2DGrassEntry.getKey();
            view[pos.y][pos.x] = 'T';
        });

        groupedAnimals.forEach(vector2DSortedSetEntry -> {
            Vector2D pos = vector2DSortedSetEntry.get(0).getPosition();
            view[pos.y][pos.x] = 'A';
        });



        for(int i = 0; i < mapSize.y; ++i) {
            for(int j = 0; j < mapSize.x; ++j) {
                viewBuilder.append(view[i][j]);
            }
            viewBuilder.append('\n');
        }

        System.out.println(viewBuilder.toString());
    }
}
