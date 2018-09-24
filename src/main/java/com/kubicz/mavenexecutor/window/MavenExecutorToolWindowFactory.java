package com.kubicz.mavenexecutor.window;

import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.*;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import com.kubicz.mavenexecutor.model.MavenArtifact;
import com.kubicz.mavenexecutor.model.Mavenize;
import com.kubicz.mavenexecutor.model.ProjectRootNode;
import com.kubicz.mavenexecutor.model.ProjectToBuild;
import myToolWindow.MavenPluginsCompletionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenArgumentsCompletionProvider;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MavenExecutorToolWindowFactory implements ToolWindowFactory {

    private Project project;

    private ToolWindow toolWindow;

    private SimpleToolWindowPanel toolWindowContent;

    private JPanel mainContent;

    private JPanel configPanel;

    private JPanel goalsSubPanel;

    private JPanel propertiesSubPanel;

    private JPanel skipPluginSubPanel;

    private JPanel favoritePanel;

    private MavenProjectsTreeView projectsTreeView;

    private ComboBox goalsComboBox;

    private EditorTextField goalsEditor;

    private JButton runMavenButton;

    private JCheckBox offlineModeCheckBox;

    private JCheckBox skipTestCheckBox;

    private JCheckBox alwaysUpdateModeCheckBox;

    private JLabel threadsLabel;

    private JTextField threadsTextField;

    private JCheckBox skipPluginCheckBox;

    private ComboBox skipPluginComboBox;

    private EditorTextField skipPluginEditor;

    private MavenExecutorSetting runSetting;

    public MavenExecutorToolWindowFactory() {

    }

    private void createUIComponents() {

    }

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        this.toolWindowContent = new SimpleToolWindowPanel(true, true);

        if(MavenExecutorService.getInstance(project).getSetting() == null) {
            MavenExecutorService.getInstance(project).setSetting(new MavenExecutorSetting());
        }
        this.runSetting = MavenExecutorService.getInstance(project).getSetting();

        System.out.println("saved: " + MavenExecutorService.getInstance(project).getValue());
        System.out.println("saved: " + runSetting.isAlwaysUpdateSnapshot());
        MavenExecutorService.getInstance(project).setValue("ssss");

        createWindowToolbar();

        createWindowContent();

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(toolWindowContent, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private void createWindowToolbar() {
        final ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar actionToolbar = actionManager.createActionToolbar("EasyMavenBuilderPanel", (DefaultActionGroup) actionManager
                .getAction("EasyMavenBuilder.ActionsToolbar"), true);

//        actionToolbar.setTargetComponent(projectsTreeView.getTreeComponent());

        toolWindowContent.setToolbar(actionToolbar.getComponent());
    }


    private ProjectToBuild toProjectToBuild(Map.Entry<ProjectRootNode, java.util.List<Mavenize>> selectedProjectEntry) {
        ProjectRootNode projectRootNode = selectedProjectEntry.getKey();
        java.util.List<Mavenize> selectedModule = selectedProjectEntry.getValue();

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

        projectsTreeView = new MavenProjectsTreeView(projectsManager, runSetting.getProjectsToBuild());
      //  projectsTreeView.addFocusLostListener(new MavenProjectsTreeViewListener(runSetting, projectsTreeView));
        projectsTreeView.addCheckboxTreeListener(new CheckboxTreeAdapter() {
            @Override
            public void nodeStateChanged(@NotNull CheckedTreeNode node) {
                Map<ProjectRootNode, List<Mavenize>> selectedProjects = projectsTreeView.findSelectedProjects();

                List<ProjectToBuild> projectsToBuild = selectedProjects.entrySet().stream()
                        .map(MavenExecutorToolWindowFactory.this::toProjectToBuild)
                        .collect(Collectors.toList());

                runSetting.setProjectsToBuild(projectsToBuild);

                runMavenButton.setEnabled(canExecute());
            }
        });

        createFavoritePanel();

        createConfigPanel();

        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(projectsTreeView.getTreeComponent());

        mainContent.add(configPanel, bagConstraintsBuilder().fillHorizontal().gridx(0).gridy(0).weightx(1.0).gridwidth(2).build());
        mainContent.add(scrollPane, bagConstraintsBuilder().fillBoth().weightx(1.0).gridx(0).gridy(1).build());
        mainContent.add(favoritePanel, bagConstraintsBuilder().fillVertical().weightx(0.0).weighty(1.0).gridx(1).gridy(1).build());

        toolWindowContent.setContent(mainContent);
    }

    private void createFavoritePanel() {
        favoritePanel = new JPanel();
        favoritePanel.add(new JLabel("All"));
    }

    private void createConfigPanel() {
        configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.PAGE_AXIS));

        runMavenButton = new JButton();
        runMavenButton.setIcon(AllIcons.General.Run);
        runMavenButton.addActionListener(new RunMavenActionListener(project, projectsTreeView));
        runMavenButton.setEnabled(canExecute());

        createGoalsSubPanel();

        createPropertiesSubPanel();

        createSkipPluginSubPanel();

        configPanel.add(goalsSubPanel);
        configPanel.add(propertiesSubPanel);
        configPanel.add(skipPluginSubPanel);
    }

    private boolean canExecute() {
        return !runSetting.getGoals().isEmpty() && !runSetting.getProjectsToBuild().isEmpty();
    }

    private void createGoalsSubPanel() {
        String[] history = {""};
        this.goalsComboBox = new ComboBox(history);


        this.goalsComboBox.setLightWeightPopupEnabled(false);
        EditorComboBoxEditor editor = new StringComboboxEditor(project, PlainTextFileType.INSTANCE, this.goalsComboBox);
        this.goalsComboBox.setRenderer(new EditorComboBoxRenderer(editor));
        this.goalsComboBox.setEditable(true);
        this.goalsComboBox.setEditor(editor);
        this.goalsComboBox.setFocusable(true);
        this.goalsEditor = editor.getEditorComponent();

        System.out.println(runSetting.goalsAsText());
        goalsEditor.addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(DocumentEvent event) {
                String goalsText = goalsComboBox.getEditor().getItem() + "";
                if(goalsText.isEmpty()) {
                    runSetting.getGoals().clear();
                }
                else {
                    runSetting.setGoals(Lists.newArrayList(goalsText.split("\\s")));
                }

                runMavenButton.setEnabled(canExecute());
            }
        });

        (new MavenArgumentsCompletionProvider(project)).apply(this.goalsEditor);
        goalsComboBox.getEditor().setItem(runSetting.goalsAsText());

        JLabel label = new JLabel("Goals");

        goalsSubPanel = new JPanel();
        GroupLayout groupLayout = new GroupLayout(goalsSubPanel);
        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);

        goalsSubPanel.setLayout(groupLayout);
        groupLayout.setHorizontalGroup(
                groupLayout.createSequentialGroup()
                        .addComponent(label)
                        .addComponent(goalsComboBox)
                        .addComponent(runMavenButton)
        );
        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(label, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(goalsComboBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(runMavenButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        )
        );
    }

    private void createPropertiesSubPanel() {
        propertiesSubPanel = new JPanel();
        JPanel innerPropertiesPanel = new JPanel(new GridBagLayout());

        offlineModeCheckBox = new JCheckBox("Offline");
        innerPropertiesPanel.add(offlineModeCheckBox, bagConstraintsBuilder().fillHorizontal().build());

        alwaysUpdateModeCheckBox = new JCheckBox("Update snapshots");

        alwaysUpdateModeCheckBox.addActionListener(event -> {
            runSetting.setAlwaysUpdateSnapshot(alwaysUpdateModeCheckBox.isSelected());
        });
        innerPropertiesPanel.add(alwaysUpdateModeCheckBox, bagConstraintsBuilder().fillHorizontal().insetLeft(20).gridx(1).gridy(0).build());

        skipTestCheckBox = new JCheckBox("Skip tests");
        innerPropertiesPanel.add(skipTestCheckBox, bagConstraintsBuilder().fillHorizontal().gridx(0).gridy(1).build());

        skipTestCheckBox.addActionListener(event -> {
            runSetting.setSkipTests(skipTestCheckBox.isSelected());
        });

        threadsLabel = new JLabel("Threads:");
        innerPropertiesPanel.add(threadsLabel, bagConstraintsBuilder().anchorWest().fillNone().insetLeft(20).gridx(1).gridy(1).build());

        threadsTextField = new JBTextField(2);
        innerPropertiesPanel.add(threadsTextField, bagConstraintsBuilder().anchorEast().fillNone().gridx(1).gridy(1).build());

        innerPropertiesPanel.setMaximumSize(new Dimension(200, 50));

        JPanel emptyPanel = new JPanel();


        CustomCheckBoxList profiles = new CustomCheckBoxList();
        MavenProjectsManager projectsManager = MavenProjectsManager.getInstance(project);
        profiles.setItems(Lists.newArrayList(projectsManager.getAvailableProfiles()), a -> a);
        profiles.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent e) {
                runSetting.setProfiles(profiles.getSelectedItemNames());
            }
        });

        JScrollPane profilesScrollPane = ScrollPaneFactory.createScrollPane(profiles);
        profilesScrollPane.setMaximumSize(new Dimension(150, 80));
        profilesScrollPane.setMinimumSize(new Dimension(150, 80));

        GroupLayout propertiesGroupLayout = new GroupLayout(propertiesSubPanel);
        propertiesGroupLayout.setAutoCreateGaps(true);
        propertiesGroupLayout.setAutoCreateContainerGaps(true);

        propertiesSubPanel.setLayout(propertiesGroupLayout);

        propertiesGroupLayout.setHorizontalGroup(
                propertiesGroupLayout.createSequentialGroup()
                        .addComponent(innerPropertiesPanel)
                        .addComponent(emptyPanel)
                        .addComponent(profilesScrollPane)
        );
        propertiesGroupLayout.setVerticalGroup(
                propertiesGroupLayout.createSequentialGroup()
                        .addGroup(propertiesGroupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(innerPropertiesPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(emptyPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(profilesScrollPane)
                        )
        );
    }

    private void createSkipPluginSubPanel() {
        skipPluginSubPanel = new JPanel();
        GroupLayout skipPluginLayout = new GroupLayout(skipPluginSubPanel);
        skipPluginLayout.setAutoCreateGaps(true);
        skipPluginLayout.setAutoCreateContainerGaps(true);

        skipPluginSubPanel.setLayout(skipPluginLayout);

        skipPluginCheckBox = new JCheckBox("Try skip plugins:");

        skipPluginComboBox = new ComboBox();
        skipPluginComboBox.setLightWeightPopupEnabled(false);
        EditorComboBoxEditor editor = new StringComboboxEditor(project, PlainTextFileType.INSTANCE, skipPluginComboBox);
        skipPluginComboBox.setRenderer(new EditorComboBoxRenderer(editor));
        skipPluginComboBox.setEditable(true);
        skipPluginComboBox.setEditor(editor);
        skipPluginComboBox.setFocusable(true);
        skipPluginComboBox.setEnabled(skipPluginCheckBox.isSelected());
        skipPluginEditor = editor.getEditorComponent();

        skipPluginCheckBox.addActionListener(event -> {
            skipPluginComboBox.setEnabled(skipPluginCheckBox.isSelected());

        });

        (new MavenPluginsCompletionProvider(project)).apply(skipPluginEditor);

        skipPluginLayout.setHorizontalGroup(
                skipPluginLayout.createSequentialGroup()
                        .addComponent(skipPluginCheckBox)
                        .addComponent(skipPluginComboBox)
        );
        skipPluginLayout.setVerticalGroup(
                skipPluginLayout.createSequentialGroup()
                        .addGroup(skipPluginLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(skipPluginCheckBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(skipPluginComboBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        )
        );
    }

    @NotNull
    private GridBagConstraintsBuilder bagConstraintsBuilder() {
        return new GridBagConstraintsBuilder();
    }

}
