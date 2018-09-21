package com.kubicz.mavenexecutor.window;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.ListModel;

import com.intellij.ui.CheckBoxList;

public class CustomCheckBoxList extends CheckBoxList<String> {

    public List<String> getSelectedItemNames() {
        List<String> selectedItems = new ArrayList<>();

        ListModel model = getModel();
        if(model == null) {
            return selectedItems;
        }

        for(int i = 0; i < model.getSize(); i++) {
            JCheckBox checkBox = (JCheckBox)model.getElementAt(i);
            if(checkBox.isSelected()) {
                selectedItems.add(checkBox.getText());
            }
        }

        return selectedItems;
    }
}