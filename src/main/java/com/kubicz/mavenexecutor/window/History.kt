package com.kubicz.mavenexecutor.window

import com.intellij.util.xmlb.annotations.Property
import com.intellij.util.xmlb.annotations.Transient
import org.apache.commons.lang.StringUtils

import java.util.ArrayList

class History(@field:Property
              private val maxItemsCount: Int = 20,
              @field:Property
              private val items: MutableList<String> = arrayListOf()) {

    fun add(item: String) {
        if (item.isBlank()) {
            return
        }

        if (!items.contains(item)) {
            items.add(0, item)
        }
        while (items.size > maxItemsCount) {
            items.removeAt(maxItemsCount)
        }
    }

    fun getItems(): List<String> {
        return items
    }

    fun asArray(): Array<String> {
        return items.toTypedArray()
    }

}