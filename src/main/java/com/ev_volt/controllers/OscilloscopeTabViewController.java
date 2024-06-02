package com.ev_volt.controllers;

import com.ev_volt.models.Oscilloscope;
import com.ev_volt.models.ScheduledUpdater;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class OscilloscopeTabViewController implements Initializable {
    private final SimpleBooleanProperty isMaking =  new SimpleBooleanProperty(false);
    private static ScheduledUpdater service = null;
    private Oscilloscope oscilloscope = new Oscilloscope(500, 500);
    private String[] choiceOptions = {"TEMP", "VOLT"};
    private String option = "";

    @FXML
    private Pane oscilloscopeContainer;

    @FXML
    private Button monitoringButton;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private void onMonitoringClick() {
        monitoringButton.setText(isMaking.get() ? "Старт" : "Стоп");
        isMaking.setValue(!isMaking.get());
    }

    public void onChoiceBoxSelect(ActionEvent event) {
        option = choiceBox.getValue();
        oscilloscope.setSelectedProperty(option);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        choiceBox.getItems().addAll(choiceOptions);
        choiceBox.setOnAction(this::onChoiceBoxSelect);
        oscilloscopeContainer.getChildren().setAll(oscilloscope);

        isMaking.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                service = new ScheduledUpdater(RootViewController.getComPortDevice(), option);
                service.setOnSucceeded(t -> {
                    String pointString = RootViewController.getComPortDevice().getSystemInfo().split(":")[3];
                    double point = Float.parseFloat(pointString);
                    oscilloscope.addDataPoint(point);
                });
                service.setPeriod(Duration.millis(100));
                service.start();
            }

            if (!newValue) service.cancel();

        });
    }

}
