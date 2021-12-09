import java.nio.ByteBuffer;

public class Util {
    public static boolean getBit(byte b, int bitNumber) {
        return (b >> bitNumber & 1) == 1;
    }

    public static byte getByte(byte b, int startBitNumber, int endBitNumber) {
        byte mask = 0;
        for (int i = startBitNumber; i <= endBitNumber; i++) {
            mask += Math.pow(2, i);
        }
        return (byte) (b & mask);
    }

    public static short convertToShort(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer.getShort();
    }

    public static int convertToInt(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer.getInt();
    }

    public static int booleanToBit(boolean bool) {
        return bool ? 1 : 0;
    }

    public static byte[] convertIntegerToByteArray(int number) {
        return ByteBuffer.allocate(Integer.BYTES).putInt(number).array();
    }

    public static byte[] convertShortToByteArray(short number) {
        return ByteBuffer.allocate(Short.BYTES).putShort(number).array();
    }
}
