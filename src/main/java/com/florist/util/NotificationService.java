package com.florist.util;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * Utility for displaying non-intrusive toast notifications.
 */
public class NotificationService {

    /**
     * Shows a success toast notification.
     * 
     * @param window  The owner window.
     * @param message The message to display.
     */
    public static void showSuccess(Window window, String message) {
        showToast(window, message, Color.rgb(34, 197, 94)); // Green
    }

    /**
     * Shows an info toast notification.
     * 
     * @param window  The owner window.
     * @param message The message to display.
     */
    public static void showInfo(Window window, String message) {
        showToast(window, message, Color.rgb(59, 130, 246)); // Blue
    }

    /**
     * Shows an error toast notification.
     * 
     * @param window  The owner window.
     * @param message The message to display.
     */
    public static void showError(Window window, String message) {
        showToast(window, message, Color.rgb(239, 68, 68)); // Red
    }

    /**
     * Shows a warning toast notification.
     * 
     * @param window  The owner window.
     * @param message The message to display.
     */
    public static void showWarning(Window window, String message) {
        showToast(window, message, Color.rgb(245, 158, 11)); // Amber/Orange
    }

    private static void showToast(Window window, String message, Color bgColor) {
        if (window == null)
            return;

        Platform.runLater(() -> {
            Popup popup = new Popup();
            popup.setAutoFix(true);
            popup.setAutoHide(false); // Don't hide on click outside

            Label label = new Label(message);
            label.setStyle(
                    "-fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-font-weight: bold;");
            label.setWrapText(true);
            label.setMaxWidth(300);

            StackPane pane = new StackPane();
            pane.setStyle("-fx-background-radius: 8; -fx-padding: 12 20 12 20; -fx-background-color: "
                    + toRxString(bgColor) + ";");
            pane.getChildren().add(label);
            pane.setOpacity(0);

            popup.getContent().add(pane);

            // Position at bottom center
            popup.show(window);
            popup.setX(window.getX() + window.getWidth() / 2 - popup.getWidth() / 2);
            popup.setY(window.getY() + window.getHeight() - 100);

            // Fade in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), pane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            // Wait, then fade out
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(500), pane);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(ev -> popup.hide());
                fadeOut.play();
            });

            // Start delay after fade in
            fadeIn.setOnFinished(e -> delay.play());

            // Allow clicking to dismiss early
            pane.setOnMouseClicked(e -> {
                popup.hide();
            });
        });
    }

    private static String toRxString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
