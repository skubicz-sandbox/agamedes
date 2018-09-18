import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;


public class MavenReimportAction extends AnAction {

    public MavenReimportAction() {
        super("");
    }

    public void actionPerformed(AnActionEvent event) {
        MavenProjectsManager projectsManager = MavenActionUtil.getProjectsManager(event.getDataContext());

        projectsManager.forceUpdateAllProjectsOrFindAllAvailablePomFiles();
    }

}