package VoxelEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class VoxelEngineBenchmark {

    public static final int ITERATIONS = 100;  // quite good sample size

    HashMap<ChunkOptions, Long[]> benchmarks = new HashMap<>();
    long measurementOverhead = nanoTimeCalibration();

    static TerrainGenerator generator = new SimplexTerrainGenerator(0L).getGeneratorForChunk(0, 0, 0);

    public static void main(String[] args) {

        System.out.println("Accepted commands: <empty> for both, \"moore\" and \"random\" for one and \"quit\"");

        VoxelEngineBenchmark benchmark = new VoxelEngineBenchmark();
        String input = "";
        while(!input.equals("quit")) {
            Scanner scanner = new Scanner(System.in);
            System.out.print(">> ");
            input = scanner.nextLine();
            switch (input) {
                case "moore":
                    benchmark.chunkBenchmark(true); break;
                case "random":
                    benchmark.chunkBenchmark(false); break;
                case "quit":
                    break;
                default:
                    benchmark.chunkBenchmark(true);
                    benchmark.chunkBenchmark(false); break;
            }
        }
    }

    void chunkBenchmark(boolean moore) {

        benchmarks.put(ChunkOptions.ARRAY, new Long[ITERATIONS]);
        benchmarks.put(ChunkOptions.INTERVAL, new Long[ITERATIONS]);
//        benchmarks.put(ChunkOptions.OCTREE, new Long[ITERATIONS]);

        System.out.println();
        System.out.println();
        System.out.println((moore ? "Moore" : "Random") + " access benchmark");
        System.out.println();
        System.out.println(String.format("%32s", "Name") + ": " + String.format("%8s", "Average") + "" +
                String.format("%6s", "Min") + String.format("%6s", "Max") + String.format("%8s", "Median"));

        for(Map.Entry<ChunkOptions, Long[]> entry: benchmarks.entrySet()) {
            for(int i = 0; i < ITERATIONS; i++) {
                if(moore) {
                    entry.getValue()[i] = entry.getKey().chunk.accessMooreBlocks(measurementOverhead);
                } else {
                    entry.getValue()[i] = entry.getKey().chunk.accessRandomBlock(measurementOverhead);
                }
            }

            int sum = 0;
            int counter = 0;
            long min = entry.getValue()[0];
            long max = entry.getValue()[0];
            for(Long time : entry.getValue()) {
                if(time < min)
                    min = time;
                if(time > max)
                    max = time;
                sum += time;
                counter++;
            }
            long med = entry.getValue()[entry.getValue().length / 2];

            System.out.println(String.format("%32s", entry.getKey().name) + ": " + String.format("%8s", (sum / counter)) + "" +
                    String.format("%6s", min) + String.format("%6s", max) + String.format("%8s", med));
        }
    }

    long nanoTimeCalibration() {
        System.out.println("Calibrating...");
        long sum = 0;
        for(int i = 0; i < 1000; i++) {
            long startTime = System.nanoTime();
            long endTime = System.nanoTime();
            sum += endTime - startTime;
        }
        System.out.println(sum / 1000 + " ns measurement overhead");
        return sum / 1000;
    }


    enum ChunkOptions {
        ARRAY ("array chunk", new ArrayChunk(generator)),
        INTERVAL ("interval tree chunk", new IntervalTreeChunk(generator)),
        OCTREE ("octree chunk", new OctreeChunk(generator));

        private final String name;
        private final Chunk chunk;

        ChunkOptions(String name, Chunk chunk) {
            this.name = name;
            this.chunk = chunk;
        }
    }
}
