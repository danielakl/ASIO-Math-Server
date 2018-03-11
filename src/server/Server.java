package server;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Server implements Runnable {
    public static final int BUFFER_SIZE = 1024;

    protected final AtomicBoolean running = new AtomicBoolean(true);
    protected int port;

    public Server(int port) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Port must be within valid range, 0 to 65535");
        }
        this.port = port;
    }

    public final void stop() {
        running.set(false);
    }

    @Override
    public abstract void run();
}
