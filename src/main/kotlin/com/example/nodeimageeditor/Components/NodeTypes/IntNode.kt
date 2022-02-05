package com.example.nodeimageeditor

import javafx.scene.control.TextField

class IntNode: DraggableNode() {
    var input_field: TextField? = null
    var int_val = 0;
    init {
        titleLabel?.text = "Integer"

        addRightHandle("Int")

        input_field = TextField()
        input_field!!.textProperty().addListener { observable, oldValue, newValue ->
            if (newValue.toIntOrNull() != null)
                int_val = newValue.toInt()
            nextNode?.update()
        }
        input_field!!.prefWidth = 60.0
        content?.children?.add(input_field)

        nodeType = NodeType.INT
    }
    override fun update() {
        nextNode?.update()
    }
    override fun getValue(): Any {
        return int_val
    }
}