package com.example.nodeimageeditor

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import kotlin.system.exitProcess

class NodeImageEditorController {
    @FXML
    private lateinit var nodesToolPanel: HBox
    @FXML
    private lateinit var imageViewButton: Button
    @FXML
    private lateinit var nodeViewButton: Button
    @FXML
    private lateinit var imageCanvas: Canvas
    @FXML
    private lateinit var nodesPane: AnchorPane

    private enum class AppMode {
        IMAGE_VIEW,
        NODES_VIEW
    }

    private var appMode = AppMode.NODES_VIEW

    private fun changeAppMode(mode: AppMode) {
        when (mode) {
            AppMode.NODES_VIEW -> {
                imageViewButton.isVisible = true
                nodeViewButton.isVisible = false
                imageCanvas.isVisible = false
                nodesPane.isVisible = true
                nodesToolPanel.isVisible = true
                appMode = AppMode.NODES_VIEW
            }
            AppMode.IMAGE_VIEW -> {
                imageViewButton.isVisible = false
                nodeViewButton.isVisible = true
                imageCanvas.isVisible = true
                nodesPane.isVisible = false
                nodesToolPanel.isVisible = false
                appMode = AppMode.IMAGE_VIEW
            }
        }
    }


    @FXML
    private fun onOpenMenuClicked() {
    }

    @FXML
    private fun onSaveMenuClicked() {
    }

    @FXML
    private fun onSaveAsMenuClicked() {
    }

    @FXML
    private fun onSaveGraphMenuClicked() {
    }

    @FXML
    private fun onSaveGraphAsMenuClicked() {
    }

    @FXML
    private fun onOpenGraphMenuClicked() {
    }

    @FXML
    private fun onExitMenuClicked() {
        exitProcess(0)
    }

    @FXML
    private fun onNodeViewButtonClick(event: ActionEvent) {
        changeAppMode(AppMode.NODES_VIEW)
    }

    @FXML
    private fun onImageViewButtonClick(event: ActionEvent) {
        changeAppMode(AppMode.IMAGE_VIEW)
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

    @FXML
    private fun onFloatNodeButtonClick(event: ActionEvent) {
        nodesPane.children.add(FloatNode())
    }
}