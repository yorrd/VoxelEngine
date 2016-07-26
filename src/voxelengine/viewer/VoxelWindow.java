package voxelengine.viewer;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

class VoxelWindow implements VoxelViewer {

    private long windowHandle;
    final static int HEIGHT = 720;
    final static int WIDTH = 1024;

    private static final ArrayList<Integer> listenTo = new ArrayList<Integer>() {{
        add(GLFW_KEY_UP);
        add(GLFW_KEY_DOWN);
        add(GLFW_KEY_RIGHT);
        add(GLFW_KEY_LEFT);
        add(GLFW_KEY_SPACE);
        add(GLFW_KEY_W);
        add(GLFW_KEY_A);
        add(GLFW_KEY_S);
        add(GLFW_KEY_D);
        add(GLFW_KEY_ESCAPE); }};
    private HashMap<Integer, Boolean> keyDown = new HashMap<>();

    VoxelWindow() {
        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit()) throw new IllegalStateException("Error while initializing GLFW");

        // configuration
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        windowHandle = glfwCreateWindow(WIDTH, HEIGHT, "voxelengine", 0, 0);
        if(windowHandle == 0) throw new RuntimeException("Failed to initialize the window");

        // center the window
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(windowHandle, (vidmode.width() - WIDTH) /2, (vidmode.height() - HEIGHT) /2);

        // final steps
        glfwMakeContextCurrent(windowHandle);
        glfwSwapInterval(1);
        glfwShowWindow(windowHandle);

        // keys
        glfwSetKeyCallback(windowHandle, (windowHandle, key, scanCode, action, mods) -> {

            if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(windowHandle, true);
            if(listenTo.contains(key))
                if(action == GLFW_PRESS)
                    keyDown.put(key, true);
                else
                    keyDown.put(key, false);
        });
    }

    @Override
    public boolean frame() {
        // refresh and ask for new keys
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();

        // return if we should stop here
        return glfwWindowShouldClose(windowHandle);
    }
}