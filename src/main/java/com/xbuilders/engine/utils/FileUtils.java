package com.xbuilders.engine.utils;

import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;


public class FileUtils {
    public final static boolean canRecycleFiles = Desktop.getDesktop().isSupported(Desktop.Action.MOVE_TO_TRASH);

    public static ByteBuffer fileToByteBuffer(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             FileChannel fileChannel = fis.getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocate((int) fileChannel.size());
            fileChannel.read(buffer);
            buffer.flip(); // Prepare for reading
            return buffer;
        }
    }

    public static void moveDirectoryToTrash(File directory) throws IOException {
        if (directory.isDirectory() && directory.exists()) {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().moveToTrash(directory);
                System.out.println("Directory moved to trash: " + directory.getAbsolutePath());
            } else {
                System.out.println("Desktop operations are not supported on this system.");
            }
        } else {
            System.out.println("The specified directory does not exist or is not a directory.");
        }
    }

    public static boolean fileIsInUse(File file) {
        boolean used;
        Channel channel = null;
        try {
            channel = new RandomAccessFile(file, "rw").getChannel();
            used = false;
        } catch (FileNotFoundException ex) {
            used = true;
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException ex) {
                    // exception handling
                }
            }
        }
        return used;
    }

}
