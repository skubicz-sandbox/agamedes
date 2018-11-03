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
import myToolWindow.MyMavenRunConfiguration;
import myToolWindow.MyMavenRunConfigurationType;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.MavenUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.stream.Collectors;

public class RunMavenActionListener implements ActionListener {

    private Project project;


    public RunMavenActionListener(Project project) {
        this.project = project;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        MavenExecutorSetting runSetting = MavenExecutorService.getInstance(project).getCurrentSettings();
        System.out.println(runSetting);

        MyMavenRunConfigurationType runConfigurationType = ConfigurationTypeUtil.findConfigurationType(MyMavenRunConfigurationType.class);

        runSetting.getProjectsToBuild().forEach(projectToBuild -> {
            final RunnerAndConfigurationSettings settings = RunManagerEx.getInstanceEx(project)
                    .createRunConfiguration(projectToBuild.getDisplayName(), runConfigurationType.getConfigurationFactories()[0]);

            settings.setActivateToolWindowBeforeRun(true);

            MyMavenRunConfiguration runConfiguration = (MyMavenRunConfiguration) settings.getConfiguration();
            MavenRunnerSettings mavenRunnerSettings = new MavenRunnerSettings();

            String jvmOptions = runSetting.jvmOptionsAsText();
            if(runSetting.isUseOptionalJvmOptions() && !runSetting.getOptionalJvmOptions().isEmpty()) {
                jvmOptions = jvmOptions + " " + runSetting.optionalJvmOptionsAsText();
            }
            mavenRunnerSettings.setVmOptions(jvmOptions);

            mavenRunnerSettings.setEnvironmentProperties(runSetting.getEnvironmentProperties());

            mavenRunnerSettings.setSkipTests(runSetting.isSkipTests());

            runConfiguration.setRunnerSettings(mavenRunnerSettings);

            runConfiguration.mavenProperties.clear();
            if(!projectToBuild.buildEntireProject()) {
                runConfiguration.mavenProperties.put("-pl", projectToBuild.selectedModulesAsText());
            }


            MavenGeneralSettings mavenGeneralSettings = new MavenGeneralSettings();
            mavenGeneralSettings.setWorkOffline(runSetting.isOfflineMode());
            mavenGeneralSettings.setAlwaysUpdateSnapshots(runSetting.isAlwaysUpdateSnapshot());
            if(runSetting.getThreadCount() != null && runSetting.getThreadCount() > 0) {
                mavenGeneralSettings.setThreads(runSetting.getThreadCount().toString());
            }

            MavenProjectsManager projectsManager = MavenProjectsManager.getInstance(project);
            File mavenHome = MavenUtil.resolveMavenHomeDirectory(projectsManager.getGeneralSettings().getMavenHome());
            mavenGeneralSettings.setMavenHome(mavenHome.getPath());
            runConfiguration.setGeneralSettings(mavenGeneralSettings);

            MavenRunnerParameters mavenRunnerParameters = new MavenRunnerParameters();

            mavenRunnerParameters.setWorkingDirPath(projectToBuild.getProjectDictionary());
            mavenRunnerParameters.setGoals(runSetting.getGoals());
            mavenRunnerParameters.setProfilesMap(runSetting.getProfiles().stream().collect(Collectors.toMap(profile -> profile, profile -> true)));

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
        });

    }
}