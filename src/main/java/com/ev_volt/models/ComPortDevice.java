package com.ev_volt.models;

import jssc.SerialPort;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class ComPortDevice {
    private final SerialPort serialPort;
    int numOfBytes;
    private byte[] accu;
    String systemInfo = "";

    public ComPortDevice(String portName) {
        serialPort = new SerialPort(portName);

        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            SerialPortEventListener listener = event -> {
                int nn = event.getEventValue();

                if (event.isRXCHAR() && nn > 15) {
                    try {
                        getCommandResult(serialPort.readBytes(nn));
                    } catch (SerialPortException ex) {
                        System.out.printf("Serial port: " + ex);
                    }
                }
            };

            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            serialPort.addEventListener(listener, SerialPort.MASK_RXCHAR);
            systemInfo = "Port " + serialPort.getPortName() + " opened successfully";
        } catch (SerialPortException ex) {
            System.out.printf("Device: " + ex);
            systemInfo = "Device: " + ex;
        }
    }

    public void sendCommand(short cmd, short param1, short param2, short param3, short param4, int param5) {
        byte[] request = new byte[14];
        accu = new byte[16];

        System.arraycopy(Transforms.shortToBytes(cmd), 0, request, 0, 2);
        System.arraycopy(Transforms.shortToBytes(param1), 0, request, 2, 2);
        System.arraycopy(Transforms.intToBytes(param5), 0, request, 4, 4);
        System.arraycopy(Transforms.shortToBytes(param2), 0, request, 8, 2);
        System.arraycopy(Transforms.shortToBytes(param3), 0, request, 10, 2);
        System.arraycopy(Transforms.shortToBytes(param4), 0, request, 12, 2);

        System.out.print("Request -> ");
        for (byte i : request) System.out.printf("0x%x ", i);
        System.out.println(">");
        bytewrite(Transforms.appendCRC(request), 10);
    }

    public void getCommandResult(byte[] data) {
//        String aa = "";

        if ((data.length + numOfBytes) > accu.length) {
            System.out.printf("data.length=%d : 0x%x 0x%x 0x%x 0x%x\r\n", data.length, data[0], data[1], data[2], data[3]);
        } else {
            System.arraycopy(data, 0, accu, numOfBytes, data.length);
        }

        numOfBytes += data.length;

        if (numOfBytes == accu.length) {
//            for (byte b : accu) aa += (String.format("%02x ", b));

            if (Transforms.CRC(accu) == 0) {
                if (accu[0] == (byte) 0x55 && accu[1] == (byte) 0x01) getDeviceVersion(data);
                if (accu[0] == (byte) 0x55 && accu[1] == (byte) 0x1f) getBatteryState(data);
                if (accu[0] == 1 && accu[1] == (byte) 0x15) getOscilloscopeData(data);
            }
        }
    }

    public void bytewrite(byte[] mes, int timeout) {
        try {
            numOfBytes = 0;
            serialPort.readBytes();
            serialPort.writeBytes(mes);

            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ex) {
                System.out.print("Exception in bytewrite");
            }

            System.out.println("Read: " + numOfBytes);
            System.out.println("Not read: " + serialPort.getInputBufferBytesCount());
        } catch (SerialPortException ex) {
            System.out.printf("Serial port: " + ex);
        }
    }

    public void close() {
        try {
            serialPort.closePort();
            systemInfo = "Port " + serialPort.getPortName() + " closed successfully";
        } catch (SerialPortException ex) {
            System.out.printf("Serial port: " + ex);
            systemInfo = "Serial port: " + ex;
        }
    }

    private void getDeviceVersion(byte[] data) {
        int verFW = Transforms.twoBytesToInt(data[2], data[3]);
        int verSN = Transforms.twoBytesToInt(data[8], data[9]);
        systemInfo = verFW + ":" + verSN;
    }

    private void getBatteryState(byte[] data) {
        int code, prm1, prm2, prm3;
        float prm5;
        byte[] tmp = new byte[4];

        System.arraycopy(data, 4, tmp, 0, 4);
        prm5 = Float.intBitsToFloat(Transforms.bytesToInt4(tmp));

        code = Transforms.twoBytesToInt(data[2], data[3]);
        prm1 = Transforms.twoBytesToInt(data[8], data[9]);
        prm2 = Transforms.twoBytesToInt(data[10], data[11]);
        prm3 = Transforms.twoBytesToInt(data[12], data[13]);

        if (code == 14) {
            prm1 = Transforms.bytesToInt(new byte[]{data[10], data[11], data[12], data[13]});
        }

        System.out.println("code: " + code);
        System.out.println("params: " + prm1 + " : " + prm2 + " : " + prm3 + " : " + prm5);
        systemInfo = prm1 + ":" + prm2 + ":" + prm3 + ":" + prm5;
    }

    private void getOscilloscopeData(byte[] data) {
        String aa = "";
        for (byte b : data) aa += (String.format("%02x ", b));

        System.out.println("getOscilloscopeData: " + aa);
    }

    public String getPortName() { return serialPort.getPortName(); }

    public String getSystemInfo() { return systemInfo; }
}
