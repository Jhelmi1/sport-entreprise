package app.controllers;

import app.dao.NotificationDAO;
import app.model.Role;
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

public class EmployeeDashboardController {

    @FXML private StackPane avatar;
    @FXML private Label avatarLabel;
    @FXML private Label notificationBadge;

    private ContextMenu contextMenu;
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @FXML
    public void initialize() {

        if (Session.getCurrentUser() == null) return;

        avatarLabel.setText(makeInitials(Session.getCurrentUser().getFullName()));

        if (Session.getCurrentUser().getRole() == Role.RH)
            avatar.setStyle("-fx-background-radius: 999; -fx-background-color: #1565c0;");
        else
            avatar.setStyle("-fx-background-radius: 999; -fx-background-color: #2e7d32;");

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

        refreshNotificationBadge();
    }

    private void refreshNotificationBadge() {
        int count = notificationDAO.countUnread(Session.getCurrentUser().getId());

        if (count > 0) {
            notificationBadge.setText(String.valueOf(count));
            notificationBadge.setVisible(true);
        } else {
            notificationBadge.setVisible(false);
        }
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
        if (parts.length == 1) return parts[0].substring(0,1).toUpperCase();
        return parts[0].substring(0,1).toUpperCase()
                + parts[parts.length-1].substring(0,1).toUpperCase();
    }

    @FXML
    private void onAvatarClick(MouseEvent event) {
        contextMenu.show(avatar, event.getScreenX(), event.getScreenY());
    }

    @FXML
    private void onNotificationsClick() {
        SceneRouter.goTo("notifications.fxml", "Notifications");
    }

    @FXML
    private void onEvents() {
        SceneRouter.goTo("employee_events.fxml", "Événements");
    }

    @FXML
    private void onMyRegistrations() {
        SceneRouter.goTo("employee_my_registrations.fxml", "Mes inscriptions");
    }
    @FXML
    private void onNotifications() {
        SceneRouter.openInNewWindow("notifications.fxml", "Notifications");
    }

}
