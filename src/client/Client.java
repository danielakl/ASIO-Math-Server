package client;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocketFactory;

public final class Client {
    private static final int PORT = 3000;

    public static void main(String[] args) {
        /* Read user input from CLI */
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the IP or hostname of the server to connect to: ");
        String hostname = scanner.nextLine();
        SocketAddress server = new InetSocketAddress(hostname, PORT); // new

        System.out.print("UDP or TLS: ");
        String mode = scanner.nextLine();
        switch (mode.toLowerCase()) {
            default:
                // Fallthrough.
            case "udp":
                udpClient(server);
                break;
            case "tls":
                tlsClient();
                break;
        }
    }

    private static void udpClient(SocketAddress server) {
        Scanner scanner = new Scanner(System.in);
        byte[] buffer = new byte[1024]; // new

        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] message;
            do {
                System.out.print("Calculate expression: ");
                message = scanner.nextLine().getBytes();

                System.arraycopy(message, 0, buffer, 0, Math.min(message.length, buffer.length));
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, server);
                socket.send(packet);

                packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String response = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Answer: " + response + "\n");
            } while(message.length == 0);
        } catch (SocketException se) {
            System.err.println("Error: creating UDP socket.");
            se.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Error: sending packet over socket.");
            ioe.printStackTrace();
        }
    }

    private static void tlsClient() {
        /* From: http://java-buddy.blogspot.com/ */
        SSLSocketFactory sslSocketFactory =
                (SSLSocketFactory)SSLSocketFactory.getDefault();
        try {
            Socket socket = sslSocketFactory.createSocket("localhost", PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            try (BufferedReader bufferedReader =
                         new BufferedReader(
                                 new InputStreamReader(socket.getInputStream()))) {
                Scanner scanner = new Scanner(System.in);
                while(true){
                    System.out.println("Enter something:");
                    String inputLine = scanner.nextLine();
                    if(inputLine.equals("q")){
                        break;
                    }

                    out.println(inputLine);
                    System.out.println(bufferedReader.readLine());
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}
