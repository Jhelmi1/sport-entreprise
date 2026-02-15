package app.ui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Toast {

    /**
     * Affiche un popup discret en haut à droite (non bloquant).
     */
    public static void show(String title, String message) {
        Stage toastStage = new Stage();
        toastStage.initStyle(StageStyle.TRANSPARENT);
        toastStage.setAlwaysOnTop(true);

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label msgLbl = new Label(message);
        msgLbl.setWrapText(true);
        msgLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");

        VBox root = new VBox(6, titleLbl, msgLbl);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(12));
        root.setStyle("""
                -fx-background-color: rgba(30, 30, 30, 0.92);
                -fx-background-radius: 12;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 12, 0.2, 0, 4);
                """);

        Scene scene = new Scene(root);
        scene.setFill(null);
        toastStage.setScene(scene);

        // Position top-right (avec marge)
        var bounds = Screen.getPrimary().getVisualBounds();
        toastStage.setX(bounds.getMaxX() - 340); // largeur approx
        toastStage.setY(bounds.getMinY() + 20);

        toastStage.show();

        // Animations: fade in -> pause -> fade out
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition stay = new PauseTransition(Duration.seconds(3));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> toastStage.close());

        new SequentialTransition(fadeIn, stay, fadeOut).play();
    }
}
