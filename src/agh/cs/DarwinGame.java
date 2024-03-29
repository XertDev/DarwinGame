package agh.cs;

import agh.cs.config.Config;
import agh.cs.engine.World;
import agh.cs.engine.WorldMap;
import agh.cs.visualizer.gui.SimulatorVisualiser;


public class DarwinGame {
    public static void main(String[] args) {
        Config config= Config.fromFile("config.json");

        WorldMap map = new WorldMap(config.getWidth(), config.getHeight(), config.getJungleRatio());
        World world = new World(
                config.getPlantEnergy(),
                config.getMoveEnergy(),
                config.getStartEnergy(),
                config.getMaxEnergy(),
                config.getInitialGrassFieldsCount(),
                map
        );

        world.placeRandomAnimals(config.getInitialAnimalCount());

        try {
            SimulatorVisualiser.start(world, map, config.getTickPeriod());
        }  catch(Exception e) {
            e.printStackTrace();
        }
    }
}
