package agh.cs;

import java.util.Objects;

public class Vector2D {
    final public int x;
    final public int y;

    public Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", x, y);
    }

    public boolean precedes(Vector2D other) {
        return x <= other.x && y <= other.y;
    }

    public boolean follows(Vector2D other) {
        return x >= other.x && y >= other.y;
    }

    public Vector2D upperRight(Vector2D other) {
        return new Vector2D(
                Math.max(x, other.x),
                Math.max(y, other.y)
        );
    }

    public Vector2D lowerLeft(Vector2D other) {
        return new Vector2D(
                Math.min(x, other.x),
                Math.min(y, other.y)
        );
    }

    public Vector2D add(Vector2D other) {
        return new Vector2D(
                x + other.x,
                y + other.y
        );
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(
                x - other.x,
                y - other.y
        );
    }

    public Vector2D opposite() {
        return new Vector2D(-x, -y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2D vector2D = (Vector2D) o;
        return x == vector2D.x &&
                y == vector2D.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
