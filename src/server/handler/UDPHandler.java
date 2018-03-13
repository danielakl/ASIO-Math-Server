package server.handler;

import server.Server;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public final class UDPHandler implements Handler {
    private final ScriptEngine scriptEngine = (new ScriptEngineManager()).getEngineByName("JavaScript");
    private final DatagramSocket socket;
    private DatagramPacket packet;
    private final byte[] buffer = new byte[Server.BUFFER_SIZE];
    private final InetAddress clientAddress;
    private final int clientPort;

    public UDPHandler(DatagramSocket socket, DatagramPacket packet) {
        this.socket = socket;
        this.packet = packet;
        clientAddress = packet.getAddress();
        clientPort = packet.getPort();
    }

    @Override
    public void run() {
        String received = new String(packet.getData(), 0, packet.getLength());
        received = received.replaceAll("[^0-9+\\-*/]", "");
        packet = new DatagramPacket(buffer, Server.BUFFER_SIZE, clientAddress, clientPort);
        try {
            byte[] result = scriptEngine.eval(received).toString().getBytes();
            if (result.length > Server.BUFFER_SIZE) {
                result = "Result too big.".getBytes();
            }
            System.arraycopy(result, 0, buffer, 0, result.length);
            socket.send(packet);
        } catch (ScriptException se) {
            System.err.println("Error: Script engine failed to evaluate: " + received);
            se.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Error: Handler failed to communicating with client.");
            ioe.printStackTrace();
        }
    }
}
