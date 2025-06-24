module org.fabianandiel {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.fabianandiel.gui to javafx.fxml;

    exports org.fabianandiel.gui;
}