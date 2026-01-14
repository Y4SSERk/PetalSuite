package com.florist.networking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Standalone client to test communication with PetalSuite Socket Server.
 */
public class RemoteClient {
    private static final String HOST = "localhost";
    private static final int PORT = 5005;

    public static void main(String[] args) {
        System.out.println("=== PetalSuite Remote Client ===");
        System.out.println("Commands: STATS, ALERTS, VERSION, EXIT");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("\nEnter command: ");
            String cmd = scanner.nextLine();
            if (cmd.equalsIgnoreCase("EXIT"))
                break;

            try (Socket socket = new Socket(HOST, PORT);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println(cmd);
                String response = in.readLine();
                System.out.println("Server Response: " + response);

            } catch (Exception e) {
                System.err.println("Error: Could not connect to server. Is PetalSuite running?");
            }
        }
        scanner.close();
    }
}
