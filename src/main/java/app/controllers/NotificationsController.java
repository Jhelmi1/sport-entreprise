package app.controllers;

import app.dao.NotificationDAO;
import app.model.Notification;
import app.session.Session;
import app.ui.Toast;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.Toolkit;
import java.util.List;

public class NotificationsController {

    @FXML private ListView<Notification> listView;
    @FXML private Label unreadLabel;

    private final NotificationDAO dao = new NotificationDAO();

    private Timeline autoRefresh;
    private int lastUnreadCount = -1;

    @FXML
    public void initialize() {

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Notification n, boolean empty) {
                super.updateItem(n, empty);
                if (empty || n == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(n.getMessage());

                // style lu/non-lu
                if (!n.isRead()) {
                    setStyle("-fx-font-weight: bold;");
                } else {
                    setStyle("-fx-text-fill: #666666;");
                }
            }
        });

        // double clic => marquer lu
        listView.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                Notification selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null && !selected.isRead()) {
                    dao.markAsRead(selected.getId());
                    refresh(false); // pas de son/popup sur action utilisateur
                }
            }
        });

        refresh(false);

        // ✅ refresh auto toutes les 30 sec
        autoRefresh = new Timeline(new javafx.animation.KeyFrame(Duration.seconds(30), ev -> refresh(true)));
        autoRefresh.setCycleCount(Timeline.INDEFINITE);
        autoRefresh.play();
    }

    private void refresh(boolean allowSoundAndToast) {

        if (Session.getCurrentUser() == null) return;

        int userId = Session.getCurrentUser().getId();

        List<Notification> all = dao.findByUser(userId);
        listView.getItems().setAll(all);

        int unread = dao.countUnread(userId);
        unreadLabel.setText(String.valueOf(unread));
        unreadLabel.setVisible(unread > 0);
        unreadLabel.setManaged(unread > 0);

        boolean changed = (lastUnreadCount != -1 && unread != lastUnreadCount);
        boolean increased = (lastUnreadCount != -1 && unread > lastUnreadCount);

        // ✅ animation badge quand ça change
        if (changed) {
            animateBadge();
        }

        // ✅ son + toast si nouvelles notifs (unread augmente)
        if (allowSoundAndToast && increased) {
            playNotifySound();

            Notification newestUnread = findFirstUnread(all);
            if (newestUnread != null) {
                Toast.show("Nouvelle notification", newestUnread.getMessage());
            } else {
                Toast.show("Nouvelle notification", "Vous avez " + unread + " notification(s) non lue(s).");
            }
        }

        lastUnreadCount = unread;
    }

    private Notification findFirstUnread(List<Notification> all) {
        for (Notification n : all) {
            if (!n.isRead()) return n;
        }
        return null;
    }

    private void animateBadge() {
        ScaleTransition st = new ScaleTransition(Duration.millis(160), unreadLabel);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.25);
        st.setToY(1.25);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    private void playNotifySound() {
        Toolkit.getDefaultToolkit().beep();
    }

    @FXML
    private void onMarkAllRead() {
        if (Session.getCurrentUser() == null) return;
        dao.markAllAsRead(Session.getCurrentUser().getId());
        refresh(false);
    }

    @FXML
    private void onClose() {
        if (autoRefresh != null) autoRefresh.stop();
        Stage stage = (Stage) listView.getScene().getWindow();
        stage.close();
    }
}
