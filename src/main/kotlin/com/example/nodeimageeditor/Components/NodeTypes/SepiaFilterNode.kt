package com.example.nodeimageeditor

import javafx.scene.SnapshotParameters
import javafx.scene.effect.SepiaTone
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage

class SepiaFilterNode: DraggableNode() {
    var image: Image? = null
    init {
        titleLabel?.text = "Sepia Filter"

        initImageView()

        addLeftHandle("Image")

        addRightHandle("Image")
    }

    override fun update() {
        image = readInput(0, null) as Image?
        if (image != null) {
            val sepiaFilter = SepiaTone()
            sepiaFilter.level = 1.0
            val sepiaView = ImageView()
            sepiaView.image = image
            sepiaView.effect = sepiaFilter
            val snapshotParameters = SnapshotParameters()
            val newImage = WritableImage(image!!.width.toInt(), image!!.height.toInt())
            sepiaView.snapshot(snapshotParameters, newImage)
            image = newImage
        }
        imageView.image = image
        nextNode?.update()
    }
    override fun getValue(): Any? {
        return image
    }
}