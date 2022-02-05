package com.example.nodeimageeditor

import javafx.scene.control.TextField

class FloatNode: DraggableNode() {
    var input_field: TextField? = null
    var float_val = 0.0F;
    init {
        titleLabel?.text = "Float"

        addRightHandle("Float")

        input_field = TextField()
        input_field!!.textProperty().addListener { observable, oldValue, newValue ->
            if (newValue.toFloatOrNull() != null)
                float_val = newValue.toFloat()
            nextNode?.update()
        }
        input_field!!.prefWidth = 60.0
        content?.children?.add(input_field)

        nodeType = NodeType.FLOAT
    }
    override fun update() {
        nextNode?.update()
    }
    override fun getValue(): Any? {
        return float_val
    }
}