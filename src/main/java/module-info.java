module org.fabianandiel {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires static lombok;
    requires jakarta.validation;
    requires org.hibernate.validator;

    opens org.fabianandiel.dto to org.hibernate.validator;
    opens org.fabianandiel.gui to javafx.fxml;

    exports org.fabianandiel.gui;
}