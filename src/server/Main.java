package server;

import server.type.ServerType;

import java.util.Scanner;

public final class Main {
    private static ServerType mode = ServerType.UDP;
    private static int port = 3000;

    public static void main(String[] args) {
        Server server;

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

        /* Print current settings. */
        System.out.println("Server will listen to port " + port + " and run '" + mode + "' mode.");

        /* Change settings. */
        System.out.print("Change port or mode? (Y/N): ");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine().toLowerCase();
        if (answer.contains("y")) {
            // Enter new mode.
            boolean invalidMode;
            do {
                System.out.print("Enter the mode to use: ");
                answer = scanner.nextLine().toLowerCase().trim();
                switch (answer) {
                    case "":
                        invalidMode = false;
                        break;
                    case "udp":
                        mode = ServerType.UDP;
                        invalidMode = false;
                        break;
                    case "tls":
                        mode = ServerType.TLS;
                        invalidMode = false;
                        break;
                    default:
                        System.err.println("Invalid mode '" + answer + "'.");
                        invalidMode = true;
                        break;
                }
            } while (invalidMode);

            // Enter new port.
            boolean invalidPort;
            do {
                System.out.print("Enter the port to use: ");
                answer = scanner.nextLine().trim();
                if (answer.equals("")) {
                    invalidPort = false;
                } else if (answer.matches("^[0-9]+$")) {
                    port = Integer.parseInt(answer);
                    if (port < 0 || port > 65535) {
                        System.err.println("The port must be between 0 and 65535.");
                        invalidPort = true;
                    } else {
                        invalidPort = false;
                    }
                } else {
                    System.err.println("Failed to parse port '" + answer + "'.");
                    invalidPort = true;
                }
            } while (invalidPort);
        }

        /* Startup server */
        switch (mode) {
            default:
                // Fallthrough.
            case UDP:
                server = new UDPServer(port);
                break;
            case TLS:
                server = new TLSServer(port);
                break;
        }
        server.start();
    }

    private static void processArg(String arg) {
        if (arg.matches("^[0-9]+$")) {
            port = Integer.parseInt(arg);
            if (port < 0 || port > 65535) {
                port = 80;
            }
        } else {
            switch (arg) {
                case "udp":
                    mode = ServerType.UDP;
                    break;
                case "tls":
                    mode = ServerType.TLS;
                    break;
                default:
                    break;
            }
        }
    }
}
