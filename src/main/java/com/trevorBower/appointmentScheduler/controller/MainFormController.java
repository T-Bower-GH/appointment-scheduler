package com.trevorBower.appointmentScheduler.controller;

import com.trevorBower.appointmentScheduler.helper.JDBC;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.trevorBower.appointmentScheduler.helper.AppointmentQuery.*;
import static com.trevorBower.appointmentScheduler.helper.CustomerQuery.deleteCustomer;
import static com.trevorBower.appointmentScheduler.helper.ExtraMethods.convertUtcTimeToSystem;

public class MainFormController {

    // Initialize User ID (Established during login)
    public static int currentUserId;

    // Initialize lists for table data
    private ObservableList<ObservableList> allAppointments = FXCollections.observableArrayList();
    private ObservableList<ObservableList> allCustomers = FXCollections.observableArrayList();

    // Text
    @FXML
    private Text appointmentsText;

    @FXML
    private Text customersText;

    @FXML
    private Text filtersText;

    // Radio Buttons
    @FXML
    private RadioButton allAppointmentsRadio;

    @FXML
    private RadioButton currentMonthRadio;

    @FXML
    private RadioButton currentWeekRadio;

    // Buttons
    @FXML
    private Button addAppointmentButton;

    @FXML
    private Button updateAppointmentButton;

    @FXML
    private Button deleteAppointmentButton;

    @FXML
    private Button addCustomerButton;

    @FXML
    private Button updateCustomerButton;

    @FXML
    private Button deleteCustomerButton;

    @FXML
    private Button reportsButton;

    @FXML
    private Button logoutButton;

    // Tables
    @FXML
    private TableView<ObservableList> appointmentsTable;

    @FXML
    private TableView<ObservableList> customersTable;


    // Appointment table columns
    @FXML
    private TableColumn<ObservableList<String>, String> apptIdCol;
    @FXML
    private TableColumn<ObservableList<String>, String> apptTitleCol;
    @FXML
    private TableColumn<ObservableList<String>, String> apptDescriptionCol;
    @FXML
    private TableColumn<ObservableList<String>, String> apptLocationCol;
    @FXML
    private TableColumn<ObservableList<String>, String> apptContactCol;
    @FXML
    private TableColumn<ObservableList<String>, String> apptTypeCol;
    @FXML
    private TableColumn<ObservableList<String>, String> apptStartCol;
    @FXML
    private TableColumn<ObservableList<String>, String> apptEndCol;
    @FXML
    private TableColumn<ObservableList<String>, String> apptCustomerIdCol;
    @FXML
    private TableColumn<ObservableList<String>, String> apptUserIdCol;

    // Customer table columns
    @FXML
    private TableColumn<ObservableList<String>, String> custIdCol;

    @FXML
    private TableColumn<ObservableList<String>, String> custNameCol;

    @FXML
    private TableColumn<ObservableList<String>, String> custAddressCol;

    @FXML
    private TableColumn<ObservableList<String>, String> custStateCol;

    @FXML
    private TableColumn<ObservableList<String>, String> custPostalCol;

    @FXML
    private TableColumn<ObservableList<String>, String> custPhoneCol;


    /**
     * Initializes form to specifications, sets default values, and configures table views
     */
    @FXML
    private void initialize() {
        buildAllAppointmentsData();
        buildCustomerData();
        allAppointmentsRadio.setSelected(true);
    }

    /**
     * Displays all specified appointments data from SQL database (Default option)
     * Lambdas used here to keep code concise and efficient
     * @return All appointments data from SQL database to be used in other table view configurations
     */
    public ObservableList<ObservableList> buildAllAppointmentsData() {
        // Clear existing table view
        appointmentsTable.getItems().clear();

        // Clear previous appointments data
        allAppointments.clear();

        // Config raw value table columns  *Lambdas
        apptIdCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
        apptTitleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
        apptDescriptionCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));
        apptLocationCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3)));
        apptTypeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(4)));
        apptCustomerIdCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(7)));
        apptUserIdCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(8)));
        apptContactCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(9)));

        // Convert start and end time from UTC to system default time to display in tableview
        apptStartCol.setCellValueFactory(cellData -> {
            String utcDateTime = cellData.getValue().get(5);
            //System.out.println(utcDateTime);
            LocalDateTime systemDateTime = convertUtcTimeToSystem(LocalDateTime.parse(utcDateTime.replace(" ", "T")));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTime = systemDateTime.format(formatter);
            return new SimpleStringProperty(formattedTime);
                });
        apptEndCol.setCellValueFactory(cellData -> {
            String utcDateTime = cellData.getValue().get(6);
            //System.out.println(utcDateTime);
            LocalDateTime systemDateTime = convertUtcTimeToSystem(LocalDateTime.parse(utcDateTime.replace(" ", "T")));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTime = systemDateTime.format(formatter);
            return new SimpleStringProperty(formattedTime);
        });

        try {
            // Select appropriate appointment values in "appointments" and "contacts"
            String appointmentTableQuery = "SELECT c.Appointment_ID, c.Title, c.Description, c.Location, c.Type, " +
                    "c.Start, c.End, c.Customer_ID, c.User_ID, d.Contact_Name FROM appointments c " // Select statement
                    + "JOIN contacts d ON c.Contact_ID = d.Contact_ID " // Join table to get contact info
                    + "ORDER BY c.Appointment_ID";
            PreparedStatement psAppointments = JDBC.connection.prepareStatement(appointmentTableQuery);
            ResultSet rsAppointments = psAppointments.executeQuery();

            // Add data to observable list
            while (rsAppointments.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rsAppointments.getMetaData().getColumnCount(); i++) {
                    row.add(rsAppointments.getString(i));
                }
                allAppointments.add(row);
            }

            // Set table view
            appointmentsTable.setItems(allAppointments);
            return allAppointments;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Populates appointments table view with only appointments in the current month
     */
    public void buildCurrentMonthAppointmentsData() {
        // Clear existing table view
        appointmentsTable.getItems().clear();

        // Get current month
        YearMonth currentYearMonth = YearMonth.now();
        Month currentMonth = currentYearMonth.getMonth();

        // Create a new list to store filtered appointments
        ObservableList filteredAppointments = FXCollections.observableArrayList();

        // Iterate results of buildAllAppointmentsData() to filter appointments for the current month
        for (ObservableList<String> appointment : buildAllAppointmentsData()) {
            // Get startDateTime string in UTC time zone from appointments
            String startDateTimeStringUtc = appointment.get(5);
            // Reformat and parse startDateTimeStringUtc to convert to LocalDateTime value
            LocalDateTime startDateTimeUtc = LocalDateTime.parse(startDateTimeStringUtc.replace(" ", "T"));
            // Convert from UTC to system default time
            LocalDateTime startDateTimeSystem = convertUtcTimeToSystem(startDateTimeUtc);
            // If appointment start date month in system default time matches the current month...
            if (startDateTimeSystem.getMonth().equals(currentMonth)) {
                // Add appointment to the filtered list
                filteredAppointments.add(appointment);
            }
        }

        // Set table view
        appointmentsTable.setItems(filteredAppointments);
    }


    /**
     * Populates appointments table view with only appointments in the current week
     */
    public void buildCurrentWeekAppointmentsData() {
        // Clear existing table view
        appointmentsTable.getItems().clear();

        // Get current week (Assumes Monday is the first day of the week)
        LocalDate today = LocalDate.now();
        LocalDate currentWeekFirstDay = today.with(DayOfWeek.MONDAY);
        LocalDate currentWeekLastDay = today.with(DayOfWeek.SUNDAY);

        // Create a new list to store filtered appointments
        ObservableList filteredAppointments = FXCollections.observableArrayList();

        // Iterate results of buildAllAppointmentsData() to filter appointments for the current month
        for (ObservableList<String> appointment : buildAllAppointmentsData()) {
            // Get startDateTime string in UTC time zone from appointments
            String startDateTimeStringUtc = appointment.get(5);
            // Reformat and parse startDateTimeStringUtc to convert to LocalDateTime value
            LocalDateTime startDateTimeUtc = LocalDateTime.parse(startDateTimeStringUtc.replace(" ", "T"));
            // Convert from UTC to system default time
            LocalDateTime startDateTimeSystem = convertUtcTimeToSystem(startDateTimeUtc);
            // If appointment start date is in the current week...
            if (startDateTimeSystem.toLocalDate().isAfter(currentWeekFirstDay.minusDays(1)) // On or after first day
            && startDateTimeSystem.toLocalDate().isBefore(currentWeekLastDay.plusDays(1))) { // On or before last day
                // Add appointment to the filtered list
                filteredAppointments.add(appointment);
            }
        }

        // Set table view
        appointmentsTable.setItems(filteredAppointments);
    }


    /**
     * Populates customer table view with all specified customer data
     * Lambdas used here to keep code concise and efficient
     */
    public void buildCustomerData() {
        // Clear existing data
        customersTable.getItems().clear();

        // Config table columns  *Lambdas
        custIdCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
        custNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
        custAddressCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));
        custPostalCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3)));
        custPhoneCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(4)));
        custStateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(5)));

        try {
            // Select appropriate appointment values in "customers" and "first_level_divisions"
            String customerTableQuery = "SELECT c.Customer_ID, c.Customer_Name, c.Address, c.Postal_Code, c.Phone, d.Division " +
                    "FROM customers c " +
                    "JOIN first_level_divisions d ON c.Division_ID = d.Division_ID " +
                    "ORDER BY c.Customer_ID";
            PreparedStatement psCustomers = JDBC.connection.prepareStatement(customerTableQuery);
            ResultSet rsCustomers = psCustomers.executeQuery();

            // Add data to observable list
            while (rsCustomers.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rsCustomers.getMetaData().getColumnCount(); i++) {
                    row.add(rsCustomers.getString(i));
                }
                allCustomers.add(row);
            }

            // Set table view
            customersTable.setItems(allCustomers);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Sets current userId from login
     * @param userId
     */
    public static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }


    /**
     * Switches to add customer form
     * @throws IOException Add customer form not found
     */
    public void switchToAddCustomer() throws IOException {
        // Switch to addCustomer form
        Parent root = FXMLLoader.load(getClass().getResource("/main/resources/addCustomer.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        // Close main form
        Stage mainStage = (Stage) logoutButton.getScene().getWindow();
        mainStage.close();
    }

    /**
     * Switches to modify customer form
     * @throws IOException Modify customer form not found
     * @throws SQLException SQL query fails
     */
    public void switchToModifyCustomer() throws IOException, SQLException {
        if (customersTable.getSelectionModel().getSelectedItem() == null) { // If a customer is not selected
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(null);
            alert.setContentText("Please select a customer to modify.");
            alert.showAndWait();
        } else {  // If a customer is selected, the Modify Customer form opens with text fields populated with
            // appropriate values for the customer
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/modifyCustomer.fxml"));
            Parent root = loader.load();
            ModifyCustomerController controller = loader.getController(); // Retrieves data from ModifyCustomer Controller to
            // enable fetchCustomerValues method

            // Populate the form with existing values
            ObservableList<Object> selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
            controller.fetchCustomerValues(selectedCustomer);

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            // Close main form
            Stage mainStage = (Stage) logoutButton.getScene().getWindow();
            mainStage.close();
        }
    }


    /**
     * Deletes selected customer from SQL database after checks and user confirmation then refreshes table views
     * @throws SQLException SQL query fails
     */
    public void deleteSelectedCustomer() throws SQLException {
        ObservableList<String> selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) { // If a customer is not selected
            Alert alertNoSelection = new Alert(Alert.AlertType.ERROR);
            alertNoSelection.setTitle(null);
            alertNoSelection.setContentText("Please select a customer to delete.");
            alertNoSelection.showAndWait();
        } else {
            // Confirmation of deletion required before customer is deleted
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setContentText("Are you sure you want to delete the selected customer?");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() == ButtonType.OK) { // If user confirms...
                String customerIdString = selectedCustomer.get(0);
                int customerId = Integer.parseInt(customerIdString);
                // Check to make sure customer has no appointments
                List<Integer> appointmentList = getAppointmentIdsByCustomerId(customerId);
                if (!appointmentList.isEmpty()) {  // If customer has appointments
                    // Display error
                    Alert alertAppointments = new Alert(Alert.AlertType.ERROR);
                    alertAppointments.setTitle(null);
                    alertAppointments.setContentText("Cannot delete customer that currently has appointments.");
                    alertAppointments.showAndWait();
                } else {
                    deleteCustomer(customerId); // ...the customer is deleted...
                    buildCustomerData(); // ...and customersTable is updated.
                    Alert deleted = new Alert(Alert.AlertType.INFORMATION);
                    deleted.setTitle("Confirmation");
                    deleted.setContentText("Customer successfully deleted.");
                    deleted.showAndWait();
                }
            }
        }
    }


    /**
     * Deletes selected appointment from SQL database after user confirmation then refreshes table views
     * @throws SQLException SQL query fails
     */
    public void deleteSelectedAppointment() throws SQLException {
        ObservableList<String> selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) { // If an appointment is not selected
            Alert alertNoSelection = new Alert(Alert.AlertType.ERROR);
            alertNoSelection.setTitle(null);
            alertNoSelection.setContentText("Please select an appointment to delete.");
            alertNoSelection.showAndWait();
        } else {
            // Confirmation of deletion required before appointment is deleted
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setContentText("Are you sure you want to delete the selected appointment?");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() == ButtonType.OK) { // If user confirms...
                String appointmentIdString = selectedAppointment.get(0);
                String appointmentTypeString = selectedAppointment.get(4);
                int appointmentId = Integer.parseInt(appointmentIdString);
                deleteAppointment(appointmentId); // ...the appointment is deleted...
                buildAllAppointmentsData(); // ...and appointmentsTable is updated.
                allAppointmentsRadio.setSelected(true);
                Alert deleted = new Alert(Alert.AlertType.INFORMATION);
                deleted.setTitle("Confirmation");
                deleted.setContentText("Appointment with ID: " + appointmentIdString + ", Type: " +
                        appointmentTypeString + " successfully deleted.");
                deleted.showAndWait();
            }
        }
    }


    /**
     * Switches to add appointment form
     * @throws IOException Add appointment form not found
     */
    public void switchToAddAppointment() throws IOException {
        // Switch to addAppointment form
        Parent root = FXMLLoader.load(getClass().getResource("/main/resources/addAppointment.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        // Close main form
        Stage mainStage = (Stage) logoutButton.getScene().getWindow();
        mainStage.close();
    }

    /**
     * Switches to modify appointment form
     * @throws IOException Modify appointment form not found
     * @throws SQLException SQL query fails
     */
    public void switchToModifyAppointment() throws IOException, SQLException {
        if (appointmentsTable.getSelectionModel().getSelectedItem() == null) { // If an appointment is not selected
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(null);
            alert.setContentText("Please select an appointment to modify.");
            alert.showAndWait();
        } else {  // If an appointment is selected, the Modify Appointment form opens with text fields populated with
            // appropriate values for the appointment
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/modifyAppointment.fxml"));
            Parent root = loader.load();
            ModifyAppointmentController controller = loader.getController(); // Retrieves data from ModifyAppointment controller
            // to enable fetchCustomerValues method

            // Populate the form with existing values
            ObservableList<Object> selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
            controller.fetchAppointmentValues(selectedAppointment);

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            // Close main form
            Stage mainStage = (Stage) logoutButton.getScene().getWindow();
            mainStage.close();
        }

    }


    /**
     * Switches to reports form
     * @throws IOException Reports form not found
     */
    public void switchToReports() throws IOException {
        // Switch to reports form
        Parent root = FXMLLoader.load(getClass().getResource("/main/resources/reports.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        // Close main form
        Stage mainStage = (Stage) logoutButton.getScene().getWindow();
        mainStage.close();
    }


    /**
     * Returns to login form
     * @throws IOException Login form not found
     */
    @FXML
    void logout() throws IOException {
        // Switch to login form
        Parent root = FXMLLoader.load(getClass().getResource("/main/resources/login.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        // Close main form
        Stage mainStage = (Stage) logoutButton.getScene().getWindow();
        mainStage.close();
    }
}
