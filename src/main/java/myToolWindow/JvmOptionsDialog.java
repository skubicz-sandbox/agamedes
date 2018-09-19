package myToolWindow;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

public class JvmOptionsDialog extends DialogWrapper {
    private JPanel contentPane;
    private JTextArea textArea1;

    public JvmOptionsDialog(@Nullable final Project project) {
        super(project);
     //   setContentPane(contentPane);
        setModal(true);
        setTitle("Configure JVM Options");


        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }


}
