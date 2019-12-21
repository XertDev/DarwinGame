package agh.cs.engine.entities;

import agh.cs.engine.utils.Vector2D;

public class Grass {
    public Grass(Vector2D position) {
        this.position = position;
    }

    final private Vector2D position;

    public Vector2D getPosition() {
        return position;
    }
}
