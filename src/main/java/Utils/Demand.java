package Utils;

import java.io.Serializable;

public class Demand implements Serializable {
    public String fileName;
    public long fileLength;
    public boolean sending;

    public Demand(String fileName, long fileLength, boolean sending) {
        this.fileName = fileName;
        this.fileLength = fileLength;
        this.sending = sending;
    }
}
