module com.ev_volt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires jssc;
    requires java.desktop;

    opens com.ev_volt to javafx.fxml;
    opens com.ev_volt.controllers to javafx.fxml;
        
    exports com.ev_volt.controllers;
    exports com.ev_volt;
}
