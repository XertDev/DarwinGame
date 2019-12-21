package agh.cs.engine;

import agh.cs.engine.utils.Vector2D;
import agh.cs.engine.entities.Animal;

public interface IPositionChangeObserver {
    void positionChanged(Animal animal, Vector2D oldPos, Vector2D newPos);
}
