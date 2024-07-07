//package com.xbuilders.engine.world.chunk;
//
//import org.joml.Vector3i;
//
//import java.io.IOException;
//import java.util.HashMap;
//
//public class BlockGridSavingLoadingUtils {
//
//
//
//    private static final byte PIPE_BYTE = -127;
//    public void toBytes(XBFilterOutputStream out) throws IOException {
//        //Write the size first!
//        out.write(ChunkSavingLoadingUtils.shortToBytes(this.size.x));
//        out.write(ChunkSavingLoadingUtils.shortToBytes(this.size.y));
//        out.write(ChunkSavingLoadingUtils.shortToBytes(this.size.z));
//        for (int i = 0; i < this.blocks.length; ++i) {
//            out.write(ChunkSavingLoadingUtils.shortToBytes(this.blocks[i]));
//            int coords = getCoordsOfIndex(i);
//            if (blockData.containsKey(coords)) {
//                blockData.get(coords).write(out);
//            }
//            out.write(PIPE_BYTE);
//        }
//    }
//
//    public BlockGrid(byte[] bytes, int start, int end) {
//        this.size = new Vector3i();
//        //Read and load the size first!
//        this.size.x = ChunkSavingLoadingUtils.bytesToShort(bytes[start], bytes[start + 1]);
//        this.size.y = ChunkSavingLoadingUtils.bytesToShort(bytes[start + 2], bytes[start + 3]);
//        this.size.z = ChunkSavingLoadingUtils.bytesToShort(bytes[start + 4], bytes[start + 5]);
//
//        dataSize = size.x * size.y * size.z;
//        light = new byte[dataSize];
//
//        this.blocks = new short[size.x * size.y * size.z];
//        this.blockData = new HashMap<>();
//        if (size.x == 0 || size.y == 0 || size.z == 0) {
//            return;
//        }
//
////        System.out.println("Loading grid: " + Arrays.toString(subarray(bytes, start+6, end)));
//        int index = 0;
//        for (int i = start + 6; i < end;) {
//            //Count the number of bytes leading up to the next pipe
//            int count = 2;
//            while (bytes[i + count] != PIPE_BYTE) {
//                count++;
//            }
//            //print the section of the bytes using sublist
////            System.out.println("\tLoading block: " + Arrays.toString(subarray(bytes, i, i + count + 1)) + " index: " + index);
//            int coords = getCoordsOfIndex(index);
//            this.blocks[coords] = ChunkSavingLoadingUtils.bytesToShort(bytes[i], bytes[i + 1]);
//            i += 2;
//            if (count > 2) {
////                System.out.println("\t\tLoading block data: " + Arrays.toString(subarray(bytes, i, i + count - 2)));
//                byte[] bytes2 = new byte[count - 2];
//                System.arraycopy(bytes, i, bytes2, 0, count - 2);
//                this.blockData.put(coords, new BlockData(bytes2));
//            }
//            i += count - 1;
//            index++;
////                System.out.println("Loading block: " + ItemList.getBlock(this.blocks[coords]) + " data: " + this.blockData.get(coords));
//        }
//    }
//}
