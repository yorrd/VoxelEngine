import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public abstract class World<T> {

    public static final int MAX_X = 16;
    public static final int MAX_Y = MAX_X;
    public static final int MAX_Z = MAX_X;

    public static final int GRID_SIZE = 1000;  // in mm, 1m = 1000mm

    protected T worldmap;

    World() {
        initializeWorld();

        // testing
        for(int i = 0; i < (World.MAX_X * World.MAX_X * World.MAX_X); i++) {
            int x = (int) (Math.random() * MAX_X * 2) - MAX_X;
            int y = (int) (Math.random() * MAX_Y * 2) - MAX_Y;
            int z = (int) (Math.random() * MAX_Z * 2) - MAX_Z;
            set(x, y, z, new Block((int) (Math.random() * 2) ));
        }

//        moveDown();
//        showCoordinateBlocks();
    }

    abstract void initializeWorld();

    abstract void set(int x, int y, int z, Block block);

    abstract Block get(int x, int y, int z);

    abstract Block[][][] getInterval(int x1, int x2, int y1, int y2, int z1, int z2);

    long accessRandomBlock() {
        int x = (int) (Math.random() * World.MAX_X * 2) - MAX_X;
        int y = (int) (Math.random() * World.MAX_Y * 2) - MAX_Y;
        int z = (int) (Math.random() * World.MAX_Z * 2) - MAX_Z;

        long startTime = System.nanoTime();
        get(x, y, z);
        long endTime = System.nanoTime();

        return endTime - startTime;
    }

    long accessMooreBlocks() {
        int x = (int) (Math.random() * World.MAX_X * 2) - MAX_X;
        int y = (int) (Math.random() * World.MAX_Y * 2) - MAX_Y;
        int z = (int) (Math.random() * World.MAX_Z * 2) - MAX_Z;

        // checking Moore environment of r = 3
        long startTime = System.nanoTime();
        getInterval(x-3, x+3, y-3, y+3, z-3, z+3);
        long endTime = System.nanoTime();

        // returning time per block
        return (endTime - startTime) / 49;
    }

    abstract void moveDown();
    abstract void showCoordinateBlocks();

    abstract public String toString();
}

// class SegmentCompressedWorld

