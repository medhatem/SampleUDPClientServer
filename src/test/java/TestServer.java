import Server.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestServer {

    public static void main (String[] args) {
        try {
            InetAddress myAddress = InetAddress.getLocalHost();

            Server server = new Server();
            server.start(myAddress);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
