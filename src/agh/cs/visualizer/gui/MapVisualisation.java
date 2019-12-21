package agh.cs.visualizer.gui;

import agh.cs.engine.WorldMap;
import agh.cs.engine.entities.Animal;
import agh.cs.engine.entities.Grass;
import agh.cs.engine.utils.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class MapVisualisation extends JPanel {
    private WorldMap map;
    private int mapWidth;
    private int mapHeight;

    private int cellSize;
    private static int MAX_WINDOW_SIZE = 700;

    private int jungleWidth;
    private int jungleHeight;

    private final int jungleXOffset;
    private final int jungleYOffset;

    private final float maxEnergy;

    private Semaphore mapLock;

    MapVisualisation(WorldMap map, float maxEnergy, Semaphore mapLock) {
        this.maxEnergy = maxEnergy;
        this.map = map;
        this.mapLock = mapLock;
        final Vector2D mapSize = this.map.getMapSize();
        final Vector2D jungleSize = this.map.getJungleSize();
        mapWidth = mapSize.x;
        mapHeight = mapSize.y;
        jungleWidth = jungleSize.x;
        jungleHeight = jungleSize.y;

        jungleXOffset = mapWidth/2 -jungleWidth/2;
        jungleYOffset = mapHeight/2 - jungleHeight/2;

        cellSize =  MAX_WINDOW_SIZE / Math.max(mapHeight, mapWidth);
        int canvasWidth = cellSize * mapWidth;
        int canvasHeight = cellSize * mapHeight;

        System.out.println(canvasHeight);

        setPreferredSize(new Dimension(canvasWidth, canvasHeight));
        setBorder(BorderFactory.createLineBorder(Color.black));
    }

    private Vector2D toScreenPosition(Vector2D pos) {
        int posX = (pos.x + jungleXOffset) % mapWidth * cellSize;
        int posY = (pos.y + jungleYOffset) % mapHeight * cellSize;
        return new Vector2D(posX, posY);
    }

    private void paintBackground(Graphics graphics) {
        graphics.setColor(Color.yellow);
        graphics.fillRect(0,0, getWidth(), getHeight());

        graphics.setColor(Color.green);
        graphics.fillRect(
                jungleXOffset * cellSize,
                jungleYOffset * cellSize,
                jungleWidth * cellSize,
                jungleHeight * cellSize);
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        paintBackground(graphics);
        try{
            mapLock.acquire();
            List<List<Animal>> groupedAnimals = map.getGroupedAnimals();
            Set<Map.Entry<Vector2D, Grass>> grassFields = map.getGrassFields();

            graphics.setColor(new Color(100, 255, 100));
            for(Map.Entry<Vector2D, Grass> grassField: grassFields) {
                Vector2D pos = toScreenPosition(grassField.getKey());
                graphics.fillRect(pos.x, pos.y, cellSize, cellSize);
            }

            for(List<Animal> animalGroup: groupedAnimals) {
                animalGroup.sort(Animal.energyEntityComparator);
                float highestEnergy = animalGroup.get(0).getEnergy();

                int redValue = Math.round(255 * highestEnergy / maxEnergy);

                graphics.setColor(new Color(redValue, 0, 0 ));

                Vector2D pos = toScreenPosition(animalGroup.get(0).getPosition());
                graphics.fillRect(pos.x, pos.y, cellSize, cellSize);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            mapLock.release();
        }

    }
}
