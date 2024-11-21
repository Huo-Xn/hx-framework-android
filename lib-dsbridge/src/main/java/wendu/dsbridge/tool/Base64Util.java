package wendu.dsbridge.tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Base64Util {

    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                //这个 方法 有 \n
//                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
                result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public static String file2Base64(File file) {
        if (file == null) {
            return null;
        }
        String base64 = null;
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            byte[] buff = new byte[fin.available()];
            fin.read(buff);
            base64 = Base64.encodeToString(buff, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return base64;
    }


    /**
     * base64转化为file流
     *
     * @param base64
     * @return
     */
    public static File base64ToFile(String base64, String url) {
        if (base64 == null || "".equals(base64)) {
            return null;
        }
        byte[] buff = decode(base64);
        File file = null;
        FileOutputStream out = null;
        try {
            file = new File(url);
            out = new FileOutputStream(file);
            out.write(buff);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }


    /**
     * 带有前缀的 base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64AddPrefix(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
//                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
                result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "data:image/jpeg;base64," + result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        Bitmap bm = null;
        if (!TextUtils.isEmpty(base64Data)) {
            if (base64Data.startsWith("data:")) {
                //这个base64是带着头的
                String[] split = base64Data.split(",");
                if (null != split && split.length >= 2) {
                    byte[] decode = Base64.decode(split[1], Base64.DEFAULT);
                    bm = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                }
            } else {
                //不带头
                byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
                bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        }
        return bm;
    }


    private static final byte[] ALPHABET = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] DECODABET = new byte[]{-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, -9, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, -9, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9};
    private static final byte WHITE_SPACE_ENC = -5;
    private static final byte EQUALS_SIGN_ENC = -1;
    private static final byte EQUALS_SIGN = 61;


    public static byte[] decode(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Input string was null.");
        } else {
            byte[] inBytes;
            try {
                inBytes = s.getBytes("US-ASCII");
            } catch (UnsupportedEncodingException var3) {
                inBytes = s.getBytes();
            }

            if (inBytes.length == 0) {
                return new byte[0];
            } else if (inBytes.length < 4) {
                throw new IllegalArgumentException("Base64-encoded string must have at least four characters, but length specified was " + inBytes.length);
            } else {
                return decodeBytes(inBytes);
            }
        }
    }

    public static String encode(byte[] source) {
        byte[] encoded = encodeBytesToBytes(source, source.length);

        try {
            return new String(encoded, "US-ASCII");
        } catch (UnsupportedEncodingException var3) {
            return new String(encoded);
        }
    }

    private static byte[] decodeBytes(byte[] pInBytes) {
        byte[] decodabet = DECODABET;
        int len34 = pInBytes.length * 3 / 4;
        byte[] outBuff = new byte[len34];
        int outBuffPosn = 0;
        byte[] b4 = new byte[4];
        int b4Posn = 0;
//        int i = 0;
//        byte sbiCrop = 0;
//        byte sbiDecode = 0;

        for (int i = 0; i < 0 + pInBytes.length; ++i) {
            byte sbiCrop = (byte) (pInBytes[i] & 127);
            byte sbiDecode = decodabet[sbiCrop];
            if (sbiDecode < -5) {
                throw new IllegalArgumentException(String.format("Bad Base64 input character '%d' in array position %d", pInBytes[i], i));
            }

            if (sbiDecode >= -1) {
                b4[b4Posn++] = sbiCrop;
                if (b4Posn > 3) {
                    outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn);
                    b4Posn = 0;
                    if (sbiCrop == 61) {
                        break;
                    }
                }
            }
        }

        byte[] out = new byte[outBuffPosn];
        System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
        return out;
    }

    private static int decode4to3(byte[] source, int srcOffset, byte[] destination, int destOffset) {
        verifyArguments(source, srcOffset, destination, destOffset);
        int outBuff;
        if (source[srcOffset + 2] == 61) {
            outBuff = (DECODABET[source[srcOffset]] & 255) << 18 | (DECODABET[source[srcOffset + 1]] & 255) << 12;
            destination[destOffset] = (byte) (outBuff >>> 16);
            return 1;
        } else if (source[srcOffset + 3] == 61) {
            outBuff = (DECODABET[source[srcOffset]] & 255) << 18 | (DECODABET[source[srcOffset + 1]] & 255) << 12 | (DECODABET[source[srcOffset + 2]] & 255) << 6;
            destination[destOffset] = (byte) (outBuff >>> 16);
            destination[destOffset + 1] = (byte) (outBuff >>> 8);
            return 2;
        } else {
            outBuff = (DECODABET[source[srcOffset]] & 255) << 18 | (DECODABET[source[srcOffset + 1]] & 255) << 12 | (DECODABET[source[srcOffset + 2]] & 255) << 6 | DECODABET[source[srcOffset + 3]] & 255;
            destination[destOffset] = (byte) (outBuff >> 16);
            destination[destOffset + 1] = (byte) (outBuff >> 8);
            destination[destOffset + 2] = (byte) outBuff;
            return 3;
        }
    }

    private static void verifyArguments(byte[] source, int srcOffset, byte[] destination, int destOffset) {
        if (source == null) {
            throw new IllegalArgumentException("Source array was null.");
        } else if (destination == null) {
            throw new IllegalArgumentException("Destination array was null.");
        } else if (srcOffset >= 0 && srcOffset + 3 < source.length) {
            if (destOffset < 0 || destOffset + 2 >= destination.length) {
                throw new IllegalArgumentException(String.format("Destination array with length %d cannot have offset of %d and still store three bytes.", destination.length, destOffset));
            }
        } else {
            throw new IllegalArgumentException(String.format("Source array with length %d cannot have offset of %d and still process four bytes.", source.length, srcOffset));
        }
    }

    public static byte[] encodeBytesToBytes(byte[] source, int len) {
        int encLen = len / 3 * 4 + (len % 3 > 0 ? 4 : 0);
        byte[] outBuff = new byte[encLen];
        int d = 0;
        int e = 0;

        for (int len2 = len - 2; d < len2; e += 4) {
            encode3to4(source, d, 3, outBuff, e);
            d += 3;
        }

        if (d < len) {
            encode3to4(source, d, len - d, outBuff, e);
            e += 4;
        }

        if (e <= outBuff.length - 1) {
            byte[] finalOut = new byte[e];
            System.arraycopy(outBuff, 0, finalOut, 0, e);
            return finalOut;
        } else {
            return outBuff;
        }
    }

    private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset) {
        int inBuff = (numSigBytes > 0 ? source[srcOffset] << 24 >>> 8 : 0) | (numSigBytes > 1 ? source[srcOffset + 1] << 24 >>> 16 : 0) | (numSigBytes > 2 ? source[srcOffset + 2] << 24 >>> 24 : 0);
        switch (numSigBytes) {
            case 1:
                destination[destOffset] = ALPHABET[inBuff >>> 18];
                destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 63];
                destination[destOffset + 2] = 61;
                destination[destOffset + 3] = 61;
                return destination;
            case 2:
                destination[destOffset] = ALPHABET[inBuff >>> 18];
                destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 63];
                destination[destOffset + 2] = ALPHABET[inBuff >>> 6 & 63];
                destination[destOffset + 3] = 61;
                return destination;
            case 3:
                destination[destOffset] = ALPHABET[inBuff >>> 18];
                destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 63];
                destination[destOffset + 2] = ALPHABET[inBuff >>> 6 & 63];
                destination[destOffset + 3] = ALPHABET[inBuff & 63];
                return destination;
            default:
                return destination;
        }
    }


}
