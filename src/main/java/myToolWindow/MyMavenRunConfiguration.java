package myToolWindow;

import java.util.HashMap;
import java.util.Map;

import com.kubicz.mavenexecutor.window.MavenAdditionalParameters;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.execution.MavenRunConfiguration;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.openapi.project.Project;

public class MyMavenRunConfiguration extends MavenRunConfiguration {
    public MyMavenRunConfiguration(final Project project, final ConfigurationFactory factory, final String name) {
        super(project, factory, name);
    }
    public MavenAdditionalParameters additionalParameters =  new MavenAdditionalParameters();

    @Override
    public JavaParameters createJavaParameters(@Nullable final Project project) throws ExecutionException {
        JavaParameters javaParameters = super.createJavaParameters(project);

        if(!additionalParameters.getProjects().isEmpty()) {
            javaParameters.getProgramParametersList().add("-pl", additionalParameters.getProjects());
        }

        return javaParameters;
    }
}
