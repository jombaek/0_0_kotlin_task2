package com.example.nodeimageeditor

import javafx.scene.SnapshotParameters
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage

class TransformRotateNode: DraggableNode() {
    var image: Image? = null
    var float_val = 0.0F;
    init {
        titleLabel?.text = "Transform Rotate"

        initImageView()

        addLeftHandle("Image")
        addLeftHandle("Float")

        addRightHandle("Image")

        nodeType = NodeType.TRANSFORM_ROTATE
    }

    override fun update() {
        image = readInput(0, null) as Image?
        float_val = readInput(1, 0.0F) as Float
        if (image != null) {
            val rotateView = ImageView()
            rotateView.image = image
            rotateView.rotate = Math.toDegrees(float_val.toDouble());
            val snapshotParams = SnapshotParameters()
            val newImage = WritableImage(rotateView.boundsInParent.width.toInt(), rotateView.boundsInParent.height.toInt())
            rotateView.snapshot(snapshotParams, newImage)
            image = newImage
        }
        imageView.image = image
        nextNode?.update()
    }
    override fun getValue(): Any? {
        return image
    }
}