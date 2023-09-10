package com.trevorBower.appointmentScheduler.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

import static com.trevorBower.appointmentScheduler.helper.CountryQuery.getCountryByCountryId;
import static com.trevorBower.appointmentScheduler.helper.CustomerQuery.updateCustomerAll;
import static com.trevorBower.appointmentScheduler.helper.FirstLevelDivisionsQuery.*;

public class ModifyCustomerController {
    // Text fields
    @FXML
    private TextField customerIdField;

    @FXML
    private TextField customerNameField;

    @FXML
    private TextField customerAddressField;

    @FXML
    private TextField customerPostalField;

    @FXML
    private TextField customerPhoneField;

    // Buttons
    @FXML
    private Button cancelButton;

    // Combo Boxes
    @FXML
    private ComboBox<String> countryComboBox;

    @FXML
    private ComboBox<String> stateComboBox;


    /**
     * Initializes form to specifications and sets default values
     */
    public void initialize() {
        // Disable Customer ID field
        customerIdField.setDisable(true);
        // Config Country combo box
        ObservableList<String> countryOptions = FXCollections.observableArrayList(
                "U.S",
                "UK",
                "Canada"
        );
        countryComboBox.setItems(countryOptions);

        // Add listener to handle country selection change  *Lambda
        countryComboBox.setOnAction(event -> {
            handleCountrySelection();
        });

    }

    /**
     * Fetches values for customer selected in customers table view
     * @param selectedCustomer Customer selected in appointments table view
     * @throws SQLException SQL query fails
     */
    public void fetchCustomerValues(ObservableList<Object> selectedCustomer) throws SQLException {
        // Populate form values
        customerIdField.setText(String.valueOf(selectedCustomer.get(0)));
        customerNameField.setText(String.valueOf(selectedCustomer.get(1)));
        customerAddressField.setText(String.valueOf(selectedCustomer.get(2)));
        customerPostalField.setText(String.valueOf(selectedCustomer.get(3)));
        customerPhoneField.setText(String.valueOf(selectedCustomer.get(4)));
        stateComboBox.setValue(String.valueOf(selectedCustomer.get(5)));
        int divisionId = getDivisionIdByDivision(String.valueOf(selectedCustomer.get(5)));
        int countryId = getCountryIdByDivisionId(divisionId);
        String country = getCountryByCountryId(countryId);
        countryComboBox.setValue(country);
        // Trigger event listener to populate appropriate state/province options for second combo box
        handleCountrySelection();
    }

    /**
     * Handler to populate state/province options when country is selected
     */
    private void handleCountrySelection() {
        String selectedCountry = countryComboBox.getValue();
        try {
            int countryId = getCountryIdByName(selectedCountry);
            ObservableList<String> stateOptions = getStatesByCountryId(countryId);
            stateComboBox.setItems(stateOptions);

            // Select the first option, if available
            if (!stateOptions.isEmpty()) {
                stateComboBox.setValue(stateOptions.get(0));
            }
        } catch (SQLException e) {
            System.out.println("ComboBox update failed.");
        }
    }

    /**
     * Takes input values, validates them, and updates customer in SQL database. See included comments for more details.
     */
    public void modifyCustomer() {
        TextField[] modifyCustomerFields = new TextField[]{
                customerNameField,  // Element 0, string
                customerAddressField,  // Element 1, string
                customerPostalField,  // Element 2, string
                customerPhoneField,  // Element 3, string
        };

        for (TextField f : modifyCustomerFields) {
            f.setStyle("-fx-border-color: #999999"); //Resets red borders from previous errors
        }

        // Check for empty fields
        for (TextField f : modifyCustomerFields) {
            if (f.getText().trim().isEmpty()) {  // If any field is empty...
                f.setStyle("-fx-border-color: red");  // Highlight empty field
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(null);
                alert.setContentText("Field missing value.");
                alert.showAndWait();  // Display error
                return; // Cancel updating customer and return to form
            }
        }

        // Update values in database
        try {
            String customerIdString = customerIdField.getText();
            int customerId = Integer.parseInt(customerIdString);
            String customerName = modifyCustomerFields[0].getText().trim();
            String address = modifyCustomerFields[1].getText().trim();
            String postalCode = modifyCustomerFields[2].getText().trim();
            String phoneNumber = modifyCustomerFields[3].getText().trim();
            String division = stateComboBox.getValue();
            int divisionId = getDivisionIdByDivision(division);

            //System.out.println(customerName + "|" + address + "|" + postalCode + "|" + phoneNumber + "|" + divisionId);
            updateCustomerAll(customerId, customerName, address, postalCode, phoneNumber, divisionId);
            switchToMainForm();
        } catch (Exception e) {
            System.out.println("Customer update failed.");
            e.printStackTrace();
        }
    }

    /**
     * Switches to main form
     * @throws IOException Main form not found
     */
    public void switchToMainForm() throws IOException {
        // Open main form
        Parent root = FXMLLoader.load(getClass().getResource("/main/resources/mainForm.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        // Close previous form
        Stage loginStage = (Stage) cancelButton.getScene().getWindow();
        loginStage.close();
    }
}
