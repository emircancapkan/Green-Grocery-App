module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires com.fasterxml.jackson.databind;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports com.example.demo.models;
    opens com.example.demo.models to javafx.fxml;
}