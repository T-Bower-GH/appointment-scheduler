module com.example.software2project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports com.trevorBower.appointmentScheduler;
    exports com.trevorBower.appointmentScheduler.controller;

    opens com.trevorBower.appointmentScheduler.controller;
}