package com.ev_volt.models;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author vt
 */
public class Transforms {

    static final String HEX_STR = ("0123456789abcdef");

    /**
     * целое в массив байтов (1 к 4, младший байт вперед)
     */
    public static byte[] intToBytes(int vv) {
        byte[] out = new byte[4];
        for (int i = 0; i < 4; i++) {
            out[i] = (byte) vv;
            vv >>= 8;
        }
        return out;
    }

    /**
     * целое в массив байтов (1 к 2, младший байт вперед)
     */
    public static byte[] shortToBytes(short vv) {
        byte[] out = new byte[2];
        for (int i = 0; i < 2; i++) {
            out[i] = (byte) vv;
            vv >>= 8;
        }
        return out;
    }

    /**
     * массив целых в массив байтов (1 к 4, младший байт вперед)
     */
    public static byte[] intToBytes(int[] vv) {
        return intToBytes(vv, vv.length);
    }

    public static byte[] intToBytes(int[] vv, int sz) {
        byte[] out = new byte[sz * 4];
        int k = 0;
        for (int i = 0; i < sz; i++) {
            int zz = vv[i];
            for (int j = 0; j < 4; j++) {
                out[k++] = (byte) zz;
                zz >>= 8;
            }
        }
        return out;
    }

    //старшим вперед
    public static byte[] intToBytesBE(int vv) {
        byte[] out = new byte[4];
        int k = 3;
        for (int j = 0; j < 4; j++) {
            out[k--] = (byte) vv;
            vv >>= 8;
        }

        return out;
    }

    //старшим вперед
    public static byte[] shortToBytesBE(short vv) {
        byte[] out = new byte[2];
        int k = 1;
        for (int i = 0; i < 2; i++) {
            out[k--] = (byte) vv;
            vv >>= 8;
        }
        return out;
    }

    public static byte[] shortToBytes(short[] vv) {
        return shortToBytes(vv, vv.length);
    }

    public static byte[] shortToBytes(short[] vv, int sz) {
        byte[] out = new byte[sz * 2];
        int k = 0;
        for (int i = 0; i < sz; i++) {
            short zz = vv[i];
            for (int j = 0; j < 2; j++) {
                out[k++] = (byte) zz;
                zz >>= 8;
            }
        }
        return out;
    }

    /**
     * массив байтов в целое (4 к 1, младший байт вперед)
     */
    public static int bytesToInt4(byte[] zz) {
        int vv = 0;
        for (int j = 3; j >= 0; j--) {
            vv <<= 8;
            vv |= (zz[j] & 0x0FF);
        }
        return vv;
    }

    /**
     * два байта в целое
     */
    public static int twoBytesToInt(byte lo, byte hi) {
        int vv = hi;
        vv <<= 8;
        vv |= (lo & 0x0FF);
        return vv;
    }

    //перевод байтов в целое младшим вперед
    public static int bytesToInt(byte[] data) {
        int out = 0;
        for (int i = ((data.length > 4) ? 3 : data.length - 1); i >= 0; i--) {
            out <<= 8;
            out |= (data[i] & 0x0FF);
        }
        return out;
    }

    /**
     * массив байтов в массив целых (4 к 1, младший байт вперед)
     */
    public static int[] bytesToIntArray(byte[] zz) {
        int[] out = new int[zz.length / 4];
        for (int i = 0; i < out.length; i++) {
            int vv = 0;
            int k = i * 4;
            for (int j = 3; j >= 0; j--) {
                vv <<= 8;
                vv |= (zz[k + j] & 0x0FF);
            }
            out[i] = vv;
        }
        return out;
    }

    /**
     * массив байтов в массив целых (4 к 1, старший байт вперед)
     */
    public static int[] bytesToIntArrayBE(byte[] zz) {
        int[] out = new int[zz.length / 4];
        for (int i = 0; i < out.length; i++) {
            int vv = 0;
            int k = i * 4;
            vv = ((zz[k] & 0x0ff) << 24 | (zz[k + 1] & 0x0ff) << 16 | (zz[k + 2] & 0x0ff) << 8 | zz[k + 3] & 0x0ff);
            out[i] = vv;
        }
        return out;
    }

    /**
     * массив байтов в массив целых (4 к 1, старший байт вперед)
     */
    public static String byteToString(byte zz) {
        String out = "";
        for (int i = 0; i < 8; i++) {
            int vv = 0;
            if (((zz >> (7 - i)) & 1) == 1) {
                out = out + 1;
            } else {
                out = out + 0;
            }

        }
        return out;
    }

    /**
     * массив байтов в массив целых (4 к 1, старший байт вперед)
     */
    public static int stringToInt(String st) {
        int nn = st.length();
        if (nn == 0) return 0;
        if (nn > 8) return 0xffffffff;
        String norm = st.toLowerCase();
        int out = 0;

        for (int i = 0; i < nn; i++) {
            out = out << 4;
            int vv = HEX_STR.indexOf(norm.charAt(i));

            if (vv == -1) {
                out = 0;
                break;
            }

            out = out | vv;
        }

        return out;
    }

    public static int floatToInt(float vv) {
        return (Float.floatToIntBits(vv));
    }

    /**
     * Вычисление CRC Modbus
     */
    public static int CRC(byte[] mes) {
        int crc = 0xFFFF;

        for (int pos = 0; pos < mes.length; pos++) {
            crc ^= (int) mes[pos] & 0xFF;   // XOR byte into least sig. byte of crc

            for (int i = 8; i != 0; i--) {    // Loop over each bit
                if ((crc & 0x0001) != 0) {      // If the LSB is set
                    crc >>= 1;                    // Shift right and XOR 0xA001
                    crc ^= 0xA001;
                } else                            // Else LSB is not set
                    crc >>= 1;                    // Just shift right
            }
        }

        return crc;
    }

    public static byte[] appendCRC(byte[] mes) {
        int crc = CRC(mes);
        byte[] out = new byte[mes.length + 2];
        System.arraycopy(mes, 0, out, 0, mes.length);
        out[mes.length] = (byte) (crc & 0x000000ff);
        out[mes.length + 1] = (byte) ((crc >> 8) & 0x000000ff);
        return out;
    }

    public static byte ASCII_filter(byte inb) {
        byte outb = '.';
        if ((inb > 0x1F) && (inb < 0x7F)) outb = inb;
        return outb;
    }
}
