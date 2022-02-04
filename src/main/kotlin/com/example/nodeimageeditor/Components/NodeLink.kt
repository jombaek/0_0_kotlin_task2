package com.example.nodeimageeditor

import javafx.beans.binding.Bindings
import javafx.beans.binding.When
import javafx.beans.property.SimpleDoubleProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Point2D
import javafx.scene.layout.AnchorPane
import javafx.scene.shape.CubicCurve
import java.io.IOException
import java.util.*

class NodeLink : AnchorPane() {
    @FXML
    var nodeLink: CubicCurve? = null

    val offsetX = SimpleDoubleProperty()
    val offsetY = SimpleDoubleProperty()
    val offsetDirX1 = SimpleDoubleProperty()
    val offsetDirX2 = SimpleDoubleProperty()
    val offsetDirY1 = SimpleDoubleProperty()
    val offsetDirY2 = SimpleDoubleProperty()

    @FXML
    private fun initialize() {
        offsetX.set(100.0)
        offsetY.set(50.0)

        offsetDirX1.bind(
            When(nodeLink!!.startXProperty().greaterThan(nodeLink!!.endXProperty())).then(-1.0).otherwise(1.0))

        offsetDirX2.bind(
            When(nodeLink!!.startXProperty().greaterThan(nodeLink!!.endXProperty())).then(1.0).otherwise(-1.0))

        nodeLink!!.controlX1Property().bind(Bindings.add(nodeLink!!.startXProperty(), offsetX.multiply(offsetDirX1)))
        nodeLink!!.controlX2Property().bind(Bindings.add(nodeLink!!.endXProperty(), offsetX.multiply(offsetDirX2)))
        nodeLink!!.controlY1Property().bind(Bindings.add(nodeLink!!.startYProperty(), offsetY.multiply(offsetDirY1)))
        nodeLink!!.controlY2Property().bind(Bindings.add(nodeLink!!.endYProperty(), offsetY.multiply(offsetDirY2)))
    }

    fun setStart(point: Point2D) {
        nodeLink!!.startX = point.x
        nodeLink!!.startY = point.y
    }

    fun setEnd(point: Point2D) {
        nodeLink!!.endX = point.x
        nodeLink!!.endY = point.y
    }

    fun bindStartEnd(source1: DraggableNode, source2: DraggableNode, source1Socket: Point2D, source2Socket: Point2D) {
        nodeLink!!.startXProperty().bind(Bindings.add(source1.layoutXProperty().add(source1Socket.x), 45.0))
        nodeLink!!.startYProperty().bind(Bindings.add(source1.layoutYProperty().add(source1Socket.y), 45.0))
        nodeLink!!.endXProperty().bind(Bindings.add(source2.layoutXProperty().add(source2Socket.x), 45.0))
        nodeLink!!.endYProperty().bind(Bindings.add(source2.layoutYProperty().add(source2Socket.y), 45.0))
    }

    fun unbindStartEnd(source: DraggableNode) {
        nodeLink!!.startXProperty().unbind()
        nodeLink!!.startYProperty().unbind()
        nodeLink!!.endXProperty().unbind()
        nodeLink!!.endYProperty().unbind()
    }

    init {
        val fxmlLoader = FXMLLoader(
            javaClass.getResource("node-link-styles.fxml")
        )
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        try {
            fxmlLoader.load<Any>()
        } catch (exception: IOException) {
            throw RuntimeException(exception)
        }
        id = UUID.randomUUID().toString()
    }
}
