package VoxelEngine;

import java.util.BitSet;
import java.util.Random;

public class Block {

    /*
    sides in the visibilityFlags:
        0 - top
        1 - back
        2 - right
        3 - front
        4 - left
        5 - bottom
     */

    public static final int TOP = 0;
    public static final int BACK = 1;
    public static final int RIGHT = 2;
    public static final int FRONT = 3;
    public static final int LEFT = 4;
    public static final int BOTTOM = 5;
    BitSet visibilityFlags = new BitSet(6);

    private BlockType type = BlockType.EMPTY;

    public Block(BlockType material) {
        this.type = material;
        visibilityFlags.clear();
    }

    public BlockType getType() {
        return type;
    }

    public void setEmpty() {
        type = BlockType.EMPTY;
    }

    public boolean isEmtpy() {
        return type == BlockType.EMPTY;
    }

    public boolean isHidden() {
        return visibilityFlags.cardinality() == 6;
    }

    public boolean isHidden(int side) {
        return visibilityFlags.get(side);
    }

    public void blockUpdate(int side, Block updatedBlock) {
        visibilityFlags.set(side, !updatedBlock.isEmtpy());
    }

    public String toString() {
        return "" + type;
    }


    public enum BlockType {

        EMPTY (-1, "empty", "texture"),
        STONE (0, "stone", "texture"),
        GRASS (1, "grass", "texture"),
        GLASS (2, "glass", "texture"),
        ;

        int ID;
        String name;
        String file;

        BlockType(int ID, String name, String file) {
            this.ID = ID;
            this.name = name;
            this.file = file;
        }

        static BlockType getTypeFromID(int ID) {
            return values()[ID];
        }

        static BlockType getRandom() {
            return getTypeFromID(new Random().nextInt(values().length));
        }
    }
}
