package com.jiangtai.count.util;

/**
 * Created by heCunCun on 2021/3/15
 */
public class CrcUtil {
    public static short CRC_calcCrc8(short crcReg, short data) {
        short i;
        short xorFlag;
        short dcdBitMask = 0x80;
        short poly = 0x1021;
        for (i = 0; i < 8; i++) {
            xorFlag = (short) (crcReg & 0x8000);
            crcReg <<= 1;
            if (((data & dcdBitMask) == dcdBitMask)) {
                (crcReg) |= 1;
            }
            if (xorFlag != 0x00) {
                crcReg = (short) ((crcReg) ^ poly);
            }
            dcdBitMask >>>= 1;
        }
        return crcReg;
    }

    public static short Calculate_Crc(byte[] buf, int start, int end) {
        int i = 0;
        short crc = (short) 0xFFFF;
        for (i = start; i < end; i++) {
            crc = CRC_calcCrc8(crc, (short) (buf[i] & 0xFF));
        }

        return (short) (crc & 0xFFFF);
    }
}
