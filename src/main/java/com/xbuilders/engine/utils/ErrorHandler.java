/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils;

import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.utils.resource.ResourceUtils;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @author zipCoder933
 */
public class ErrorHandler {

    public static void createPopupWindow(String title, String str) {
        final JFrame parent = new JFrame();
        JLabel label = new JLabel("");
        label.setText("<html><body style='padding:5px;'>" + str.replace("\n", "<br>") + "</body></html>");
//        label.setFont(new Font("Arial", 0, 12));
        label.setVerticalAlignment(JLabel.TOP);
        parent.add(label);
        parent.pack();
        parent.getContentPane().setBackground(Color.white);
        parent.setVisible(true);
        parent.pack();

        parent.setIconImage(popupWindowIcon.getImage());
        parent.setTitle(title);
        parent.setLocationRelativeTo(null);
        parent.setAlwaysOnTop(true);
        parent.setVisible(true);
        parent.setSize(380, 240);
    }

    private final static ImageIcon popupWindowIcon = new ImageIcon(ResourceUtils.file("logo.png").getAbsolutePath());
    private static final String localDir = new File("").getAbsolutePath();
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");

    public static void report(Throwable ex) {
        report("error", ex);
    }

    public static void report(String title, String details) {
        if (title == null || title.isBlank()) title = "Error";
        ClientWindow.popupMessage.message(title, details);
    }

    public static void report(String userMsg, Throwable ex) {
        String errMessage = (ex.getMessage() != null ? " \n(" + ex.getMessage() + ")" : "");
        if (userMsg == null || userMsg.isBlank()) userMsg = "Runtime Error!";

        ClientWindow.popupMessage.message(userMsg, errMessage + "\n(Content saved to clipboard)");
        log(ex, userMsg);
    }


    /**
     * Prints the stack trace and saves the error to log file.
     *
     * @param ex         the throwable
     * @param devMessage
     */
    public static void log(Throwable ex, String devMessage) {
        String errorStr;
        if (ex != null) {
            if (ex.getMessage() == null) {
                errorStr = "Message: \t" + devMessage + "\n"
                        + "Class: \t" + ex.getClass() + "\n\n"
                        + "Stack trace:\n" + Arrays.toString(ex.getStackTrace()).replace(",", "\n");
            } else {
                errorStr = "Message: \t" + devMessage + "\n"
                        + "Error: \t" + ex.getMessage() + "\n"
                        + "Class: \t" + ex.getClass() + "\n\n"
                        + "Stack trace:\n" + Arrays.toString(ex.getStackTrace()).replace(",", "\n");
            }
            System.out.println(errorStr);
            try {
                //Create log file directory if it doesn't exist
                saveLogToFile(devMessage, errorStr);
                //Copy to clipboard
                MiscUtils.setClipboard(errorStr);
            } catch (IOException ex1) {
            }
        }
    }

    private static File saveLogToFile(String devMessage, String errorStr) throws IOException {
        String date = dateFormat.format(new Date()).replace(":", "_");
        File logFile = ResourceUtils.localFile("error logs\\" + date + ".txt");
        if (!logFile.getParentFile().exists()) logFile.getParentFile().mkdirs();

        if (devMessage != null) errorStr = "Message: \t" + devMessage + "\n" + errorStr;
        Files.writeString(logFile.toPath(), errorStr);
        return logFile;
    }

    public static void log(Throwable throwable) {
        log(throwable, "unnamed exception");
    }
}
