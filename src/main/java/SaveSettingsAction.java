import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.kubicz.mavenexecutor.window.MavenExecutorService;
import com.kubicz.mavenexecutor.window.MavenExecutorToolWindow;
import myToolWindow.SaveConfirmationDialog;

public class SaveSettingsAction extends AnAction {

    public SaveSettingsAction() {
        super("");
    }

    public void actionPerformed(AnActionEvent event) {
        MavenExecutorService settingsService = MavenExecutorService.getInstance(event.getProject());

        System.out.println(settingsService.getFavoriteSettings());

        SaveConfirmationDialog saveConfirmationDialog = new SaveConfirmationDialog(event.getProject());
        saveConfirmationDialog.setSettingsName(settingsService.getLastLoaded());

        if(saveConfirmationDialog.showAndGet()) {
            settingsService.addSettings(saveConfirmationDialog.getSettingsName(), settingsService.getCurrentSettings());

            MavenExecutorToolWindow.getInstance(event.getProject()).updateFavorite();
        }
    }

}