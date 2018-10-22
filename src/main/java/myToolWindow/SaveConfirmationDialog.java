package myToolWindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SaveConfirmationDialog extends DialogWrapper {
    private JPanel contentPane;
    private JTextField settingsName;

    public SaveConfirmationDialog(@Nullable final Project project) {
        super(project);
        setModal(true);
        setTitle("Save Settings");

        init();
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    public String getSettingsName() {
        return settingsName.getText();
    }

    public void setSettingsName(String settingsName) {
        this.settingsName.setText(settingsName);
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return settingsName;
    }
}
