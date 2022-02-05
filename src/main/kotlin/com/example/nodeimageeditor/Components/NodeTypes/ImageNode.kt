package com.example.nodeimageeditor

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.image.Image

class ImageNode() : DraggableNode() {
    val openButton = Button("Open")
    var imagePath: String = "null"
    init {
        titleLabel?.text = "Image"

        addRightHandle("Image")
        initImageView()
        imageView.fitHeight = 60.0
        imageView.isManaged = false

        openButton.onAction = EventHandler {
            val file = app.controller.chooseImageFileToOpen()
            if (file != null) {
                app.controller.rememberLastSavePath(file.path, "image")
                imagePath = file.path
                val image = Image(file.toURI().toString())
                imageView.isManaged = true
                imageView.image = image
                nextNode?.update()
            }
        }
        openButton.prefWidth = 50.0
        content?.children?.add(openButton)
        nodeMainPane?.prefHeight = 130.0
    }
    override fun update() {
        nextNode?.update()
    }
    override fun getValue(): Any? {
        return imageView.image
    }
}