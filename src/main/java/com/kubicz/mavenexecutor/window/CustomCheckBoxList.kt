package com.kubicz.mavenexecutor.window

import java.util.ArrayList

import javax.swing.JCheckBox
import javax.swing.ListModel

import com.intellij.ui.CheckBoxList

class CustomCheckBoxList : CheckBoxList<String>() {

    val selectedItemNames: List<String>
        get() {
            val selectedItems = ArrayList<String>()

            val model = model ?: return selectedItems

            for (i in 0 until model.size) {
                val checkBox = model.getElementAt(i) as JCheckBox
                if (checkBox.isSelected) {
                    selectedItems.add(checkBox.text)
                }
            }

            return selectedItems
        }
}