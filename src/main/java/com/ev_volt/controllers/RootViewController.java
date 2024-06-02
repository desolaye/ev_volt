package com.ev_volt.controllers;

import com.ev_volt.App;
import com.ev_volt.models.ComPortDevice;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

    public class RootViewController implements Initializable {
    private static final SimpleBooleanProperty isDeviceConnected = new SimpleBooleanProperty(false);
    private static ComPortDevice device = null;
    @FXML
    private VBox tabView;
    @FXML
    private Button mainViewButton, batteryViewButton, oscilloscopeViewButton;

    public static ComPortDevice getComPortDevice() {
        return device;
    }

    public static void setComPortDevice(ComPortDevice newDevice) {
        device = newDevice;
        isDeviceConnected.set(newDevice != null);
    }

    @FXML
    private void onMainViewButtonClick() {
        setCurrentTab("main-tab-view");
    }

    @FXML
    private void onBatteryViewButtonClick() {
        setCurrentTab("battery-tab-view");
    }

    @FXML
    private void onOscilloscopeViewButtonClick() {
        setCurrentTab("oscilloscope-tab-view");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setCurrentTab("main-tab-view");

        isDeviceConnected.addListener((observable, oldValue, newValue) -> {
            setTabsStatus(!newValue);
        });
    }

    private void setCurrentTab(String tabName) {
        String selectedTabStyle = "-fx-background-color: #ff7f00;" +
                "-fx-effect: innershadow(gaussian , rgba(0,0,0,0.7), 8,0,0,1);" +
                "-fx-text-fill: #ffffff;";

        String tabStyle = "-fx-background-color: #F4F4F4;" +
                "-fx-effect: dropshadow(gaussian , rgba(0,0,0,0.7), 8,0,0,1);";

        try {
            tabView.getChildren().clear();
            tabView.getChildren().add(App.loadFXML(tabName));

            mainViewButton.setStyle(tabStyle);
            batteryViewButton.setStyle(tabStyle);
            oscilloscopeViewButton.setStyle(tabStyle);

            if (Objects.equals(tabName, "main-tab-view")) mainViewButton.setStyle(selectedTabStyle);
            if (Objects.equals(tabName, "battery-tab-view")) batteryViewButton.setStyle(selectedTabStyle);
            if (Objects.equals(tabName, "oscilloscope-tab-view")) oscilloscopeViewButton.setStyle(selectedTabStyle);
        } catch (IOException e) {
            System.out.println("Error while setting new tab");
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
    }

    private void setTabsStatus(boolean b) {
        batteryViewButton.setDisable(b);
        oscilloscopeViewButton.setDisable(b);
    }
}
