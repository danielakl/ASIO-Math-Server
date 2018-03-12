package server;

import server.handler.UDPHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public final class UDPServer extends Server {
    UDPServer(int port) {
        super(port);
    }

    @Override
    public void start() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                    System.out.println("Waiting for packets.");
                    socket.receive(packet);
                    new Thread(new UDPHandler(socket, packet)).start();
                } catch (IOException ioe) {
                    System.err.println("Error: Communicating over socket.");
                    ioe.printStackTrace();
                }
            }
        } catch (SocketException se) {
            System.err.println("Error: creating UDP socket on port " + port);
            se.printStackTrace();
        }
    }
}
