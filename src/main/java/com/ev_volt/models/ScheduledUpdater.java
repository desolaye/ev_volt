package com.ev_volt.models;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class ScheduledUpdater extends ScheduledService<Void> {
    ComPortDevice device;
    int cmd;

    public ScheduledUpdater(ComPortDevice newDevice, String option) {
        device = newDevice;
        cmd = option.equals("TEMP") ? 4 : 8;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    device.sendCommand((short)0x1f55, (short)cmd, (short)0, (short)0, (short)0, 0);
                });
                sleep();
                return null;
            }
        };
    }

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
