package com.kubicz.mavenexecutor.window;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.RunManagerEx;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.project.Project;
import com.kubicz.mavenexecutor.model.Mavenize;
import com.kubicz.mavenexecutor.model.ProjectRoot;
import myToolWindow.MyMavenRunConfiguration;
import myToolWindow.MyMavenRunConfigurationType;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class RunMavenActionListener implements ActionListener {

    private Project project;

    private MavenProjectsTreeView projectsTree;

    public RunMavenActionListener(Project project, MavenProjectsTreeView projectsTree) {
        this.project = project;
        this.projectsTree = projectsTree;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        MavenExecutorSetting runSetting = MavenExecutorService.getInstance(project).getSetting();
        System.out.println(runSetting);

        MyMavenRunConfigurationType runConfigurationType = ConfigurationTypeUtil.findConfigurationType(MyMavenRunConfigurationType.class);

        Map<ProjectRoot, List<Mavenize>> projectRootMap = projectsTree.findSelectedProjects();

        for(Map.Entry<ProjectRoot, List<Mavenize>> projectRootListEntry : projectRootMap.entrySet()) {
            String module = "";

            if(!projectRootListEntry.getValue().isEmpty()) {
                for (Mavenize label : projectRootListEntry.getValue()) {
                    module = module + label.getMavenId().getGroupId() + ":" + label.getMavenId().getArtifactId() + ",";
                }
                module = module.substring(0, module.lastIndexOf(','));
            }

            final RunnerAndConfigurationSettings settings = RunManagerEx.getInstanceEx(project)
                    .createRunConfiguration(projectRootListEntry.getKey().getDisplayName(), runConfigurationType.getConfigurationFactories()[0]);

            settings.setActivateToolWindowBeforeRun(true);

            MyMavenRunConfiguration runConfiguration = (MyMavenRunConfiguration) settings.getConfiguration();
            MavenRunnerSettings mavenRunnerSettings = new MavenRunnerSettings();
            mavenRunnerSettings.setSkipTests(runSetting.isSkipTests());
            //        Map<String, String> mavenProperties =  new HashMap<>();
            runConfiguration.mavenProperties.clear();
            if(!module.isEmpty()) {
                runConfiguration.mavenProperties.put("-pl", module);
            }

            //   mavenRunnerSettings.setVmOptions("-pl " + module);
            runConfiguration.setRunnerSettings(mavenRunnerSettings);

            MavenGeneralSettings mavenGeneralSettings = new MavenGeneralSettings();
            mavenGeneralSettings.setAlwaysUpdateSnapshots(runSetting.isAlwaysUpdateSnapshot());

            runConfiguration.setGeneralSettings(mavenGeneralSettings);
            //parametersList.add("-pl", "app-api");
            MavenRunnerParameters mavenRunnerParameters = new MavenRunnerParameters();
            mavenRunnerParameters.setWorkingDirPath(projectRootListEntry.getKey().getVirtualFile().getPath());
            mavenRunnerParameters.setGoals(runSetting.getGoals());

            runConfiguration.setRunnerParameters(mavenRunnerParameters);

            ProgramRunner runner = DefaultJavaProgramRunner.getInstance();

            Executor executor = DefaultRunExecutor.getRunExecutorInstance();


            ExecutionEnvironment env = new ExecutionEnvironment(executor, runner, settings, project);
            try {
                runner.execute(env);
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}