import Client.Client;

import java.io.IOException;
import java.net.InetAddress;

public class TestClient {

    public static void main (String[] args) {
        try {
            InetAddress myAddress = InetAddress.getLocalHost();

            Client client = new Client();
            client.start(myAddress, 60000, myAddress);

            client.sendFile("image.jpg");
            client.receiveFile("final.txt");

            client.end();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
