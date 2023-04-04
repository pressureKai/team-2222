package com.jiangtai.count.util;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to encode en decode BCD numbers
 */
public class BCD {
    private BCD () {

    }

    private static Pattern BCD_PATTERN = Pattern.compile("[0-9]+");

    /**
     * Encode string with numbers (decimal) to BCD encoded bytes (big endian)
     * @param value Number that needs to be converted
     * @return BCD encoded number
     * @throws IllegalArgumentException if input is not a number
     */
    public static byte[] encode(String value) {
        if (!BCD_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Can only encode numerical strings");
        }

        final byte[] bcd = new byte[(value.length() + 1) / 2];
        int i, j;
        if (value.length() % 2 == 1) {
            bcd[0] = (byte) (value.codePointAt(0) & 0xf);
            i = 1;
            j = 1;
        } else {
            i = 0;
            j = 0;
        }
        for ( ; i < bcd.length; i++, j+= 2) {
            bcd[i] = (byte) ( ((value.codePointAt(j) & 0xf) << 4) | (value.codePointAt(j + 1) & 0xf) );
        }
        return bcd;
    }

    /**
     * Encode value to BCD encoded bytes (big endian)
     * @param value number
     * @return BCD encoded number
     * @throws IllegalArgumentException if input is negative
     */
    public static byte[] encode(BigInteger value) {
        if (value.signum() == -1) {
            throw new IllegalArgumentException("Only non-negative values are supported");
        }
        if (value.bitLength() > 63) {
            return encode(value.toString());
        } else {
            return encode(value.longValue());
        }
    }

    /**
     * Encode value to BCD encoded bytes (big endian) in a byte array of a specific length
     * @param value number
     * @param length length of the byte array
     * @return BCD encoded number
     * @throws IllegalArgumentException if input is negative or does not fit in byte array
     */
    public static byte[] encode(BigInteger value, int length) {
        if (value.signum() == -1) {
            throw new IllegalArgumentException("Only non-negative values are supported");
        } else if (value.bitLength() > length * 8) {
            throw new IllegalArgumentException("Value does not fit in byte array of length" + length);
        }
        if (value.bitLength() > 63) {
            return encode(String.format("%0" + (length * 2) + "d", value));
        } else {
            return encode(value.longValue(), length);
        }
    }

    /**
     * Encode value to BCD encoded bytes (big endian) in a byte array of a specific length
     * @param value number
     * @param length length of the byte array
     * @return BCD encoded number
     * @throws IllegalArgumentException if input is negative or does not fit in byte array
     */
    public static byte[] encode(long value, int length) {
        if (value < 0) {
            throw new IllegalArgumentException("Only non-negative values are supported");
        } else if (value == 0) {
            return new byte[length];
        }
        final byte[] bcd = new byte[length];

        for (int i = bcd.length - 1; i >= 0; i--) {
            int b = (int) (value % 10);
            value /= 10;
            b |= (value % 10) << 4;
            value /= 10;
            bcd[i] = (byte) b;
        }
        if (value != 0) {
            throw new IllegalArgumentException("Value does not fit in byte array of length " + length);
        }

        return bcd;
    }

    /**
     * Encode value to BCD encoded bytes (big endian)
     * @param value number
     * @return BCD encoded number
     * @throws IllegalArgumentException if input is negative
     */
    public static byte[] encode(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Only non-negative values are supported");
        } else if (value == 0) {
            return new byte[1];
        }
        final int length = (int) Math.log10(value) + 1;
        return encode(value, (length + 1) / 2);
    }

    /**
     * Decodes BCD encoded bytes to BigInteger
     * @param bcd BCD encoded bytes
     * @return encoded number
     * @throws IllegalArgumentException if an illegal byte is detected
     */
    public static BigInteger decode(byte[] bcd) {
        BigInteger value = BigInteger.ZERO;
        for (int i =  0; i < bcd.length; i++) {
            final int high = ((int) bcd[i] & 0xff) >> 4;
            final int low = (int) bcd[i] & 0xf;

            if (high > 10 || low > 10)
                throw new IllegalArgumentException(String.format("Illegal byte %x%x at %d", high, low, i));

            value = value.multiply((BigInteger.TEN)).add(BigInteger.valueOf(high));
            value = value.multiply((BigInteger.TEN)).add(BigInteger.valueOf(low));
        }

        return value;
    }

    /**
     * Decodes BCD encoded bytes directly to a decimal string
     * @param bcd BCD encoded bytes
     * @param stripLeadingZero strip leading zero if value is of odd length
     * @return encoded number as String
     * @throws IllegalArgumentException if an illegal byte is detected
     */
    public static String decodeAsString(byte[] bcd, boolean stripLeadingZero) {
        final StringBuilder buf = new StringBuilder(bcd.length * 2);
        for (int i = 0; i < bcd.length; i++) {
            final int high = ((int) bcd[i] & 0xff) >> 4;
            final int low = (int) bcd[i] & 0xf;

            if (high > 10 || low > 10)
                throw new IllegalArgumentException(String.format("Illegal byte %x%x at %d", high, low, i));
            buf.append((char) (0x30 | high));
            buf.append((char) (0x30 | low));
        }
        return stripLeadingZero && buf.charAt(0) == '0' ? buf.substring(1) : buf.toString();
    }




    /**
     * 将十进制字符串转换为 BCD编码
     * @param str
     * @return
     */
    public static byte[] String_BCD(String str) {
        byte[] s = str.getBytes();
        byte[] b = new byte[s.length/2];
        for(int i=0;i<b.length;i++) {
            b[i] = (byte) (s[2*i] << 4 | (s[2*i+1] & 0xf));
        }
        return b;
    }
    /**
     * 将BCD编码的byte数组转换为String
     * @param bcd
     * @return
     */
    public static String BCD_String(byte[] bcd) {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<bcd.length;i++) {
            sb.append(bcd[i]>>4&0xf)
                    .append(bcd[i]&0xf);
        }
        return sb.toString();
    }
    public static byte[] AcsToBcd(String asc) {

        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;

        for (int p = 0; p < asc.length()/2; p++) {
            if ( (abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            }
            else if ( (abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            }
            else {
                j = abt[2 * p] - 'A' + 0x0a;
            }

            if ( (abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            }
            else if ( (abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            }
            else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
//
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }



    public static byte[] ByteToBCD(byte[] in)
    {
        byte[] out = new byte[in.length * 2];
        int tmp;
        for(int i=0; i< in.length * 2; i++)
        {
            tmp = (in[i >> 1 ] >> ((i + 1) % 2)*4) & 0x0f ;

            switch(tmp)
            {
                case 10: out[i] = 'A'; break;
                case 11: out[i] = 'B'; break;
                case 12: out[i] = 'C'; break;
                case 13: out[i] = 'D'; break;
                case 14: out[i] = 'E'; break;
                case 15: out[i] = 'F'; break;
                default :
                    out[i] = (byte)(tmp|0x30);
            }
        }

        return out;
    }
}
