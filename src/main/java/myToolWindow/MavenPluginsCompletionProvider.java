package myToolWindow;

import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.externalSystem.service.execution.cmd.CommandLineCompletionProvider;
import com.intellij.openapi.project.Project;

import groovyjarjarcommonscli.Options;

public class MavenPluginsCompletionProvider  extends CommandLineCompletionProvider {
    private volatile List<LookupElement> myCachedElements;
    private final Project project;

    public MavenPluginsCompletionProvider(@NotNull Project project) {
        super(new Options());
        this.project = project;
    }

    protected void addArgumentVariants(@NotNull CompletionResultSet result) {
        List<LookupElement> cachedElements = this.myCachedElements;
        if (cachedElements == null) {
            cachedElements = MavenProjectsManager.getInstance(this.project).getProjects().get(0).getPlugins()
                    .stream()
                    .map(plugin -> LookupElementBuilder.create(plugin.getArtifactId()))
                    .collect(Collectors.toList());
            this.myCachedElements = cachedElements;
        }

        result.addAllElements(cachedElements);
    }

}