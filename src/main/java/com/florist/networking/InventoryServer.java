package com.florist.networking;

import com.florist.application.service.StatisticsService;
import com.florist.config.ServiceFactory;
import com.florist.domain.repository.StockAlertRepository;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Socket Server that allows remote clients to query inventory status.
 * Demonstrates Client/Server communication and Multithreading.
 */
public class InventoryServer {
    private static final int PORT = 5005;
    private final ExecutorService clientPool;
    private ServerSocket serverSocket;
    private boolean running = false;

    public InventoryServer() {
        this.clientPool = Executors.newCachedThreadPool();
    }

    public void start() {
        running = true;
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                System.out.println("[SOCKET-SERVER] Started on port " + PORT);

                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        clientPool.execute(new ClientHandler(clientSocket));
                    } catch (IOException e) {
                        if (running)
                            System.err.println("[SOCKET-SERVER] Accept error: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("[SOCKET-SERVER] Could not listen on port " + PORT);
            }
        }).start();
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null)
                serverSocket.close();
            clientPool.shutdown();
            System.out.println("[SOCKET-SERVER] Stopped");
        } catch (IOException e) {
            System.err.println("[SOCKET-SERVER] Stop error: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String request = in.readLine();
                if (request == null)
                    return;

                System.out.println("[SOCKET-SERVER] Received command: " + request);
                ServiceFactory factory = ServiceFactory.getInstance();
                StatisticsService statsService = factory.getStatisticsService();
                StockAlertRepository alertRepo = factory.getAlertRepository();

                switch (request.toUpperCase()) {
                    case "STATS":
                        long totalFlowers = statsService.getTotalFlowerCount();
                        double revenue = statsService.calculateTotalRevenue();
                        out.println(String.format("STAT_DATA: Total Flowers: %d, Total Revenue: %.2f MAD", totalFlowers,
                                revenue));
                        break;
                    case "ALERTS":
                        int unresovled = alertRepo.countUnresolved();
                        out.println("ALERT_DATA: Unresolved alerts: " + unresovled);
                        break;
                    case "VERSION":
                        out.println("PLATFORM: PetalSuite Server v1.0 (Bonus Socket Feature)");
                        break;
                    default:
                        out.println("ERROR: Unknown command. Try STATS, ALERTS, or VERSION.");
                }
            } catch (IOException e) {
                System.err.println("[SOCKET-SERVER] Handler error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    /* ignored */ }
            }
        }
    }
}
