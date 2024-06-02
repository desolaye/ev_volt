package com.ev_volt.controllers;

import com.ev_volt.models.ComPortDevice;
import com.ev_volt.models.Transforms;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class BatteryTabViewController implements Initializable {
    @FXML // Set Inputs
    private TextField batteryAmperInput, ambientTempInput, minAllowedTempInput, maxAllowedTempInput;

    @FXML // Battery labels
    private Label ambientTempLabel, amperBatteryLabel, voltBatteryLabel, percentBatteryLabel,
            minAllowedTempLabel, maxAllowedTempLabel;

    @FXML
    private Button refreshCellsParamsButton, refreshBatteryParamsButton;

    @FXML // Cells Labels
    private Label cellCountLabel, avgCellTempLabel, avgCellVoltLabel, minCellVoltLabel, minCellNumberVoltLabel,
            maxCellVoltLabel, maxCellNumberVoltLabel;

    @FXML
    private void onRefreshBatteryParams() {
        ComPortDevice device = RootViewController.getComPortDevice();

        // Ambient temp (C)
        device.sendCommand((short)0x1f55, (short)2, (short)0, (short)0, (short)0, 0);
        sleep();
        ambientTempLabel.setText(device.getSystemInfo().split(":")[3]);

        // Battery ampers (A)
        device.sendCommand((short)0x1f55, (short)1, (short)0, (short)0, (short)0, 0);
        sleep();
        amperBatteryLabel.setText(device.getSystemInfo().split(":")[3]);

        // Battery volts (V)
        device.sendCommand((short)0x1f55, (short)8, (short)0, (short)0, (short)0, 0);
        sleep();
        voltBatteryLabel.setText(device.getSystemInfo().split(":")[3]);

        // Battery charge (%)
        device.sendCommand((short)0x1f55, (short)9, (short)0, (short)0, (short)0, 0);
        sleep();
        percentBatteryLabel.setText(device.getSystemInfo().split(":")[3]);

        // Min alarm temp (C)
        device.sendCommand((short)0x1f55, (short)12, (short)0, (short)0, (short)0, 0);
        sleep();
        minAllowedTempLabel.setText(device.getSystemInfo().split(":")[3]);

        // Max alarm temp (C)
        device.sendCommand((short)0x1f55, (short)11, (short)0, (short)0, (short)0, 0);
        sleep();
        maxAllowedTempLabel.setText(device.getSystemInfo().split(":")[3]);

        // Timestamp (ms)
        device.sendCommand((short)0x1f55, (short)14, (short)0, (short)0, (short)0, 0);
        sleep();
        System.out.println(device.getSystemInfo());
    }

    @FXML
    private void onRefreshCellsParams() {
        ComPortDevice device = RootViewController.getComPortDevice();

        // Total Number of cells
        device.sendCommand((short)0x1f55, (short)0, (short)0, (short)0, (short)0, 0);
        sleep();
        cellCountLabel.setText(device.getSystemInfo().split(":")[0]);

        // Avg temp of cells (C)
        device.sendCommand((short)0x1f55, (short)4, (short)0, (short)0, (short)0, 0);
        sleep();
        avgCellTempLabel.setText(device.getSystemInfo().split(":")[3]);

        // Avg volts of cells (V)
        device.sendCommand((short)0x1f55, (short)5, (short)0, (short)0, (short)0, 0);
        sleep();
        avgCellVoltLabel.setText(device.getSystemInfo().split(":")[3]);

        // Min volts (V) on cell
        device.sendCommand((short)0x1f55, (short)7, (short)0, (short)0, (short)0, 0);
        sleep();
        String[] minVolts = device.getSystemInfo().split(":");
        minCellVoltLabel.setText(minVolts[3]);
        minCellNumberVoltLabel.setText(minVolts[0]);

        // Max volts (V) on cell
        device.sendCommand((short)0x1f55, (short)6, (short)0, (short)0, (short)0, 0);
        sleep();
        String[] maxVolts = device.getSystemInfo().split(":");
        maxCellVoltLabel.setText(maxVolts[3]);
        maxCellNumberVoltLabel.setText(maxVolts[0]);
    }

    @FXML
    private void onRefreshAlarms() { }

    @FXML
    private void onResetAlarms() {
        ComPortDevice device = RootViewController.getComPortDevice();
        device.sendCommand((short)0x1e55, (short)5, (short)0, (short)0, (short)0, 0);
        sleep();
    }

    @FXML
    private void onSetBatteryAmper() {
        ComPortDevice device = RootViewController.getComPortDevice();

        try {
            float input = Float.parseFloat(batteryAmperInput.getText());
            device.sendCommand((short)0x1e55, (short)1, (short)0, (short)0, (short)0, Transforms.floatToInt(input));
            sleep();
        } catch (RuntimeException e) {
            batteryAmperInput.clear();
            batteryAmperInput.setStyle("-fx-background-color: #ffd1d1");
        }
    }

    @FXML
    private void onSetAmbientTemp() {
        ComPortDevice device = RootViewController.getComPortDevice();

        try {
            float input = Float.parseFloat(ambientTempInput.getText());
            device.sendCommand((short)0x1e55, (short)2, (short)0, (short)0, (short)0, Transforms.floatToInt(input));
            sleep();
        } catch (RuntimeException e) {
            ambientTempInput.clear();
            ambientTempInput.setStyle("-fx-background-color: #ffd1d1");
        }
    }

    @FXML
    private void onSetMinAllowedTemp() {
        ComPortDevice device = RootViewController.getComPortDevice();

        try {
            float input = Float.parseFloat(minAllowedTempInput.getText());
            device.sendCommand((short)0x1e55, (short)4, (short)0, (short)0, (short)0, Transforms.floatToInt(input));
            sleep();
        } catch (RuntimeException e) {
            minAllowedTempInput.clear();
            minAllowedTempInput.setStyle("-fx-background-color: #ffd1d1");
        }
    }

    @FXML
    private void onSetMaxAllowedTemp() {
        ComPortDevice device = RootViewController.getComPortDevice();

        try {
            float input = Float.parseFloat(maxAllowedTempInput.getText());
            device.sendCommand((short)0x1e55, (short)3, (short)0, (short)0, (short)0, Transforms.floatToInt(input));
            sleep();
        } catch (RuntimeException e) {
            maxAllowedTempInput.clear();
            maxAllowedTempInput.setStyle("-fx-background-color: #ffd1d1");
        }
    }

    @FXML
    private void onBatteryAmperInputClick() { batteryAmperInput.setStyle(""); }

    @FXML
    private void onAmbientTempInputClick() { ambientTempInput.setStyle(""); }

    @FXML
    private void onMinAllowedTempInputClick() { minAllowedTempInput.setStyle(""); }

    @FXML
    private void onMaxAllowedTempInputClick() { maxAllowedTempInput.setStyle(""); }

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    private void sleep() {
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
