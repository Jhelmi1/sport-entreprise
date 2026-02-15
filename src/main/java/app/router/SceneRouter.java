package app.router;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneRouter {

    private static Stage stage;

    public static void setStage(Stage s) {
        stage = s;
    }

    public static void goTo(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneRouter.class.getResource("/views/" + fxml));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            stage.setTitle(title);
            stage.setScene(scene);

            applySizePreset(fxml);

            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            throw new RuntimeException("Erreur chargement: " + fxml, e);
        }
    }

    public static void openInNewWindow(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneRouter.class.getResource("/views/" + fxml));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setTitle(title);
            newStage.setScene(new Scene(root));

            // preset fenêtre secondaire
            if ("notifications.fxml".equalsIgnoreCase(fxml)) {
                newStage.setMinWidth(520);
                newStage.setMinHeight(520);
                newStage.setWidth(600);
                newStage.setHeight(600);
            } else {
                newStage.setMinWidth(700);
                newStage.setMinHeight(500);
                newStage.setWidth(900);
                newStage.setHeight(600);
            }

            newStage.centerOnScreen();
            newStage.show();

        } catch (Exception e) {
            throw new RuntimeException("Erreur chargement: " + fxml, e);
        }
    }

    // ✅ Tailles automatiques selon écran
    private static void applySizePreset(String fxml) {

        String name = fxml.toLowerCase();

        // Login / Register
        if (name.contains("login") || name.contains("register")) {
            stage.setMinWidth(780);
            stage.setMinHeight(520);
            stage.setWidth(900);
            stage.setHeight(600);
            return;
        }

        // Dashboards
        if (name.contains("dashboard")) {
            stage.setMinWidth(1100);
            stage.setMinHeight(700);
            stage.setWidth(1200);
            stage.setHeight(750);
            return;
        }

        // Écrans de gestion (events, objectives, stats…)
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        stage.setWidth(1100);
        stage.setHeight(700);
    }
}
