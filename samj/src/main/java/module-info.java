module com.samj {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires java.desktop;

    exports com.samj;
    exports com.samj.backend;
    exports com.samj.shared;
    exports com.samj.frontend;
    opens com.samj.backend to javafx.fxml;
    opens com.samj.shared to javafx.fxml;
    opens com.samj.frontend to javafx.fxml;
    opens com.samj to javafx.fxml;
    exports com.samj.frontend.tables;
    opens com.samj.frontend.tables to javafx.fxml;
}