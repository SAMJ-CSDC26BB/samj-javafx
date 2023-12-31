module com.samj.samj {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.desktop;

    opens com.samj.samj to javafx.fxml;
    exports com.samj.samj;
    exports com.samj.backend;
    opens com.samj.backend to javafx.fxml;
}