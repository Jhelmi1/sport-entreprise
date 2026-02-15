package app.controllers;

import app.dao.UserDAO;
import app.model.User;
import app.router.SceneRouter;
import app.services.NotificationService;
import app.session.Session;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController {

    @FXML private TextField emailField;

    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private ToggleButton togglePassword;

    @FXML private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    private void onTogglePassword() {
        boolean show = togglePassword.isSelected();

        passwordTextField.setVisible(show);
        passwordTextField.setManaged(show);

        passwordField.setVisible(!show);
        passwordField.setManaged(!show);
    }

    @FXML
    private void onLogin() {

        errorLabel.setText("");

        String email = emailField.getText();
        String password = passwordField.getText();

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        User user = userDAO.findByEmail(email);
        if (user == null) {
            errorLabel.setText("Utilisateur non trouvé.");
            return;
        }

        boolean ok = BCrypt.checkpw(password, user.getPasswordHash());
        if (!ok) {
            errorLabel.setText("Mot de passe incorrect.");
            return;
        }

        Session.setCurrentUser(user);

        // ✅ Générer automatiquement les rappels 24h (si inscrit à un event bientôt)
        NotificationService.generateEventRemindersForUser(user.getId(), 24);

        switch (user.getRole()) {
            case RH -> SceneRouter.goTo("rh_dashboard.fxml", "Dashboard RH");
            default -> SceneRouter.goTo("employee_dashboard.fxml", "Dashboard Employé");
        }
    }

    @FXML
    private void onGoRegister() {
        SceneRouter.goTo("register.fxml", "Créer un compte");
    }
}
