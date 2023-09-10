package com.trevorBower.appointmentScheduler.helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserQuery {

    // Getters:

    /**
     * Gets password associated with username
     * @param userName Username
     * @return Password
     * @throws SQLException SQL query fails
     */
    public static String getUserPassword(String userName) throws SQLException {
        String sql = "SELECT * FROM users WHERE User_Name = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, userName);
        ResultSet rs = ps.executeQuery();
        // Move cursor to front row
        if (rs.next()) {
            String password = rs.getString("Password");
            return password;
        }
        // If nothing found...
        return null;
    }

    /**
     * Gets user ID based on username
     * @param userName Username
     * @return User ID
     * @throws SQLException SQL query fails
     */
    public static int getUserIdByUserName(String userName) throws SQLException {
        String sql = "SELECT * FROM users WHERE User_Name = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, userName);
        ResultSet rs = ps.executeQuery();
        // Move cursor to front row
        if (rs.next()) {
            int userId = rs.getInt("User_ID");
            return userId;
        }
        // If nothing found...
        return 0;
    }

}
