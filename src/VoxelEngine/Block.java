package VoxelEngine;

public class Block {

    public static final int NUMBER_MATERIALS = 256;
    public static final int EMPTY = -1;

    private int type = 0;

    public Block(int material) {
        this.type = material;
    }

    public int getType() {
        return type;
    }

    public void setEmpty() {
        type = EMPTY;
    }

    public boolean isEmtpy() {
        return type == EMPTY;
    }

    public String toString() {
        return "" + type;
    }
}
