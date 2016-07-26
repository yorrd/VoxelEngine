package voxelengine.viewer;

import voxelengine.World;

/**
 * Main window, built on LWJGL
 */
public class VoxelEngine {

    private static VoxelViewer window;
    private static VoxelRenderer renderer;
    private static World world;

    private VoxelEngine() {
        world = new World();
        window = new VoxelWindow();
        renderer = new GeometryVoxelRenderer(world, VoxelWindow.WIDTH / VoxelWindow.HEIGHT);
    }

    static VoxelViewer getViewer() { return window; }

    public static void main(String args[]) { new VoxelEngine(); }
}
