package agh.cs;

public interface IPositionChangeObserver {
    void positionChanged(Animal animal, Vector2D oldPos, Vector2D newPos);
}
