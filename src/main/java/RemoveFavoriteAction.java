import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.kubicz.mavenexecutor.window.MavenExecutorService;
import com.kubicz.mavenexecutor.window.MavenExecutorToolWindow;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import javax.swing.*;
import java.awt.*;


public class RemoveFavoriteAction extends AnAction {

    public RemoveFavoriteAction() {
        super("");
    }

    public void actionPerformed(AnActionEvent event) {
        MavenExecutorService settingsService = MavenExecutorService.getInstance(event.getProject());

        JButton favoriteButton = (JButton)event.getData(PlatformDataKeys.CONTEXT_COMPONENT);

        settingsService.removeFavoriteSettings(favoriteButton.getText());

        MavenExecutorToolWindow.getInstance(event.getProject()).updateFavorite();
    }

}