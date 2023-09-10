package com.trevorBower.appointmentScheduler.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import com.trevorBower.appointmentScheduler.helper.JDBC;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static com.trevorBower.appointmentScheduler.helper.ExtraMethods.convertUtcTimeToSystem;
import static com.trevorBower.appointmentScheduler.helper.FirstLevelDivisionsQuery.*;

public class ReportsController {

    @FXML
    private Button cancelButton;

    /**
     * Displays total number of customer appointments by type and month
     * @throws SQLException SQL query fails
     */
    public void report1() throws SQLException {
        // Report 1 = Total number of customer appointments by type and month

        // Prepare SQL query for # appointments by type
        String sqlType = "SELECT c.Type, COUNT(*) " +
                "FROM appointments c " +
                "GROUP BY c.Type " +
                "ORDER BY c.Type";
        PreparedStatement psType = JDBC.connection.prepareStatement(sqlType);
        ResultSet rsType = psType.executeQuery();

        // Create report for # appointments by type
        String appointmentsByTypeCount = "Total number of customer appointments by type:\n\n";
        while(rsType.next()) {
            String type = rsType.getString(1);
            int count = rsType.getInt(2);
            appointmentsByTypeCount += ("Type: " + type + " | Count: " + count + "\n");
        }

        // Prepare SQL query for # appointments by month
        String sqlMonth = "SELECT MONTH(c.Start), COUNT(*) " +
                "FROM appointments c " +
                "GROUP BY MONTH(c.Start) " +
                "ORDER BY MONTH(c.Start)";
        PreparedStatement psMonth = JDBC.connection.prepareStatement(sqlMonth);
        ResultSet rsMonth = psMonth.executeQuery();

        // Create report for # appointments by month
        String appointmentsByMonthCount = "Total number of customer appointments by month:\n\n";
        while(rsMonth.next()) {
            String month = rsMonth.getString(1);
            int count = rsMonth.getInt(2);
            appointmentsByMonthCount += ("Month: " + month + " | Count: " + count + "\n");
        }

        // Put both reports together
        String fullReport = (appointmentsByTypeCount + "\n\n" + appointmentsByMonthCount);

        // Display report
        Alert report1Alert = new Alert(Alert.AlertType.INFORMATION);
        report1Alert.setTitle("Report 1");
        report1Alert.setContentText(fullReport);
        report1Alert.showAndWait();

    }

    /**
     * Displays schedule for each contact in organization that includes appointment ID, title, type, description, start
     * date/time, end date/time, and customer ID
     * @throws SQLException SQL query fails
     */
    public void report2() throws SQLException {
        // Report 2 = Schedule for each contact in organization that includes appointment ID, title, type, description,
        // start date/time, end date/time, and customer ID
        String report2 = "Appointments in chronological order for each contact in organization:\n\n";

        // Prepare SQL query
        String sql = "SELECT c.Appointment_ID, c.Title, c.Type, c.Description, " +
                "c.Start, c.End, c.Customer_ID, d.Contact_Name " +
                "FROM appointments c " +
                "JOIN contacts d ON c.Contact_ID = d.Contact_ID " +
                "ORDER BY d.Contact_Name, c.Start";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String contactName = rs.getString("Contact_Name");
            int appointmentId = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String type = rs.getString("Type");
            String description = rs.getString("Description");
            LocalDateTime startDateTimeUtc = rs.getTimestamp("Start").toLocalDateTime();
            LocalDateTime startDateTimeSystem = convertUtcTimeToSystem(startDateTimeUtc);
            LocalDateTime endDateTimeUtc = rs.getTimestamp("End").toLocalDateTime();
            LocalDateTime endDateTimeSystem = convertUtcTimeToSystem(endDateTimeUtc);
            int customerId = rs.getInt("Customer_ID");
            report2 += ("Contact name: " + contactName + " | Appointment ID: " + appointmentId + " | Title: " + title +
                    " | Type: " + type + " | Description: " + description + " | Start Date/Time: " + startDateTimeSystem +
                    " | End Date/Time: " + endDateTimeSystem + " | Customer ID: " + customerId + "\n\n");
        }

        // Display report
        Alert report1Alert = new Alert(Alert.AlertType.INFORMATION);
        report1Alert.setTitle("Report 2");
        report1Alert.setContentText(report2);
        report1Alert.showAndWait();

    }

    /**
     * Displays list of all customer names and their contact information
     * @throws SQLException SQL query fails
     */
    public void report3() throws SQLException {
        // Report 3 = List of all customer names and their contact information (Additional report of your choice)
        String report3 = "List of all customer names and their contact information:\n\n";

        // Prepare SQL query
        String sql = "SELECT * FROM customers";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            String customerName = rs.getString("Customer_Name");
            String address = rs.getString("Address");
            int divisionId = rs.getInt("Division_ID");
            String division = getDivisionByDivisionId(divisionId);
            int countryId = getCountryIdByDivisionId(divisionId);
            String countryName = getCountryNameById(countryId);
            String postalCode = rs.getString("Postal_Code");
            String phoneNumber = rs.getString("Phone");
            report3 += ("Customer Name: " + customerName + " | Full Address: " + address + ", " + division + ", " +
                    countryName + ", " + postalCode + " | Phone Number: " + phoneNumber + "\n\n");
        }

        // Display report
        Alert report1Alert = new Alert(Alert.AlertType.INFORMATION);
        report1Alert.setTitle("Report 3");
        report1Alert.setContentText(report3);
        report1Alert.showAndWait();

    }

    public void switchToMainForm() throws IOException {
        // Open main form
        Parent root = FXMLLoader.load(getClass().getResource("/main/resources/mainForm.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        // Close previous form
        Stage reportStage = (Stage) cancelButton.getScene().getWindow();
        reportStage.close();
    }
}
