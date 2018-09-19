import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;

public class OpenMavenSettingsAction extends AnAction {

    public OpenMavenSettingsAction() {
        super("");
    }

    public void actionPerformed(AnActionEvent event) {
        ShowSettingsUtil.getInstance().showSettingsDialog(event.getProject(), "Maven");
    }

}