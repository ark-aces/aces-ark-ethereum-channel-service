package com.arkaces.ark_eth_channel_service.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteUtils {

    public static byte[] intToBytesNoLeadingZeros(int num) {
        byte[] bytes = ByteBuffer.allocate(4).putInt(num).array();
        int firstNonZeroIndex = findFirstNonZeroIndex(bytes);
        if (firstNonZeroIndex == -1) {
            return new byte[]{0};
        }
        return Arrays.copyOfRange(bytes, firstNonZeroIndex, bytes.length);
    }

    public static byte[] floatToBytesNoLeadingZeros(float num) {
        byte[] bytes = ByteBuffer.allocate(4).putFloat(num).array();
        int firstNonZeroIndex = findFirstNonZeroIndex(bytes);
        if (firstNonZeroIndex == -1) {
            return new byte[]{0};
        }
        return Arrays.copyOfRange(bytes, firstNonZeroIndex, bytes.length);
    }

    public static byte[] longToBytesNoLeadingZeros(long num) {
        byte[] bytes = ByteBuffer.allocate(8).putLong(num).array();
        int firstNonZeroIndex = findFirstNonZeroIndex(bytes);
        if (firstNonZeroIndex == -1) {
            return new byte[]{0};
        }
        return Arrays.copyOfRange(bytes, firstNonZeroIndex, bytes.length);
    }

    public static byte[] doubleToBytesNoLeadingZeros(double num) {
        byte[] bytes = ByteBuffer.allocate(8).putDouble(num).array();
        int firstNonZeroIndex = findFirstNonZeroIndex(bytes);
        if (firstNonZeroIndex == -1) {
            return new byte[]{0};
        }
        return Arrays.copyOfRange(bytes, firstNonZeroIndex, bytes.length);
    }

    private static int findFirstNonZeroIndex(byte[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != 0) {
                return i;
            }
        }
        return -1;
    }
}
