package app.controllers;

import app.router.SceneRouter;
import app.session.Session;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class RHDashboardController {

    @FXML private StackPane avatar;
    @FXML private Label avatarLabel;

    private ContextMenu contextMenu;

    @FXML
    public void initialize() {

        if (Session.getCurrentUser() == null) {
            avatarLabel.setText("??");
            return;
        }

        avatarLabel.setText(makeInitials(Session.getCurrentUser().getFullName()));

        avatar.setOnMouseEntered(e -> animateAvatar(1.1));
        avatar.setOnMouseExited(e -> animateAvatar(1.0));

        MenuItem changePassword = new MenuItem("Changer mot de passe");
        changePassword.setOnAction(e ->
                SceneRouter.goTo("change_password.fxml", "Changer mot de passe")
        );

        MenuItem logout = new MenuItem("Déconnexion");
        logout.setOnAction(e -> {
            Session.clear();
            SceneRouter.goTo("login.fxml", "Connexion");
        });

        contextMenu = new ContextMenu(changePassword, logout);
    }

    private void animateAvatar(double scale) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), avatar);
        st.setToX(scale);
        st.setToY(scale);
        st.play();
    }

    private String makeInitials(String fullName) {
        if (fullName == null || fullName.isBlank()) return "??";

        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();

        return parts[0].substring(0, 1).toUpperCase()
                + parts[parts.length - 1].substring(0, 1).toUpperCase();
    }

    @FXML
    private void onAvatarClick(MouseEvent event) {
        contextMenu.show(avatar, event.getScreenX(), event.getScreenY());
    }

    @FXML
    private void onObjectives() {
        System.out.println("Objectifs");
    }

    @FXML
    private void onEvents() {
        SceneRouter.goTo("event_management.fxml", "Gestion événements");
    }

    @FXML
    private void onStats() {
        System.out.println("Stats");
    }
    @FXML
    private void onNotifications() {
        SceneRouter.openInNewWindow("notifications.fxml", "Notifications");
    }

}
