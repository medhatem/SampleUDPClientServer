package Utils;

import java.util.List;

public abstract class Packets {
    protected static final int dataChunkSize = 1024;
    protected static final int windowSize = 12;

    protected List<Packet> messagesList;
    protected int indexBegin;
    protected int indexEnd;

    public void updateWindow() {
        while (messagesList.get(indexBegin).getState() == 0 && indexBegin < messagesList.size() - 1) {
            indexBegin++;

            if (indexEnd < messagesList.size() - 1) {
                indexEnd++;
            }
        }
    }

    public boolean notEnd() {
        return messagesList.get(indexBegin).getState() != 0 || indexBegin != indexEnd;
    }
}
