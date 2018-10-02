package com.kubicz.mavenexecutor.window;

import com.google.common.collect.Lists;
import com.intellij.ProjectTopics;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.*;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.kubicz.mavenexecutor.model.MavenArtifact;
import com.kubicz.mavenexecutor.model.Mavenize;
import com.kubicz.mavenexecutor.model.ProjectRootNode;
import com.kubicz.mavenexecutor.model.ProjectToBuild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenArgumentsCompletionProvider;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MavenExecutorToolWindow {

    private Project project;

    private ToolWindow toolWindow;

    private SimpleToolWindowPanel toolWindowContent;

    private JPanel mainContent;

    private JPanel configPanel;

    private JPanel goalsSubPanel;

    private JPanel propertiesSubPanel;

    private JPanel optionalJvmOptionsSubPanel;

    private JPanel favoritePanel;

    private MavenProjectsTreeView projectsTreeView;

    private ComboBox goalsComboBox;

    private EditorTextField goalsEditor;

    private JButton runMavenButton;

    private JCheckBox offlineModeCheckBox;

    private JCheckBox skipTestCheckBox;

    private JCheckBox alwaysUpdateModeCheckBox;

    private JLabel threadsLabel;

    private IntegerField threadsTextField;

    private JCheckBox optionalJvmOptionsCheckBox;

    private ComboBox optionalJvmOptionsComboBox;

    private EditorTextField optionalJvmOptionsEditor;

    private CustomCheckBoxList profiles;

    private MavenExecutorSetting runSetting;

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

        project.getMessageBus().connect()
                .subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootListener() {
                    @Override
                    public void rootsChanged(final ModuleRootEvent event) {
                        MavenExecutorToolWindow.this.project = (Project)event.getSource();

                        MavenProjectsManager projectsManager = MavenProjectsManager.getInstance(project);

                        profiles.clear();

                        projectsManager.getAvailableProfiles().forEach(profile -> {
                            profiles.addItem(profile, profile, runSetting.getProfiles().contains(profile));
                        });
                    }
                });

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

        projectsTreeView = new MavenProjectsTreeView(projectsManager, runSetting.getProjectsToBuild());
      //  projectsTreeView.addFocusLostListener(new MavenProjectsTreeViewListener(runSetting, projectsTreeView));
        projectsTreeView.addCheckboxTreeListener(new CheckboxTreeAdapter() {
            @Override
            public void nodeStateChanged(@NotNull CheckedTreeNode node) {
                Map<ProjectRootNode, List<Mavenize>> selectedProjects = projectsTreeView.findSelectedProjects();

                List<ProjectToBuild> projectsToBuild = selectedProjects.entrySet().stream()
                        .map(MavenExecutorToolWindow.this::toProjectToBuild)
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
        configPanel.add(optionalJvmOptionsSubPanel);
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
        offlineModeCheckBox.setSelected(runSetting.isOfflineMode());
        offlineModeCheckBox.addActionListener(event -> {
            MavenExecutorSetting setting = MavenExecutorService.getInstance(project).getSetting();
            System.out.println(setting);
            runSetting.setOfflineMode(offlineModeCheckBox.isSelected());
        });
        innerPropertiesPanel.add(offlineModeCheckBox, bagConstraintsBuilder().fillHorizontal().build());

        alwaysUpdateModeCheckBox = new JCheckBox("Update snapshots");
        alwaysUpdateModeCheckBox.setSelected(runSetting.isAlwaysUpdateSnapshot());
        alwaysUpdateModeCheckBox.addActionListener(event -> {
            runSetting.setAlwaysUpdateSnapshot(alwaysUpdateModeCheckBox.isSelected());
        });
        innerPropertiesPanel.add(alwaysUpdateModeCheckBox, bagConstraintsBuilder().fillHorizontal().insetLeft(20).gridx(1).gridy(0).build());

        skipTestCheckBox = new JCheckBox("Skip tests");
        skipTestCheckBox.setSelected(runSetting.isSkipTests());
        innerPropertiesPanel.add(skipTestCheckBox, bagConstraintsBuilder().fillHorizontal().gridx(0).gridy(1).build());

        skipTestCheckBox.addActionListener(event -> {
            runSetting.setSkipTests(skipTestCheckBox.isSelected());
        });

        threadsLabel = new JLabel("Threads:");
        innerPropertiesPanel.add(threadsLabel, bagConstraintsBuilder().anchorWest().fillNone().insetLeft(20).gridx(1).gridy(1).build());

        threadsTextField = new IntegerField(null, 0, 99);
        threadsTextField.setColumns(2);
        threadsTextField.setCanBeEmpty(true);
        if(runSetting.getThreadCount() != null) {
            threadsTextField.setValue(runSetting.getThreadCount());
        }
        threadsTextField.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent event) {
                try {
                    threadsTextField.validateContent();
                    System.out.println(threadsTextField.getValue());
                    runSetting.setThreadCount(threadsTextField.getValue());
                }
                catch (ConfigurationException e) {
                    System.out.println("null");
                    runSetting.setThreadCount(null);
                }
            }
        });
        innerPropertiesPanel.add(threadsTextField, bagConstraintsBuilder().anchorEast().fillNone().gridx(1).gridy(1).build());

        innerPropertiesPanel.setMaximumSize(new Dimension(200, 50));

        JPanel emptyPanel = new JPanel();


        this.profiles = new CustomCheckBoxList();
        MavenProjectsManager projectsManager = MavenProjectsManager.getInstance(project);
        //profiles.setItems(Lists.newArrayList(projectsManager.getAvailableProfiles()), a -> a);
        projectsManager.getAvailableProfiles().forEach(profile -> {
            profiles.addItem(profile, profile, runSetting.getProfiles().contains(profile));
        });
        profiles.setCheckBoxListListener(new CheckBoxListListener() {
            @Override
            public void checkBoxSelectionChanged(int index, boolean value) {
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
        optionalJvmOptionsSubPanel = new JPanel();
        GroupLayout optionalJvmOptionsLayout = new GroupLayout(optionalJvmOptionsSubPanel);
        optionalJvmOptionsLayout.setAutoCreateGaps(true);
        optionalJvmOptionsLayout.setAutoCreateContainerGaps(true);
        optionalJvmOptionsSubPanel.setLayout(optionalJvmOptionsLayout);

        optionalJvmOptionsCheckBox = new JCheckBox("JVM options:");
        optionalJvmOptionsCheckBox.setSelected(runSetting.isUseOptionalJvmOptions());

        optionalJvmOptionsCheckBox.addActionListener(event -> {
            optionalJvmOptionsComboBox.setEnabled(optionalJvmOptionsCheckBox.isSelected());
            runSetting.setUseOptionalJvmOptions(optionalJvmOptionsCheckBox.isSelected());

        });

        optionalJvmOptionsComboBox = new ComboBox();
        optionalJvmOptionsComboBox.setLightWeightPopupEnabled(false);
        EditorComboBoxEditor editor = new StringComboboxEditor(project, PlainTextFileType.INSTANCE, optionalJvmOptionsComboBox);
        optionalJvmOptionsComboBox.setRenderer(new EditorComboBoxRenderer(editor));
        optionalJvmOptionsComboBox.setEditable(true);
        optionalJvmOptionsComboBox.setEditor(editor);
        optionalJvmOptionsComboBox.setFocusable(true);
        optionalJvmOptionsComboBox.setEnabled(optionalJvmOptionsCheckBox.isSelected());
        optionalJvmOptionsComboBox.getEditor().setItem(runSetting.optionalJvmOptionsAsText());
        optionalJvmOptionsEditor = editor.getEditorComponent();
//        (new MavenPluginsCompletionProvider(project)).apply(optionalJvmOptionsEditor);

        optionalJvmOptionsEditor.addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(DocumentEvent event) {
                runSetting.setOptionalJvmOptions(Lists.newArrayList(optionalJvmOptionsComboBox.getEditor().getItem().toString().split("\\s")));
            }
        });

        optionalJvmOptionsLayout.setHorizontalGroup(
                optionalJvmOptionsLayout.createSequentialGroup()
                        .addComponent(optionalJvmOptionsCheckBox)
                        .addComponent(optionalJvmOptionsComboBox)
        );
        optionalJvmOptionsLayout.setVerticalGroup(
                optionalJvmOptionsLayout.createSequentialGroup()
                        .addGroup(optionalJvmOptionsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(optionalJvmOptionsCheckBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(optionalJvmOptionsComboBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        )
        );
    }

    @NotNull
    private GridBagConstraintsBuilder bagConstraintsBuilder() {
        return new GridBagConstraintsBuilder();
    }

}