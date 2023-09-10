package com.trevorBower.appointmentScheduler.controller;

import com.trevorBower.appointmentScheduler.helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.trevorBower.appointmentScheduler.helper.AppointmentQuery.updateAppointmentAll;
import static com.trevorBower.appointmentScheduler.helper.ContactQuery.getContactIdByName;
import static com.trevorBower.appointmentScheduler.helper.ExtraMethods.*;

public class ModifyAppointmentController {

    // Input fields
    @FXML
    private TextField appointmentIdField;

    @FXML
    private TextField appointmentTitleField;

    @FXML
    private TextField appointmentDescriptionField;

    @FXML
    private TextField appointmentLocationField;

    @FXML
    private ComboBox<String> contactComboBox;

    @FXML
    private TextField appointmentTypeField;

    @FXML
    private DatePicker appointmentStartDatePicker;

    @FXML
    private TextField appointmentStartTimeField;

    @FXML
    private DatePicker appointmentEndDatePicker;

    @FXML
    private TextField appointmentEndTimeField;

    @FXML
    private ComboBox<Integer> customerIdComboBox;

    @FXML
    private ComboBox<Integer> userIdComboBox;

    @FXML
    private Button cancelButton;


    /**
     * Initializes form to specifications and sets default values
     */
    public void initialize() {
        // Disable Appointment ID field
        appointmentIdField.setDisable(true);
        // Config ComboBoxes
        populateContactComboBox();
        populateCustomerIdComboBox();
        populateUserIdComboBox();
    }

    /**
     * Fetches values for appointment selected in appointments table view
     * @param selectedAppointment Appointment selected in appointments table view
     * @throws SQLException SQL query fails
     */
    public void fetchAppointmentValues(ObservableList<Object> selectedAppointment) throws SQLException {
        // selectedAppointment format: [0Appointment ID, 1Title, 2Description, 3Location, 4Type, 5Start DateTime,
        // 6End DateTime, 7Customer ID, 8User ID, 9Contact Name]

        // Get startDateTime UTC values and convert to system default time
        String startDateTimeString = (String) selectedAppointment.get(5);
        LocalDateTime startDateTimeUtc = LocalDateTime.parse(startDateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime startDateTimeSystem = convertUtcTimeToSystem(startDateTimeUtc);
        // Separate startDateTime into startDate and startTime to populate corresponding fields
        LocalDate startDate = startDateTimeSystem.toLocalDate();
        LocalTime startTime = startDateTimeSystem.toLocalTime();

        // Get endDateTime UTC values and convert to system default time
        String endDateTimeString = (String) selectedAppointment.get(6);
        LocalDateTime endDateTimeUtc = LocalDateTime.parse(endDateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endDateTimeSystem = convertUtcTimeToSystem(endDateTimeUtc);
        // Separate endDateTime into endDate and endTime to populate corresponding fields
        LocalDate endDate = endDateTimeSystem.toLocalDate();
        LocalTime endTime = endDateTimeSystem.toLocalTime();

        // Populate form values
        appointmentIdField.setText(String.valueOf(selectedAppointment.get(0)));
        appointmentTitleField.setText(String.valueOf(selectedAppointment.get(1)));
        appointmentDescriptionField.setText(String.valueOf(selectedAppointment.get(2)));
        appointmentLocationField.setText(String.valueOf(selectedAppointment.get(3)));
        contactComboBox.setValue(String.valueOf(selectedAppointment.get(9)));
        appointmentTypeField.setText(String.valueOf(selectedAppointment.get(4)));
        appointmentStartDatePicker.setValue(startDate);
        appointmentStartTimeField.setText(String.valueOf(startTime));
        appointmentEndDatePicker.setValue(endDate);
        appointmentEndTimeField.setText(String.valueOf(endTime));
        customerIdComboBox.setValue(Integer.parseInt(String.valueOf(selectedAppointment.get(7))));
        userIdComboBox.setValue(Integer.parseInt(String.valueOf(selectedAppointment.get(8))));
    }

    /**
     * Populates contact combo box with contact name options
     */
    public void populateContactComboBox() {
        List<String> contactNameList = new ArrayList<>();  // List to store contact names
        try {
            // Select contact info from database and insert into contactNameList
            String sql = "SELECT Contact_Name FROM contacts ORDER BY Contact_Name ASC";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String contactName = rs.getString("Contact_Name");
                contactNameList.add(contactName);
            }
            // Populate contactComboBox with contactNameList
            ObservableList<String> contactOptions = FXCollections.observableArrayList(contactNameList);
            contactComboBox.setItems(contactOptions);
            contactComboBox.setValue(contactOptions.get(0)); // Set default value to first option
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Populates combo box with customer ID options
     */
    public void populateCustomerIdComboBox() {
        List<Integer> customerIdList = new ArrayList<>();  // List to store customer ID's
        try {
            // Select customer ID info from database and insert into customerIdList
            String sql = "SELECT Customer_ID FROM customers ORDER BY Customer_ID ASC";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int customerId = rs.getInt("Customer_ID");
                customerIdList.add(customerId);
            }
            // Populate contactComboBox with customerIdList
            ObservableList<Integer> customerIdOptions = FXCollections.observableArrayList(customerIdList);
            customerIdComboBox.setItems(customerIdOptions);
            customerIdComboBox.setValue(customerIdOptions.get(0)); // Set default value to first option
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Populates combo box with user ID options
     */
    public void populateUserIdComboBox() {
        List<Integer> userIdList = new ArrayList<>();  // List to store user ID's
        try {
            // Select user ID info from database and insert into userIdList
            String sql = "SELECT User_ID FROM users ORDER BY User_ID ASC";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("User_ID");
                userIdList.add(userId);
            }
            // Populate contactComboBox with customerIdList
            ObservableList<Integer> userIdOptions = FXCollections.observableArrayList(userIdList);
            userIdComboBox.setItems(userIdOptions);
            userIdComboBox.setValue(userIdOptions.get(0)); // Set default value to first option
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Takes input values, validates them, and updates appointment in SQL database. See included comments for more details.
     */
    public void modifyAppointment() {
        TextField[] addAppointmentFields = new TextField[]{
                appointmentTitleField,  // Element 0
                appointmentDescriptionField,  // Element 1
                appointmentLocationField,  // Element 2
                appointmentTypeField,  // Element 3
                appointmentStartTimeField,  // Element 4
                appointmentEndTimeField,  // Element 5
        };

        for (TextField f : addAppointmentFields) {
            f.setStyle("-fx-border-color: #999999"); //Resets red borders from previous errors
        }

        // Check for empty fields
        for (TextField f : addAppointmentFields) {
            if (f.getText().trim().isEmpty()) {  // If any field is empty...
                f.setStyle("-fx-border-color: red");  // Highlight empty field
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(null);
                alert.setContentText("Field missing value.");
                alert.showAndWait();  // Display error
                return; // Cancel updating appointment and return to form
            }
        }

        // Insert values into database
        try {
            // Set variables for insert
            String appointmentIdString = appointmentIdField.getText();
            int appointmentId = Integer.parseInt(appointmentIdString);
            String title = addAppointmentFields[0].getText().trim();
            String description = addAppointmentFields[1].getText().trim();
            String location = addAppointmentFields[2].getText().trim();
            String type = addAppointmentFields[3].getText().trim();
            int customerId = customerIdComboBox.getValue();
            int userId = userIdComboBox.getValue();
            String contactName = contactComboBox.getValue();
            int contactId = getContactIdByName(contactName);

            // Convert inputs to LocalDateTime values for start date/time
            LocalDateTime startDateTimeSystem;
            try {
                LocalDate startDate = appointmentStartDatePicker.getValue();
                String startTimeString = addAppointmentFields[4].getText().trim();
                LocalTime startTime = LocalTime.parse(startTimeString);
                startDateTimeSystem = LocalDateTime.of(startDate, startTime);
            } catch (Exception e) {
                appointmentStartTimeField.setStyle("-fx-border-color: red");  // Highlight field
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(null);
                alert.setContentText("Incorrect time format. (Ex: 00:00 or 00:00:00)");
                alert.showAndWait();  // Display error
                return; // Cancel updating appointment and return to form
            }
            // Convert inputs to LocalDateTime values for start date/time
            LocalDateTime endDateTimeSystem;
            try {
                LocalDate endDate = appointmentEndDatePicker.getValue();
                String endTimeString = addAppointmentFields[5].getText().trim();
                LocalTime endTime = LocalTime.parse(endTimeString);
                endDateTimeSystem = LocalDateTime.of(endDate, endTime);
            } catch (Exception e) {
                appointmentEndTimeField.setStyle("-fx-border-color: red");  // Highlight field
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(null);
                alert.setContentText("Incorrect time format. (Ex: 00:00 or 00:00:00)");
                alert.showAndWait();  // Display error
                return; // Cancel updating appointment and return to form
            }

            // Validation checks for startDateTime and endDateTime values:
            // Check if dates within business hours
            boolean startWithinBusinessHours = checkIfBusinessHours(startDateTimeSystem);
            if (!startWithinBusinessHours) {
                appointmentStartTimeField.setStyle("-fx-border-color: red");
                return;  // Cancel operation
            }
            boolean endWithinBusinessHours = checkIfBusinessHours(endDateTimeSystem);
            if (!endWithinBusinessHours) {
                appointmentEndTimeField.setStyle("-fx-border-color: red");
                return; // Cancel operation
            }
            // Check if end endDateTime is before startDateTime
            if (endDateTimeSystem.isBefore(startDateTimeSystem)) {
                appointmentStartDatePicker.setStyle("-fx-border-color: red");
                appointmentStartTimeField.setStyle("-fx-border-color: red");
                appointmentEndDatePicker.setStyle("-fx-border-color: red");
                appointmentEndTimeField.setStyle("-fx-border-color: red");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(null);
                alert.setContentText("Appointment end date/time cannot be before start date/time");
                alert.showAndWait();  // Display error
                return; // Cancel operation
            }
            // Check if any overlapping appointments
            boolean noOverlappingAppointments = checkForOverlappingAppointmentsModify(appointmentId,
                    startDateTimeSystem, endDateTimeSystem, customerId);
            if (!noOverlappingAppointments) {
                appointmentStartDatePicker.setStyle("-fx-border-color: red");
                appointmentStartTimeField.setStyle("-fx-border-color: red");
                appointmentEndDatePicker.setStyle("-fx-border-color: red");
                appointmentEndTimeField.setStyle("-fx-border-color: red");
                return;
            }


            // Insert values into database
            updateAppointmentAll(appointmentId, title, description, location, type, startDateTimeSystem, endDateTimeSystem, customerId,
                    userId, contactId);
            switchToMainForm();
        } catch (Exception e) {
            System.out.println("Appointment update failed.");
            e.printStackTrace();
        }
    }

    /**
     * Switches to main form
     * @throws IOException Main form not found
     */
    public void switchToMainForm() throws IOException {
        // Open main form
        Parent root = FXMLLoader.load(getClass().getResource("/main/resources/mainForm.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        // Close previous form
        Stage loginStage = (Stage) cancelButton.getScene().getWindow();
        loginStage.close();
    }
}
