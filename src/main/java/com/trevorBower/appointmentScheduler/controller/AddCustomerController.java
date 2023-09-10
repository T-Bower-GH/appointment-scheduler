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

import static com.trevorBower.appointmentScheduler.helper.CustomerQuery.insertCustomer;
import static com.trevorBower.appointmentScheduler.helper.FirstLevelDivisionsQuery.*;

public class AddCustomerController {

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
     * Lambda used here to remove the need to define additional separate method
     */
    public void initialize() {
        customerIdField.setDisable(true); // Disables the ID field
        customerIdField.setText("Disabled - Auto-generated");
        // Config Country combo box
        ObservableList<String> countryOptions = FXCollections.observableArrayList(
                "U.S",
                "UK",
                "Canada"
        );
        countryComboBox.setItems(countryOptions);
        countryComboBox.setValue(countryOptions.get(0));  // Automatically selects first available country option
        handleCountrySelection();  // Automatically populates state options based on first available country

        // Add listener to handle country selection change  *Lambda
        countryComboBox.setOnAction(event -> {
            handleCountrySelection();
        });
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
     * Takes input values, validates them, and inserts into SQL database as customer. See included comments for more details.
     */
    public void addCustomer() {
        TextField[] addCustomerFields = new TextField[]{
                customerNameField,  // Element 0, string
                customerAddressField,  // Element 1, string
                customerPostalField,  // Element 2, string
                customerPhoneField,  // Element 3, string
        };

        for (TextField f : addCustomerFields) {
            f.setStyle("-fx-border-color: #999999"); //Resets red borders from previous errors
        }

        // Check for empty fields
        for (TextField f : addCustomerFields) {
            if (f.getText().trim().isEmpty()) {  // If any field is empty...
                f.setStyle("-fx-border-color: red");  // Highlight empty field
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(null);
                alert.setContentText("Field missing value.");
                alert.showAndWait();  // Display error
                return; // Cancel adding customer and return to form
            }
        }

        // Insert values into database
        try {
            String customerName = addCustomerFields[0].getText().trim();
            String address = addCustomerFields[1].getText().trim();
            String postalCode = addCustomerFields[2].getText().trim();
            String phoneNumber = addCustomerFields[3].getText().trim();
            String division = stateComboBox.getValue();
            int divisionId = getDivisionIdByDivision(division);

            //System.out.println(customerName + "|" + address + "|" + postalCode + "|" + phoneNumber + "|" + divisionId);
            insertCustomer(customerName, address, postalCode, phoneNumber, divisionId);
            switchToMainForm();
        } catch (Exception e) {
            System.out.println("Customer add failed.");
            e.printStackTrace();
        }
    }

    /**
     * Switches back to main form
     * @throws IOException Main form file cannot be found
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
