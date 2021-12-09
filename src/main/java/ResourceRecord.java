import com.google.common.primitives.Bytes;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResourceRecord {
    private short length = 0;
    private byte[] data = new byte[0];

    public ResourceRecord() {}

    public ResourceRecord(byte[] bytes) {
        length = Util.convertToShort(Arrays.copyOfRange(bytes, 0, 2));
        data = Arrays.copyOfRange(bytes, 2, length + 3);
    }

    public short getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getDataString() {
        return new String(data, StandardCharsets.UTF_8);
    }

    public List<Byte> getResourceRecordBytes() {
        List<Byte> resourceRecordList = new ArrayList<>(Bytes.asList(Util.convertShortToByteArray(length)));
        resourceRecordList.addAll(Bytes.asList(data));
        return resourceRecordList;
    }
}
