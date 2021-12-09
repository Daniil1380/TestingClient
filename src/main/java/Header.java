
import com.google.common.primitives.Bytes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Header {
    private boolean cs = false;
    private byte mode = 0;
    private byte rCode = 0;
    private final byte mbz = 0;
    private int rrCount = 0;

    public Header() {}

    public Header(byte[] bytes) {
        System.out.println(Arrays.toString(bytes));
        if (bytes.length < 6) {
            throw new IllegalArgumentException("The number of bytes does not match 6.");
        }

        cs = Util.getBit(bytes[0], 0);
        mode =(byte) ((bytes[0] & 0b01111000) >> 3);
        rCode = (byte) (bytes[0] & 0b00000111);
        rrCount = Util.convertToInt(Arrays.copyOfRange(bytes, 2, 6));
    }

    public boolean isServer() {
        return cs;
    }

    public void setCs(boolean cs) {
        this.cs = cs;
    }

    public byte getMode() {
        return mode;
    }

    public void setMode(byte mode) {
        this.mode = mode;
    }

    public byte getRCode() {
        return rCode;
    }

    public void setRCode(byte rCode) {
        this.rCode = rCode;
    }

    public byte getMbz() {
        return mbz;
    }

    public int getRrCount() {
        return rrCount;
    }

    public void setRrCount(int rrCount) {
        this.rrCount = rrCount;
    }

    public List<Byte> getHeaderBytes() {
        List<Byte> headerList = new ArrayList<>();
        int firstByte = (Util.booleanToBit(cs) << 7) | (mode << 3) | (rCode);
        headerList.add((byte) firstByte);
        headerList.add(mbz);
        headerList.addAll(Bytes.asList(Util.convertIntegerToByteArray(rrCount)));
        return headerList;
    }
}
