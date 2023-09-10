package com.trevorBower.appointmentScheduler.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.trevorBower.appointmentScheduler.controller.MainFormController.currentUserId;
import static com.trevorBower.appointmentScheduler.controller.MainFormController.setCurrentUserId;
import static com.trevorBower.appointmentScheduler.helper.AppointmentQuery.getAppointmentsByUserId;
import static com.trevorBower.appointmentScheduler.helper.ExtraMethods.convertUtcTimeToSystem;
import static com.trevorBower.appointmentScheduler.helper.UserQuery.getUserIdByUserName;
import static com.trevorBower.appointmentScheduler.helper.UserQuery.getUserPassword;

public class LoginController {

    // Boolean for language settings
    Boolean french = false;

    // Text
    @FXML
    private Text userLoginText;

    @FXML
    private Text timeZoneText;

    @FXML
    private Text usernameText;

    @FXML
    private Text passwordText;


    // Entry fields and buttons
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label timeZoneLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button exitButton;

    /**
     * Initializes form to specifications and sets default values
     */
    @FXML
    private void initialize() {
        // Config time zone text
        setTextByLanguage(timeZoneText, "login.timeZone");
        ZoneId userTimeZone = ZoneId.systemDefault();
        String timeZoneId = userTimeZone.getId();
        timeZoneLabel.setText(timeZoneId);
        // Config button text
        setButtonByLanguage(loginButton, "login.button");
        setButtonByLanguage(exitButton, "exit.button");
        // Config text
        setTextByLanguage(userLoginText, "login.title");
        setTextByLanguage(usernameText, "login.username");
        setTextByLanguage(passwordText, "login.password");
    }

    /**
     * Validates login credentials, logs attempt in log file, notifies user of any upcoming appointments, and switches
     * to main form
     * @throws SQLException If error in SQL query
     * @throws IOException If language resource bundle files cannot be found
     */
    @FXML
    void login() throws SQLException, IOException {
        String enteredUsername = usernameField.getText();
        String enteredPassword = passwordField.getText();
        String actualPassword = getUserPassword(enteredUsername);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        // Configure error messages based on system language
        if (Locale.getDefault().getLanguage().equals("fr")) {
            french = true;
        }
        if (french) {
            alert.setTitle("Erreur");
        } else {
            alert.setTitle("Error");
        }
        if (actualPassword != null) {
            if (actualPassword.equals(enteredPassword)) {
                int currentUserId = getUserIdByUserName(enteredUsername);  // Gets User ID from login info
                setCurrentUserId(currentUserId); // Sets User ID in MainForm controller
                checkUpcomingAppointments();
                trackLoginActivity(enteredUsername, true);  // Tracks successful login attempt in log
                switchToMainForm();
            } else {
                if (french) {
                    alert.setHeaderText("Échec de la connexion: mot de passe incorrect");
                } else {
                    alert.setHeaderText("Login failed: Incorrect Password");
                }
                trackLoginActivity(enteredUsername, false); // Tracks unsuccessful login attempt in log
                alert.showAndWait();
            }
        }
        else {
            if (usernameField.getText().isEmpty()) {
                if (french) {
                    alert.setHeaderText("Merci d'entrer un nom d'utilisateur.");
                } else {
                    alert.setHeaderText("Please enter a username.");
                }
                trackLoginActivity("[None]", false); // Tracks unsuccessful login attempt in log
                alert.showAndWait();
            }
            else {
                if (french) {
                    alert.setHeaderText("Nom d'utilisateur introuvable.");
                } else {
                    alert.setHeaderText("Username not found.");
                }
                trackLoginActivity(enteredUsername, false); // Tracks unsuccessful login attempt in log
                alert.showAndWait();
            }

        }

    }

    /**
     * Closes program after confirmation from user
     * @param event Exit button clicked
     */
    @FXML
    void exit(ActionEvent event) {
        // Confirmation required before program closes
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if (french) {
            alert.setTitle("Confirmation"); // Same spelling in French
            alert.setContentText("Voulez-vous vraiment quitter le programme ?");
        } else {
            alert.setTitle("Confirmation");
            alert.setContentText("Are you sure you want to exit the program?");
        }
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) { // If user confirms...
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close(); // ...the program closes.
            System.out.println("The program has successfully closed. Have a great day!");
        }
    }

    /**
     * Change button text based on language settings
     * @param button Button being translated
     * @param translationTerm Term being translated
     */
    public void setButtonByLanguage(Button button, String translationTerm) {
        ResourceBundle rb = ResourceBundle.getBundle("il8n/messages", Locale.getDefault());
        //System.out.println(rb.getString(translationTerm));
        if (Locale.getDefault().getLanguage().equals("fr")) {
            button.setText(rb.getString(translationTerm));
        }
    }

    /**
     * Change label text based on language settings
     * @param label Label being translated
     * @param translationTerm Term being translated
     */
    public void setLabelByLanguage(Label label, String translationTerm) {
        ResourceBundle rb = ResourceBundle.getBundle("il8n/messages", Locale.getDefault());
        //System.out.println(rb.getString(translationTerm));
        if (Locale.getDefault().getLanguage().equals("fr")) {
            label.setText(rb.getString(translationTerm));
        }
    }

    /**
     * Change text based on language settings
     * @param text Text being translated
     * @param translationTerm Term being translated
     */
    // Change label text based on language settings
    public void setTextByLanguage(Text text, String translationTerm) {
        ResourceBundle rb = ResourceBundle.getBundle("il8n/messages", Locale.getDefault());
        //System.out.println(rb.getString(translationTerm));
        if (Locale.getDefault().getLanguage().equals("fr")) {
            text.setText(rb.getString(translationTerm));
        }
    }

    /**
     * Checks to see if there are any appointments scheduled to start in the next 15 minutes and presents a list of
     * those appointments
     * @throws SQLException If SQL query fails
     */
    public void checkUpcomingAppointments() throws SQLException {
        // Initialize list to hold upcoming appointments
        ObservableList<ObservableList> upcomingAppointments = FXCollections.observableArrayList();

        // Get current system default time
        LocalDateTime currentDateTimeSystem = LocalDateTime.now(ZoneId.systemDefault());

        // Get list of appointments by User ID (Current User ID is established upon login)
        ObservableList<ObservableList> userAppointments = getAppointmentsByUserId(currentUserId);

        // Populate upcomingAppointments with appointments that have startDateTime within 15 minutes
        for (ObservableList appointment : userAppointments) {
            // Get appointment start time and convert to system default
            LocalDateTime startDateTimeUtc = (LocalDateTime) appointment.get(5);
            LocalDateTime startDateTimeSystem = convertUtcTimeToSystem(startDateTimeUtc);
            // Calculate minutes remaining until appointment start time
            long minutesUntilAppointmentStart = Duration.between(currentDateTimeSystem, startDateTimeSystem).toMinutes();
            // If minutesUntilAppointmentStart is at or between 0 and 15...
            if (minutesUntilAppointmentStart >= 0 && minutesUntilAppointmentStart <= 15) {
                upcomingAppointments.add(appointment);
            }
        }

        // Create string with list of details for each upcoming appointment (appointmentId, startDate, startTime)
        String upcomingAppointmentsDetails = "You have the following appointment(s) starting in the next 15 minutes: \n \n";
        for (ObservableList appointment : upcomingAppointments) {
            // Change startDateTime to startDate and startTime
            LocalDateTime startDateTime = (LocalDateTime) appointment.get(5);
            LocalDate startDate = startDateTime.toLocalDate();
            LocalTime startTime = startDateTime.toLocalTime();
            // Set message to display
            String appointmentDetails = ("Appointment ID: " + appointment.get(0) + ", Start Date: " + startDate +
                    ", Start Time: " + startTime + "\n" + "\n");
            upcomingAppointmentsDetails += appointmentDetails;
        }

        // Display window containing upcoming appointments details
        Alert upcomingAppointmentsAlert = new Alert(Alert.AlertType.INFORMATION);
        upcomingAppointmentsAlert.setTitle("Upcoming Appointments");
        if (!upcomingAppointments.isEmpty()) {
            upcomingAppointmentsAlert.setContentText(upcomingAppointmentsDetails);
        } else {
            upcomingAppointmentsAlert.setContentText("No appointments starting within the next 15 minutes.");
        }
        upcomingAppointmentsAlert.showAndWait();

    }

    /**
     * Tracks login activity and documents to text file
     * @param username Username of login being attempted
     * @param successful Boolean depicting if login was successful or not
     */
    private void trackLoginActivity(String username, boolean successful) {
        // Log username
        String logDocumentation = ("Login attempt by username: " + username + " ");
        // Log if successful
        String successfulLogin;
        if(successful) {
            successfulLogin = "succeeded ";
        } else {
            successfulLogin = "failed ";
        }
        // Log attempt time (Time of execution)
        LocalDateTime attemptTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = attemptTime.format(formatter);

        // Combine to complete documentation
        logDocumentation += (successfulLogin + "at " + formattedDateTime + ".");

        // Write to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("login_activity.txt", true))) {
            writer.write(logDocumentation);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Switches back to main form
     * @throws IOException Main form file not found
     */
    public void switchToMainForm() throws IOException {
        // Open main form
        Parent root = FXMLLoader.load(getClass().getResource("/forms/MainForm.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        // Close login form
        Stage loginStage = (Stage) loginButton.getScene().getWindow();
        loginStage.close();
    }
}
