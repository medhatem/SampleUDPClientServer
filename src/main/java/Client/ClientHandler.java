package Client;

import Utils.Demand;
import Utils.DemandInterpreter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Class Client.Client
 * Description: TODO
 */
public class ClientHandler extends GeneralClient {

    private InetAddress clientAddress;
    private int clientPort;

    public ClientHandler(InetAddress address, int port, InetAddress clientAddress, int clientPort) {
        System.out.println("Starting new client handler");

        try {
            this.socket = new DatagramSocket(port, address);
            this.clientAddress = clientAddress;
            this.clientPort = clientPort;

            socket.setSoTimeout(1000);
        } catch (SocketException e) {
            e.printStackTrace();
            end();
        }
    }

    public void end() {
        System.out.println("Ending new client handler");

        socket.close();
    }

    public void confirmation(Demand demand) throws IOException {
        byte[] bytes;

        if (!demand.sending) {
            demand.fileLength = uploadPath.resolve(demand.fileName).toFile().length();
        }
        bytes = DemandInterpreter.toBytes(demand);
        socket.send(new DatagramPacket(bytes, bytes.length, clientAddress, clientPort));
    }

    public void sendFile(String fileName) throws IOException {
        System.out.println("Sending file " + fileName);

        send(clientAddress, clientPort, fileName);
    }

    public void receiveFile(String fileName, long fileLength) throws IOException {
        System.out.println("Receiving file " + fileName);

        receive(clientAddress, clientPort, fileName, fileLength);
    }
}
