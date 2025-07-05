module org.fabianandiel {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires static lombok;
    requires jakarta.validation;
    requires org.hibernate.validator;

    opens org.fabianandiel.validation to org.hibernate.validator;
    opens org.fabianandiel.gui to javafx.fxml;


    exports org.fabianandiel.gui;
    exports org.fabianandiel.services;
    exports org.fabianandiel.validation;
    exports org.fabianandiel.entities;

}