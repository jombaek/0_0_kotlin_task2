package com.example.nodeimageeditor

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.stage.FileChooser
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_4BYTE_ABGR
import java.io.*
import javax.imageio.ImageIO
import kotlin.system.exitProcess

class NodeImageEditorController {
    @FXML
    private lateinit var nodesToolPanel: HBox
    @FXML
    private lateinit var imageViewButton: Button
    @FXML
    private lateinit var nodeViewButton: Button
    @FXML
    private lateinit var fullImageView: ImageView
    @FXML
    private lateinit var nodesPane: AnchorPane
    @FXML
    private lateinit var nodeStart: StartNode
    @FXML
    private lateinit var nodeFinish: FinishNode
    @FXML
    private lateinit var saveImageMenuItem : MenuItem
    @FXML
    private lateinit var saveImageAsMenuItem: MenuItem

    private enum class AppMode {
        IMAGE_VIEW,
        NODES_VIEW
    }

    private var appMode = AppMode.NODES_VIEW
    var hasImage = false

    private fun convertToFxImage(image: BufferedImage?): Image? {
        var wr: WritableImage? = null
        if (image != null) {
            wr = WritableImage(image.width, image.height)
            val pw = wr.pixelWriter
            for (x in 0 until image.width) {
                for (y in 0 until image.height) {
                    pw.setArgb(x, y, image.getRGB(x, y))
                }
            }
        }
        return ImageView(wr).image
    }

    private fun convertToBufferedImage(image: Image?): BufferedImage? {
        var wr: BufferedImage? = null
        if (image != null) {
            wr = BufferedImage(image.width.toInt(), image.height.toInt(), TYPE_4BYTE_ABGR)
            var pw = image.pixelReader
            for (x in 0 until image.width.toInt()) {
                for (y in 0 until image.height.toInt()) {
                    wr.setRGB(x, y, pw.getArgb(x, y))
                }
            }
        }
        return wr
    }

    private fun changeAppMode(mode: AppMode) {
        when (mode) {
            AppMode.NODES_VIEW -> {
                imageViewButton.isVisible = true
                nodeViewButton.isVisible = false
                fullImageView.isVisible = false
                nodesPane.isVisible = true
                nodesToolPanel.isVisible = true
                appMode = AppMode.NODES_VIEW
            }
            AppMode.IMAGE_VIEW -> {
                imageViewButton.isVisible = false
                nodeViewButton.isVisible = true
                fullImageView.isVisible = true
                nodesPane.isVisible = false
                nodesToolPanel.isVisible = false
                appMode = AppMode.IMAGE_VIEW
            }
        }
    }

    fun rememberLastSavePath(path: String, type: String) {
        try {
            var configFileName = ""
            if (type == "image")
                configFileName = saveLocationImageConfigFileName
            else if (type == "scene")
                configFileName = saveLocationSceneConfigFileName

            BufferedWriter(PrintWriter(System.getenv("USERPROFILE") + saveLocationConfigFilePath + configFileName + ".txt")).use { bw ->
                var file = File(path);
                if (file.isFile)
                    bw.write(file.parent)
                else
                    bw.write(path)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getPathFromConfig(type: String): String {
        var configFileName = ""
        var defaultSaveLocation = ""
        if (type == "image") {
            configFileName = saveLocationImageConfigFileName
            defaultSaveLocation = defaultImageSaveLocation
        }
        else if (type == "scene") {
            configFileName = saveLocationSceneConfigFileName
            defaultSaveLocation = defaultNodeSceneSaveLocation
        }

        var file = File(System.getenv("USERPROFILE") + saveLocationConfigFilePath + configFileName + ".txt")
        if (!file.exists())
            return System.getenv("USERPROFILE") + defaultSaveLocation
        BufferedReader(FileReader(System.getenv("USERPROFILE") + saveLocationConfigFilePath + configFileName + ".txt")).use { reader ->
            return reader.readLine()
        }
    }

    fun chooseImageFileToOpen(): File? {
        try {
            val fileChooser = FileChooser()
            val imageFilter = FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png")
            fileChooser.extensionFilters.add(imageFilter)
            fileChooser.initialDirectory = File(getPathFromConfig("image"))
            return fileChooser.showOpenDialog(nodesPane.scene.window)
        } catch (e : NullPointerException){
        }
        return null
    }

    private fun openImage() {
        var file = chooseImageFileToOpen()
        if (file != null) {
            rememberLastSavePath(file.path, "image")
            val image = Image(file.toURI().toString());
            nodeStart.imageView.image = image
            nodeStart.imagePath = file.path
            nodeStart.update()
        }
    }

    fun changeFullImageView(newImage: Image?) {
        if (newImage != null) {
            fullImageView.image = newImage
            hasImage = true
            saveImageMenuItem.isDisable = false
            saveImageAsMenuItem.isDisable = false
        }
    }

    private fun saveImage() {
        val file = File(System.getenv("USERPROFILE") + defaultImageSaveLocation + quickImageName + ".png")
        val image = nodeFinish.image
        ImageIO.write(convertToBufferedImage(image), "png", file)
    }

    private fun saveImageAs() {
        val fileChooser = FileChooser()
        val imageFilter = FileChooser.ExtensionFilter("Image Files", "*.png")
        fileChooser.extensionFilters.add(imageFilter)
        fileChooser.initialDirectory = File(getPathFromConfig("image"))
        fileChooser.initialFileName = defaultImageName
        val file = fileChooser.showSaveDialog(nodesPane.scene.window)

        val image = nodeFinish.image
        ImageIO.write(convertToBufferedImage(image), "png", file)
        rememberLastSavePath(file.path, "image")
    }

    @FXML
    private fun onOpenMenuClicked() {
        openImage()
    }

    @FXML
    private fun onSaveImageMenuClicked() {
        saveImage()
    }

    @FXML
    private fun onSaveImageAsMenuClicked() {
        saveImageAs()
    }

    @FXML
    private fun onSaveSceneMenuClicked() {
    }

    @FXML
    private fun onSaveSceneAsMenuClicked() {
    }

    @FXML
    private fun onOpenSceneMenuClicked() {
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
    @FXML
    private fun onIntNodeButtonClick(event: ActionEvent) {
        nodesPane.children.add(IntNode())
    }
    @FXML
    private fun onStringNodeButtonClick(event: ActionEvent) {
        nodesPane.children.add(StringNode())
    }
    @FXML
    private fun onImageNodeButtonClick(event: ActionEvent) {
        nodesPane.children.add(ImageNode())
    }

}