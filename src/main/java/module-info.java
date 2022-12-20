module com.example.supplychainneeraj {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.supplychainneeraj to javafx.fxml;
    exports com.example.supplychainneeraj;
}