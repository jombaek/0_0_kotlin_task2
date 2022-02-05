package com.example.nodeimageeditor

import javafx.scene.image.Image
import javafx.scene.image.WritableImage

class InvertFilterNode: DraggableNode() {
    var image: Image? = null
    init {
        titleLabel?.text = "Invert Filter"

        initImageView()

        addLeftHandle("Image")

        addRightHandle("Image")

        nodeType = NodeType.INVERT_FILTER
    }

    override fun update() {
        image = readInput(0, null) as Image?
        if (image != null) {
            val newImage = WritableImage(image!!.width.toInt(), image!!.height.toInt())
            val reader = image!!.pixelReader
            val writer = newImage.pixelWriter
            for (y in 0 until image!!.height.toInt()) {
                for (x in 0 until image!!.width.toInt()) {
                    writer.setColor(x, y, reader.getColor(x, y).invert())
                }
            }
            image = newImage
        }
        imageView.image = image
        nextNode?.update()
    }
    override fun getValue(): Any {
        return image!!
    }
}