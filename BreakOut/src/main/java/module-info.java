module com.example.breakout {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example to javafx.fxml;
    exports com.example;
}