package org.kubicz.mavenexecutor.view.components

import com.intellij.ui.CheckedTreeNode
import com.intellij.ui.TreeSpeedSearch

class MyCheckboxTree(cellRenderer: CheckboxTreeCellRendererBase, root: CheckedTreeNode?) : MyCheckboxTreeBase(cellRenderer, root) {

    init {

        installSpeedSearch()
    }

    private fun installSpeedSearch() {
        TreeSpeedSearch(this)
    }
}
