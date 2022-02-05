package com.example.nodeimageeditor

import javafx.application.Application
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.input.DataFormat
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage
import java.io.File

var app = NodeImageEditor()
var stateAddLink = DataFormat("linkAdd")
var stateAddNode = DataFormat("nodeAdd")

var saveLocationConfigFilePath = "\\Documents\\NodeImageEditor\\config\\"
var saveLocationImageConfigFileName = "image_saves"
var saveLocationSceneConfigFileName = "scene_saves"

var defaultImageSaveLocation = "\\Documents\\NodeImageEditor\\images\\"
var quickImageName = "quicksave"
var defaultImageName = "unnamed"

var defaultNodeSceneSaveLocation = "\\Documents\\NodeImageEditor\\scenes\\"
var quickSceneName = "quicksave"
var defaultSceneName = "unnamed"

class NodeImageEditor : Application() {
    lateinit var nodeStart: StartNode
    lateinit var nodeFinish: FinishNode
    @FXML
    lateinit var nodesCanvas: AnchorPane
    var nodeList = mutableSetOf<DraggableNode>()
    lateinit var controller: NodeImageEditorController

    private fun checkAllDirectories() {
        File(System.getenv("USERPROFILE") + saveLocationConfigFilePath + "test.txt").parentFile.mkdirs()
        File(System.getenv("USERPROFILE") + defaultImageSaveLocation + "test.txt").parentFile.mkdirs()
        File(System.getenv("USERPROFILE") + defaultNodeSceneSaveLocation + "test.txt").parentFile.mkdir()
    }

    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(NodeImageEditor::class.java.getResource("node-image-editor-styles.fxml"))
        val scene = Scene(fxmlLoader.load(), 1087.0, 800.0)
        controller = fxmlLoader.getController<Any>() as NodeImageEditorController
        stage.title = "NodeImageEditor"
        stage.scene = scene
        stage.show()
        stage.sizeToScene()
        stage.isResizable = false
        app = this
        checkAllDirectories()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(NodeImageEditor::class.java)
        }
    }
}