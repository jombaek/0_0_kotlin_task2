package com.example.nodeimageeditor

import com.example.nodeimageeditor.FinishNode
import com.example.nodeimageeditor.StartNode
import javafx.application.Application
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.input.DataFormat
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

var app = NodeImageEditor()
var stateAddLink = DataFormat("linkAdd")
var stateAddNode = DataFormat("nodeAdd")

class NodeImageEditor : Application() {
    lateinit var nodeStart: StartNode
    lateinit var nodeFinish: FinishNode
    @FXML
    lateinit var nodesCanvas: AnchorPane
    var nodeList = mutableSetOf<DraggableNode>()

    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(NodeImageEditor::class.java.getResource("node-image-editor-styles.fxml"))
        val scene = Scene(fxmlLoader.load(), 1087.0, 800.0)
        stage.title = "NodeImageEditor"
        stage.scene = scene
        stage.show()
        stage.sizeToScene()
        stage.isResizable = false
        app = this
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(NodeImageEditor::class.java)
        }
    }
}