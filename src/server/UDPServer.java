package server;

import server.handler.UDPHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

final class UDPServer extends Server {
    UDPServer(int port) {
        super(port);
    }

    /**
     * Sets the port the server will listen to.
     * @param port - the port to set.
     * @return true if the given port was within the valid range of 0 to 65535.
     */
    boolean setPort(int port) {
        if (port >= 0 && port <= 65535) {
            this.port = port;
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            while (running.get()) {
                try {
                    DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
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
