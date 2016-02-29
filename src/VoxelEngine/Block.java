package VoxelEngine;

import java.util.BitSet;
import java.util.Random;

public class Block {

    public static final int TOP = 0;
    public static final int BACK = 1;
    public static final int RIGHT = 2;
    public static final int FRONT = 3;
    public static final int LEFT = 4;
    public static final int BOTTOM = 5;
    BitSet visibilityFlags = new BitSet(6);
    Block[] neighbors = new Block[6];

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
        // updatedBlock == null means that the world has ended here and no block could be found
        // TODO do we really need both?
        if(updatedBlock != null) {
            visibilityFlags.set(side, !updatedBlock.isEmtpy());
        } else {
            visibilityFlags.set(side, true);
        }
        neighbors[side] = updatedBlock;
    }

    public int oppositeSide(int side) {
        switch (side) {
            case TOP: return BOTTOM;
            case BOTTOM: return TOP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            case FRONT: return BACK;
            case BACK: return FRONT;
            default: throw new IllegalStateException("It has to be one of the sides...");
        }
    }

    public String toString() {
        return type.toString();
    }


    public enum BlockType {

        EMPTY (-1, "empty", "texture"),
        STONE (0, "stone", "texture"),
        GRASS (1, "grass", "texture"),
        GLASS (2, "glass", "texture"),
        DEBUG (2048, "debug", "texture"),
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
