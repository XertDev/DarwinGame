package agh.cs.config;

import java.io.FileReader;
import java.io.IOException;


import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class Config {
    private int width;
    private int height;

    private float jungleRatio;

    private float startEnergy;
    private float plantEnergy;
    private float moveEnergy;

    private float maxEnergy;
    private int initialGrassFieldsCount;

    private int tickCount;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getJungleRatio() {
        return jungleRatio;
    }

    public float getStartEnergy() {
        return startEnergy;
    }

    public float getPlantEnergy() {
        return plantEnergy;
    }

    public float getMoveEnergy() {
        return moveEnergy;
    }

    public float getMaxEnergy() {
        return maxEnergy;
    }

    public int getTickCount() {
        return tickCount;
    }

    public int getInitialGrassFieldsCount() {
        return initialGrassFieldsCount;
    }

    public void validateValues() {
        final String errorPrefix = "InvalidConfigOption:";

        if(width <= 0) {
            throw new IllegalArgumentException(errorPrefix + "Width must be a positive integer");
        }
        if(height <= 0) {
            throw new IllegalArgumentException(errorPrefix + "Height must be a positive integer");
        }
        if(0.0 >= jungleRatio && jungleRatio >= 1.0) {
            throw new IllegalArgumentException(errorPrefix + "jungleRation must be in range: 0.0 - 1.0");
        }
        if(startEnergy <= 0) {
            throw new IllegalArgumentException(errorPrefix + "startEnergy must be a positive float");
        }
        if(plantEnergy <= 0) {
            throw new IllegalArgumentException(errorPrefix + "plantEnergy must be a positive float");
        }
        if(moveEnergy <= 0) {
            throw new IllegalArgumentException(errorPrefix + "moveEnergy must be a positive float");
        }
        if(maxEnergy < startEnergy) {
            throw new IllegalArgumentException(errorPrefix + "maxEnergy must be bigger than start energy");
        }
        if(initialGrassFieldsCount < 0) {
            throw new IllegalArgumentException(errorPrefix + "initialGrassFieldsCount must be a positive integer");
        }
        if(tickCount <= 15) {
            throw new IllegalArgumentException(errorPrefix + "tickCount should be above 15");
        }
    }

    static public Config fromFile(String filename) {
        Gson gson = new Gson();
        Config config;
        try {
            config = gson.fromJson(new FileReader(filename), Config.class);
        } catch (IOException | JsonParseException e) {
            throw new RuntimeException(e);
        }
        if(config == null) {
            throw new IllegalArgumentException("Invalid config file");
        }
        config.validateValues();
        return config;
    }
}
