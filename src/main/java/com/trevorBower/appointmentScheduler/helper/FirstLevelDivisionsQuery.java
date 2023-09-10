package com.trevorBower.appointmentScheduler.helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FirstLevelDivisionsQuery {

    /**
     * Gets country ID by country name
     * @param country Country name
     * @return Country ID
     * @throws SQLException SQL query fails
     */
    public static int getCountryIdByName(String country) throws SQLException {
        String sql = "SELECT Country_ID FROM countries WHERE Country = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, country);
        ResultSet rs = ps.executeQuery();
        // Move cursor to front row
        if (rs.next()) {
            int countryId = rs.getInt("Country_ID");
            return countryId;
        }
        return 0;  // Int cannot be null, so 0 is used instead as no countries have ID of 0
    }

    /**
     * Gets country name by country ID
     * @param countryId Country ID
     * @return Country name
     * @throws SQLException SQL query fails
     */
    public static String getCountryNameById(int countryId) throws SQLException {
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

    /**
     * Gets list of states/provinces associated with country ID
     * @param countryId Country ID
     * @return ObservableList of Strings of state/province names
     * @throws SQLException SQL query fails
     */
    public static ObservableList<String> getStatesByCountryId(int countryId) throws SQLException {
        String sql = "SELECT Division FROM first_level_divisions WHERE Country_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, countryId);
        ResultSet rs = ps.executeQuery();
        // Create an ObservableList to store the results
        ObservableList<String> states = FXCollections.observableArrayList();
        // Move cursor to front row
        while (rs.next()) {
            String division = rs.getString("Division");
            states.add(division);
        }
        return states;
    }

    /**
     * Gets division ID based on division name
     * @param division Division name
     * @return Division ID
     * @throws SQLException SQL query fails
     */
    public static int getDivisionIdByDivision(String division) throws SQLException {
        String sql = "SELECT Division_ID FROM first_level_divisions WHERE Division = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, division);
        ResultSet rs = ps.executeQuery();
        // Move cursor to front row
        if (rs.next()) {
            int divisionId = rs.getInt("Division_ID");
            return divisionId;
        }
        return 0;  // Int cannot be null, so 0 is used instead as no countries have ID of 0
    }

    /**
     * Gets division name by division ID
     * @param divisionId Division ID
     * @return Division name
     * @throws SQLException SQL query fails
     */
    public static String getDivisionByDivisionId(int divisionId) throws SQLException {
        String sql = "SELECT Division FROM first_level_divisions WHERE Division_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, divisionId);
        ResultSet rs = ps.executeQuery();
        // Move cursor to front row
        if (rs.next()) {
            String division = rs.getString("Division");
            return division;
        }
        return null;
    }

    /**
     * Gets country ID by division ID
     * @param divisionId Division ID
     * @return Country ID
     * @throws SQLException SQL query fails
     */
    public static int getCountryIdByDivisionId(int divisionId) throws SQLException {
        String sql = "SELECT Country_ID FROM first_level_divisions WHERE Division_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, divisionId);
        ResultSet rs = ps.executeQuery();
        // Move cursor to front row
        if (rs.next()) {
            int countryId = rs.getInt("Country_ID");
            return countryId;
        }
        return 0; // Int cannot be null, so 0 is used instead as no countries have ID of 0
    }
}
