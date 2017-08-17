package com.visionvera.bmob.activity.plan.camera.opengl;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * @param <T>
 * @author wbin 实现.net BitConverter的部分功能
 */
public class BitConverter {
    private static boolean IsLittleEndian = true;

    /**
     * 从输入流中当前位置读取一个整型
     */
    public static int ToInt(InputStream stream) {
        return ToInt(GetBytes(stream, 4));
    }

    /**
     * 将字节数组转换成整型
     */
    public static int ToLong(byte[] bytes) {
        byte[] bs = bytes;
        if (IsLittleEndian)

            return (bs[0] | (bs[1] << 8) | (bs[2] << 16) | (bs[3] << 24) | (bs[4] << 32) | (bs[5] << 40) | (bs[6] << 48) | (bs[7] << 56));

        else
            return (bytes[3] & 0xff) | ((bytes[2] << 8) & 0xff00) | ((bytes[1] << 24) >> 8) | (bytes[0] << 24);
    }

    /**
     * 将字节数组转换成整型
     */
    public static int ToInt(byte[] bytes) {

        if (IsLittleEndian)
            return (bytes[0] & 0xff) | ((bytes[1] << 8) & 0xff00) | ((bytes[2] << 24) >> 8) | (bytes[3] << 24);
        else
            return (bytes[3] & 0xff) | ((bytes[2] << 8) & 0xff00) | ((bytes[1] << 24) >> 8) | (bytes[0] << 24);
    }

    /**
     * 将字节数组转换成short
     */
    public static short ToShort(byte[] bytes) {
        if (IsLittleEndian)
            return (short) ((bytes[0] & 0xff) | ((bytes[1] << 8) & 0xff00));
        else
            return (short) ((bytes[1] & 0xff) | ((bytes[0] << 8) & 0xff00));
    }

    /**
     * 将输入流转换成字符串
     */
    public static String ToString(InputStream stream, int length) {
        return ToString(GetBytes(stream, length));
    }

    /**
     * 将字节数组转换成字符串
     */
    public static String ToString(byte[] value) {
        try {
            return new String(value, "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);

        }
    }

    /**
     * 将字节数组转换成字符串
     */
    public static String ToString(byte[] value, int offset, int length) {
        try {
            return new String(value, offset, length, "UTF8");
            // return new String(value, "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);

        }
    }

    /**
     * 将整型转换成字节数组
     */
    public static byte[] GetBytes(int value) {
        byte[] buff = new byte[4];
        if (IsLittleEndian) {
            buff[0] = (byte) ((value >> 0));
            buff[1] = (byte) ((value >> 8));
            buff[2] = (byte) ((value >> 16));
            buff[3] = (byte) ((value >> 24));
        } else {
            buff[0] = (byte) ((value >> 24));
            buff[1] = (byte) ((value >> 16));
            buff[2] = (byte) ((value >> 8));
            buff[3] = (byte) ((value >> 0));
        }
        return buff;
    }

    /**
     * 将整型转换成字节数组
     */
    public static byte[] GetBytes(long value) {
        byte[] buff = new byte[8];
        if (IsLittleEndian) {
            buff[0] = (byte) ((value >> 0));
            buff[1] = (byte) ((value >> 8));
            buff[2] = (byte) ((value >> 16));
            buff[3] = (byte) ((value >> 24));
            buff[4] = (byte) ((value >> 32));
            buff[5] = (byte) ((value >> 40));
            buff[6] = (byte) ((value >> 48));
            buff[7] = (byte) ((value >> 56));
        } else {
            buff[0] = (byte) ((value >> 56));
            buff[1] = (byte) ((value >> 48));
            buff[2] = (byte) ((value >> 40));
            buff[3] = (byte) ((value >> 32));
            buff[4] = (byte) ((value >> 24));
            buff[5] = (byte) ((value >> 16));
            buff[6] = (byte) ((value >> 8));
            buff[7] = (byte) ((value >> 0));
        }
        return buff;
    }

    /**
     * 将short转换成字节数组
     */
    public static byte[] GetBytes(short value) {
        byte[] buff = new byte[2];
        if (IsLittleEndian) {
            buff[0] = (byte) ((value >> 0));
            buff[1] = (byte) ((value >> 8));
        } else {

            buff[1] = (byte) ((value >> 8));
            buff[0] = (byte) ((value >> 0));
        }
        return buff;
    }

    /**
     * 将字符串转换成字节数组 ，使用UTF-8编码
     */
    public static byte[] GetBytes(String value) {
        byte[] result = new byte[0];
        if (value != null) {
            try {
                result = value.getBytes("UTF8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    /**
     * 从输入流获取指定字节的数组
     */
    private static byte[] GetBytes(InputStream stream, int length) {
        byte[] bytes = new byte[length];
        try {
            if (stream.available() >= length) {
                stream.read(bytes, 0, length);
            } else {
                throw new Exception("");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }

    private static char GetHexValue(int i) {
        if (i < 10) {
            return (char) (i + 0x30);
        }
        return (char) ((i - 10) + 0x41);
    }
}