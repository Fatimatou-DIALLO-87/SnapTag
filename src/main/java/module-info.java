module com.example.projet {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;

    opens com.example.projet to javafx.fxml;
    exports com.example.projet;
    exports com.example.projet.filter;
    opens com.example.projet.filter to javafx.fxml;
}
