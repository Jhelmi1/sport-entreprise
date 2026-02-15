package app.controllers;

import app.dao.EventDAO;
import app.model.Event;
import app.model.EventStatus;
import app.router.SceneRouter;
import app.services.NotificationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventManagementController {

    @FXML private TextField sportField;
    @FXML private DatePicker datePicker;
    @FXML private TextField locationField;
    @FXML private TextField capacityField;

    @FXML private TableView<Event> tableView;

    @FXML private TableColumn<Event, Integer> colId;
    @FXML private TableColumn<Event, String> colSport;
    @FXML private TableColumn<Event, LocalDate> colDate;
    @FXML private TableColumn<Event, String> colLocation;
    @FXML private TableColumn<Event, Integer> colCapacity;
    @FXML private TableColumn<Event, EventStatus> colStatus;

    private final ObservableList<Event> data = FXCollections.observableArrayList();
    private final EventDAO eventDAO = new EventDAO();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSport.setCellValueFactory(new PropertyValueFactory<>("sport"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadEvents();
    }

    private void loadEvents() {
        data.clear();
        data.addAll(eventDAO.findAll());
        tableView.setItems(data);
    }

    @FXML
    private void onAdd() {

        String sport = sportField.getText();
        LocalDate date = datePicker.getValue();
        String location = locationField.getText();
        String capText = capacityField.getText();

        if (sport == null || sport.isBlank() ||
                date == null ||
                location == null || location.isBlank() ||
                capText == null || capText.isBlank()) {

            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Champs incomplets");
            a.setHeaderText(null);
            a.setContentText("Veuillez remplir tous les champs.");
            a.showAndWait();
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capText);
        } catch (NumberFormatException ex) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Capacité invalide");
            a.setHeaderText(null);
            a.setContentText("La capacité doit être un nombre.");
            a.showAndWait();
            return;
        }

        Event event = new Event(sport, date, location, capacity, null, 1);
        event.setStatus(EventStatus.OPEN);

        // ✅ Insert retourne l'ID généré
        int newId = eventDAO.insert(event);

        // ✅ Notification pour tous les employés
        if (newId > 0) {
            LocalDateTime eventDateTime = date.atStartOfDay();
            NotificationService.notifyNewEvent(newId, sport, eventDateTime);
        }

        loadEvents();

        sportField.clear();
        locationField.clear();
        capacityField.clear();
        datePicker.setValue(null);

        Alert ok = new Alert(Alert.AlertType.INFORMATION);
        ok.setTitle("Succès");
        ok.setHeaderText(null);
        ok.setContentText("Événement ajouté ✅");
        ok.showAndWait();
    }

    @FXML
    private void onBack() {
        SceneRouter.goTo("rh_dashboard.fxml", "Dashboard RH");
    }

    @FXML
    private void onDelete() {

        Event selected = tableView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            System.out.println("Aucune sélection");
            return;
        }

        // 1) Interdire si inscriptions existantes
        if (eventDAO.hasRegistrations(selected.getId())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Suppression impossible");
            alert.setHeaderText("Cet événement contient déjà des inscriptions.");
            alert.setContentText("Vous ne pouvez pas le supprimer. Vous pouvez le clôturer ou l’annuler.");
            alert.showAndWait();
            return;
        }

        // 2) Soft delete = CANCELLED
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Annuler l'événement ?");
        confirm.setContentText("L’événement passera au statut CANCELLED (soft delete).");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            eventDAO.cancelById(selected.getId());
            loadEvents();
        }
    }
}
