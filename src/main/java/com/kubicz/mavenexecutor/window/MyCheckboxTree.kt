package com.kubicz.mavenexecutor.window

import com.intellij.ui.CheckedTreeNode
import com.intellij.ui.TreeSpeedSearch

class MyCheckboxTree(cellRenderer: MyCheckboxTreeBase.CheckboxTreeCellRendererBase, root: CheckedTreeNode?) : MyCheckboxTreeBase(cellRenderer, root) {

    init {

        installSpeedSearch()
    }

    private fun installSpeedSearch() {
        TreeSpeedSearch(this)
    }
}
