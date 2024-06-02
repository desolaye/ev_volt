package com.ev_volt.models;

import javafx.beans.DefaultProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

@DefaultProperty("children")
public class Oscilloscope extends Pane {
    private final int DEFAULT_STEP = 50;
    private final int DEFAULT_POINTS = 100;
    private final double[] DASH_ARRAY = {2, 8};

    private final double[] TEMP_LIMITS = {20, 50};
    private final double[] VOLT_LIMITS = {500, 800};

    private final int PREF_WIDTH;
    private final int PREF_HEIGHT;
    private final double CENTER_Y;
    private final double CENTER_X;
    private final Font FONT;

    private final double offset = 11;
    private GraphicsContext backgroundContext;
    private GraphicsContext lineContext;

    private double[] data;
    private int dataIndex;
    private String selectedProperty;

    public Oscilloscope(int width, int height) {
        PREF_WIDTH = width;
        PREF_HEIGHT = height;
        FONT = Font.font(10);

        CENTER_Y = PREF_HEIGHT / 2.0;
        CENTER_X = PREF_WIDTH / 2.0;

        data = new double[DEFAULT_POINTS];
        dataIndex = 0;

        setPrefSize(width, height);
        initGraphics();
    }

    private void initGraphics() {
        Canvas backgroundCanvas = new Canvas(getPrefWidth(), getPrefHeight());
        Canvas lineCanvas = new Canvas(getPrefWidth(), getPrefHeight());

        backgroundContext = backgroundCanvas.getGraphicsContext2D();
        backgroundContext.setLineJoin(StrokeLineJoin.ROUND);
        backgroundContext.setLineCap(StrokeLineCap.BUTT);
        backgroundContext.setLineWidth(1);
        backgroundContext.setTextAlign(TextAlignment.RIGHT);

        lineContext = lineCanvas.getGraphicsContext2D();
        lineContext.setLineJoin(StrokeLineJoin.ROUND);
        lineContext.setLineCap(StrokeLineCap.BUTT);
        lineContext.setLineWidth(3.0);

        Pane ROOT_PANE = new Pane(backgroundCanvas, lineCanvas);
        ROOT_PANE.setMinSize(PREF_WIDTH, PREF_HEIGHT);

        getChildren().setAll(ROOT_PANE);
        drawBackground();
    }

    private void drawBackground() {
        backgroundContext.clearRect(0, 0, PREF_WIDTH, PREF_HEIGHT);
        backgroundContext.setFill(Color.WHITE);
        backgroundContext.fillRect(0, 0, PREF_WIDTH, PREF_HEIGHT);

        backgroundContext.setStroke(Color.BLACK);
        backgroundContext.setFill(Color.BLACK);
        backgroundContext.setFont(FONT);

        backgroundContext.setLineDashes(0);
        backgroundContext.strokeLine(CENTER_X, 0, CENTER_X, PREF_WIDTH);
        backgroundContext.strokeLine(0, CENTER_Y, PREF_HEIGHT, CENTER_Y);

        backgroundContext.setLineDashes(DASH_ARRAY);

        for (int i = DEFAULT_STEP; i < PREF_WIDTH; i += DEFAULT_STEP) {
            if (i == CENTER_X || i == CENTER_Y) continue;
            backgroundContext.strokeLine(i, -offset, i, PREF_WIDTH + offset);
            backgroundContext.strokeLine(-offset, i, PREF_HEIGHT + offset, i);
        }

        if (selectedProperty != null && selectedProperty.equals("TEMP")) {
            backgroundContext.fillText("20", CENTER_X - offset / 2, PREF_HEIGHT);
            backgroundContext.fillText("50", CENTER_X - offset / 2, offset);
        }

        if (selectedProperty != null && selectedProperty.equals("VOLT")) {
            backgroundContext.fillText("500", CENTER_X - offset / 2, PREF_HEIGHT);
            backgroundContext.fillText("800", CENTER_X - offset / 2, offset);
        }

        backgroundContext.fillText("0", offset, CENTER_Y + offset);
        backgroundContext.fillText("5", PREF_WIDTH - offset, CENTER_Y + offset);
    }

    private void drawLine() {
        lineContext.setLineWidth(3);
        lineContext.setStroke(Color.GREEN);
        int step = PREF_WIDTH / DEFAULT_POINTS;

        for (int i = 0; i < DEFAULT_POINTS; i++) {
            if (data[i] == 0.0) break;
            if (i < 2) continue;

            if (selectedProperty != null && selectedProperty.equals("TEMP")) {
                lineContext.strokeLine((i - 1) * step,
                        paramToPx(data[i - 2], TEMP_LIMITS),
                        i * step,
                        paramToPx(data[i - 1], TEMP_LIMITS)
                );
            }

            if (selectedProperty != null && selectedProperty.equals("VOLT")) {
                lineContext.strokeLine((i - 1) * step,
                        paramToPx(data[i - 2], VOLT_LIMITS),
                        i * step,
                        paramToPx(data[i - 1], VOLT_LIMITS)
                );
            }
        }
    }

    private double paramToPx(double param, double[] limits) {
        double ratio = PREF_HEIGHT / (limits[1] - limits[0]);
        return PREF_HEIGHT - (param - limits[0]) * ratio;
    }

    public void addDataPoint(double point) {
        if (dataIndex >= DEFAULT_POINTS) {
            dataIndex = 0;
            data[dataIndex] = point;
        } else {
            data[dataIndex] = point;
            dataIndex++;
        }

        lineContext.clearRect(0, 0, PREF_WIDTH, PREF_HEIGHT);
        drawLine();
    }

    public void setSelectedProperty(String pr) {
        selectedProperty = pr;
        dataIndex = 0;
        data = new double[100];
        drawBackground();
    }
}
