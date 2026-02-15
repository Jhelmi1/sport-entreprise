package app.controllers;

import app.dao.EventDAO;
import app.dao.ObjectiveDAO;
import app.dao.RegistrationDAO;
import app.model.Event;
import app.model.EventStatus;
import app.router.SceneRouter;
import app.services.NotificationService;
import app.session.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmployeeEventsController {

    @FXML private TableView<Event> tableView;

    @FXML private TableColumn<Event, Integer> colId;
    @FXML private TableColumn<Event, String> colSport;
    @FXML private TableColumn<Event, LocalDate> colDate;
    @FXML private TableColumn<Event, String> colLocation;
    @FXML private TableColumn<Event, Integer> colCapacity;
    @FXML private TableColumn<Event, EventStatus> colStatus;

    private final ObservableList<Event> data = FXCollections.observableArrayList();
    private final EventDAO eventDAO = new EventDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private final ObjectiveDAO objectiveDAO = new ObjectiveDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSport.setCellValueFactory(new PropertyValueFactory<>("sport"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadOpenEvents();
    }

    private void loadOpenEvents() {
        data.clear();
        data.addAll(eventDAO.findOpenEvents());
        tableView.setItems(data);
    }

    @FXML
    private void onRegister() {

        if (Session.getCurrentUser() == null) {
            new Alert(Alert.AlertType.WARNING, "Session expirée. Reconnectez-vous.", ButtonType.OK).showAndWait();
            SceneRouter.goTo("login.fxml", "Connexion");
            return;
        }

        int employeeId = Session.getCurrentUser().getId();

        Event selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Sélectionne un événement.", ButtonType.OK).showAndWait();
            return;
        }

        // capacité
        int count = registrationDAO.countForEvent(selected.getId());
        if (count >= selected.getCapacity()) {
            new Alert(Alert.AlertType.WARNING, "Capacité pleine.", ButtonType.OK).showAndWait();
            return;
        }

        // double inscription
        if (registrationDAO.exists(selected.getId(), employeeId)) {
            new Alert(Alert.AlertType.INFORMATION, "Déjà inscrit.", ButtonType.OK).showAndWait();
            return;
        }

        // inscription
        registrationDAO.insert(selected.getId(), employeeId);

        // fermer event si complet
        eventDAO.closeIfFull(selected.getId());

        // Mise à jour objectif si lié
        if (selected.getObjectiveId() != null) {
            objectiveDAO.updateStatusAutomatically(selected.getObjectiveId());
        }

        // ✅ NOTIFICATION: inscription confirmée
        // (On utilise le sport comme "titre" et la date en LocalDateTime)
        LocalDateTime eventDateTime = selected.getEventDate().atStartOfDay();
        NotificationService.notifyRegistrationConfirmed(
                employeeId,
                selected.getId(),
                selected.getSport(),
                eventDateTime
        );

        // refresh table (si event devient CLOSED après inscription)
        loadOpenEvents();

        new Alert(Alert.AlertType.INFORMATION, "Inscription réussie ✅", ButtonType.OK).showAndWait();
    }

    @FXML
    private void onBack() {
        SceneRouter.goTo("employee_dashboard.fxml", "Dashboard Employé");
    }
}
