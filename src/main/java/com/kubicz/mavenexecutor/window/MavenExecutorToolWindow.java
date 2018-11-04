package com.kubicz.mavenexecutor.window;

import com.intellij.ProjectTopics;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.CheckboxTreeAdapter;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.kubicz.mavenexecutor.model.MavenArtifact;
import com.kubicz.mavenexecutor.model.Mavenize;
import com.kubicz.mavenexecutor.model.ProjectRootNode;
import com.kubicz.mavenexecutor.model.ProjectToBuild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MavenExecutorToolWindow {

    private Project project;

    private ToolWindow toolWindow;

    private SimpleToolWindowPanel toolWindowContent;

    private JPanel mainContent;

    private SelectCurrentPanel selectCurrentPanel;

    private FavoritePanel favoritePanel;

    private MavenProjectsTree projectsTreeView;

    private ConfigPanel configPanel;

    public MavenExecutorService settingsService;

    public MavenExecutorToolWindow(Project project) {
        this.project = project;
    }

    public static MavenExecutorToolWindow getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, MavenExecutorToolWindow.class);
    }


    public void createToolWindowContent(ToolWindow toolWindow) {
        //this.project = project;
        this.toolWindow = toolWindow;
        this.toolWindowContent = new SimpleToolWindowPanel(true, true);

        settingsService = MavenExecutorService.getInstance(project);

        createWindowToolbar();

        createWindowContent();

        project.getMessageBus().connect()
                .subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootListener() {
                    @Override
                    public void rootsChanged(final ModuleRootEvent event) {
                        MavenExecutorToolWindow.this.project = (Project)event.getSource();

                        MavenProjectsManager projectsManager = MavenProjectsManager.getInstance(project);

                        configPanel.updateProfile();
                        projectsTreeView.update(projectsManager, settingsService.getCurrentSettings().getProjectsToBuild());
                    }
                });

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(toolWindowContent, "", false);
        this.toolWindow.getContentManager().addContent(content);
    }

    private void createWindowToolbar() {
        final ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar actionToolbar = actionManager.createActionToolbar("EasyMavenBuilderPanel", (DefaultActionGroup) actionManager
                .getAction("EasyMavenBuilder.ActionsToolbar"), true);

//        actionToolbar.setTargetComponent(projectsTreeView.getTreeComponent());

        toolWindowContent.setToolbar(actionToolbar.getComponent());
    }


    private ProjectToBuild toProjectToBuild(Map.Entry<ProjectRootNode, List<Mavenize>> selectedProjectEntry) {
        ProjectRootNode projectRootNode = selectedProjectEntry.getKey();
        List<Mavenize> selectedModule = selectedProjectEntry.getValue();

        ProjectToBuild projectToBuild;
        if(projectRootNode.isSelected()) {
            projectToBuild = new ProjectToBuild(projectRootNode.getDisplayName(), projectRootNode.getMavenArtifact(), projectRootNode.getProjectDirectory().getPath());
        }
        else {
            List<MavenArtifact> modules = selectedModule.stream().map(Mavenize::getMavenArtifact).collect(Collectors.toList());
            projectToBuild = new ProjectToBuild(projectRootNode.getDisplayName(), projectRootNode.getMavenArtifact(), projectRootNode.getProjectDirectory().getPath(), modules);
        }

        return projectToBuild;
    }

    private void createWindowContent() {
        mainContent = new JPanel(new GridBagLayout());
        mainContent.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        MavenProjectsManager projectsManager = MavenProjectsManager.getInstance(project);

        favoritePanel = new FavoritePanel(project, this);

        configPanel = new ConfigPanel(project, this);

        selectCurrentPanel = new SelectCurrentPanel(project, this);

        projectsTreeView = new MavenProjectsTree(projectsManager, settingsService.getCurrentSettings().getProjectsToBuild());
      //  projectsTreeView.addFocusLostListener(new MavenProjectsTreeViewListener(runSetting, projectsTreeView));
        projectsTreeView.addCheckboxTreeListener(new CheckboxTreeAdapter() {
            @Override
            public void nodeStateChanged(@NotNull CheckedTreeNode node) {
            //    System.out.println(((Mavenize)node.getUserObject()).getMavenArtifact().getArtifactId());
                Map<ProjectRootNode, List<Mavenize>> selectedProjects = projectsTreeView.findSelectedProjects();

                List<ProjectToBuild> projectsToBuild = selectedProjects.entrySet().stream()
                        .map(MavenExecutorToolWindow.this::toProjectToBuild)
                        .collect(Collectors.toList());

                settingsService.getCurrentSettings().setProjectsToBuild(projectsToBuild);

                configPanel.updateRunButton();
            }
        });

        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(projectsTreeView.getTreeComponent());

        mainContent.add(configPanel.getComponent(), bagConstraintsBuilder().fillHorizontal().gridx(0).gridy(0).weightx(1.0).gridwidth(2).build());
        mainContent.add(scrollPane, bagConstraintsBuilder().fillBoth().weightx(1.0).gridx(0).gridy(1).build());
        mainContent.add(favoritePanel.getComponent(), bagConstraintsBuilder().fillVertical().weightx(0.0).weighty(1.0).gridx(1).gridy(1).build());
        mainContent.add(selectCurrentPanel.getComponent(), bagConstraintsBuilder().fillHorizontal().weightx(1.0).weighty(0.0).gridx(0).gridy(2).build());

        toolWindowContent.setContent(mainContent);
    }


    public void updateWithoutFavorite() {
        configPanel.update();
        updateProjectTree();
    }

    public void updateProjectTree() {
        projectsTreeView.updateTreeSelection(settingsService.getCurrentSettings().getProjectsToBuild());
    }


    public void updateFavorite() {
        favoritePanel.refresh();
    }

    @NotNull
    private GridBagConstraintsBuilder bagConstraintsBuilder() {
        return new GridBagConstraintsBuilder();
    }

}
