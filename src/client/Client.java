package client;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

public final class Client {
    private static void udpClient(SocketAddress server) {
        try (DatagramSocket socket = new DatagramSocket()) {

        } catch (SocketException se) {
            System.err.println("Error: creating UDP socket.");
            se.printStackTrace();
        }
    }

    private static void asioClient(SocketAddress server) {

    }

    public static void main(String[] args) {
        String clientType = args[0].toLowerCase();
        String hostname = args[1];
        int port = Integer.parseInt(args[2]);
        SocketAddress server = new InetSocketAddress(hostname, port);
        switch (clientType) {
            case "udp":
                udpClient(server);
                break;
            case "asio":
                asioClient(server);
                break;
            default:
                break;
        }
    }
}
