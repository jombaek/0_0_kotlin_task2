package com.example.nodeimageeditor

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.input.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import java.util.*

lateinit var LinkStartNode: DraggableNode
lateinit var LinkStartSocketPosition: Point2D
lateinit var LinkEndSocketPosition: Point2D

open class DraggableNode : AnchorPane() {
    @FXML
    var nodeMainPane: AnchorPane? = null
    @FXML
    var leftLinks: VBox? = null
    @FXML
    var rightLinks: VBox? = null
    @FXML
    var titleBar: StackPane? = null
    @FXML
    var titleLabel: Label? = null
    @FXML
    var titleClose: AnchorPane? = null
    @FXML
    var content: VBox? = null

    var nextNode: DraggableNode? = null
    var inputs: MutableMap<Any, DraggableNode> = mutableMapOf()
    var inputsByIndex: MutableMap<Int, Any> = mutableMapOf()
    var imageView = ImageView()
    var inputTypes: MutableMap<Any, String> = mutableMapOf()
    var outputType: String? = null

    var x: Int = 0
    var y: Int = 0

    lateinit var contextDragOver: EventHandler<DragEvent>
    lateinit var contextDragDropped: EventHandler<DragEvent>

    lateinit var linkDragDetected: EventHandler<MouseEvent>
    lateinit var linkDragDropped: EventHandler<DragEvent>
    lateinit var contextLinkDragOver: EventHandler<DragEvent>
    lateinit var contextLinkDragDropped: EventHandler<DragEvent>

    var link = NodeLink()
    var offset = Point2D(0.0, 0.0)

    var superParent: AnchorPane? = null

    @FXML
    open fun initialize() {
        nodeHandlers()
        linkHandlers()

        link.isVisible = false

        parentProperty().addListener{ o, old, new -> superParent = parent as AnchorPane? }
    }

    init {
        val fxmlLoader = FXMLLoader(
            javaClass.getResource("node-styles.fxml")
        )
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        fxmlLoader.load<Any>()
        id = UUID.randomUUID().toString()
        app.nodeList.add(this)
    }

    fun updatePoint(p: Point2D) {
        val local = parent.sceneToLocal(p)
        relocate(
            (local.x - offset.x),
            (local.y - offset.y)
        )
    }

    fun nodeHandlers() {

        contextDragOver = EventHandler { event ->
            if (event.x - offset.x >= 0 && event.y - offset.y >= 0)
                updatePoint(Point2D(event.sceneX, event.sceneY))
            event.consume()
        }

        contextDragDropped = EventHandler { event ->
            parent.onDragDropped = null
            parent.onDragOver = null
            event.isDropCompleted = true
            event.consume()
        }

        titleBar!!.onDragDetected = EventHandler { event ->
            try {
                parent.onDragOver = contextDragOver
                parent.onDragDropped = contextDragDropped

                offset = Point2D(event.x, event.y)
                updatePoint(Point2D(event.sceneX, event.sceneY))

                val content = ClipboardContent()
                content[stateAddNode] = "node"
                startDragAndDrop(*TransferMode.ANY).setContent(content)
            } catch (e: NullPointerException){}
        }

        titleClose!!.onMousePressed = EventHandler { event ->
            if (nextNode != null) {
                for ((key, value) in nextNode!!.inputs)
                    if (this == value) {
                        nextNode!!.inputs.remove(key)
                        break
                    }
                nextNode!!.update()
            }
            for ((key, value) in this.inputs) {
                value.nextNode = null
                superParent!!.children.remove(value.link)
            }
            link.isVisible = false
            app.nodeList.remove(this)
            superParent!!.children.remove(link)
            superParent!!.children.remove(this)
        }

    }

    fun linkHandlers() {

        linkDragDetected = EventHandler { event ->
            parent.onDragOver = null
            parent.onDragDropped = null

            parent.onDragOver = contextLinkDragOver
            parent.onDragDropped = contextLinkDragDropped
            if (!superParent!!.children.contains(link))
                superParent!!.children.add(0, link)
            link.isVisible = true
            link.unbindStartEnd(this)
            LinkStartNode = this

            if (this.nextNode != null) {
                for ((key, value) in nextNode!!.inputs)
                    if (this == value) {
                        nextNode!!.inputs.remove(key)
                        break
                    }
                nextNode!!.update()
                this.nextNode = null
            }

            var p = Point2D(this.layoutX, this.layoutY)
            LinkStartSocketPosition = Point2D(0.0, 0.0)
            for (item: Node in rightLinks?.children!!) {
                if (item.contains(event.x - 8, event.y - 0))
                {
                    p = Point2D(layoutX + rightLinks!!.layoutX + item.layoutX + 15,
                                layoutY + titleBar!!.height + rightLinks!!.layoutY + item.layoutY + 15 / 2)
                    LinkStartSocketPosition = Point2D(rightLinks!!.layoutX + item.layoutX - 30, titleBar!!.height + rightLinks!!.layoutY + item.layoutY - 37.5)
                }
            }
            link.setStart(p)

            val content = ClipboardContent()
            content[stateAddLink] = "link"
            startDragAndDrop(*TransferMode.ANY).setContent(content)
            event.consume()
        }

        linkDragDropped = EventHandler { event ->
            parent.onDragDropped = null
            parent.onDragOver = null

            LinkStartNode.link.isVisible = false

            if (inputTypes[event.source] == LinkStartNode.outputType && inputs[event.source] == null && notALoop(
                    LinkStartNode, this)) {
                LinkStartNode.link.isVisible = true
                LinkStartNode.link.bindStartEnd(LinkStartNode, this, LinkStartSocketPosition, LinkEndSocketPosition)

                inputs[event.source] = LinkStartNode
                LinkStartNode.nextNode = this
                this.update()
            }
            event.isDropCompleted = true
            event.consume()
        }


        contextLinkDragOver = EventHandler { event ->
            if (event.x >= 0 && event.y >= 0) {
                event.acceptTransferModes(*TransferMode.ANY)
                if (!link.isVisible)
                    link.isVisible = true
                var p = Point2D(event.x, event.y)
                LinkEndSocketPosition = Point2D(-45.0, titleBar!!.height + rightLinks!!.layoutY - 15)
                link.setEnd(p)
            }

            event.consume()
        }

        contextLinkDragDropped = EventHandler { event ->
            parent.onDragDropped = null
            parent.onDragOver = null

            link.isVisible = false
            superParent!!.children.remove(link)

            event.isDropCompleted = true
            event.consume()
        }
    }

    fun notALoop(node1: DraggableNode, node2: DraggableNode): Boolean {
        var it = node2
        if (node1 == node2)
            return false
        while (it.nextNode != null) {
            if (it.nextNode == node1)
                return false
            it = it.nextNode!!
        }
        return true
    }

    fun initImageView() {
        imageView.fitWidth = 120.0
        imageView.fitHeight = 60.0
        imageView.isPreserveRatio = true
        content?.children?.add(imageView)
    }

    fun addLeftHandle(type: String) {
        val new_handle = AnchorPane()
        leftLinks?.children?.add(new_handle)
        new_handle.prefHeight = 15.0
        new_handle.style =  "-fx-border-color: rgba(200,200,200,1); -fx-background-radius: 0 0 0 10;"
        new_handle.onDragDropped = linkDragDropped
        inputTypes[leftLinks?.children!![leftLinks?.children!!.size - 1]] = type
        inputsByIndex[leftLinks?.children!!.size - 1] = leftLinks?.children!![leftLinks?.children!!.size - 1]
        when (type) {
            "Int" -> new_handle.style = "-fx-background-color: rgba(100, 100, 255, 1);"
            "Float" -> new_handle.style = "-fx-background-color: rgba(255, 100, 100, 1);"
            "String" -> new_handle.style = "-fx-background-color: rgba(100, 255, 100, 1);"
            "Image" -> new_handle.style = "-fx-background-color: rgba(100, 100, 100, 1);"
        }
        for (item: Node in leftLinks?.children!!) {
            (item as AnchorPane).prefHeight = 15.0 / leftLinks?.children!!.size
        }
    }

    fun addRightHandle(type: String) {
        val new_handle = AnchorPane()
        new_handle.style =  "-fx-border-color: rgba(200,200,200,1); -fx-background-radius: 0 0 10 0;"
        new_handle.prefHeight = 15.0
        new_handle.onDragDetected = linkDragDetected
        rightLinks?.children?.add(new_handle)
        outputType = type
        when (type) {
            "Int" -> new_handle.style = "-fx-background-color: rgba(100, 100, 255, 1);"
            "Float" -> new_handle.style = "-fx-background-color: rgba(255, 100, 100, 1);"
            "String" -> new_handle.style = "-fx-background-color: rgba(100, 255, 100, 1);"
            "Image" -> new_handle.style = "-fx-background-color: rgba(100, 100, 100, 1);"
        }
    }

    fun readInput(index: Int, default: Any?): Any? {
        if (inputs[leftLinks?.children!![index]] != null)
            return inputs[leftLinks?.children!![index]]?.getValue()
        return default
    }

    open fun update() {}
    open fun getValue(): Any? {
        return 0
    }
}
