package app.controllers;

import app.dao.UserDAO;
import app.model.Role;
import app.model.User;
import app.model.UserStatus;
import app.router.SceneRouter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;

    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private ToggleButton togglePassword;

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

        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
        confirmPasswordTextField.textProperty().bindBidirectional(confirmPasswordField.textProperty());

        passwordField.textProperty().addListener((obs, o, n) -> updateStrengthAndMatch());
        confirmPasswordField.textProperty().addListener((obs, o, n) -> updateStrengthAndMatch());

        updateStrengthAndMatch();
    }

    @FXML
    private void onTogglePassword() {
        boolean show = togglePassword.isSelected();
        setShowField(passwordField, passwordTextField, show);
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

        String p = passwordField.getText();
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
    private void onRegister() {

        errorLabel.setText("");
        successLabel.setText("");

        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (isBlank(fullName) || isBlank(email) || isBlank(pass) || isBlank(confirm)) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        if (!pass.equals(confirm)) {
            errorLabel.setText("Confirmation incorrecte.");
            return;
        }

        if (pass.length() < 8) {
            errorLabel.setText("Mot de passe trop court (min 8 caractères).");
            return;
        }

        // Simple règle: au moins 3/5 critères (bonne ou forte)
        Strength st = computeStrength(pass);
        if (st.progress < 0.6) {
            errorLabel.setText("Mot de passe trop faible. Ajoute majuscules/chiffres/symbole.");
            return;
        }

        String hash = BCrypt.hashpw(pass, BCrypt.gensalt());
        User user = new User(fullName, email, hash, Role.EMPLOYEE, UserStatus.ACTIVE);

        int id = userDAO.insert(user);
        if (id == -1) {
            errorLabel.setText("Cet email existe déjà.");
            return;
        }

        successLabel.setText("Compte créé ✅ Vous pouvez vous connecter.");
        clearFields();
        updateStrengthAndMatch();
    }

    private void clearFields() {
        fullNameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();

        togglePassword.setSelected(false);
        toggleConfirm.setSelected(false);

        setShowField(passwordField, passwordTextField, false);
        setShowField(confirmPasswordField, confirmPasswordTextField, false);
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    @FXML
    private void onGoLogin() {
        SceneRouter.goTo("login.fxml", "Connexion");
    }
}
