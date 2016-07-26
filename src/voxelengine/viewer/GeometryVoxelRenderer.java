package voxelengine.viewer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import voxelengine.Block;
import voxelengine.Chunk;
import voxelengine.World;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

class GeometryVoxelRenderer implements VoxelRenderer {

    private World world;

    private HashMap<Block.BlockType, Integer[]> textures = new HashMap<>();
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Matrix4f modelMatrix;
    private Vector3f viewerPosition;
    private float aspect;

    private int pId;
    private int vaoId;
    private int vboId;
    private int iboId;
    private float[] vertexArray;
    private int[] indexArray;
    private FloatBuffer vertexBuffer;
    private IntBuffer indexBuffer;

    GeometryVoxelRenderer(World world, float aspect) {
        this.aspect = aspect;
        this.world = world;
        createCapabilities();
        glClearColor(.1f, .1f, .1f, 0);
        glEnable(GL_DEPTH_TEST);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        refreshMatrices();
        try { loadTextures(); }
        catch (IOException e) {
            System.err.println("There is an error loading textures. Are they in the right directory?");
            e.printStackTrace();
        }
        initBuffers();
        pId = RenderUtils.setupShader("./src/voxelengine/viewer/gshader");

        loop();
    }

    private void refreshMatrices() {
        projectionMatrix = new Matrix4f().setPerspective(1, aspect, 0.1f, 25);
        viewerPosition = new Vector3f(4, 4, -4);
        viewMatrix = new Matrix4f().lookAt(viewerPosition, new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
        modelMatrix = new Matrix4f().identity();
    }

    private void loadTextures() throws IOException {

        for(Block.BlockType type : Block.BlockType.values()) {
            textures.put(type, new Integer[6]);
            String[] files = type.getTextureFiles();
            for(int i = 0; i < 6; i++)
                textures.get(type)[i] = RenderUtils.loadTexture(ImageIO.read(new File(files[i])));
        }
    }

    private void initBuffers() {
        // creates the buffer in the main memory
        vaoId = glGenVertexArrays();
        vboId = glGenBuffers();
        iboId = glGenBuffers();

        // creates the buffer in the graphic card
        glBindVertexArray(vaoId);

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
    }

    private void updateBuffers() {
        vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        indexBuffer = BufferUtils.createIntBuffer(indexArray.length);

        // clears the buffer, assigns the new data
        vertexBuffer.clear();
        vertexBuffer.put(vertexArray);
        vertexBuffer.flip();

        indexBuffer.clear();
        indexBuffer.put(indexArray);
        indexBuffer.flip();

        // loads the new data into the graphic card
        glBindVertexArray(vaoId);

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STREAM_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STREAM_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
    }


    private void loop() {
        while(!VoxelEngine.getViewer().frame()) {

            int sideLength = (2 * World.VIEW_DISTANCE + 1) * (Chunk.CHUNK_SIZE + 1);
            vertexArray = new float[sideLength * sideLength * sideLength * 3];
            indexArray = new int[sideLength * sideLength * sideLength];

            int vertexPointer = 0;
            Chunk[][][] chunks = world.getVisibleChunks();
            for(int cx = 0; cx < chunks.length; cx++) {
                for(int cy = 0; cy < chunks[0].length; cy++) {
                    for(int cz = 0; cz < chunks[0].length; cz++) {
                        int chunkX = cx - World.VIEW_DISTANCE;
                        int chunkY = cy - World.VIEW_DISTANCE;
                        int chunkZ = cz - World.VIEW_DISTANCE;
                        Chunk currentChunk = chunks[cx][cy][cz];
                        if (currentChunk != null) {

                            Block[][][] chunkBlocks = currentChunk.getEntireChunk();

                            float blockSize = Chunk.GRID_SIZE / 1000;  // convert to meters
                            float halfBlockSize = blockSize / 2f;

                            for(short x = 0; x < Chunk.CHUNK_SIZE; x++)
                                for(short y = 0; y < Chunk.CHUNK_SIZE; y++)
                                    for(short z = 0; z < Chunk.CHUNK_SIZE; z++) {

                                        Block current = chunkBlocks[x][y][z];
                                        if(current.isEmtpy() || current.isHidden()) continue;

                                        vertexArray[vertexPointer++] = x * blockSize + halfBlockSize + chunkX * Chunk.CHUNK_SIZE * blockSize;
                                        vertexArray[vertexPointer++] = y * blockSize + halfBlockSize + chunkY * Chunk.CHUNK_SIZE * blockSize;
                                        vertexArray[vertexPointer++] = z * blockSize + halfBlockSize + chunkZ * Chunk.CHUNK_SIZE * blockSize;
                                    }
                        }
                    }
                }
            }

            for(int c = 0; c < indexArray.length; c++)
                indexArray[c] = c;

            // draw
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            updateBuffers();
            glUseProgram(pId);
            glBindTexture(GL_TEXTURE_2D, textures.get(Block.BlockType.STONE)[0]);

            glUniform2f(glGetUniformLocation(pId, "tex_size"), 16, 16);
            FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
            glUniformMatrix4fv(glGetUniformLocation(pId, "projectionMatrix"), false, projectionMatrix.get(matrixBuffer));
            glUniformMatrix4fv(glGetUniformLocation(pId, "viewMatrix"), false, viewMatrix.get(matrixBuffer));
            glUniformMatrix4fv(glGetUniformLocation(pId, "modelMatrix"), false, modelMatrix.get(matrixBuffer));
            glUniform3f(glGetUniformLocation(pId, "viewerPosition"), viewerPosition.x, viewerPosition.y, viewerPosition.z);
            FloatBuffer vecBuffer = BufferUtils.createFloatBuffer(4);
            float s = (float) Chunk.GRID_SIZE / 1000 / 2;
            glUniform4fv(glGetUniformLocation(pId, "blf"), (new Vector4f(-s, -s,  s, 0).mul(modelMatrix).mul(viewMatrix).mul(projectionMatrix)).get(vecBuffer));
            glUniform4fv(glGetUniformLocation(pId, "blb"), (new Vector4f(-s, -s, -s, 0).mul(modelMatrix).mul(viewMatrix).mul(projectionMatrix)).get(vecBuffer));
            glUniform4fv(glGetUniformLocation(pId, "brf"), (new Vector4f( s, -s,  s, 0).mul(modelMatrix).mul(viewMatrix).mul(projectionMatrix)).get(vecBuffer));
            glUniform4fv(glGetUniformLocation(pId, "brb"), (new Vector4f( s, -s, -s, 0).mul(modelMatrix).mul(viewMatrix).mul(projectionMatrix)).get(vecBuffer));
            glUniform4fv(glGetUniformLocation(pId, "tlf"), (new Vector4f(-s,  s,  s, 0).mul(modelMatrix).mul(viewMatrix).mul(projectionMatrix)).get(vecBuffer));
            glUniform4fv(glGetUniformLocation(pId, "tlb"), (new Vector4f(-s,  s, -s, 0).mul(modelMatrix).mul(viewMatrix).mul(projectionMatrix)).get(vecBuffer));
            glUniform4fv(glGetUniformLocation(pId, "trf"), (new Vector4f( s,  s,  s, 0).mul(modelMatrix).mul(viewMatrix).mul(projectionMatrix)).get(vecBuffer));
            glUniform4fv(glGetUniformLocation(pId, "trb"), (new Vector4f( s,  s, -s, 0).mul(modelMatrix).mul(viewMatrix).mul(projectionMatrix)).get(vecBuffer));

            glBindVertexArray(vaoId);
            glEnableVertexAttribArray(0);
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);

            glDrawElements(GL_POINTS, indexArray.length, GL_UNSIGNED_INT, 0);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            glBindTexture(GL_TEXTURE_2D, 0);
            glDisableVertexAttribArray(0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
            glUseProgram(0);

            printFps();
        }
    }

    private int fps = 0;
    private long lastFPSUpdate = 0;
    private void printFps() {
        fps++;
        if(System.currentTimeMillis() - lastFPSUpdate >= 1000) {
            System.out.println("fps = " + fps);
            lastFPSUpdate = System.currentTimeMillis();
            fps = 0;
        }
    }
}
