package VoxelEngine;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;

public class IntervalTreeChunkTest {

    World world;
    TerrainGenerator generator;
    Chunk[] neighbors;

    IntervalTreeChunk test;
    Block[] shouldBe = new Block[16];

    @Before
    public void beforeTest() {

        world = new World();
        generator = new ChaosTerrainGenerator(0, 0, 0);
        neighbors = new Chunk[0];

        test = new IntervalTreeChunk(world, generator, neighbors);

        for(int i = 0; i < 16; i++) {
            Block tmp = new Block(Block.BlockType.getRandom());

            shouldBe[i] = tmp;

            test.set((short)i, (short)0, (short)0, tmp);
        }


    }

    @Test
    public void testGetBlock() {

        for (int i = 0; i < 16; i++) {
            Block reference = shouldBe[i];

            Block toTest = test.get((short)i, (short)0, (short)0);

            assertEquals(toTest.toString(), reference.toString());
        }
    }

    @Test
    public void testGetInterval() {
        //TODO Fehler eventuell in getInterval-Methode bei Array Initialisierung des Rückgabe-Arrays
        Block[][][] toTest = test.getInterval((short)0, (short)16, (short)0, (short)1, (short)0, (short)1);

        for (int i = 0; i < 16; i++) {
            Block reference = shouldBe[i];

            Block toTestBlock = toTest[0][0][i];

            assertEquals(toTestBlock.toString(), reference.toString());
        }

    }
}