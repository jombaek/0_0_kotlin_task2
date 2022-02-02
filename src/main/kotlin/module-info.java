module com.example.nodeimageeditor {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires org.kordamp.bootstrapfx.core;
    requires kotlinx.serialization.core;
    requires kotlinx.serialization.json;

    opens com.example.nodeimageeditor to javafx.fxml;
    exports com.example.nodeimageeditor;
}