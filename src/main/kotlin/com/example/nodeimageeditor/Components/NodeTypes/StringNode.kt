package com.example.nodeimageeditor

import javafx.scene.control.TextField

class StringNode: DraggableNode() {
    var input_field: TextField? = null
    var string_val = "";
    init {
        titleLabel?.text = "String"

        addRightHandle("String")

        input_field = TextField()
        input_field!!.textProperty().addListener { observable, oldValue, newValue ->
            string_val = newValue
            nextNode?.update()
        }
        input_field!!.prefWidth = 60.0
        content?.children?.add(input_field)
    }
    override fun update() {
        nextNode?.update()
    }
    override fun getValue(): Any? {
        return string_val
    }
}