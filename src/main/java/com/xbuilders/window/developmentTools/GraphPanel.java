package com.xbuilders.window.developmentTools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GraphPanel extends JPanel {

    private int padding = 25;
    private int labelPadding = 25;
    private Color lineColor = new Color(44, 102, 230, 180);
    private Color pointColor = new Color(100, 100, 100, 180);
    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private int pointWidth = 4;

    private int numberYDivisions = 8;
    private int numberXDivisions = 10;

    public final List<Double> dataPoints = new ArrayList<>();

    public void update() {
        invalidate();
        this.repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        try {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (dataPoints.size() - 1);
            double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (getMaxScore() - getMinScore());

            List<Point> graphPoints = new ArrayList<>();
            for (int i = 0; i < dataPoints.size(); i++) {
                int x1 = (int) (i * xScale + padding + labelPadding);
                int y1 = (int) ((getMaxScore() - dataPoints.get(i)) * yScale + padding);
                graphPoints.add(new Point(x1, y1));
            }

            // draw white background
            g2.setColor(Color.WHITE);
            g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
            g2.setColor(Color.BLACK);

            // create hatch marks and grid lines for y axis.
            for (int i = 0; i < numberYDivisions + 1; i++) {
                int x0 = padding + labelPadding;
                int x1 = pointWidth + padding + labelPadding;
                int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
                int y1 = y0;
                if (dataPoints.size() > 0) {
                    g2.setColor(gridColor);
                    g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
                    g2.setColor(Color.BLACK);
                    String yLabel = ((int) ((getMinScore() + (getMaxScore() - getMinScore()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
                    FontMetrics metrics = g2.getFontMetrics();
                    int labelWidth = metrics.stringWidth(yLabel);
                    g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
                }
                g2.drawLine(x0, y0, x1, y1);
            }

            // and for x axis
            for (int i = 0; i < dataPoints.size(); i++) {
                if (dataPoints.size() > 1) {
                    int x0 = i * (getWidth() - padding * 2 - labelPadding) / (dataPoints.size() - 1) + padding + labelPadding;
                    int x1 = x0;
                    int y0 = getHeight() - padding - labelPadding;
                    int y1 = y0 - pointWidth;
                    if ((i % ((int) ((dataPoints.size() / numberXDivisions)) + 1)) == 0) {
                        g2.setColor(gridColor);
                        g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
                        g2.setColor(Color.BLACK);
                        String xLabel = i + "";
                        FontMetrics metrics = g2.getFontMetrics();
                        int labelWidth = metrics.stringWidth(xLabel);
                        g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                    }
                    g2.drawLine(x0, y0, x1, y1);
                }
            }

            // create x and y axes
            g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
            g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);

            Stroke oldStroke = g2.getStroke();
            g2.setColor(lineColor);
            g2.setStroke(GRAPH_STROKE);
            for (int i = 0; i < graphPoints.size() - 1; i++) {
                int x1 = graphPoints.get(i).x;
                int y1 = graphPoints.get(i).y;
                int x2 = graphPoints.get(i + 1).x;
                int y2 = graphPoints.get(i + 1).y;
                g2.drawLine(x1, y1, x2, y2);
            }

            g2.setStroke(oldStroke);
            g2.setColor(pointColor);
            for (int i = 0; i < graphPoints.size(); i++) {
                int x = graphPoints.get(i).x - pointWidth / 2;
                int y = graphPoints.get(i).y - pointWidth / 2;
                int ovalW = pointWidth;
                int ovalH = pointWidth;
                g2.fillOval(x, y, ovalW, ovalH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean useYBounds = false;
    double minBound = 0.0;
    double maxBound = 1.0;

    public void setYBounds(double minScore, double maxScore) {
        this.minBound = minScore;
        this.maxBound = maxScore;
        useYBounds = true;
    }

    public void dontUseYBounds() {
        useYBounds = false;
    }

    private double getMinScore() {
        if (useYBounds) {
            return minBound;
        }
        double minScore = Double.MAX_VALUE;
        for (Double score : dataPoints) {
            minScore = Math.min(minScore, score);
        }
        return minScore;
    }

    private double getMaxScore() {
        if (useYBounds) {
            return maxBound;
        }
        double maxScore = Double.MIN_VALUE;
        for (Double score : dataPoints) {
            if (score != null) maxScore = Math.max(maxScore, score);
        }
        return maxScore;
    }

    public void addDataPoint(double score) {
        dataPoints.add(score);
    }

    public void addDataPoint(double score, int maxDataPoints) {
        if (dataPoints.size() > maxDataPoints) dataPoints.remove(0);
        dataPoints.add(score);
    }


    public static void main(String[] args) {
        GraphPanel mainPanel = new GraphPanel();

        mainPanel.setPreferredSize(new Dimension(800, 600));
        JFrame frame = new JFrame("DrawGraph");
        mainPanel.setVisible(true);
        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        List<Double> scores = mainPanel.dataPoints;
        Random random = new Random();
        int maxScore = 10;
        (new Thread() {
            public void run() {
                while (true) {
                    scores.add((double) random.nextDouble() * maxScore);
                    if (scores.size() > 100) scores.remove(0);
                    mainPanel.update();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();


    }
}