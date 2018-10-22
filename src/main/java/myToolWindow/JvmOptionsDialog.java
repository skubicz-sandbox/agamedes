package myToolWindow;

import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

public class JvmOptionsDialog extends DialogWrapper {
    private JPanel contentPane;
    private JTextArea jvmOptionTextArea;

    public JvmOptionsDialog(@Nullable final Project project) {
        super(project);
     //   setContentPane(contentPane);
        setModal(true);
        setTitle("Configure JVM Options");


        init();
    }

    public List<String> getJvmOptions() {
        return Lists.newArrayList(jvmOptionTextArea.getText().split("\n"));
    }

    public void setJvmOptions(List<String> jmOptions) {
        if(jmOptions == null) {
            jvmOptionTextArea.setText("");
        }
        else {
            jvmOptionTextArea.setText(jmOptions.stream().collect(Collectors.joining("\n")));
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return jvmOptionTextArea;
    }
}
