package com.trevorBower.appointmentScheduler.helper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerQuery {

    // Setters:

    /**
     * Inserts a new appointment into SQL database
     * @param customerName Customer name
     * @param address Customer address
     * @param postalCode Customer postal code
     * @param phoneNumber Customer phone number
     * @param divisionId Division ID of customer
     * @return Number of rows changed by command
     * @throws SQLException SQL query fails
     */
    public static int insertCustomer(String customerName, String address, String postalCode, String phoneNumber, int divisionId) throws SQLException {
        String sql = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Division_ID) VALUES(?, ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, customerName);
        ps.setString(2, address);
        ps.setString(3, postalCode);
        ps.setString(4, phoneNumber);
        ps.setInt(5, divisionId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /**
     * Updates customer name in SQL database
     * @param customerId Customer ID
     * @param customerName Customer name
     * @return Number of rows changed by command
     * @throws SQLException SQL query fails
     */
    public static int updateCustomerName(int customerId, String customerName) throws SQLException {
        String sql = "UPDATE customers SET Customer_Name = ? WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, customerName);
        ps.setInt(2,customerId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /**
     * Updates address name in SQL database
     * @param customerId Customer ID
     * @param address Customer address
     * @return Number of rows changed by command
     * @throws SQLException SQL query fails
     */
    public static int updateCustomerAddress(int customerId, String address) throws SQLException {
        String sql = "UPDATE customers SET Address = ? WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, address);
        ps.setInt(2,customerId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /**
     * Updates customer postal code in SQL database
     * @param customerId Customer ID
     * @param postalCode Customer postal code
     * @return Number of rows changed by command
     * @throws SQLException SQL query fails
     */
    public static int updateCustomerPostalCode(int customerId, String postalCode) throws SQLException {
        String sql = "UPDATE customers SET Postal_Code = ? WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, postalCode);
        ps.setInt(2,customerId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /**
     * Updates customer phone number in SQL database
     * @param customerId Customer ID
     * @param phoneNumber Customer phone number
     * @return Number of rows changed by command
     * @throws SQLException SQL query fails
     */
    public static int updateCustomerPhone(int customerId, String phoneNumber) throws SQLException {
        String sql = "UPDATE customers SET Phone = ? WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, phoneNumber);
        ps.setInt(2,customerId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /**
     * Updates customer Division ID in SQL database
     * @param customerId Customer ID
     * @param divisionId Division ID of Customer
     * @return Number of rows changed by command
     * @throws SQLException SQL query fails
     */
    public static int updateCustomerDivisionId(int customerId, int divisionId) throws SQLException {
        String sql = "UPDATE customers SET Division_ID = ? WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, divisionId);
        ps.setInt(2,customerId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /**
     * Updates all specified customer values in SQL database
     * @param customerId Customer ID
     * @param customerName Customer
     * @param address Customer
     * @param postalCode Customer
     * @param phoneNumber Customer
     * @param divisionId Division ID of Customer
     * @throws SQLException SQL query fails
     */
    public static void updateCustomerAll(int customerId, String customerName, String address, String postalCode,
                                         String phoneNumber, int divisionId) throws SQLException {
        updateCustomerName(customerId, customerName);
        updateCustomerAddress(customerId, address);
        updateCustomerPostalCode(customerId, postalCode);
        updateCustomerPhone(customerId, phoneNumber);
        updateCustomerDivisionId(customerId, divisionId);
    }


    // Delete

    /**
     * Deletes customer based on customer ID
     * @param customerId Customer ID
     * @return Number of rows changed by command
     * @throws SQLException SQL query fails
     */
    public static int deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }
}
