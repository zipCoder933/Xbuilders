package com.xbuilders.game.blockTools.tools;

import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.FileDialog;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import com.xbuilders.engine.world.chunk.XBFilterOutputStream;
import com.xbuilders.engine.world.chunk.saving.ChunkSavingLoadingUtils;
import org.joml.Vector3i;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class PrefabUtils {


    public static void toBytes(ChunkVoxels blocks, OutputStream out) throws IOException {
        //Write the size first!
        out.write(ByteUtils.shortToBytes(blocks.size.x));
        out.write(ByteUtils.shortToBytes(blocks.size.y));
        out.write(ByteUtils.shortToBytes(blocks.size.z));

        for (int x = 0; x < blocks.size.x; x++) {
            for (int y = 0; y < blocks.size.y; y++) {
                for (int z = 0; z < blocks.size.z; z++) {
                    short block = blocks.getBlock(x, y, z);
                    out.write(ByteUtils.shortToBytes(block));
                    if (blocks.getBlockData(x, y, z) != null) {
                        out.write(blocks.getBlockData(x, y, z).toByteArray());
                    }
                    out.write(ChunkSavingLoadingUtils.NEWLINE_BYTE);
                }
            }
        }
    }

    public static ChunkVoxels fromBytes(byte[] bytes, int start, int end) {
        Vector3i size = new Vector3i(0, 0, 0);
        //Read and load the size first!
        size.x = ByteUtils.bytesToShort(bytes[start], bytes[start + 1]);
        size.y = ByteUtils.bytesToShort(bytes[start + 2], bytes[start + 3]);
        size.z = ByteUtils.bytesToShort(bytes[start + 4], bytes[start + 5]);

        if (size.x == 0 || size.y == 0 || size.z == 0) {
            return new ChunkVoxels(0, 0, 0);
        }

        ChunkVoxels data = new ChunkVoxels(size.x, size.y, size.z);


        int index = 0;
        for (int i = start + 6; i < end; ) {
            //Count the number of bytes leading up to the next pipe
            int count = 2;
            while (bytes[i + count] != ChunkSavingLoadingUtils.NEWLINE_BYTE) {
                count++;
            }

            Vector3i coords = data.getCoordsOfIndex(index);
            short block = (short) ByteUtils.bytesToShort(bytes[i], bytes[i + 1]);
            data.setBlock(coords.x, coords.y, coords.z, block);

            i += 2;
            if (count > 2) {
//                System.out.println("\t\tLoading block data: " + Arrays.toString(subarray(bytes, i, i + count - 2)));
                byte[] bytes2 = new byte[count - 2];
                System.arraycopy(bytes, i, bytes2, 0, count - 2);
                BlockData blockData = new BlockData(bytes2);
                data.setBlockData(coords.x, coords.y, coords.z, blockData);
            }
            i += count - 1;
            index++;
        }
        return data;
    }


    public static ChunkVoxels loadPrefabFromFile(File file) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        return fromBytes(bytes, 0, bytes.length);
    }

    public static void savePrefabToFile(ChunkVoxels data, File file) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        toBytes(data, baos);
        Files.write(file.toPath(), baos.toByteArray());
    }

    public static void
    loadPrefabFromFileDialog(Consumer<File> consumer) {
        FileDialog.fileDialog((fd) -> {
            fd.setDirectory(ResourceUtils.appDataResource("prefabs").getAbsolutePath());
            fd.setFilenameFilter(filter);
        }, consumer);
    }

    static FilenameFilter filter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".xbprefab");
        }
    };

    public static void savePrefabToFileDialog(ChunkVoxels data) {
        FileDialog.fileDialog((fd) -> {
            fd.setDirectory(ResourceUtils.appDataResource("prefabs").getAbsolutePath());
            fd.setFilenameFilter(filter);
        }, (file) -> {
            if (file == null) {
                return;
            }
            try {
                PrefabUtils.savePrefabToFile(data, file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
