package com.xbuilders.engine.utils;

import javax.swing.*;
import java.io.File;
import java.util.function.Consumer;

public class FileDialog {
    public static void fileDialog(Consumer<java.awt.FileDialog> setupConsumer, Consumer<File> chosenFile) {
        (new Thread(() -> {
            JFrame frame = new JFrame();
            java.awt.FileDialog fd = new java.awt.FileDialog(frame, "Choose a file", java.awt.FileDialog.LOAD);
            setupConsumer.accept(fd);
            fd.setVisible(true);
            String filename = fd.getFile();
            chosenFile.accept(new File(fd.getDirectory(), filename));
            frame.dispose();
        })).start();
    }
}
