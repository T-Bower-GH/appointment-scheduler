package com.trevorBower.appointmentScheduler.helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CountryQuery {

    // Getters

    /**
     * Get country name based on country ID
     * @param countryId Country ID
     * @return Country name
     * @throws SQLException SQL query fails
     */
    public static String getCountryByCountryId(int countryId) throws SQLException {
        String sql = "SELECT Country FROM countries WHERE Country_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, countryId);
        ResultSet rs = ps.executeQuery();
        // Move cursor to front row
        if (rs.next()) {
            String country = rs.getString("Country");
            return country;
        }
        return null;
    }
}
