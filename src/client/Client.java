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
    private static int port = 3000;
    private static String mode = "udp";

    public static void main(String[] args) {
        /* Process commandline arguments. */
        switch (args.length) {
            default:
                // Fallthrough.
            case 2:
                String arg2 = args[1];
                processArg(arg2);
                // Fallthrough
            case 1:
                String arg1 = args[0];
                processArg(arg1);
                break;
            case 0:
                break;
        }

        /* Read user input from CLI */
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the IP or hostname of the server to connect to: ");
        String hostname = scanner.nextLine().trim();
        SocketAddress server = new InetSocketAddress(hostname, port);

        // Print current settings.
        System.out.println("Connecting to '" + hostname + "' at port '" + port + "' in '" + mode + "' mode.");

        // Change settings.
        System.out.print("Change these settings? (Y/N): ");
        String answer = scanner.nextLine().toLowerCase();
        if (answer.contains("y")) {
            // Change hostname.
            System.out.print("Hostname: ");
            String input = scanner.nextLine().trim();
            if (!input.equals("")) {
                hostname = input;
            }

            // Change port.
            do {
                System.out.print("Port: ");
                input = scanner.nextLine().trim();
                if (!input.equals("")) {
                    if (input.matches("[0-9]+")) {
                        port = Integer.parseInt(input);
                        if (port < 0 || port > 65535) {
                            System.err.println("Port must be between valid range of 0 to 65535.");
                        }
                    }
                }
            } while (port < 0 || port > 65535);
            server = new InetSocketAddress(hostname, port);

            // Change mode.
            System.out.print("UDP or TLS: ");
            processArg(scanner.nextLine());
        }

        // Start client.
        switch (mode) {
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
            String message;
            do {
                System.out.print("Calculate expression: ");
                message = scanner.nextLine();
                byte[] byteMessage = message.getBytes();

                System.arraycopy(byteMessage, 0, buffer, 0, Math.min(byteMessage.length, buffer.length));
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, server);
                socket.send(packet);

                packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String response = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Answer: " + response + "\n");
            } while(!(message.equalsIgnoreCase("e") || message.equalsIgnoreCase("exit") || message.length() != 0));
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
            Socket socket = sslSocketFactory.createSocket("localhost", port);
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

    private static void processArg(String arg) {
        if (arg.matches("^[0-9]+$")) {
            port = Integer.parseInt(arg);
            if (port < 0 || port > 65535) {
                port = 80;
            }
        } else {
            switch (arg.toLowerCase()) {
                case "udp":
                    mode = "udp";
                    break;
                case "tls":
                    mode = "tls";
                    break;
                default:
                    break;
            }
        }
    }
}
