package com.example.nodeimageeditor

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class NodeImageEditor : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(NodeImageEditor::class.java.getResource("node-image-editor-styles.fxml"))
        val scene = Scene(fxmlLoader.load(), 1200.0, 800.0)
        stage.title = "NodeImageEditor"
        stage.scene = scene
        stage.show()
        stage.sizeToScene()
        stage.isResizable = false
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(NodeImageEditor::class.java)
        }
    }
}