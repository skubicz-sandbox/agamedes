import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.kubicz.mavenexecutor.window.MavenExecutorService;
import com.kubicz.mavenexecutor.window.MavenExecutorSetting;
import com.kubicz.mavenexecutor.window.MavenExecutorToolWindow;
import myToolWindow.SaveConfirmationDialog;
import org.apache.commons.lang3.SerializationUtils;

public class SaveSettingsAction extends AnAction {

    public SaveSettingsAction() {
        super("");
    }

    public void actionPerformed(AnActionEvent event) {
        MavenExecutorService settingsService = MavenExecutorService.Companion.getInstance(event.getProject());

        System.out.println(settingsService.getFavoriteSettings());

        SaveConfirmationDialog saveConfirmationDialog = new SaveConfirmationDialog(event.getProject());
        saveConfirmationDialog.setSettingsName(settingsService.getCurrentSettingsLabel());

        if(saveConfirmationDialog.showAndGet()) {
            settingsService.addSettings(saveConfirmationDialog.getSettingsName(), copySetting(settingsService.getCurrentSettings()));
            settingsService.loadSettings(saveConfirmationDialog.getSettingsName());

            MavenExecutorToolWindow.getInstance(event.getProject()).updateFavorite();
        }
    }

    private MavenExecutorSetting copySetting(MavenExecutorSetting setting) {
//        MavenExecutorSetting copied = new MavenExecutorSetting();
//        copied.setProjectsToBuild(setting.getProjectsToBuild());
//        copied.setThreadCount(setting.getThreadCount());
//        copied.setEnvironmentProperties(setting.getEnvironmentProperties());
//        copied.setUseOptionalJvmOptions(setting.isUseOptionalJvmOptions());
//        copied.setOfflineMode(setting.isOfflineMode());
//        copied.setAlwaysUpdateSnapshot(setting.isAlwaysUpdateSnapshot());
//        copied.setGoals(setting.getGoals());
//        copied.setJvmOptions(setting.getJvmOptions());
//        copied.setProfiles(setting.getProfiles());
//        copied.setSkipTests(setting.isSkipTests());
//
//        return copied;
        return XmlSerializerUtil.createCopy(setting);
    }

}