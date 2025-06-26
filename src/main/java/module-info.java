module org.fabianandiel {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;

    opens org.fabianandiel.gui to javafx.fxml;

    exports org.fabianandiel.gui;
}