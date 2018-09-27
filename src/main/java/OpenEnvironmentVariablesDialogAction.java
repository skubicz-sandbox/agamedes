import com.intellij.execution.util.EnvVariablesTable;
import com.intellij.execution.util.EnvironmentVariable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogBuilder;
import com.kubicz.mavenexecutor.window.MavenExecutorService;
import com.kubicz.mavenexecutor.window.MavenExecutorSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class OpenEnvironmentVariablesDialogAction extends AnAction {

    public OpenEnvironmentVariablesDialogAction() {
        super("");
    }

    public void actionPerformed(AnActionEvent event) {
        MavenExecutorSetting setting = MavenExecutorService.getInstance(event.getProject()).getSetting();

        DialogBuilder builder = new DialogBuilder();
        builder.setTitle("Configure Environment Variables");
        builder.addOkAction();
        builder.addCancelAction();

        EnvVariablesTable table = new EnvVariablesTable();
        table.setValues(toEnvVariables(setting.getEnvironmentProperties()));
        table.getActionsPanel().setVisible(true);
        builder.centerPanel(table.getComponent());

        if (builder.showAndGet()) {
            setting.setEnvironmentProperties(toEnvironmentPropertiesMap(table.getEnvironmentVariables()));
        }

    }

    private List<EnvironmentVariable> toEnvVariables(Map<String, String> environmentProperties) {
        if (environmentProperties == null) {
            return new ArrayList<>();
        }

        return environmentProperties.entrySet().stream()
                .map(envEntry -> new EnvironmentVariable(envEntry.getKey(), envEntry.getValue(), false))
                .collect(Collectors.toList());
    }


    private Map<String, String> toEnvironmentPropertiesMap(List<EnvironmentVariable> environmentProperties) {
        return environmentProperties.stream()
                .collect(Collectors.toMap(var -> var.getName(), var -> var.getValue()));
    }

}