package com.example.nodeimageeditor

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.Point2D
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

enum class NodeType (val value : Int) {
    START(0),
    FINISH(1),
    FLOAT(2),
    INT(3),
    STRING(4),
    IMAGE(5),
    ADD_TEXT(6),
    ADD_IMAGE(7),
    BRIGHTNESS_FILTER(8),
    GREY_FILTER(9),
    SEPIA_FILTER(10),
    INVERT_FILTER(11),
    BLUR_FILTER(12),
    TRANSFORM_MOVE(13),
    TRANSFORM_SCALE(14),
    TRANSFORM_ROTATE(15);

    companion object {
        fun getByValue(value: Int) = values().firstOrNull { it.value == value }
    }
}

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

    private fun saveNodesData(file: File) {
        var text = ""
        if (!app.nodeList.contains(nodeStart))
            app.nodeList.add(nodeStart)
        if (!app.nodeList.contains(nodeFinish))
            app.nodeList.add(nodeFinish)
        for (item in app.nodeList) {
            text += item.id + " "
            text += item.nodeType?.value.toString() + " "
            text += item.layoutX.toString() + " "
            text += item.layoutY.toString() + " "
            text += item.nextNode?.id.toString() + " "
            if (item.nextNode != null) {
                for ((key, value) in item.nextNode!!.inputsByIndex)
                    if (item == item.nextNode!!.inputs[value])
                        text += "$key "
            } else {
                text += "null" + " "
            }
            if (item.nodeType == NodeType.INT)
                text += (item as IntNode).int_val.toString()
            if (item.nodeType == NodeType.FLOAT)
                text += (item as FloatNode).float_val.toString()
            if (item.nodeType == NodeType.STRING)
                text += (item as StringNode).string_val
            if (item.nodeType == NodeType.IMAGE)
                text += (item as ImageNode).imagePath
            if (item.nodeType == NodeType.START) {
                text += (item as StartNode).imagePath
            }
            println(text)
            text += "\n"
        }
        try {
            BufferedWriter(PrintWriter(file.path)).use { bw ->
                val file = File(text);
                if (file.isFile)
                    bw.write(file.parent)
                else
                    bw.write(text)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun saveScene() {
        val file = File(System.getenv("USERPROFILE") + defaultNodeSceneSaveLocation + quickSceneName + ".nodes")
        saveNodesData(file)
    }

    private fun saveSceneAs() {
        try {
            val fileChooser = FileChooser()
            val imageFilter = FileChooser.ExtensionFilter("Scene Files", "*.nodes")
            fileChooser.extensionFilters.add(imageFilter)
            fileChooser.initialDirectory = File(getPathFromConfig("scene"))
            fileChooser.initialFileName = defaultSceneName
            val file = fileChooser.showSaveDialog(nodesPane.scene.window)
            saveNodesData(file)
            rememberLastSavePath(file.path, "scene")
        } catch (e: NullPointerException) {}
    }

    private fun openScene() {

            val fileChooser = FileChooser()
            val imageFilter = FileChooser.ExtensionFilter("Scene Files", "*.nodes")
            fileChooser.extensionFilters.add(imageFilter)
            fileChooser.initialDirectory = File(getPathFromConfig("scene"))
            val file = fileChooser.showOpenDialog(nodesPane.scene.window)
            var text: String

            BufferedReader(FileReader(file)).use { reader ->
                text = reader.readText()
            }

            app.nodeList.clear()
            nodesPane.children.clear()

            var i = 0
            val nodeMap = mutableMapOf<String, DraggableNode>()
            val linkMap = mutableMapOf<String, Pair<String, String>>()
            while(i < text.length) {
                var id = ""
                while(text[i] != ' ') {
                    id += text[i]
                    i++
                }
                i++
                val info = arrayOf("", "", "", "", "", "")
                for (j: Int in 0..4) {
                    var item = ""
                    while (text[i] != ' ') {
                        item += text[i]
                        i++
                    }
                    i++
                    info[j] = item
                }
                linkMap[id] = Pair(info[3], info[4])
                var item = ""
                while (text[i] != '\n') {
                    item += text[i]
                    i++
                }
                i++
                info[5] = item

                when (NodeType.getByValue(info[0].toInt())) {
                    NodeType.INT -> {
                        nodeMap[id] = IntNode()
                        (nodeMap[id] as IntNode).input_field!!.text = info[5]
                        (nodeMap[id] as IntNode).int_val = info[5].toInt()
                    }
                    NodeType.FLOAT -> {
                        nodeMap[id] = FloatNode()
                        (nodeMap[id] as FloatNode).input_field!!.text = info[5]
                        (nodeMap[id] as FloatNode).float_val = info[5].toFloat()
                    }
                    NodeType.STRING -> {
                        nodeMap[id] = StringNode()
                        (nodeMap[id] as StringNode).input_field!!.text = info[5]
                        (nodeMap[id] as StringNode).string_val = info[5]
                    }
                    NodeType.IMAGE -> {
                        nodeMap[id] = ImageNode()
                        if (info[5] != "null") {
                            val stream: InputStream = FileInputStream(info[5])
                            nodeMap[id]?.imageView?.image = Image(stream)
                            nodeMap[id]?.imageView?.isManaged = true
                            (nodeMap[id] as ImageNode).imagePath = info[5]
                        }
                    }
                    NodeType.START -> {
                        nodeMap[id] = StartNode()
                        if (info[5] != "null") {
                            val stream: InputStream = FileInputStream(info[5])
                            nodeMap[id]?.imageView?.image = Image(stream)
                            (nodeMap[id] as StartNode).imagePath = info[5]
                        }
                        nodeStart = (nodeMap[id] as StartNode?)!!
                    }
                    NodeType.FINISH -> {
                        nodeMap[id] = FinishNode()
                        nodeFinish = (nodeMap[id] as FinishNode?)!!
                    }
                    NodeType.ADD_IMAGE -> nodeMap[id] = AddImageNode()
                    NodeType.ADD_TEXT -> nodeMap[id] = AddTextNode()
                    NodeType.BLUR_FILTER -> nodeMap[id] = BlurFilterNode()
                    NodeType.BRIGHTNESS_FILTER -> nodeMap[id] = BrightnessNode()
                    NodeType.GREY_FILTER -> nodeMap[id] = GreyFilterNode()
                    NodeType.INVERT_FILTER -> nodeMap[id] = InvertFilterNode()
                    NodeType.SEPIA_FILTER -> nodeMap[id] = SepiaFilterNode()
                    NodeType.TRANSFORM_MOVE -> nodeMap[id] = TransformMoveNode()
                    NodeType.TRANSFORM_ROTATE -> nodeMap[id] = TransformRotateNode()
                    NodeType.TRANSFORM_SCALE -> nodeMap[id] = TransformScaleNode()
                }
                println(info[0])
                nodesPane.children.add(nodeMap[id])
                val local = nodeMap[id]?.parent?.sceneToLocal(Point2D(0.0, 0.0))
                if (local != null)
                    nodeMap[id]?.updatePoint(Point2D(info[1].toDouble() - local.x, info[2].toDouble() - local.y))
            }

            for ((key, item) in nodeMap) {
                if (linkMap[key]?.first != "null") {
                    val nextNode = nodeMap[linkMap[key]?.first]
                    val leftHandle = linkMap[key]?.second?.let { nextNode?.inputsByIndex?.get(it.toInt()) }

                    item.link.isVisible = true
                    item.superParent!!.children.add(0, item.link)
                    if (nextNode != null) {
                        item.link.bindStartEnd(item, nextNode, Point2D(0.0, 0.0), Point2D(0.0, 0.0))
                        nextNode.inputs[leftHandle as Any] = item
                    }
                    item.nextNode = nextNode
                }
                item.update()
            }
            rememberLastSavePath(file.path, "scene")

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
        saveScene()
    }

    @FXML
    private fun onSaveSceneAsMenuClicked() {
        saveSceneAs()
    }

    @FXML
    private fun onOpenSceneMenuClicked() {
        openScene()
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

    @FXML
    private fun onAddTextNodeButtonClick(event: ActionEvent) {
        nodesPane.children.add(AddTextNode())
    }
    @FXML
    private fun onAddImageNodeButtonClick(event: ActionEvent) {
        nodesPane.children.add(AddImageNode())
    }

    @FXML
    private fun onBrightnessNodeButtonClick(event: ActionEvent) {
        nodesPane.children.add(BrightnessNode())
    }
    @FXML
    private fun onGreyFilterNodeButtonClick(event: ActionEvent) {
        nodesPane.children.add(GreyFilterNode())
    }
    @FXML
    private fun onSepiaFilterNodeButtonClick(event: ActionEvent) {
        nodesPane.children.add(SepiaFilterNode())
    }
    @FXML
    private fun onInvertFilterNodeButtonClick(event: ActionEvent) {
        nodesPane.children.add(InvertFilterNode())
    }
    @FXML
    private fun onBlurFilterNodeButtonClick(event: ActionEvent) {
        nodesPane.children.add(BlurFilterNode())
    }


    @FXML
    private fun onTransformMoveNodeButtonClick(event: ActionEvent) {
        nodesPane.children.add(TransformMoveNode())
    }
    @FXML
    private fun onTransformScaleNodeButtonClick(event: ActionEvent) {
        nodesPane.children.add(TransformScaleNode())
    }
    @FXML
    private fun onTransformRotateNodeButtonClick(event: ActionEvent) {
        nodesPane.children.add(TransformRotateNode())
    }
}