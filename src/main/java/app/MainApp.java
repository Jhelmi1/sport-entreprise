package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        app.router.SceneRouter.setStage(stage);
        app.router.SceneRouter.goTo("login.fxml", "Connexion");
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
