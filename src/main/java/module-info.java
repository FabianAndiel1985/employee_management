module org.fabianandiel {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires static lombok;
    requires jakarta.validation;
    requires org.hibernate.validator;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    opens org.fabianandiel.validation to org.hibernate.validator;
    opens org.fabianandiel.gui to javafx.fxml;
    opens org.fabianandiel.entities to org.hibernate.orm.core, jakarta.persistence;


    exports org.fabianandiel.gui;
    exports org.fabianandiel.services;
    exports org.fabianandiel.validation;
    exports org.fabianandiel.entities;
    exports org.fabianandiel.constants;
    exports org.fabianandiel.context;
}