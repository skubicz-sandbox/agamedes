package org.kubicz.mavenexecutor.view.components

import com.intellij.ui.CheckedTreeNode
import com.intellij.ui.TreeSpeedSearch

class CheckboxTree(cellRenderer: CheckboxTreeCellRendererBase, root: CheckedTreeNode?) : CheckboxTreeBase(cellRenderer, root) {

    init {

        installSpeedSearch()
    }

    private fun installSpeedSearch() {
        TreeSpeedSearch(this)
    }
}
