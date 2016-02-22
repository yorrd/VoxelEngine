import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.TextureIO;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VoxelEngineDemo extends GLCanvas implements GLEventListener {

    public static String TITLE = "VoxelEngine Simple Demo";
    private static final int FPS = 60;

    private static World world;

    public static void main(String[] args) {
        new VoxelEngineDemo();
        SwingUtilities.invokeLater(() -> {
            GLCanvas canvas = new VoxelEngineDemo();
            canvas.setPreferredSize(new Dimension(1024, 720));
            JFrame frame = new JFrame();
            frame.getContentPane().add(canvas);

            FPSAnimator animator = new FPSAnimator(canvas, FPS);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    new Thread() {
                        @Override
                        public void run() {
                            if(animator.isStarted()) animator.stop();
                            System.exit(0);
                        }
                    }.start();
                }
            });
            frame.setTitle(TITLE);
            frame.pack();
            frame.setVisible(true);
            animator.start();
        });
    }

    private GLU glu;  // for the GL Utility

    // cameraAngle of rotation for the camera direction
    float cameraAngle =0.0f;
    // XZ position of the camera
    float x=0.0f, z=5.0f;
    float cameraDistance = 25f;

    static ArrayList<ArrayList<Long>> benchmarks = new ArrayList<>();

    private int[] textures = new int[Block.NUMBER_MATERIALS];

    VoxelEngineDemo() {
        this.addGLEventListener(this);  // this class also serves as the renderer
        this.addKeyListener(new VoxelEngineKeyListener());
        world = new ArrayWorld();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        gl.glClearColor(0f, .2f, 0f, 0f);
        gl.glClearDepth(1f);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glShadeModel(GL_SMOOTH);

        try {
            textures[0] = TextureIO.newTexture(new File("./block_textures/stone.png"), true).getTextureObject();
            textures[1] = TextureIO.newTexture(new File("./block_textures/grass_side.png"), true).getTextureObject();
            textures[2] = TextureIO.newTexture(new File("./block_textures/glass.png"), true).getTextureObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        initializeBenchmarks();
    }

    public void initializeBenchmarks() {
        for(int i = 0; i < 2; i++) {
            benchmarks.add(new ArrayList<>());
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        if (height == 0) height = 1;
        float aspect = (float) width / height;

        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(70.0, aspect, 0.1, 1000.0);

        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        // TODO rotating around something close to the center, not the center. no idea why yet
        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        glu.gluLookAt(x, cameraDistance / 2, z, 0f, 1f, 0f, 0f, 1f, 0f);

        drawCubes(gl);

        // do benchmarks
        benchmarks.get(0).add(world.accessRandomBlock());
        benchmarks.get(1).add(world.accessMooreBlocks());

        cameraAngle += .01;
        x = cameraDistance * (float) Math.sin(cameraAngle);
        z = -1f * cameraDistance * (float) Math.cos(cameraAngle);
    }

    private static void printBenchmarks() {
        for(int i = 0; i < 2; i++) {
            long sum = 0;
            for(long c : benchmarks.get(i)) {
                sum += c;
            }
            int number = benchmarks.get(i).size();
            number = number == 0 ? 1 : number;
            long result = sum / number;
            System.out.println(i + ": " + result);
        }
        System.out.println("--------------------------------");
    }

    private void drawCubes(GL2 gl) {

        for(int x = -World.MAX_X; x < World.MAX_X; x++)
            for(int y = -World.MAX_Y; y < World.MAX_Y; y++)
                for(int z = -World.MAX_Z; z < World.MAX_Z; z++) {

                    Block current = world.get(x, y, z);
                    if(current.isEmtpy()) continue;

                    float blockSize = World.GRID_SIZE / 1000;  // convert to meters

                    gl.glPushMatrix();

                    gl.glTranslatef(
                            x * blockSize + blockSize / 2f,
                            z * blockSize + blockSize / 2f,
                            y * blockSize + blockSize / 2f);

                    // draw cube
                    gl.glEnable(GL_TEXTURE_2D);
                    gl.glEnable(GL_BLEND);
                    gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    gl.glBindTexture(GL_TEXTURE_2D, textures[current.getType()]);
                    gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                    gl.glBegin(GL_QUADS);

                        // Front Face
                        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-(blockSize / 2f), -(blockSize / 2f), (blockSize / 2f));
                        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( (blockSize / 2f), -(blockSize / 2f), (blockSize / 2f));
                        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( (blockSize / 2f), (blockSize / 2f), (blockSize / 2f));
                        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-(blockSize / 2f), (blockSize / 2f), (blockSize / 2f));

                        // Back Face
                        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-(blockSize / 2f), -(blockSize / 2f), -(blockSize / 2f));
                        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-(blockSize / 2f), (blockSize / 2f), -(blockSize / 2f));
                        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( (blockSize / 2f), (blockSize / 2f), -(blockSize / 2f));
                        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( (blockSize / 2f), -(blockSize / 2f), -(blockSize / 2f));

                        // Top Face
                        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-(blockSize / 2f), (blockSize / 2f), -(blockSize / 2f));
                        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-(blockSize / 2f), (blockSize / 2f), (blockSize / 2f));
                        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( (blockSize / 2f), (blockSize / 2f), (blockSize / 2f));
                        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( (blockSize / 2f), (blockSize / 2f), -(blockSize / 2f));

                        // Bottom Face
                        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-(blockSize / 2f), -(blockSize / 2f), -(blockSize / 2f));
                        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( (blockSize / 2f), -(blockSize / 2f), -(blockSize / 2f));
                        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( (blockSize / 2f), -(blockSize / 2f), (blockSize / 2f));
                        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-(blockSize / 2f), -(blockSize / 2f), (blockSize / 2f));

                        // Right face
                        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( (blockSize / 2f), -(blockSize / 2f), -(blockSize / 2f));
                        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( (blockSize / 2f), (blockSize / 2f), -(blockSize / 2f));
                        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( (blockSize / 2f), (blockSize / 2f), (blockSize / 2f));
                        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( (blockSize / 2f), -(blockSize / 2f), (blockSize / 2f));

                        // Left Face
                        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-(blockSize / 2f), -(blockSize / 2f), -(blockSize / 2f));
                        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-(blockSize / 2f), -(blockSize / 2f), (blockSize / 2f));
                        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-(blockSize / 2f), (blockSize / 2f), (blockSize / 2f));
                        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-(blockSize / 2f), (blockSize / 2f), -(blockSize / 2f));

                    gl.glEnd();

                    gl.glPopMatrix();
                }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        printBenchmarks();
    }

    static class VoxelEngineKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            printBenchmarks();
            if(e.getKeyChar() == KeyEvent.VK_ESCAPE) {
                benchmarks.get(0).clear();
                benchmarks.get(1).clear();
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }
}