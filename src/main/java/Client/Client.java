package Client;

import Utils.Demand;
import Utils.DemandInterpreter;

import java.io.IOException;
import java.net.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

/**
 * Class Client.Client
 * Description: TODO
 */
public class Client extends GeneralClient {

    private static final int serverPort = 50000;
    private InetAddress serverAddress;

    public void start(InetAddress address, int port, InetAddress serverAddress) {
        System.out.println("Starting client");

        try {
            this.socket = new DatagramSocket(port, address);
            this.serverAddress = serverAddress;

            socket.setSoTimeout(1000);
        } catch (SocketException e) {
            e.printStackTrace();
            end();
        }
    }

    public void end() {
        System.out.println("Ending client");

        socket.close();
    }

    public void sendFile(String fileName) throws IOException {
        System.out.println("Sending file " + fileName);

        long fileLength;
        DatagramPacket packet;

        fileLength = uploadPath.resolve(fileName).toFile().length();
        packet = confirmation(new Demand(fileName, fileLength, true));

        send(serverAddress, packet.getPort(), fileName);
    }

    public void receiveFile(String fileName) throws IOException, ClassNotFoundException {
        System.out.println("Receiving file " + fileName);

        long fileLength;
        DatagramPacket packet;

        packet = confirmation(new Demand(fileName, -1, false));
        fileLength = DemandInterpreter.fromBytes(packet.getData()).fileLength;

        receive(serverAddress, packet.getPort(), fileName, fileLength);
    }

    private DatagramPacket confirmation(Demand demand) throws IOException {
        byte[] bytes = DemandInterpreter.toBytes(demand);
        DatagramPacket send = new DatagramPacket(bytes, bytes.length, serverAddress, serverPort);
        DatagramPacket receive = new DatagramPacket(bytes, bytes.length);

        do {
            socket.send(send);
            try {
                socket.receive(receive);
            } catch (SocketTimeoutException e) {
                System.out.println("timeout");
            }
        } while (!Arrays.equals(receive.getData(), bytes));

        return receive;
    }
}
