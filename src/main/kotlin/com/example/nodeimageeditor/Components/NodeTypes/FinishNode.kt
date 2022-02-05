package com.example.nodeimageeditor

import javafx.scene.image.Image

class FinishNode: DraggableNode() {
    var image: Image? = null
    init {
        titleLabel?.text = "Finish Image"

        titleClose?.isVisible = false

        initImageView()

        addLeftHandle("Image")

        relocate(800.0, 220.0)
    }

    override fun update() {
        image = readInput(0, null) as Image?
        imageView.image = image
        app.controller.changeFullImageView(image)
        nextNode?.update()
    }
}