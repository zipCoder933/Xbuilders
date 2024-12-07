package com.xbuilders.engine.utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;


public class FileUtils {
    public final static boolean canRecycleFiles = Desktop.getDesktop().isSupported(Desktop.Action.MOVE_TO_TRASH);

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

}
