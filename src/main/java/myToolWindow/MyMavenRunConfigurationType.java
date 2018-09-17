package myToolWindow;

import com.intellij.compiler.options.CompileStepBeforeRun;
import com.intellij.compiler.options.CompileStepBeforeRunNoErrorCheck;
import com.intellij.execution.*;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.execution.RunnerBundle;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.MavenUtil;

import javax.swing.*;

import java.util.Iterator;
import java.util.List;

import icons.MavenIcons;

public class MyMavenRunConfigurationType implements ConfigurationType {

    private final ConfigurationFactory myFactory = new ConfigurationFactory(this) {
        public RunConfiguration createTemplateConfiguration(Project project) {
            return new MyMavenRunConfiguration(project, this, "");
        }

        public RunConfiguration createTemplateConfiguration(Project project, RunManager runManager) {
            return new MyMavenRunConfiguration(project, this, "");
        }

        public RunConfiguration createConfiguration(String name, RunConfiguration template) {
            MyMavenRunConfiguration cfg = (MyMavenRunConfiguration) super.createConfiguration(name, template);
            if (!StringUtil.isEmptyOrSpaces(cfg.getRunnerParameters().getWorkingDirPath())) {
                return cfg;
            }
            else {
                Project project = cfg.getProject();
                if (project == null) {
                    return cfg;
                }
                else {
                    MavenProjectsManager projectsManager = MavenProjectsManager.getInstance(project);
                    List<MavenProject> projects = projectsManager.getProjects();
                    if (projects.size() != 1) {
                        return cfg;
                    }
                    else {
                        VirtualFile directory = ((MavenProject) projects.get(0)).getDirectoryFile();
                        cfg.getRunnerParameters().setWorkingDirPath(directory.getPath());
                        return cfg;
                    }
                }
            }
        }

        public void configureBeforeRunTaskDefaults(Key<? extends BeforeRunTask> providerID, BeforeRunTask task) {
            if (providerID == CompileStepBeforeRun.ID || providerID == CompileStepBeforeRunNoErrorCheck.ID) {
                task.setEnabled(false);
            }

        }
    };
    private static final int MAX_NAME_LENGTH = 40;

    public static MyMavenRunConfigurationType getInstance() {
        return (MyMavenRunConfigurationType) ConfigurationTypeUtil.findConfigurationType(MyMavenRunConfigurationType.class);
    }

    MyMavenRunConfigurationType() {
    }

    public String getDisplayName() {
        return RunnerBundle.message("maven.run.configuration.name", new Object[0]);
    }

    public String getConfigurationTypeDescription() {
        return RunnerBundle.message("maven.run.configuration.description", new Object[0]);
    }

    public Icon getIcon() {
        return MavenIcons.Phase;
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{this.myFactory};
    }

    @NonNls
    @NotNull
    public String getId() {
        if ("MyMavenRunConfigurationType" == null) {
        }

        return "MyMavenRunConfigurationType";
    }

    public static String generateName(Project project, MavenRunnerParameters runnerParameters) {
        StringBuilder stringBuilder = new StringBuilder();
        String name = getMavenProjectName(project, runnerParameters);
        if (!StringUtil.isEmptyOrSpaces(name)) {
            stringBuilder.append(name);
            stringBuilder.append(" ");
        }

        stringBuilder.append("[");
        listGoals(stringBuilder, runnerParameters.getGoals());
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    private static void listGoals(StringBuilder stringBuilder, List<String> goals) {
        int index = 0;

        for (Iterator var3 = goals.iterator(); var3.hasNext(); ++index) {
            String goal = (String) var3.next();
            if (index != 0) {
                if (stringBuilder.length() + goal.length() >= 40) {
                    stringBuilder.append("...");
                    break;
                }

                stringBuilder.append(",");
            }

            stringBuilder.append(goal);
        }

    }

    @Nullable
    private static String getMavenProjectName(Project project, MavenRunnerParameters runnerParameters) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(runnerParameters.getWorkingDirPath() + "/pom.xml");
        if (virtualFile != null) {
            MavenProject mavenProject = MavenProjectsManager.getInstance(project).findProject(virtualFile);
            if (mavenProject != null && !StringUtil.isEmptyOrSpaces(mavenProject.getMavenId().getArtifactId())) {
                return mavenProject.getMavenId().getArtifactId();
            }
        }

        return null;
    }

    public static void runConfiguration(Project project, MavenRunnerParameters params, @Nullable ProgramRunner.Callback callback) {
        runConfiguration(project, params, (MavenGeneralSettings) null, (MavenRunnerSettings) null, callback);
    }

    public static void runConfiguration(Project project, @NotNull MavenRunnerParameters params, @Nullable MavenGeneralSettings settings,
            @Nullable MavenRunnerSettings runnerSettings, @Nullable ProgramRunner.Callback callback) {
        if (params == null) {
            //    $$$reportNull$$$0(1);
        }

        RunnerAndConfigurationSettings configSettings = createRunnerAndConfigurationSettings(settings, runnerSettings, params, project);
        ProgramRunner runner = DefaultJavaProgramRunner.getInstance();
        Executor executor = DefaultRunExecutor.getRunExecutorInstance();

        try {
            runner.execute(new ExecutionEnvironment(executor, runner, configSettings, project), callback);
        }
        catch (ExecutionException var9) {
            MavenUtil.showError(project, "Failed to execute Maven goal", var9);
        }

    }

    @NotNull
    public static RunnerAndConfigurationSettings createRunnerAndConfigurationSettings(@Nullable MavenGeneralSettings generalSettings,
            @Nullable MavenRunnerSettings runnerSettings, MavenRunnerParameters params, Project project) {
        MyMavenRunConfigurationType type = (MyMavenRunConfigurationType) ConfigurationTypeUtil
                .findConfigurationType(MyMavenRunConfigurationType.class);
        RunnerAndConfigurationSettings settings = RunManager.getInstance(project)
                .createRunConfiguration(generateName(project, params), type.myFactory);
        MyMavenRunConfiguration runConfiguration = (MyMavenRunConfiguration) settings.getConfiguration();
        runConfiguration.setRunnerParameters(params);
        runConfiguration.setGeneralSettings(generalSettings);
        runConfiguration.setRunnerSettings(runnerSettings);
        if (settings == null) {
            //   $$$reportNull$$$0(2);
        }

        return settings;
    }
}

