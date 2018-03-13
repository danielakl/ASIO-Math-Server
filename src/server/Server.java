package server;

public abstract class Server {
    public static final int BUFFER_SIZE = 1024;

    protected int port;

    public Server() {
    }

    public Server(int port) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Port must be within valid range, 0 to 65535");
        }
        this.port = port;
    }

    /**
     * Sets the port the server will listen to.
     *
     * @param port - the port to set.
     * @return true if the given port was within the valid range of 0 to 65535.
     */
    public final boolean setPort(int port) {
        if (port >= 0 && port <= 65535) {
            this.port = port;
            return true;
        }
        return false;
    }

    public abstract void start();
}
