package com.xzj.babyfun.utility;

import java.util.BitSet;

public class BitSetConvert {
    
    public static byte[] bitSet2ByteArray(BitSet bitSet) {
        byte[] bytes = new byte[bitSet.size() / 16];
        for (int i = 0; i < bitSet.size()/2; i++) {
            int index = i / 8;
            int offset = 7 - i % 8;
            bytes[index] |= (bitSet.get(i) ? 1 : 0) << offset;
        }
        return bytes;
    }

    public static BitSet byteArray2BitSet(byte[] bytes) {
        BitSet bitSet = new BitSet(bytes.length * 8);
        int index = 0;
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 7; j >= 0; j--) {
                bitSet.set(index++, (bytes[i] & (1 << j)) >> j == 1 ? true
                        : false);
            }
        }
        return bitSet;
    }
    
    public static int getTimeValue(BitSet bSet, int start, int len) {
        // TODO Auto-generated method stub
        byte temp = 0;
        for (int i = start; i < start + len; i++) {
            if (bSet.get(i)) {
                temp |= (byte) ((temp | (1 << (len + start -i - 1))));
            } else {
                temp |= (byte) ((temp & (0 << (len + start -i - 1))));
            }
        }
        return (temp & 0xff);
    }
}
