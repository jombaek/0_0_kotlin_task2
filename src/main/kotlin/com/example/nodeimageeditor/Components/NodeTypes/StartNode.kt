package com.example.nodeimageeditor

class StartNode: DraggableNode() {
    var imagePath: String = "null"
    init {
        titleLabel?.text = "Start Image"

        titleClose?.isVisible = false

        initImageView()

        addRightHandle("Image")

        relocate(200.0, 220.0)

        nodeType = NodeType.START
    }
    override fun update() {
        nextNode?.update()
    }
    override fun getValue(): Any? {
        return imageView.image
    }
}