/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils;

import com.xbuilders.game.Main;

import java.awt.Color;
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
 * @author zipCoder933
 */
public class ErrorHandler {

    private static final String localDir = new File("").getAbsolutePath();
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");

    public static void report(Throwable ex) {
        report("error", ex);
    }


    public static void report(String userMsg, Throwable ex) {
        String errMessage = (ex.getMessage() != null ? " \n(" + ex.getMessage() + ")" : "");
        Main.popupMessage.message("Runtime Error", userMsg + errMessage);
        log(ex, "##" + userMsg + "##\t" + errMessage);
    }


    /**
     * Prints the stack trace and saves the error to log file.
     *
     * @param ex         the throwable
     * @param devMessage
     */
    public static void log(Throwable ex, String devMessage) {
        String date = dateFormat.format(new Date());
        File logFile = new File(localDir, "error logs\\log_" + date + ".txt");

        if (!devMessage.isBlank()) {
            devMessage = devMessage.length() > 50 ? devMessage.substring(0, 50) : devMessage;
            logFile = new File(localDir, "error logs\\" + devMessage.replaceAll("[^\\w\\.]", "_") + "\\log_" + date + ".txt");
        }

        if (!logFile.getParentFile().exists()) {
            logFile.getParentFile().mkdirs();
        }

        String errorStr = "Message: \t" + devMessage + "\n";

        if (ex != null) {
            if (ex.getMessage() == null) {
                errorStr = "Message: \t" + devMessage + "\n"
                        + "Class: \t" + ex.getClass() + "\n\n"
                        + "Stack trace:\n" + Arrays.toString(ex.getStackTrace()).replace(",", "\n");
            } else {
                errorStr = "Message: \t" + ex.getMessage() + "\n"
                        + "Message: \t" + devMessage + "\n"
                        + "Class: \t" + ex.getClass() + "\n\n"
                        + "Stack trace:\n" + Arrays.toString(ex.getStackTrace()).replace(",", "\n");
            }
        }
        System.out.println(errorStr);
        try {
            Files.writeString(logFile.toPath(), errorStr);
        } catch (IOException ex1) {
        }
    }

    public static void log(Throwable throwable) {
        log(throwable, "unnamed exception");
    }
}
