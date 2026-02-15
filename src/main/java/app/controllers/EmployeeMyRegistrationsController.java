package app.controllers;
import app.dao.ObjectiveDAO;
import app.dao.EventDAO;

import app.dao.RegistrationDAO;
import app.model.MyRegistrationRow;
import app.router.SceneRouter;
import app.session.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class EmployeeMyRegistrationsController {

    @FXML private TableView<MyRegistrationRow> tableView;

    @FXML private TableColumn<MyRegistrationRow, Integer> colEventId;
    @FXML private TableColumn<MyRegistrationRow, String> colSport;
    @FXML private TableColumn<MyRegistrationRow, LocalDate> colDate;
    @FXML private TableColumn<MyRegistrationRow, String> colLocation;
    @FXML private TableColumn<MyRegistrationRow, String> colRegStatus;

    private final ObservableList<MyRegistrationRow> data = FXCollections.observableArrayList();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private final ObjectiveDAO objectiveDAO = new ObjectiveDAO();
    private final EventDAO eventDAO = new EventDAO();

    @FXML
    public void initialize() {
        colEventId.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        colSport.setCellValueFactory(new PropertyValueFactory<>("sport"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colRegStatus.setCellValueFactory(new PropertyValueFactory<>("registrationStatus"));

        loadMyRegistrations();
    }

    private void loadMyRegistrations() {
        int employeeId = Session.getCurrentUser().getId();
        data.clear();
        data.addAll(registrationDAO.findRowsByEmployee(employeeId));
        tableView.setItems(data);
        tableView.refresh();
    }

    @FXML
    private void onCancel() {
        MyRegistrationRow selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Sélectionne une inscription.", ButtonType.OK).showAndWait();
            return;
        }

        int employeeId = Session.getCurrentUser().getId();

        String sql = "UPDATE registrations SET status = 'CANCELLED' WHERE event_id = ? AND employee_id = ?";

        try (java.sql.Connection cnx = app.db.Database.getConnection();
             java.sql.PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, selected.getEventId());
            ps.setInt(2, employeeId);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

// 🔎 Récupérer l'objectif lié à l'événement
        app.model.Event event = eventDAO.findById(selected.getEventId());

        if (event.getObjectiveId() != null) {
            objectiveDAO.updateStatusAutomatically(event.getObjectiveId());
        }

        loadMyRegistrations();
    }

    @FXML
    private void onBack() {
        SceneRouter.goTo("employee_dashboard.fxml", "Dashboard Employé");
    }
}
