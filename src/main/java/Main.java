import Client.Client;
import Server.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        InetAddress myAddress = InetAddress.getLocalHost();
        Scanner scanner = new Scanner(System.in);

        System.out.println("IFT585 - TP1");
        System.out.println("By Christophe Pigeon & Mohamed Hatem Diabi");

        System.out.println("Select the mode:");
        System.out.println("0 - Server");
        System.out.println("1 - Client");

        switch (scanner.nextInt()) {
            case 0:
                Server server = new Server();
                server.start(myAddress);

                server.end();
                break;

            case 1:
                Client client = new Client();
                client.start(myAddress, 60000, myAddress);

                boolean quitFlag = true;
                Map<Integer, String> map = Stream.of(
                        new AbstractMap.SimpleImmutableEntry<>(0, "short.txt"),
                        new AbstractMap.SimpleImmutableEntry<>(1, "medium.txt"),
                        new AbstractMap.SimpleImmutableEntry<>(2, "long.txt"),
                        new AbstractMap.SimpleImmutableEntry<>(3, "final.txt"))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                do {
                    System.out.println("What do you want to do:");
                    System.out.println("0 - quit");
                    System.out.println("1 - upload a file");
                    System.out.println("2 - download a file");

                    switch (scanner.nextInt()) {
                        case 0:
                            quitFlag = false;
                            break;

                        case 1:
                            System.out.println("Which file:");
                            System.out.println("0 - short (1 kB)");
                            System.out.println("1 - medium (256 KB)");
                            System.out.println("2 - long (21 MB)");
                            System.out.println("3 - final (42 MB)");

                            client.sendFile(map.getOrDefault(scanner.nextInt(), "short"));
                            break;

                        case 2:
                            System.out.println("Which file:");
                            System.out.println("0 - short (1 kB)");
                            System.out.println("1 - medium (256 KB)");
                            System.out.println("2 - long (21 MB)");
                            System.out.println("3 - final (42 MB)");

                            client.receiveFile(map.getOrDefault(scanner.nextInt(), "short"));
                            break;

                        default:
                            break;
                    }
                } while (quitFlag);

                client.end();
                break;

            default:
                break;
        }
    }
}
