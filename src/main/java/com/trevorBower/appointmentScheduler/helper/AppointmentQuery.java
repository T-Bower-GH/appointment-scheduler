package com.trevorBower.appointmentScheduler.helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.trevorBower.appointmentScheduler.helper.ExtraMethods.convertSystemTimeToUtc;

public class AppointmentQuery {

    // Setters

    /**
     * Inserts a new appointment into SQL database
     * @param title Appointment Title
     * @param description Appointment Description
     * @param location Appointment Location
     * @param type Appointment Type
     * @param startDateTimeSystem Start date/time of appointment in system default time
     * @param endDateTimeSystem End date/time of appointment in system default time
     * @param customerId Customer ID (FK) for Appointment
     * @param userId User ID (FK) for Appointment
     * @param contactId Contact ID (FK) for Appointment
     * @return Number of rows changed by command
     * @throws SQLException SQL query fails
     */
    public static int insertAppointment(String title, String description, String location, String type,
                                        LocalDateTime startDateTimeSystem, LocalDateTime endDateTimeSystem,
                                        int customerId, int userId, int contactId) throws SQLException {
        String sql = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Customer_ID, User_ID, "
                + "Contact_ID) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        // Convert system time values to UTC
        LocalDateTime startDateTimeUTC = convertSystemTimeToUtc(startDateTimeSystem);
        LocalDateTime endDateTimeUTC = convertSystemTimeToUtc(endDateTimeSystem);
        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, location);
        ps.setString(4, type);
        ps.setObject(5, startDateTimeUTC);
        ps.setObject(6, endDateTimeUTC);
        ps.setInt(7, customerId);
        ps.setInt(8, userId);
        ps.setInt(9, contactId);
        int rowsAffected = ps.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Appointment successfully added.");
        } else {
            System.out.println("Appointment add failed.");
        }
        return rowsAffected;
    }

    /**
     * Updates appointment Title
     * @param appointmentId Appointment ID
     * @param title Appointment Title
     * @return Number of rows changed by command
     * @throws SQLException SQL query fails
     */
    public static int updateAppointmentTitle(int appointmentId, String title) throws SQLException {
        String sql = "UPDATE appointments SET Title = ? WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, title);
        ps.setInt(2,appointmentId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /**
     * Updates appointment Description
     * @param appointmentId Appointment ID
     * @param description Appointment Description
     * @return Number of rows changed by command
     * @throws SQLException SQL query fails
     */
    public static int updateAppointmentDescription(int appointmentId, String description) throws SQLException {
        String sql = "UPDATE appointments SET Description = ? WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, description);
        ps.setInt(2,appointmentId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /**
     * Updates appointment Location
     * @param appointmentId Appointment ID
     * @param location Appointment Location
     * @return Number of rows changed by command
     * @throws SQLException SQL query fails
     */
    public static int updateAppointmentLocation(int appointmentId, String location) throws SQLException {
        String sql = "UPDATE appointments SET Location = ? WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, location);
        ps.setInt(2,appointmentId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /**
     * Updates appointment Type
     * @param appointmentId Appointment ID
     * @param type Appointment Type
     * @return Number of rows changed by command
     * @throws SQLException SQL query fails
     */
    public static int updateAppointmentType(int appointmentId, String type) throws SQLException {
        String sql = "UPDATE appointments SET Type = ? WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, type);
        ps.setInt(2,appointmentId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /**
     * Updates appointment Start Date/Time (Converts from system default time to UTC)
     * @param appointmentId Appointment ID
     * @param startDateTimeSystem Appointment Start Date/Time
     * @return Number of rows changed by command
     * @throws SQLException SQL query fails
     */
    public static int updateAppointmentStart(int appointmentId, LocalDateTime startDateTimeSystem) throws SQLException {
        String sql = "UPDATE appointments SET Start = ? WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        // Convert system time back to UTC
        LocalDateTime startDateTimeUTC = convertSystemTimeToUtc(startDateTimeSystem);
        ps.setObject(1, startDateTimeUTC);
        ps.setInt(2,appointmentId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /**
     * Updates appointment End Date/Time (Converts from system default time to UTC)
     * @param appointmentId Appointment ID
     * @param endDateTimeSystem Appointment End Date/Time
     * @return Number of rows changed by command
     * @throws SQLException SQL query fails
     */
    public static int updateAppointmentEnd(int appointmentId, LocalDateTime endDateTimeSystem) throws SQLException {
        String sql = "UPDATE appointments SET End = ? WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        // Convert system time back to UTC
        LocalDateTime endDateTimeUTC = convertSystemTimeToUtc(endDateTimeSystem);
        ps.setObject(1, endDateTimeUTC);
        ps.setInt(2,appointmentId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /**
     * Updates appointment Customer ID
     * @param appointmentId Appointment ID
     * @param customerId Customer ID for Appointment
     * @return Number of rows changed by command
     * @throws SQLException SQL query fails
     */
    public static int updateAppointmentCustomerId(int appointmentId, int customerId) throws SQLException {
        String sql = "UPDATE appointments SET Customer_ID = ? WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerId);
        ps.setInt(2,appointmentId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /**
     * Updates appointment User ID
     * @param appointmentId Appointment ID
     * @param userId User ID for Appointment
     * @return Number of rows changed by command
     * @throws SQLException SQL query fails
     */
    public static int updateAppointmentUserId(int appointmentId, int userId) throws SQLException {
        String sql = "UPDATE appointments SET User_ID = ? WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2,appointmentId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /**
     * Updates appointment Contact ID
     * @param appointmentId Appointment ID
     * @param contactId Contact ID for Appointment
     * @return Number of rows changed by command
     * @throws SQLException SQL query fails
     */
    public static int updateAppointmentContactId(int appointmentId, int contactId) throws SQLException {
        String sql = "UPDATE appointments SET Contact_ID = ? WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, contactId);
        ps.setInt(2,appointmentId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /**
     * Updates all specified appointment values
     * @param appointmentId Appointment ID
     * @param title Appointment title
     * @param description Appointment description
     * @param location Appointment location
     * @param type Appointment type
     * @param startDateTime Start Date/Time for Appointment
     * @param endDateTime End Date/Time for Appointment
     * @param customerId Customer ID for Appointment
     * @param userId User ID for Appointment
     * @param contactId Contact ID for Appointment
     * @throws SQLException SQL query fails
     */
    public static void updateAppointmentAll(int appointmentId, String title, String description, String location,
                                           String type, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                           int customerId, int userId, int contactId) throws SQLException {
        updateAppointmentTitle(appointmentId, title);
        updateAppointmentDescription(appointmentId, description);
        updateAppointmentLocation(appointmentId, location);
        updateAppointmentType(appointmentId, type);
        updateAppointmentStart(appointmentId, startDateTime);
        updateAppointmentEnd(appointmentId, endDateTime);
        updateAppointmentCustomerId(appointmentId, customerId);
        updateAppointmentUserId(appointmentId, userId);
        updateAppointmentContactId(appointmentId, contactId);
    }

    // Getters

    /**
     * Get all appointment ID's based on customer ID
     * @param customerId Customer ID
     * @return List of appointment ID's
     * @throws SQLException SQL query fails
     */
    public static List<Integer> getAppointmentIdsByCustomerId(int customerId) throws SQLException {
        List<Integer> appointmentList = new ArrayList<>();
        String sql = "SELECT Appointment_ID FROM appointments WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();
        // Move cursor to front row
        while (rs.next()) {
            int appointment = rs.getInt("Appointment_ID");
            appointmentList.add(appointment);
        }
        return appointmentList;
    }

    /**
     * Get all information for all appointments based on user ID
     * @param userId User ID
     * @return ObservableList of ObservableLists of all information for selected appointments
     * @throws SQLException SQL query fails
     */
    public static ObservableList<ObservableList> getAppointmentsByUserId(int userId) throws SQLException {
        ObservableList<ObservableList> userAppointments = FXCollections.observableArrayList();
        String sql = "SELECT * FROM appointments WHERE User_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        // Move cursor to front row
        while (rs.next()) {
            ObservableList appointment = FXCollections.observableArrayList();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                Object columnValue = rs.getObject(i);
                appointment.add(columnValue);
            }

            userAppointments.add(appointment);
        }
        return userAppointments;
    }

    // Delete

    /**
     * Deletes appointment from SQL Database based on appointment ID
     * @param appointmentId Appointment ID
     * @return Number of rows changed by command
     * @throws SQLException SQL query fails
     */
    public static int deleteAppointment(int appointmentId) throws SQLException {
        String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, appointmentId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

}
