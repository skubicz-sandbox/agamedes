import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.kubicz.mavenexecutor.window.MavenExecutorService;
import com.kubicz.mavenexecutor.window.MavenExecutorSetting;
import myToolWindow.JvmOptionsDialog;


public class OpenJvmOptionsDialogAction extends AnAction {

    public OpenJvmOptionsDialogAction() {
        super("");
    }

    public void actionPerformed(AnActionEvent event) {
        MavenExecutorSetting setting = MavenExecutorService.getInstance(event.getProject()).getSetting();

        JvmOptionsDialog jvmOptionsDialog = new JvmOptionsDialog(event.getProject());
        jvmOptionsDialog.setJvmOptions(setting.getJvmOptions());

        if(jvmOptionsDialog.showAndGet()) {
            setting.setJvmOptions(jvmOptionsDialog.getJvmOptions());
        }
    }

}