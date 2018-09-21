import java.util.Map;
import java.util.stream.Collectors;

import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.kubicz.mavenexecutor.window.MavenExecutorService;

import myToolWindow.JvmOptionsDialog;


public class OpenJvmOptionsDialogAction extends AnAction {

    public OpenJvmOptionsDialogAction() {
        super("");
    }

    public void actionPerformed(AnActionEvent event) {
        MavenProjectsManager projectsManager = MavenActionUtil.getProjectsManager(event.getDataContext());

        JvmOptionsDialog jvmOptionsDialog = new JvmOptionsDialog(event.getProject());

        if(jvmOptionsDialog.showAndGet()) {
            MavenExecutorService.getInstance(event.getProject()).getSetting().setJvmOptions(jvmOptionsDialog.getJvmOptions());
        }
    }

}