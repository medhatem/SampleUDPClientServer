package Server;

import Client.ClientHandler;
import Utils.Demand;
import Utils.DemandInterpreter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Class Server.Server
 * Description: TODO
 */
public class Server {
    private static final int serverPort = 50000;
    private static int threadPorts = 50100;

    private DatagramSocket socket;

    public void start(InetAddress address) {
        System.out.println("Starting server");

        byte[] bytes;
        DatagramPacket receive;
        Demand info;

        try {
            socket = new DatagramSocket(serverPort, address);

            while (true) {
                bytes = new byte[1024];
                receive = new DatagramPacket(bytes, bytes.length);

                socket.receive(receive);
                info = DemandInterpreter.fromBytes(receive.getData());
                new EchoClientHandler(address, threadPorts++, receive.getAddress(), receive.getPort(), info).start();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            end();
        }
    }

    public void end() {
        System.out.println("Ending server");

        socket.close();
    }

    private static class EchoClientHandler extends Thread {
        private ClientHandler clientHandler;
        private Demand info;

        public EchoClientHandler(InetAddress address, int port, InetAddress clientAddress, int clientPort, Demand info) {
            clientHandler = new ClientHandler(address, port, clientAddress, clientPort);
            this.info = info;
        }

        @Override
        public void run() {
            System.out.println("Starting client handler");

            try {
                clientHandler.confirmation(info);

                if (info.sending) {
                    clientHandler.receiveFile(info.fileName, info.fileLength);
                } else {
                    clientHandler.sendFile(info.fileName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Ending client handler");
            clientHandler.end();
        }
    }
}
