package com.trevorBower.appointmentScheduler.helper;

import javafx.scene.control.Alert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ExtraMethods {

    // Time zone conversions

    /**
     * Takes a LocalDateTime in system default time and converts to UTC
     * @param systemDateTime LocalDateTime (presumably in system default time zone) to be converted to UTC
     * @return Input time as UTC
     */
    public static LocalDateTime convertSystemTimeToUtc(LocalDateTime systemDateTime) {
        // Set localDateTime as system default time zone
        ZoneId systemZoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = systemDateTime.atZone(systemZoneId);

        // Convert system time to UTC
        ZonedDateTime utcDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        return utcDateTime.toLocalDateTime();
    }

    /**
     * Takes a LocalDateTime in UTC time and converts to system default time
     * @param utcDateTime LocalDateTime (presumably in UTC) to be converted to system default time
     * @return Input time as system default time
     */
    public static LocalDateTime convertUtcTimeToSystem(LocalDateTime utcDateTime) {
        // Identify system default zone
        ZoneId systemDefaultZone = ZoneId.systemDefault();

        // Convert UTC LocalDateTime to system default
        ZonedDateTime systemDateTime = utcDateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(systemDefaultZone);
        return systemDateTime.toLocalDateTime();
    }

    /**
     * Takes a LocalDateTime in system default time and converts to EST
     * @param systemDateTime LocalDateTime (presumably in system default time zone) to be converted to EST
     * @return Input time as EST
     */
    public static LocalDateTime convertSystemTimeToEst(LocalDateTime systemDateTime) {
        // Set localDateTime as system default time zone
        ZoneId systemZoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = systemDateTime.atZone(systemZoneId);

        // Convert system time to UTC
        ZonedDateTime estDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));
        return estDateTime.toLocalDateTime();
    }

    // Input Validation Checks

    /**
     * Checks to see if given time is within set business hours
     * @param localDateTimeSystem LocalDateTime value in system default time
     * @return True if input time is within business hours
     */
    public static boolean checkIfBusinessHours(LocalDateTime localDateTimeSystem) {
        // Convert system default time to EST (since business hours are EST-based) and get only LocalTime value
        LocalDateTime localDateTimeEst = convertSystemTimeToEst(localDateTimeSystem);
        LocalTime localTimeEst = localDateTimeEst.toLocalTime();
        // Set business hours to compare to
        LocalTime businessHoursStart = LocalTime.of(8, 0);
        LocalTime businessHoursEnd = LocalTime.of(22, 0);
        // Compare input time to business hours
        if (localTimeEst.isBefore(businessHoursStart) || localTimeEst.isAfter(businessHoursEnd)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(null);
            alert.setContentText("Appointment time entered is outside EST business hours of " + businessHoursStart +
                    " to " + businessHoursEnd + ". Entered time in EST: " + localTimeEst);
            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Checks to see if there are any appointment times overlapping with input potential appointment
     * @param startDateTimeSystem Start date/time of potential appointment in system default time
     * @param endDateTimeSystem End date/time of potential appointment in system default time
     * @param customerId ID of customer having appointment added
     * @return True if input times do not overlap with existing appointments
     */
    public static boolean checkForOverlappingAppointmentsAdd(LocalDateTime startDateTimeSystem,
                                                   LocalDateTime endDateTimeSystem, int customerId) {
        try {
            // SQL statement to select appointments that has one of the following...
            String sql = "SELECT COUNT(*) FROM appointments WHERE Customer_ID = ? AND " +
                    "((? >= Start AND ? < End) OR " +  // Check if the given start time is within existing appointment's time range
                    "(? > Start AND ? <= End) OR " + // Check if the given end time is within existing appointment's time range
                    "(? <= End AND ? >= Start))";  // Check if given time is completely inside existing appointment
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, customerId);
            ps.setTimestamp(2, Timestamp.valueOf(endDateTimeSystem));
            ps.setTimestamp(3, Timestamp.valueOf(startDateTimeSystem));
            ps.setTimestamp(4, Timestamp.valueOf(endDateTimeSystem));
            ps.setTimestamp(5, Timestamp.valueOf(startDateTimeSystem));
            ps.setTimestamp(6, Timestamp.valueOf(endDateTimeSystem));
            ps.setTimestamp(7, Timestamp.valueOf(startDateTimeSystem));
            ResultSet rs = ps.executeQuery();
            // Move cursor to front row
            if (rs.next()) {
                int overlaps = rs.getInt(1);  // Count number of overlaps
                if (overlaps > 0) { // If any overlaps...
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(null);
                    alert.setContentText("Customer has overlapping appointment times.");
                    alert.showAndWait();
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Checks to see if there are any appointment times overlapping with input potential appointment
     * Extra SQL statement section added to make sure appointment being modified does not count itself as overlap
     * @param appointmentId Appointment ID of appointment being modified
     * @param startDateTimeSystem Start date/time of potential appointment in system default time
     * @param endDateTimeSystem End date/time of potential appointment in system default time
     * @param customerId ID of customer having appointment modified
     * @return True if input times do not overlap with other existing appointments
     */
    public static boolean checkForOverlappingAppointmentsModify(int appointmentId, LocalDateTime startDateTimeSystem,
                                                          LocalDateTime endDateTimeSystem, int customerId) {
        try {
            // SQL statement to select appointments with ID other than one being checked AND has one of the following...
            String sql = "SELECT COUNT(*) FROM appointments WHERE Appointment_ID <> ? AND Customer_ID = ? AND " +
                    "((? >= Start AND ? < End) OR " +  // Check if the given start time is within existing appointment's time range
                    "(? > Start AND ? <= End) OR " + // Check if the given end time is within existing appointment's time range
                    "(? <= End AND ? >= Start))";  // Check if given time is completely inside existing appointment
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, appointmentId);
            ps.setInt(2, customerId);
            ps.setTimestamp(3, Timestamp.valueOf(endDateTimeSystem));
            ps.setTimestamp(4, Timestamp.valueOf(startDateTimeSystem));
            ps.setTimestamp(5, Timestamp.valueOf(endDateTimeSystem));
            ps.setTimestamp(6, Timestamp.valueOf(startDateTimeSystem));
            ps.setTimestamp(7, Timestamp.valueOf(endDateTimeSystem));
            ps.setTimestamp(8, Timestamp.valueOf(startDateTimeSystem));
            ResultSet rs = ps.executeQuery();
            // Move cursor to front row
            if (rs.next()) {
                int overlaps = rs.getInt(1);  // Count number of overlaps
                if (overlaps > 0) { // If any overlaps...
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(null);
                    alert.setContentText("Customer has overlapping appointment times.");
                    alert.showAndWait();
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
