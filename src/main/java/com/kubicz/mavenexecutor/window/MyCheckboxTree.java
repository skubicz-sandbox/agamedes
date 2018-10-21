package com.kubicz.mavenexecutor.window;

import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.TreeSpeedSearch;
import org.jetbrains.annotations.NotNull;

public class MyCheckboxTree extends MyCheckboxTreeBase {

    public MyCheckboxTree(final CheckboxTreeCellRendererBase cellRenderer, CheckedTreeNode root) {
        super(cellRenderer, root);

        installSpeedSearch();
    }

//    public MyCheckboxTree(final CheckboxTree.CheckboxTreeCellRenderer cellRenderer, CheckedTreeNode root, final CheckPolicy checkPolicy) {
//        super(cellRenderer, root, checkPolicy);
//
//        installSpeedSearch();
//    }

    @Override
    public void setNodeState(@NotNull CheckedTreeNode node, boolean checked) {
        super.setNodeState(node, checked);
    }

    protected void installSpeedSearch() {
        new TreeSpeedSearch(this);
    }
}
