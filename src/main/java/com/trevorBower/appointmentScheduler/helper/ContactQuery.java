package com.trevorBower.appointmentScheduler.helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactQuery {

    /**
     * Get contact name based on contact ID
     * @param contactId Contact ID
     * @return Contact name
     * @throws SQLException SQL query fails
     */
    public static String getContactNameById(int contactId) throws SQLException {
        String sql = "SELECT Contact_Name FROM contacts WHERE Contact_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, contactId);
        ResultSet rs = ps.executeQuery();
        // Move cursor to front row
        if (rs.next()) {
            String contactName = rs.getString("Contact_Name");
            return contactName;
        }
        // If nothing found...
        return null;
    }

    /**
     * Get contact ID based on contact name
     * @param contactName Contact name
     * @return Contact ID
     * @throws SQLException SQL query fails
     */
    public static int getContactIdByName(String contactName) throws SQLException {
        String sql = "SELECT Contact_ID FROM contacts WHERE Contact_Name = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, contactName);
        ResultSet rs = ps.executeQuery();
        // Move cursor to front row
        if (rs.next()) {
            int contactId = rs.getInt("Contact_ID");
            return contactId;
        }
        // If nothing found...
        return 0;
    }

}
