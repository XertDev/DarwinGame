package agh.cs;

public enum MapDirection {
    NORTH( "N", new Vector2D(0, 1)),
    NORTH_EAST("NE", new Vector2D(1, 1)),
    EAST("E", new Vector2D(1, 0)),
    SOUTH_EAST("SE", new Vector2D(1, -1)),
    SOUTH("S", new Vector2D(0, -1)),
    SOUTH_WEST("SW", new Vector2D(-1, -1)),
    WEST("W", new Vector2D(-1, 0)),
    NORTH_WEST("NW", new Vector2D(-1, 1));

    final String mapDirSymbol;
    final Vector2D dirVector;
    private static MapDirection[] vals = values();

    MapDirection(String mapDirSymbol, Vector2D dirVector) {
        this.mapDirSymbol = mapDirSymbol;
        this.dirVector = dirVector;
    }

    @Override
    public String toString() {
        return this.mapDirSymbol;
    }

    public MapDirection rotate(int steps) {
       return vals[(this.ordinal()+steps) % vals.length];
    }

    public Vector2D toUnitVector()
    {
        return dirVector;
    }
}
