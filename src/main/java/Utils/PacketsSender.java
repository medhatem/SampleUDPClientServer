package Utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class PacketsSender extends Packets {

    public PacketsSender(Path filePath) throws IOException {
        messagesList = new ArrayList<>();
        indexBegin = 0;
        indexEnd = windowSize - 1;

        byte[] dataToSend = Files.readAllBytes(filePath);

        for (int i = 0, j = 0; i < dataToSend.length; i += dataChunkSize, j++) {
            byte[] noMessage = ByteBuffer.allocate(4).putInt(j).array();
            byte[] message = Arrays.copyOfRange(dataToSend, i, Math.min(dataToSend.length, i + dataChunkSize));
            byte[] finalMessage = new byte[noMessage.length + message.length];

            System.arraycopy(noMessage, 0, finalMessage, 0, noMessage.length);
            System.arraycopy(message, 0, finalMessage, noMessage.length, message.length);

            messagesList.add(new Packet(finalMessage, j, 2));
        }

        if (messagesList.size() < windowSize) {
            indexEnd = messagesList.size() - 1;
        }
    }

    public Stream<Packet> getUsable() {
        List<Packet> usable = new ArrayList<>();

        for (int i = indexBegin; i <= indexEnd; i++) {
            if (messagesList.get(i).getState() == 2) {
                usable.add(messagesList.get(i));

            } else if (messagesList.get(i).getState() == 1) {
                usable.add(messagesList.get(i));
            }
        }

        return usable.stream();
    }

    public int getSent() {
        int sent = 0;

        for (int i = indexBegin; i <= indexEnd; i++) {
            if (messagesList.get(i).getState() == 1) {
                sent++;
            }
        }

        return sent;
    }

    public void setAsSent(int packetIndex) {
        if (packetIndex >= indexBegin && packetIndex <= indexEnd) {
            messagesList.get(packetIndex).setState(1);
            System.out.println("packet no " + packetIndex + " was sent");
        }
    }

    public void setAsAcknowledged(int packetIndex) {
        if (packetIndex >= indexBegin && packetIndex <= indexEnd) {
            messagesList.get(packetIndex).setState(0);
            System.out.println("packet no " + packetIndex + " was acknowledged");
        }
    }
}
