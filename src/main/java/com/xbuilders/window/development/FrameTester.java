package com.xbuilders.window.development;

import com.xbuilders.window.utils.preformance.Stopwatch;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FrameTester extends JFrame {
    private JEditorPane editorPane;
    private final Stopwatch stopwatch = new Stopwatch();
    long lastUpdate = 0;
    private int updateTimeMS = 500;

    public void setUpdateTimeMS(int updateTimeMS) {
        this.updateTimeMS = updateTimeMS;
    }

    private boolean started = false;
    private final Map<String, TimeList> processList = new LinkedHashMap<>();
    private final Map<String, Long> counterList = new LinkedHashMap<>();
    boolean frameStarted = false;

    //For the current period, a period ends when the updateStatus() method is called
    private long processTimeNanos = 0;
    private int periodFrameCount = 0;
    private long periodStartNanos = 0;


    public void setStarted(boolean started) {
        this.started = started;
        if (started) {
            startStopButton.setText("Stop");
        } else {
            startStopButton.setText("Start");
        }
    }

    static class TimeList {
        public long totalTime = 0;
    }


    public void startFrame() {
        if (started) {
            if (System.currentTimeMillis() - lastUpdate > updateTimeMS) {
                updateStatus();
                lastUpdate = System.currentTimeMillis();
            }

            stopwatch.start();
            periodFrameCount++;
//            System.out.println("\n\nstart frame");
        } else if (frameStarted) {
            //Finish up if it was stopped
            processList.clear();
            counterList.clear();
        }
        frameStarted = started;
    }

    public void startProcess() {
        if (frameStarted) {
            stopwatch.start();
        }
    }

    public void count(String name, int count) {
        if (frameStarted) {
            if (!counterList.containsKey(name)) {
                counterList.put(name, 0L);
            }
            counterList.put(name, counterList.get(name) + count);
        }
    }

    public void set(String name, int value) {
        if (frameStarted) {
            if (!counterList.containsKey(name)) {
                counterList.put(name, 0L);
            }
            counterList.put(name, (long)value);
        }
    }


    public void endProcess(String... names) {
        endProcess(String.join(" > ", names));
    }

    public void endProcess(String name) {
        if (frameStarted) {
            stopwatch.calculateElapsedTime();

            if (!processList.containsKey(name)) {
                processList.put(name, new TimeList());
            }
            processList.get(name).totalTime += stopwatch.getElapsedNanoseconds();
            processTimeNanos += stopwatch.getElapsedNanoseconds();

//            System.out.println("process " + name + ", time " + stopwatch.getElapsedMicroseconds());
            stopwatch.start();
        }
    }

    public static String formatTime(long nanoseconds) {
        if (nanoseconds < 1_000) {
            return nanoseconds + " ns"; // nanoseconds
        } else if (nanoseconds < 1_000_000) {
            return String.format("%.0f μs", nanoseconds / 1_000.0); // microseconds with 3 decimal places
        } else if (nanoseconds < 1_000_000_000) {
            return String.format("%.0f ms", nanoseconds / 1_000_000.0); // milliseconds with 3 decimal places
        } else {
            return String.format("%.0f s", nanoseconds / 1_000_000_000.0); // seconds with 3 decimal places
        }
    }

    private final String startHtml = "<html><style>" +
            "table, th, td {border: 1px solid black;}" +
            ".bar{width: 80px; height: 10px;background-color: #ddd; }" +
            ".bar div{background-color: blue; height: 10px;}</style><body><b>This period:</b>";


    private void updateStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append(startHtml);

        long periodTimeNanos = System.nanoTime() - periodStartNanos;
        periodStartNanos = System.nanoTime();
        sb.append("<br>Total Period Time: ").append(formatTime(periodTimeNanos));
        sb.append("<br>Total Process Time: ").append(formatTime(processTimeNanos));
        sb.append("<br>Total frames: ").append(periodFrameCount);

        sb.append("<table><tr>" +
                "<th>Name</th>" +
                "<th>time/frame</th>" +
                "<th>total time</th>" +
                "<th>Total %</th></tr>");

        for (HashMap.Entry<String, TimeList> entry : processList.entrySet()) {
            String name = entry.getKey();
            String color = "blue";
            if (name.startsWith("red ")) {
                color = "red";
                name = name.replaceFirst("red", "");
            }if (name.startsWith("green ")) {
                color = "green";
                name = name.replaceFirst("green", "");
            }if (name.startsWith("black ")){
                color = "black";
                name = name.replaceFirst("black", "");
            }


            long totalTime = entry.getValue().totalTime; //The total time this period
            long averageTime = entry.getValue().totalTime / periodFrameCount; //The average time per frame
            double usagePercent = (double) totalTime / processTimeNanos; //The percentage of the total time used
            entry.getValue().totalTime = 0;

            sb.append("<tr><td>")
                    .append(name).append("</td><td>")
                    .append(formatTime(averageTime)).append("/frame</td><td>")
                    .append(formatTime(totalTime)).append("</td><td>");

            //Usage bar
            sb.append("<div class=\"bar\">" +
                            "<div style=\"width: ")
                    .append(usagePercent * 100).append("%;")
                    .append("background-color: " + color + ";\"></div>" +
                            "</div>");

            sb.append("</td></tr>");
        }
        sb.append("</table>");

        //Tally the count
        if (counterList.size() > 0) {
            sb.append("<p>Counters/Values:</p><table><tr><th>Name</th><th>Value</th></tr>");
            for (HashMap.Entry<String, Long> entry : counterList.entrySet()) {
                String name = entry.getKey();
                long count = entry.getValue();
                sb.append("<tr><td>").append(name).append("</td><td>").append(count).append("</td></tr>");
            }
            sb.append("</table>");
        }

        sb.append("</body></html>");

        processTimeNanos = 0;
        periodFrameCount = 0;

        editorPane.setText(sb.toString());
    }


    JButton startStopButton;

    public FrameTester(String title) {
        // Create the JEditorPane
        editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);

        startStopButton = new JButton("Start");
        startStopButton.addActionListener(e -> {
            started = !started;
            setStarted(started);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startStopButton);
        add(buttonPanel, BorderLayout.NORTH);

        setTitle(title + " (Frame Tester)");

        // Initialize with some HTML content
        editorPane.setText("<html><body></body></html>");

        // Add the editor pane to a scroll pane (optional, but provides scrolling)
        JScrollPane scrollPane = new JScrollPane(editorPane);
        add(scrollPane, BorderLayout.CENTER);

        // Set up the JFrame
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Run the frame creation on the Swing event dispatch thread
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
        });
    }

    public static void main(String[] args) {
        FrameTester tester = new FrameTester("Test");

        while (true) {
            try {
                tester.startFrame();
                Thread.sleep(1);
                tester.endProcess("1ms a");
                Thread.sleep(1);
                tester.endProcess("1ms b");
                Thread.sleep(20);
                tester.endProcess("2000ms");
                Thread.sleep(1);
                tester.endProcess("1ms c");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
