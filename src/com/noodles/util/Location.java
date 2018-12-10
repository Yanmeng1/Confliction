package com.noodles.util;

public class Location implements Cloneable {

    private int row;
    private int col;

    public static final Location[] adjacent = {
            new Location(-1,-1),new Location(-1,0),new Location(-1,1),
            new Location(0,-1), new Location(0,0), new Location(0,1),
            new Location(1,-1), new Location(1,0), new Location(1,1)
    };

    public Location(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }

    @Override
    public Object clone() {
        Object cloneObj = null;
        try {
            cloneObj = super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return cloneObj;
    }
}
