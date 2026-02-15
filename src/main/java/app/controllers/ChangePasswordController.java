package app.controllers;

import app.dao.UserDAO;
import app.model.Role;
import app.router.SceneRouter;
import app.session.Session;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.mindrot.jbcrypt.BCrypt;

public class ChangePasswordController {

    @FXML private PasswordField currentPasswordField;

    @FXML private PasswordField newPasswordField;
    @FXML private TextField newPasswordTextField;
    @FXML private ToggleButton toggleNew;

    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordTextField;
    @FXML private ToggleButton toggleConfirm;

    @FXML private ProgressBar strengthBar;
    @FXML private Label strengthLabel;
    @FXML private Label matchLabel;

    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        // Bind texte entre PasswordField/TextField (new)
        newPasswordTextField.textProperty().bindBidirectional(newPasswordField.textProperty());
        confirmPasswordTextField.textProperty().bindBidirectional(confirmPasswordField.textProperty());

        // Listeners: force + match
        newPasswordField.textProperty().addListener((obs, oldV, newV) -> updateStrengthAndMatch());
        confirmPasswordField.textProperty().addListener((obs, oldV, newV) -> updateStrengthAndMatch());

        updateStrengthAndMatch();
    }

    @FXML
    private void onToggleNew() {
        boolean show = toggleNew.isSelected();
        setShowField(newPasswordField, newPasswordTextField, show);
    }

    @FXML
    private void onToggleConfirm() {
        boolean show = toggleConfirm.isSelected();
        setShowField(confirmPasswordField, confirmPasswordTextField, show);
    }

    private void setShowField(PasswordField pf, TextField tf, boolean show) {
        tf.setVisible(show);
        tf.setManaged(show);

        pf.setVisible(!show);
        pf.setManaged(!show);
    }

    private void updateStrengthAndMatch() {
        String p = newPasswordField.getText();
        Strength s = computeStrength(p);

        strengthBar.setProgress(s.progress);
        strengthLabel.setText("Force : " + s.label);
        strengthLabel.setStyle("-fx-text-fill: " + s.color + "; -fx-font-weight: bold;");

        String confirm = confirmPasswordField.getText();
        if (isBlank(p) && isBlank(confirm)) {
            matchLabel.setText("");
            return;
        }

        if (!isBlank(confirm) && p.equals(confirm)) {
            matchLabel.setText("✔ Confirmation correcte");
            matchLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
        } else if (!isBlank(confirm)) {
            matchLabel.setText("✖ Confirmation différente");
            matchLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
        } else {
            matchLabel.setText("");
        }
    }

    private Strength computeStrength(String p) {
        if (p == null) p = "";
        int score = 0;

        if (p.length() >= 8) score++;
        if (p.matches(".*[A-Z].*")) score++;
        if (p.matches(".*[a-z].*")) score++;
        if (p.matches(".*\\d.*")) score++;
        if (p.matches(".*[^A-Za-z0-9].*")) score++;

        // score 0..5
        double progress = Math.min(1.0, score / 5.0);

        if (p.isBlank()) return new Strength(0, "-", "#666666");
        if (score <= 1) return new Strength(progress, "Faible", "#c62828");
        if (score == 2) return new Strength(progress, "Moyenne", "#ef6c00");
        if (score == 3) return new Strength(progress, "Bonne", "#2e7d32");
        return new Strength(progress, "Forte", "#1565c0");
    }

    private static class Strength {
        final double progress;
        final String label;
        final String color;
        Strength(double progress, String label, String color) {
            this.progress = progress;
            this.label = label;
            this.color = color;
        }
    }

    @FXML
    private void onSave() {

        errorLabel.setText("");
        successLabel.setText("");

        if (Session.getCurrentUser() == null) {
            errorLabel.setText("Session expirée. Reconnectez-vous.");
            SceneRouter.goTo("login.fxml", "Connexion");
            return;
        }

        String currentPass = currentPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (isBlank(currentPass) || isBlank(newPass) || isBlank(confirm)) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        if (!newPass.equals(confirm)) {
            errorLabel.setText("Confirmation incorrecte.");
            return;
        }

        // un minimum un peu plus solide
        if (newPass.length() < 8) {
            errorLabel.setText("Mot de passe trop court (min 8 caractères).");
            return;
        }

        boolean ok = BCrypt.checkpw(currentPass, Session.getCurrentUser().getPasswordHash());
        if (!ok) {
            errorLabel.setText("Mot de passe actuel incorrect.");
            return;
        }

        String newHash = BCrypt.hashpw(newPass, BCrypt.gensalt());
        userDAO.updatePasswordHash(Session.getCurrentUser().getId(), newHash);

        successLabel.setText("Mot de passe modifié ✅ Déconnexion...");
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
        toggleNew.setSelected(false);
        toggleConfirm.setSelected(false);
        setShowField(newPasswordField, newPasswordTextField, false);
        setShowField(confirmPasswordField, confirmPasswordTextField, false);

        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> {
            Session.clear();
            SceneRouter.goTo("login.fxml", "Connexion");
        });
        pause.play();
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    @FXML
    private void onBack() {
        if (Session.getCurrentUser() == null) {
            SceneRouter.goTo("login.fxml", "Connexion");
            return;
        }

        Role role = Session.getCurrentUser().getRole();
        if (role == Role.RH) {
            SceneRouter.goTo("rh_dashboard.fxml", "Dashboard RH");
        } else {
            SceneRouter.goTo("employee_dashboard.fxml", "Dashboard Employé");
        }
    }
}
