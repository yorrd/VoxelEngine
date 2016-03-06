package VoxelEngine;

import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLAutoDrawable;

import javax.swing.*;
import java.util.concurrent.Semaphore;

public class VoxelEngineBenchmark {

    enum BenchmarkOptions {
        INTERVALTREECHUNK_MOORE ("Chunk Interval Moore", _Chunk._IntervalTreeChunkMoore.class),
        INTERVALTREECHUNK_SINGLE ("Chunk Interval Single", _Chunk._IntervalTreeChunkSingle.class),
        ARRAYCHUNK_MOORE ("Chunk Array Moore", _Chunk._ArrayChunkMoore.class),
        ARRAYCHUNK_SINGLE ("Chunk Array Single", _Chunk._ArrayChunkSingle.class),
        VOXELENGINEDEMO ("Rendering", _VoxelEngineDemo.class),
        ;

        String name;
        Class b;

        BenchmarkOptions(String name, Class b) {
            this.name = name;
            this.b = b;
        }

        Benchmark getInstance() {
            try {
                return (Benchmark) b.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("A Benchmark couldn't be initialized");
            }
        }
    }

    public static void main(String[] args) {

        System.out.println("Accepted commands: <empty> for both, \"moore\" and \"random\" for one and \"quit\"");

        VoxelEngineBenchmark benchmark = new VoxelEngineBenchmark();
        benchmark.accumulate();
//        String input = "";
//        while(!input.equals("quit")) {
//            Scanner scanner = new Scanner(System.in);
//            System.out.print(">> ");
//            input = scanner.nextLine();
//            switch (input) {
//                case "quit":
//                    break;
//                default:
//                    benchmark.accumulate();
//                    break;
//            }
//        }
    }

    void accumulate() {
        System.out.println(String.format("%32s", "Name") + ": " + String.format("%14s", "avg") + String.format("%14s", "min") + String.format("%14s", "max") + String.format("%14s", "median"));

        for(BenchmarkOptions option : BenchmarkOptions.values()) {
            Benchmark.BenchmarkResult result = option.getInstance()._benchmark();
            System.out.println(String.format("%32s", option.name) + ": " + String.format("%,14d", result.avg) + String.format("%,14d", result.min) + String.format("%,14d", result.max) + String.format("%,14d", result.getMedian()));
        }
    }
}


interface Benchmark {
    int ITERATIONS = 1000;
    int FEWER_ITERATIONS = 100;
    int WARM_UP = 350;  // number of iterations skipped to prevent the cold-start-effect

    BenchmarkResult _benchmark();

    void doWork();
    default void doWork(short[] values) {
        doWork();
    }

    class BenchmarkResult {

        Long[] timings;
        double sum;
        long min;
        long max;
        long avg;

        BenchmarkResult(Long[] timings) {
            this.timings = timings;
            calculate();
        }
        
        void calculate() {
            sum = 0;
            int counter = 0;
            min = Long.MAX_VALUE;
            max = 0;
            for(int i = timings.length > WARM_UP + 100 ? WARM_UP : 0; i < timings.length; i++) {
                sum += timings[i];
                counter++;
                if (timings[i] < min)
                    min = timings[i];
                if (timings[i] > max)
                    max = timings[i];
            }
            avg = (long) (sum / counter);
        }

        long getMedian() {
            return timings[timings.length / 2];
        }
    }
}


class _VoxelEngineDemo extends VoxelEngineDemo implements Benchmark {

    private int pointer = 0;
    private Long[] benchmarkTimings = new Long[FEWER_ITERATIONS];

    private Semaphore mutex = new Semaphore(0);

    public BenchmarkResult _benchmark() {
        mutex.acquireUninterruptibly();

        SwingUtilities.invokeLater(() -> {
            GLAnimatorControl animator = getAnimator();
            if (animator.isStarted()) animator.stop();
            _VoxelEngineDemo.this.destroy();
            _VoxelEngineDemo.this.frame.setVisible(false);
            _VoxelEngineDemo.this.frame.dispose();
        });

        return new BenchmarkResult(benchmarkTimings);
    }

    @Override
    public void doWork() {
       // unused
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        long startTime = System.nanoTime();
        super.display(drawable);
        long endTime = System.nanoTime();

        if(pointer < FEWER_ITERATIONS) {  // fixed 100 iterations
            benchmarkTimings[pointer] = endTime - startTime;
            pointer++;
        } else {
            mutex.release();
        }
    }
}





interface _Chunk extends Benchmark {

    int MOORE_OFFSET = 3;
    TerrainGenerator generator = new SimplexTerrainGenerator(0L).getGeneratorForChunk(0, 0, 0);


    default BenchmarkResult _benchmarkPerBlock() {
        Long[] timings = new Long[ITERATIONS];
        for (int i = 0; i < ITERATIONS; i++) {

            short x = (short) (Math.random() * (Chunk.CHUNK_SIZE - 2 * MOORE_OFFSET) + MOORE_OFFSET);
            short y = (short) (Math.random() * (Chunk.CHUNK_SIZE - 2 * MOORE_OFFSET) + MOORE_OFFSET);
            short z = (short) (Math.random() * (Chunk.CHUNK_SIZE - 2 * MOORE_OFFSET) + MOORE_OFFSET);

            short[] values = new short[6];
            values[0] = (short) (x - MOORE_OFFSET);
            values[1] = (short) (x + MOORE_OFFSET);
            values[2] = (short) (y - MOORE_OFFSET);
            values[3] = (short) (y + MOORE_OFFSET);
            values[4] = (short) (z - MOORE_OFFSET);
            values[5] = (short) (z + MOORE_OFFSET);

            long startTime = System.nanoTime();
            doWork(values);
            long endTime = System.nanoTime();
            timings[i] = (long) ((endTime - startTime) / Math.pow(MOORE_OFFSET * 2 + 1, 3));
        }
        return new BenchmarkResult(timings);
    }

    default BenchmarkResult _benchmark() {
        Long[] timings = new Long[ITERATIONS];
        for (int i = 0; i < ITERATIONS; i++) {
            short[] values = new short[3];
            values[0] = (short) (Math.random() * (Chunk.CHUNK_SIZE));
            values[1] = (short) (Math.random() * (Chunk.CHUNK_SIZE));
            values[2] = (short) (Math.random() * (Chunk.CHUNK_SIZE));

            long startTime = System.nanoTime();
            doWork(values);
            long endTime = System.nanoTime();
            timings[i] = endTime - startTime;
        }
        return new BenchmarkResult(timings);
    }

    default void accessRandomBlock(Chunk chunk, short x, short y, short z) {
        chunk.get(x, y, z);
    }

    default void accessMooreBlocks(Chunk chunk, short x1, short x2, short y1, short y2, short z1, short z2) {
        chunk.getInterval(x1, x2, y1, y2, z1, z2);
    }


    class _IntervalTreeChunkSingle extends IntervalTreeChunk implements _Chunk {

        _IntervalTreeChunkSingle() {
            super(null, generator, new Chunk[6]);
        }

        @Override
        public void doWork() {
            // unused
        }

        @Override
        public void doWork(short[] values) {
            accessRandomBlock(this, values[0], values[1], values[2]);
        }
    }


    class _IntervalTreeChunkMoore extends IntervalTreeChunk implements _Chunk {

        _IntervalTreeChunkMoore() {
            super(null, generator, new Chunk[6]);
        }

        public BenchmarkResult _benchmark() {
            return _benchmarkPerBlock();
        }

        @Override
        public void doWork() {
            // unused
        }

        @Override
        public void doWork(short[] values) {
            accessMooreBlocks(this, values[0], values[1], values[2], values[3], values[4], values[5]);
        }
    }


    class _ArrayChunkSingle extends ArrayChunk implements _Chunk {

        _ArrayChunkSingle() {
            super(null, generator, new Chunk[6]);
        }

        @Override
        public void doWork() {
            // unused
        }

        @Override
        public void doWork(short[] values) {
            accessRandomBlock(this, values[0], values[1], values[2]);
        }
    }


    class _ArrayChunkMoore extends ArrayChunk implements _Chunk {

        _ArrayChunkMoore() {
            super(null, generator, new Chunk[6]);
        }

        public BenchmarkResult _benchmark() {
            return _benchmarkPerBlock();
        }

        @Override
        public void doWork() {
            // unused
        }

        @Override
        public void doWork(short[] values) {
            accessMooreBlocks(this, values[0], values[1], values[2], values[3], values[4], values[5]);
        }
    }
}