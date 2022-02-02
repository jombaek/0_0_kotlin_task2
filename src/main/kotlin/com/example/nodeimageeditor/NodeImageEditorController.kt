package com.example.nodeimageeditor

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent

class NodeImageEditorController {
    @FXML
    private lateinit var welcomeText: Label

    @FXML
    private fun onHelloButtonClick() {
        welcomeText.text = "Welcome to JavaFX Application!"
    }

    @FXML
    private fun onCanvasMousePressed(event: MouseEvent) {
    }
    @FXML
    private fun onCanvasMouseDragged(event: MouseEvent) {
    }
    @FXML
    private fun onCanvasMouseReleased(event: MouseEvent) {
    }

}