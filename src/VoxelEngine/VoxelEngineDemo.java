package VoxelEngine;

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
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;

public class VoxelEngineDemo extends GLCanvas implements GLEventListener {

    public static String TITLE = "VoxelEngine Simple Demo";
    private static final int FPS = 60;
    protected static final float MOUSE_SPEED = .2f;
    protected JFrame frame;
    protected VoxelEngineKeyListener keyListener;

    protected static World world;

    public static void main(String[] args) {
        new VoxelEngineDemo();
    }


    private GLU glu;  // for the GL Utility

    // cameraLeftRight of rotation for the camera direction
    float cameraLeftRight = 0.0f, cameraUpDown = 0.0f;
    // XZ position of the camera
    float cameraX = 0.0f, cameraY = 5.0f, cameraZ = 0.0f;
    float viewDistance = 25f;
    float movementSpeed = .15f;

    private int[] textures = new int[2048];

    VoxelEngineDemo() {

        this.addGLEventListener(this);  // this class also serves as the renderer
        try {
            this.addMouseMotionListener(new VoxelEngineMouseMotionListener());
        } catch (AWTException e) {
            // fail, whatever
            System.out.println("failed to initialize mouse listener");
        }
        keyListener = new VoxelEngineKeyListener();
        this.addKeyListener(keyListener);
        world = new World();

        SwingUtilities.invokeLater(() -> {
            VoxelEngineDemo.this.setPreferredSize(new Dimension(1024, 720));
            frame = new JFrame();
            frame.getContentPane().add(VoxelEngineDemo.this);
            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            frame.getContentPane().setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                    cursorImg, new Point(0, 0), "blank cursor"));

            FPSAnimator animator = new FPSAnimator(VoxelEngineDemo.this, FPS, false);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    new Thread() {
                        @Override
                        public void run() {
                            if(animator.isStarted()) animator.stop();
                            VoxelEngineDemo.this.destroy();
                            VoxelEngineDemo.this.frame.setVisible(false);
                            VoxelEngineDemo.this.frame.dispose();
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
            textures[2047] = TextureIO.newTexture(new File("./block_textures/red.png"), true).getTextureObject();
        } catch (IOException e) {
            e.printStackTrace();
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
        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        updateCamera();

        Chunk[][] chunks = world.getVisibleChunks();

        for(int x = 0; x < chunks.length; x++) {
            for(int y = 0; y < chunks[0].length; y++) {
                int chunkX = x - chunks.length / 2;
                int chunkY = y - chunks[0].length / 2;
                drawChunk(gl, chunks[x][y], chunkX, chunkY);
            }
        }
    }

    protected void drawChunk(GL2 gl, Chunk chunk, int chunkX, int chunkY) {

        Block[][][] chunkBlocks = chunk.getEntireChunk();

        float blockSize = Chunk.GRID_SIZE / 1000;  // convert to meters
        float halfBlockSize = blockSize / 2f;

        for(short x = 0; x < Chunk.CHUNK_SIZE; x++)
            for(short y = 0; y < Chunk.CHUNK_SIZE; y++)
                for(short z = 0; z < Chunk.CHUNK_SIZE; z++) {

                    Block current = chunkBlocks[x][y][z];
                    if(current.isEmtpy() || current.isHidden()) continue;

                    gl.glPushMatrix();

                    gl.glTranslatef(
                            x + chunkX * Chunk.CHUNK_SIZE * blockSize + blockSize / 2f,
                            z                             * blockSize + blockSize / 2f,
                            y + chunkY * Chunk.CHUNK_SIZE * blockSize + blockSize / 2f);

                    // draw cube
                    gl.glEnable(GL_TEXTURE_2D);
                    gl.glEnable(GL_BLEND);
                    gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    gl.glBindTexture(GL_TEXTURE_2D, textures[current.getType().ID]);
                    gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                    gl.glBegin(GL_QUADS);

                    // TODO some sides should be rendered but aren't. Not sure which ones

                    // Front Face
                    if (!current.isHidden(Block.FRONT)) {
                        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-halfBlockSize, -halfBlockSize, halfBlockSize);
                        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( halfBlockSize, -halfBlockSize, halfBlockSize);
                        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( halfBlockSize, halfBlockSize, halfBlockSize);
                        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-halfBlockSize, halfBlockSize, halfBlockSize);
                    }

                    // Back Face
                    if (!current.isHidden(Block.BACK)) {
                        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-halfBlockSize, -halfBlockSize, -halfBlockSize);
                        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-halfBlockSize, halfBlockSize, -halfBlockSize);
                        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( halfBlockSize, halfBlockSize, -halfBlockSize);
                        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( halfBlockSize, -halfBlockSize, -halfBlockSize);
                    }

                    // Top Face
                    if (!current.isHidden(Block.TOP)) {
                        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-halfBlockSize, halfBlockSize, -halfBlockSize);
                        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-halfBlockSize, halfBlockSize, halfBlockSize);
                        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( halfBlockSize, halfBlockSize, halfBlockSize);
                        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( halfBlockSize, halfBlockSize, -halfBlockSize);
                    }

                    // Bottom Face
                    if (!current.isHidden(Block.BOTTOM)) {
                        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-halfBlockSize, -halfBlockSize, -halfBlockSize);
                        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( halfBlockSize, -halfBlockSize, -halfBlockSize);
                        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( halfBlockSize, -halfBlockSize, halfBlockSize);
                        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-halfBlockSize, -halfBlockSize, halfBlockSize);
                    }

                    // Right face
                    if (!current.isHidden(Block.RIGHT)) {
                        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( halfBlockSize, -halfBlockSize, -halfBlockSize);
                        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( halfBlockSize, halfBlockSize, -halfBlockSize);
                        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( halfBlockSize, halfBlockSize, halfBlockSize);
                        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( halfBlockSize, -halfBlockSize, halfBlockSize);
                    }

                    // Left Face
                    if (!current.isHidden(Block.LEFT)) {
                        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-halfBlockSize, -halfBlockSize, -halfBlockSize);
                        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-halfBlockSize, -halfBlockSize, halfBlockSize);
                        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-halfBlockSize, halfBlockSize, halfBlockSize);
                        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-halfBlockSize, halfBlockSize, -halfBlockSize);
                    }

                    gl.glEnd();

                    gl.glPopMatrix();
                }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    private void updateCamera() {
        glu.gluLookAt(cameraX, cameraZ, cameraY,
                cameraX + viewDistance * Math.sin(cameraLeftRight), cameraZ + viewDistance * Math.tan(cameraUpDown), cameraY + -1 * viewDistance * Math.cos(cameraLeftRight),
                0f, 1f, 0f);

        if(keyListener.keysDown.get(VoxelEngineKeyListener.FRONT)) {
            cameraX += movementSpeed * Math.sin(cameraLeftRight);
            cameraY -= movementSpeed * Math.cos(cameraLeftRight);
        }
        if(keyListener.keysDown.get(VoxelEngineKeyListener.BACK)) {
            cameraX -= movementSpeed * Math.sin(cameraLeftRight);
            cameraY += movementSpeed * Math.cos(cameraLeftRight);
        }
        if(keyListener.keysDown.get(VoxelEngineKeyListener.UP)) {
            cameraZ += movementSpeed;
        }
        if(keyListener.keysDown.get(VoxelEngineKeyListener.DOWN)) {
            cameraZ -= movementSpeed;
        }
        if(keyListener.keysDown.get(VoxelEngineKeyListener.LEFT)) {
            cameraX -= movementSpeed * Math.cos(cameraLeftRight);
            cameraY -= movementSpeed * Math.sin(cameraLeftRight);
        }
        if(keyListener.keysDown.get(VoxelEngineKeyListener.RIGHT)) {
            cameraX += movementSpeed * Math.cos(cameraLeftRight);
            cameraY += movementSpeed * Math.sin(cameraLeftRight);
        }
    }


    class VoxelEngineKeyListener implements KeyListener {

        static final int UP = 0;
        static final int BACK = 1;
        static final int RIGHT = 2;
        static final int FRONT = 3;
        static final int LEFT = 4;
        static final int DOWN = 5;
        public BitSet keysDown = new BitSet(6);

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            toggleKey(e.getKeyCode(), true);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            toggleKey(e.getKeyCode(), false);
        }

        private void toggleKey(int keyCode, boolean onOff) {
            switch (keyCode) {
                case KeyEvent.VK_W:
                    keysDown.set(FRONT, onOff);
                    break;
                case KeyEvent.VK_S:
                    keysDown.set(BACK, onOff);
                    break;
                case KeyEvent.VK_A:
                    keysDown.set(LEFT, onOff);
                    break;
                case KeyEvent.VK_D:
                    keysDown.set(RIGHT, onOff);
                    break;
                case KeyEvent.VK_SPACE:
                    keysDown.set(UP, onOff);
                    break;
                case KeyEvent.VK_SHIFT:
                    keysDown.set(DOWN, onOff);
                    break;
            }
        }
    }

    class VoxelEngineMouseMotionListener implements MouseMotionListener {

        private Robot robot;
        private int oldX = 0;
        private int oldY = 0;

        final static int THRESHOLD = 50;

        VoxelEngineMouseMotionListener() throws AWTException {
            robot = new Robot();
        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {

            if(Math.abs((e.getX() - oldX) / viewDistance) > 1 || Math.abs((e.getY() - oldY) / viewDistance) > 1){
                oldX = e.getX();
                oldY = e.getY();
                return;
            }

            cameraLeftRight += Math.asin((e.getX() - oldX) / viewDistance) * MOUSE_SPEED;
            cameraUpDown -= Math.asin((e.getY() - oldY) / viewDistance) * MOUSE_SPEED;
            if(cameraUpDown >= Math.PI / 2) {
                cameraUpDown = (float) (Math.PI / 2) - 0.001f;
            } else if(cameraUpDown <= -Math.PI / 2) {
                cameraUpDown = (float) (-Math.PI / 2) + 0.001f;
            }

            // edge reset to center if cursor is about to leave
            int width = getWidth();
            int height = getHeight();
            int mouseX = e.getX();
            int mouseY = e.getY();

            if(mouseX - THRESHOLD < 0 || mouseX + THRESHOLD > width || mouseY - THRESHOLD < 0 || mouseY + THRESHOLD > height) {
                robot.mouseMove(getLocationOnScreen().x + getWidth() / 2, getLocationOnScreen().y + getHeight() / 2);
            }

            oldX = e.getX();
            oldY = e.getY();
        }
    }
}