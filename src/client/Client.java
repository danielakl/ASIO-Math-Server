package client;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public final class Client {
    private static final int PORT = 3000;

    public static void main(String[] args) {
        /* Read user input from CLI */
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the IP or hostname of the server to connect to: ");
        String hostname = scanner.nextLine();
        SocketAddress server = new InetSocketAddress(hostname, PORT); // new

        byte[] buffer = new byte[512]; // new

        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "Hello from client.";
            System.arraycopy(message.getBytes(), 0, buffer, 0, Math.min(message.getBytes().length, buffer.length));
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, server);
            socket.send(packet);
        } catch (SocketException se) {
            System.err.println("Error: creating UDP socket.");
            se.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Error: sending packet over socket.");
            ioe.printStackTrace();
        }
    }
}
