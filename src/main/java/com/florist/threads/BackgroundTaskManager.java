package com.florist.threads;

import com.florist.config.ServiceFactory;
import com.florist.domain.repository.FlowerRepository;
import com.florist.domain.repository.SaleRepository;
import com.florist.domain.repository.SupplierRepository;
import com.florist.io.FileExportService;
import com.florist.application.service.InventoryService;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Background task manager for automated operations.
 * REFACTORED: Now uses Repositories/Services via ServiceFactory.
 */
public class BackgroundTaskManager {

    private final ScheduledExecutorService scheduler;
    private final FileExportService exportService;
    private final FlowerRepository flowerRepository;
    private final SaleRepository saleRepository;
    private final SupplierRepository supplierRepository;

    private static final String BACKUP_DIR = "backups";
    private static final DateTimeFormatter BACKUP_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public BackgroundTaskManager() {
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.exportService = new FileExportService();

        ServiceFactory factory = ServiceFactory.getInstance();
        this.flowerRepository = factory.getFlowerRepository();
        this.saleRepository = factory.getSaleRepository();
        this.supplierRepository = factory.getSupplierRepository();

        new File(BACKUP_DIR).mkdirs();
    }

    public void startAutoBackup() {
        Runnable backupTask = () -> {
            try {
                cleanOldBackups();
                String timestamp = LocalDateTime.now().format(BACKUP_DATE_FORMAT);

                exportService.exportFlowersToCsv(
                        flowerRepository.findAll(),
                        BACKUP_DIR + "/flowers_" + timestamp + ".csv");

                exportService.exportSalesToCsv(
                        saleRepository.findAll(),
                        BACKUP_DIR + "/sales_" + timestamp + ".csv");

                exportService.exportSuppliersToCsv(
                        supplierRepository.findAll(),
                        BACKUP_DIR + "/suppliers_" + timestamp + ".csv");

                System.out.println("[AUTO-BACKUP] Backup completed at " + timestamp);
            } catch (Exception e) {
                System.err.println("[AUTO-BACKUP] Failed: " + e.getMessage());
            }
        };

        scheduler.scheduleAtFixedRate(backupTask, 1, 30, TimeUnit.MINUTES);
        System.out.println("[BackgroundTaskManager] Auto-backup started (every 30 minutes)");
    }

    private void cleanOldBackups() {
        try {
            File backupFolder = new File(BACKUP_DIR);
            if (!backupFolder.exists())
                return;

            File[] files = backupFolder.listFiles((dir, name) -> name.endsWith(".csv") && !name.startsWith("manual_"));
            if (files == null || files.length <= 30)
                return;

            java.util.Arrays.sort(files, java.util.Comparator.comparingLong(File::lastModified));

            int filesToDelete = files.length - 30;
            for (int i = 0; i < filesToDelete; i++) {
                if (files[i].delete()) {
                    System.out.println("[AUTO-BACKUP] Deleted old backup: " + files[i].getName());
                }
            }
        } catch (Exception e) {
            System.err.println("[AUTO-BACKUP] Cleanup failed: " + e.getMessage());
        }
    }

    public void startAlertMonitoring() {
        Runnable monitorTask = () -> {
            try {
                long lowStockCount = flowerRepository.findAll().stream()
                        .filter(f -> f.getQuantity() < InventoryService.DEFAULT_LOW_STOCK_THRESHOLD)
                        .count();

                long expiringCount = flowerRepository.findAll().stream()
                        .filter(f -> {
                            var expiryDate = f.getExpiryDate();
                            return expiryDate != null &&
                                    !expiryDate.isAfter(LocalDateTime.now().toLocalDate().plusDays(3));
                        })
                        .count();

                if (lowStockCount > 0 || expiringCount > 0) {
                    System.out.println("[ALERT-MONITOR] Low stock: " + lowStockCount +
                            ", Expiring soon: " + expiringCount);
                }
            } catch (Exception e) {
                System.err.println("[ALERT-MONITOR] Failed: " + e.getMessage());
            }
        };

        scheduler.scheduleAtFixedRate(monitorTask, 30, 300, TimeUnit.SECONDS);
        System.out.println("[BackgroundTaskManager] Alert monitoring started (every 5 minutes)");
    }

    public void shutdown() {
        try {
            System.out.println("[BackgroundTaskManager] Shutting down...");
            scheduler.shutdown();
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            System.out.println("[BackgroundTaskManager] Shutdown complete");
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public boolean isRunning() {
        return !scheduler.isShutdown();
    }
}
