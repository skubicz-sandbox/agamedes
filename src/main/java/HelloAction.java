import java.nio.charset.Charset;

import com.intellij.icons.AllIcons;
import org.jetbrains.idea.maven.execution.MavenRunConfiguration;
import org.jetbrains.idea.maven.execution.MavenRunConfigurationType;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import com.google.common.collect.Lists;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManagerEx;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.dashboard.RunDashboardManager;
import com.intellij.execution.dashboard.RunDashboardToolWindowFactory;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowEP;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;


public class HelloAction extends AnAction {
    public HelloAction() {
        super("Hello");
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
   //     Messages.showMessageDialog(project, "Hello world!" + project.getClass(), "Greeting", Messages.getInformationIcon());

    //    ToolWindowEP.instantiate()
//        ToolWindowManager toolWindowMgr = ToolWindowManager.getInstance(project);
//        toolWindowMgr.getToolWindow(RunDashboardToolWindowFactory)
        RunDashboardManager runDashboardManager = RunDashboardManager.getInstance(project);

        ToolWindowManager toolWindowMgr = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowMgr.getToolWindow("Run");

        ToolWindowManagerEx toolWindowMgr2 = ToolWindowManagerEx.getInstanceEx(project);
        ToolWindow toolWindow2 = toolWindowMgr2.getToolWindow("Run");
     //   toolWindowMgr2.registerToolWindow("Run", true, toolWindow.getAnchor());

        ToolWindowEP[] beans = Extensions.getExtensions(ToolWindowEP.EP_NAME);
        for (final ToolWindowEP bean : beans) {
            if ("Run".equals(bean.id)) {
                toolWindowMgr2.initToolWindow(bean);
                System.out.println(bean);
            }
//            if (id.equals(bean.id)) {
//                managerEx.initToolWindow(bean);
//            }
        }

     //   RunDashboardToolWindowFactory
        MavenRunConfigurationType runConfigurationType = ConfigurationTypeUtil.findConfigurationType(MavenRunConfigurationType.class);

        final RunnerAndConfigurationSettings settings = RunManagerEx.getInstanceEx(project).createRunConfiguration("213",
                runConfigurationType.getConfigurationFactories()[0]);

        settings.setActivateToolWindowBeforeRun(true);

        MavenRunConfiguration runConfiguration = (MavenRunConfiguration)settings.getConfiguration();
        MavenRunnerSettings mavenRunnerSettings = new MavenRunnerSettings();
        runConfiguration.setRunnerSettings(mavenRunnerSettings);

        MavenGeneralSettings mavenGeneralSettings = new MavenGeneralSettings();
        runConfiguration.setGeneralSettings(mavenGeneralSettings);

        MavenRunnerParameters mavenRunnerParameters = new MavenRunnerParameters();
        mavenRunnerParameters.setWorkingDirPath(project.getBasePath());
        mavenRunnerParameters.setGoals(Lists.newArrayList("clean", "install"));
        runConfiguration.setRunnerParameters(mavenRunnerParameters);
        System.out.println(runConfiguration.getRunnerSettings());
//        MvnRunConfiguration runConfiguration = (MvnRunConfiguration) settings.getConfiguration();
//        runConfiguration.setRunType(getPhaseRunType(params.getGoals()));
//        runConfiguration.setRunnerParameters(params);
//        runConfiguration.setGeneralSettings(generalSettings);
//        runConfiguration.setRunnerSettings(runnerSettings);

      //  ProgramRunnerUtil.getRunner()
   //     ProgramRunner[] runners = DefaultJavaProgramRunner.PROGRAM_RUNNER_EP.getExtensions();
        ProgramRunner runner = DefaultJavaProgramRunner.getInstance();
      //  if (runners.length == 0) return;
        Executor executor = DefaultRunExecutor.getRunExecutorInstance();


        ExecutionEnvironment env = new ExecutionEnvironment(executor, runner, settings, project);
        try {
            runner.execute(env);
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }

        for(MavenProject mavenProject : MavenProjectsManager.getInstance(project).getProjects()) {

            System.out.println(mavenProject);
        }


//        ProcessHandler processHandler = null;
//        try {
//            processHandler = new OSProcessHandler(commandLine);
//            processHandler.startNotify();
//        }
//        catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }
}