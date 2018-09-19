import com.google.common.collect.Lists;
import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.util.EnvVariablesTable;
import com.intellij.execution.util.EnvironmentVariable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogBuilder;


public class OpenEnvironmentVariablesDialogAction extends AnAction {

    public OpenEnvironmentVariablesDialogAction() {
        super("");
    }

    public void actionPerformed(AnActionEvent event) {
        EnvironmentVariable variable = new EnvironmentVariable("name2", "value2", false);
        EnvVariablesTable table = new EnvVariablesTable();
        table.setValues(Lists.newArrayList(variable));
        table.getActionsPanel().setVisible(true);
        DialogBuilder builder = new DialogBuilder();
        builder.setTitle("Configure Environment Variables");
        builder.centerPanel(table.getComponent());
        builder.addOkAction();
        builder.addCancelAction();

        builder.showAndGet();

        System.out.println(table.getEnvironmentVariables().size());
    }

}