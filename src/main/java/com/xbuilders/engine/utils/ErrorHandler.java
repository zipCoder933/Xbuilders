/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author zipCoder933
 */
public class ErrorHandler {

    private static final String localDir = new File("").getAbsolutePath();
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");

    public static void handleFatalError(Throwable ex) {
        String exMsg = ex.getMessage();
        if (exMsg == null) {
            exMsg = "Unknown error.";
        }
        handleFatalError("Runtime error", exMsg, ex,true);
        saveErrorToLogFile(ex, "unnamed error");
    }

    public static void handleFatalError(String message, Throwable ex) {
        handleFatalError("Runtime error", message, ex,true);
        saveErrorToLogFile(ex, message);
    }

    public static void handleFatalError(String title, String body, Throwable ex,boolean terminate) {
        String message = ex.getMessage();
        if (message == null) {
            message = "Unknown error";
        }

        createPopupWindow(title, "<h3>" + title + "</h3>"
                + "" + body + "\n\n\n<span style='color: #888888; font-size: 0.95em;'>"
                + "<b>ERROR INFO:</b> " + message + "\n(" + ex.getClass() + ")\n\n"
                + "<b>Stack trace:</b>\n" + Arrays.toString(ex.getStackTrace()).replace(",", "\n") + "</span>");
        saveErrorToLogFile(ex, "##" + title + "##\t" + body);
     if(terminate)   Runtime.getRuntime().exit(1);
    }

    public static void createPopupWindow(String title, String str) {
        final JFrame parent = new JFrame();
        JLabel label = new JLabel("");
        label.setText("<html><body style='padding:5px;'>" + str.replace("\n", "<br>") + "</body></html>");
        label.setFont(label.getFont().deriveFont(12f));
        label.setVerticalAlignment(JLabel.TOP);
        parent.add(label);
        parent.pack();
        parent.getContentPane().setBackground(Color.white);
        parent.setVisible(true);
        parent.pack();
        parent.setTitle(title);
        parent.setLocationRelativeTo(null);
        parent.setAlwaysOnTop(true);
        parent.setVisible(true);
        parent.setSize(350, 200);
    }

    /**
     * Prints the stack trace and saves the error to log file.
     *
     * @param ex the throwable
     * @param devMessage
     */
    public static void saveErrorToLogFile(Throwable ex, String devMessage) {
        String date = dateFormat.format(new Date());

        System.err.println("\nError: \""+devMessage+"\"");

        if (ex != null) {
            System.err.println("STACK TRACE:");
            ex.printStackTrace();
        }

        File logFile = new File(localDir, "error logs\\log_" + date + ".txt");

        if (!devMessage.isBlank()) {
            devMessage = devMessage.length() > 50 ? devMessage.substring(0, 50) : devMessage;
            logFile = new File(localDir, "error logs\\" + devMessage.replaceAll("[^\\w\\.]", "_") + "\\log_" + date + ".txt");
        }

        if (!logFile.getParentFile().exists()) {
            logFile.getParentFile().mkdirs();
        }

        String errorStr = "Developer Message: \t" + devMessage + "\n";

        if (ex != null) {
            if (ex.getMessage() == null) {
                errorStr = "Developer Message: \t" + devMessage + "\n"
                        + "Class: \t" + ex.getClass() + "\n\n"
                        + "Stack trace:\n" + Arrays.toString(ex.getStackTrace()).replace(",", "\n");
            } else {
                errorStr = "Message: \t" + ex.getMessage() + "\n"
                        + "Developer Message: \t" + devMessage + "\n"
                        + "Class: \t" + ex.getClass() + "\n\n"
                        + "Stack trace:\n" + Arrays.toString(ex.getStackTrace()).replace(",", "\n");
            }
        }

        try {
            Files.writeString(logFile.toPath(), errorStr);
        } catch (IOException ex1) {
        }
    }

    public static void saveErrorToLogFile(Throwable throwable) {
        saveErrorToLogFile(throwable, "unnamed exception");
    }
}
