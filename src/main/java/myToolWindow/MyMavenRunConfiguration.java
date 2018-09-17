package myToolWindow;

import java.util.HashMap;
import java.util.Map;

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
    public Map<String, String> mavenProperties =  new HashMap<>();

    @Override
    public JavaParameters createJavaParameters(@Nullable final Project project) throws ExecutionException {
        JavaParameters javaParameters = super.createJavaParameters(project);

        mavenProperties.entrySet().forEach(entry -> {
            javaParameters.getProgramParametersList().add(entry.getKey(), entry.getValue());
        });
        return javaParameters;
    }
}
