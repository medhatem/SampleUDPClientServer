package Client;

import Utils.Packet;
import Utils.PacketsReceiver;
import Utils.PacketsSender;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Class Client.Client
 * Description: TODO
 */
public abstract class GeneralClient {
    protected static final Path downloadPath = Paths.get("src/main/resources/download");
    protected static final Path uploadPath = Paths.get("src/main/resources/upload");

    protected DatagramSocket socket;

    protected void send(InetAddress address, int port, String fileName) throws IOException {
        int packetsSent;
        Path filePath = uploadPath.resolve(fileName);
        PacketsSender packets = new PacketsSender(filePath);

        while (packets.notEnd()) {
            System.out.println("---------Sending---------");

            packets.getUsable().forEach(packet -> {
                DatagramPacket datagramPacket = new DatagramPacket(packet.getMessage(), packet.getMessage().length, address, port);

                try {
                    socket.send(datagramPacket);
                    packets.setAsSent(packet.getNoMessage());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            System.out.println("---------Receiving---------");
            packetsSent = packets.getSent();

            for (int i = 0; i < packetsSent; i++) {
                byte[] acknowledge = ByteBuffer.allocate(4).array();
                DatagramPacket datagramPacket = new DatagramPacket(acknowledge, acknowledge.length);

                try {
                    socket.receive(datagramPacket);
                    packets.setAsAcknowledged(ByteBuffer.wrap(datagramPacket.getData()).getInt());

                } catch (SocketTimeoutException e) {
                    System.out.println("timeout");
                }
            }

            packets.updateWindow();
            System.out.println("---------Update window---------");
        }
    }

    protected void receive(InetAddress address, int port, String fileName, long fileLength) throws IOException {
        Path filePath = downloadPath.resolve(fileName);
        PacketsReceiver packets = new PacketsReceiver(fileLength);
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }

        while (packets.notEnd()) {
            int noMessage;
            byte[] acknowledge;

            byte[] message = ByteBuffer.allocate(1028).array();
            DatagramPacket receivePacket = new DatagramPacket(message, message.length);

            try {
                socket.receive(receivePacket);

                noMessage = ByteBuffer.wrap(Arrays.copyOfRange(message, 0, 4)).getInt();
                message = Arrays.copyOfRange(message, 4, 1028);
                packets.setMessage(noMessage, message);

                acknowledge = ByteBuffer.allocate(4).putInt(noMessage).array();
                DatagramPacket sendPacket = new DatagramPacket(acknowledge, acknowledge.length, address, port);
                socket.send(sendPacket);

                System.out.println("ack for message no " + noMessage + " was sent");
            } catch (SocketTimeoutException e) {
                System.out.println("timeout");
            }

            packets.updateWindow();
            System.out.println("---------Update window---------");
        }

        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            packets.getStreamOfMessage().forEachOrdered(bytes -> {
                try {
                    fos.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
