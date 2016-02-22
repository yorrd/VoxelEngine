import java.util.Arrays;

public class ArrayWorld extends World<Block[][][]> {

    @Override
    void initializeWorld() {
        worldmap = new Block[MAX_X * 2 + 1][MAX_Y * 2 + 1][MAX_Z * 2 + 1];
        for(int x = -1 * MAX_X; x < MAX_X; x++)
            for(int y = -1 * MAX_Y; y < MAX_Y; y++) {
                for (int z = -1 * MAX_Z; z < MAX_Z; z++) {
                    set(x, y, z, new Block(Block.EMPTY));
                }
            }
    }

    @Override
    void set(int x, int y, int z, Block block) {
        worldmap[x + MAX_X][y + MAX_Y][z + MAX_Z] = block;
    }

    @Override
    Block get(int x, int y, int z) {
        if(Math.abs(x) > MAX_X || Math.abs(y) > MAX_Y || Math.abs(z) > MAX_Z)
            return null;
        return worldmap[x + MAX_X][y + MAX_Y][z + MAX_Z];
    }

    @Override
    Block[][][] getInterval(int x1, int x2, int y1, int y2, int z1, int z2) {
        Block[][][] returnArray = new Block[x2-x1][y2-y1][z2-z1];
        for(int xRunner = x1; xRunner < x2; xRunner++) {
            for(int yRunner = y1; yRunner < y2; yRunner++) {
                for(int zRunner = z1; zRunner < z2; zRunner++) {
                    try {
                        returnArray[xRunner][yRunner][zRunner] = get(x1 + xRunner, y1 + yRunner, z1 + zRunner);
                    } catch(ArrayIndexOutOfBoundsException e) {}
                }
            }
        }
        return returnArray;
    }

    Block[] get(int x, int y) {
        return worldmap[x + MAX_X][y + MAX_Y];
    }

    void moveDown() {
        for(int x = -1 * MAX_X; x < MAX_X; x++)
            for(int y = -1 * MAX_Y; y < MAX_Y; y++) {
                Block[] column = get(x, y);
                Arrays.sort(column, (o1, o2) -> {
                    if(o1.isEmtpy())
                        return o2.isEmtpy() ? 0 : 1;
                    else
                        return o2.isEmtpy() ? 0 : -1;
                });
            }
    }

    void showCoordinateBlocks() {
        for(int x = -1 * MAX_X; x < MAX_X; x++)
            for(int y = -1 * MAX_Y; y < MAX_Y; y++) {
                for(int z = -1 * MAX_Z; z < MAX_Z; z++) {
                    if(x == 0 || y == 0 || z == 0)
                        set(x, y, z, new Block(2));
                }
            }
    }

    public String toString() {
        String output = "";
        for(int x = -1 * MAX_X; x < MAX_X; x++)
            for(int y = -1 * MAX_Y; y < MAX_Y; y++) {
                for(int z = -1 * MAX_Z; z < MAX_Z; z++) {
                    Block c = get(x, y, z);
                    output += c.toString() + ", ";
                }
                output += "\n";
            }
        return output;
    }
}
