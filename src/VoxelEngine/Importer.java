package VoxelEngine;

import org.jnbt.NBTInputStream;
import org.jnbt.Tag;

import java.io.*;

public class Importer {

    Chunk chunk;  // TODO when initializing, give the chunk a custom TerrainGenerator
    File[] mcaFiles;

    Importer(String directory) {
        mcaFiles = (new File(directory)).listFiles((dir, name) -> {
            return name.endsWith(".mca");
        });
    }

    Chunk processFiles() {

        for(File file : mcaFiles) {

            System.out.println(file.getAbsolutePath());
//            try {
//                NBTInputStream inputStream = new NBTInputStream(new FileInputStream(file), false);
//                Tag tag = inputStream.readTag();
//                System.out.println(tag.getName());
//            } catch (IOException e) {
//                // should never happen, we got the file out of the directory
//                // if it does happen, it's because the file has been deleted after reading it for the first time
//                e.printStackTrace();
//            }

            // TODO remove me
            break;
        }

        return chunk;
    }
}
