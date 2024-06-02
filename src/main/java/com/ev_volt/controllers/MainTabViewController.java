package com.ev_volt.controllers;

import com.ev_volt.models.ComPortDevice;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import jssc.SerialPortList;

import java.net.URL;
import java.util.ResourceBundle;

public class MainTabViewController implements Initializable {
    private final ObservableList<String> comPorts = FXCollections.observableArrayList();

    @FXML
    private Label portLabel, verFWLabel, verSNLabel;
    @FXML
    private ListView<String> portList;
    @FXML
    private Label selectedPort;
    @FXML
    private Button connectPortButton, versionRequestButton;

    @FXML
    private void onVersionRequest() {
        ComPortDevice device = RootViewController.getComPortDevice();
        portLabel.setText(device.getPortName());

        device.sendCommand((short)0x0155, (short) 0, (short) 0, (short) 0, (short) 0, 0);
        String[] versions = device.getSystemInfo().split(":");
        verFWLabel.setText(versions[0]);
        verSNLabel.setText(versions[1]);
    }

    @FXML
    private void onConnectPort() {
        ComPortDevice device = RootViewController.getComPortDevice();

        if (device == null) {
            RootViewController.setComPortDevice(new ComPortDevice(selectedPort.getText()));
            connectPortButton.setText("Отключиться");
            versionRequestButton.setDisable(false);
        } else {
            device.close();
            connectPortButton.setText("Подключиться");
            versionRequestButton.setDisable(true);
            RootViewController.setComPortDevice(null);
        }
    }

    @FXML
    private void onRefreshPortList() { comPorts.setAll(SerialPortList.getPortNames()); }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ComPortDevice device = RootViewController.getComPortDevice();

        if (device != null) {
            selectedPort.setText(device.getPortName());
            connectPortButton.setText("Отключиться");
            versionRequestButton.setDisable(false);
            connectPortButton.setDisable(false);
        }

        portList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedPort.setText(newValue);
                connectPortButton.setDisable(false);
            } else {
                selectedPort.setText("Не выбран");
                connectPortButton.setDisable(true);
            }
        });

        comPorts.addListener((ListChangeListener<? super String>) change -> {
            portList.setItems(comPorts);
        });

        comPorts.setAll(SerialPortList.getPortNames());
    }
}
