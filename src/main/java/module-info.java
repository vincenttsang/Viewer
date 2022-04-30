module org.vincenttsang.viewer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires sanselan;
    requires java.datatransfer;
    requires java.desktop;

    opens org.vincenttsang.viewer to javafx.fxml;
    exports org.vincenttsang.viewer;
}