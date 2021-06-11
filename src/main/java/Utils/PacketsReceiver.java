package Utils;

import java.util.ArrayList;
import java.util.stream.Stream;

public class PacketsReceiver extends Packets {

    public PacketsReceiver(long fileLength) {
        messagesList = new ArrayList<>();
        indexBegin = 0;
        indexEnd = windowSize - 1;

        for (int i = 0, j = 0; i < fileLength; i += dataChunkSize, j++) {
            messagesList.add(new Packet(new byte[dataChunkSize], j, 1));
        }

        if (messagesList.size() < windowSize) {
            indexEnd = messagesList.size() - 1;
        }
    }

    public Stream<byte[]> getStreamOfMessage() {
        return messagesList.stream().map(Packet::getMessage);
    }

    public void setMessage(int packetIndex, byte[] packetMessage) {
        if (packetIndex >= indexBegin && packetIndex <= indexEnd) {
            if (messagesList.get(packetIndex).getState() == 1) {
                messagesList.get(packetIndex).setMessage(packetMessage);

                messagesList.get(packetIndex).setState(0);
                System.out.println("packet no " + packetIndex + " was received");
            }
        }
    }
}
