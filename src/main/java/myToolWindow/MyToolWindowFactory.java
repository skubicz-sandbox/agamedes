package myToolWindow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.*;
import com.intellij.ui.components.JBList;
import org.apache.batik.util.gui.resource.JToolbarButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.AsyncPromise;
import org.jetbrains.idea.maven.execution.MavenArgumentsCompletionProvider;
import org.jetbrains.idea.maven.execution.MavenRunConfiguration;
import org.jetbrains.idea.maven.execution.MavenRunConfigurationType;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.project.MavenProjectsManagerWatcher;

import com.google.common.collect.Lists;
import com.intellij.ProjectTopics;
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
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ArrayUtilRt;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey.Chursin
 * Date: Aug 25, 2010
 * Time: 2:09:00 PM
 */
public class MyToolWindowFactory implements ToolWindowFactory {

    private Project project;
    private JButton refreshToolWindowButton;
    private JButton hideToolWindowButton;
    private JLabel currentDate;
    private JLabel currentTime;
    private JLabel timeZone;
    private JPanel myToolWindowContent;
    private JPanel panelContent;
    private JPanel configPanel;
    private JPanel favoritePanel;
    private JTree tree1;
    private JTree tree2;
    private JButton button1;
    private ToolWindow myToolWindow;

    private JTextField goals;
    private ComboBox goalsComboBox;
    private EditorTextField goalsEditor;
    private JButton runMavenButton;

    public MyToolWindowFactory() {

        hideToolWindowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myToolWindow.hide(null);
            }
        });
        refreshToolWindowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                MavenProjectsManager projectsManager = MavenProjectsManager.getInstance(project);
                System.out.println("reimport call");
               // AsyncPromise<Void> promise =
                projectsManager.forceUpdateAllProjectsOrFindAllAvailablePomFiles();
                project.getMessageBus().connect()
                        .subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootListener() {
                            @Override
                            public void rootsChanged(final ModuleRootEvent event) {
                                System.out.println("zaimportowano:" + event);
                            }
                        });

//                promise.done(a -> System.out.println("zaimportowano:" + a));
                System.out.println("reimport called");

              //  MavenProjectsManagerWatcher watcher = new MavenProjectsManagerWatcher();

                ProjectRoot [] labels = {};//getCheckedNodes(ProjectRoot.class, ((CheckboxTree)tree1).getModel());

                Map<ProjectRoot, List<Mavenize>> projectRootMap = findProjects(tree1.getModel());
                System.out.println(projectRootMap);


                MyMavenRunConfigurationType runConfigurationType = ConfigurationTypeUtil.findConfigurationType(MyMavenRunConfigurationType.class);

                for(Map.Entry<ProjectRoot, List<Mavenize>> projectRootListEntry : projectRootMap.entrySet()) {
                    String module = "";

                    for (Mavenize label : projectRootListEntry.getValue()) {
                        module = module + label.getMavenId().getGroupId() + ":" + label.getMavenId().getArtifactId() + ",";
                    }
                    module = module.substring(0, module.lastIndexOf(','));

                    final RunnerAndConfigurationSettings settings = RunManagerEx.getInstanceEx(project)
                            .createRunConfiguration("213", runConfigurationType.getConfigurationFactories()[0]);

                    settings.setActivateToolWindowBeforeRun(true);

                    MyMavenRunConfiguration runConfiguration = (MyMavenRunConfiguration) settings.getConfiguration();
                    MavenRunnerSettings mavenRunnerSettings = new MavenRunnerSettings();
                    //        Map<String, String> mavenProperties =  new HashMap<>();
                    runConfiguration.mavenProperties.clear();
                    runConfiguration.mavenProperties.put("-pl", module);

                    //   mavenRunnerSettings.setVmOptions("-pl " + module);
                    runConfiguration.setRunnerSettings(mavenRunnerSettings);

                    MavenGeneralSettings mavenGeneralSettings = new MavenGeneralSettings();

                    runConfiguration.setGeneralSettings(mavenGeneralSettings);
                    //parametersList.add("-pl", "app-api");
                    MavenRunnerParameters mavenRunnerParameters = new MavenRunnerParameters();
                    mavenRunnerParameters.setWorkingDirPath(projectRootListEntry.getKey().getVirtualFile().getPath());
                    mavenRunnerParameters.setGoals(Lists.newArrayList("clean", "install"));

                    runConfiguration.setRunnerParameters(mavenRunnerParameters);
                    System.out.println(runConfiguration.getRunnerSettings());

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
                MyToolWindowFactory.this.currentDateTime();
            }
        });

    }
    public static Map<ProjectRoot, List<Mavenize>> findProjects(final TreeModel model) {
        Map<ProjectRoot, List<Mavenize>> projectRootMap = new HashMap<>();

        final List<CheckedTreeNode> projectRootNodes = findProjectRootNodes(model);

        projectRootNodes.forEach(projectRootNode -> {
            projectRootMap.put((ProjectRoot)projectRootNode.getUserObject(), getCheckedNodes(Mavenize.class, projectRootNode));
        });


        return projectRootMap;
    }

    public static List<CheckedTreeNode> findProjectRootNodes(final TreeModel model) {
        final List<CheckedTreeNode> nodes = new ArrayList<>();
        final Object root = model.getRoot();

        if (!(root instanceof CheckedTreeNode)) {
            throw new IllegalStateException(
                    "The root must be instance of the " + CheckedTreeNode.class.getName() + ": " + root.getClass().getName());
        }
        new Object() {
            @SuppressWarnings("unchecked")
            public void collect(CheckedTreeNode node) {
                if (node.isLeaf()) {
                    Object userObject = node.getUserObject();
                    if (userObject != null && ProjectRoot.class.isAssignableFrom(userObject.getClass())) {
                        nodes.add(node);
                    }
                }
                else {
                    Object userObject = node.getUserObject();
                    if(userObject != null && ProjectRoot.class.isAssignableFrom(userObject.getClass())) {
                        nodes.add(node);
                    }
                    for (int i = 0; i < node.getChildCount(); i++) {
                        final TreeNode child = node.getChildAt(i);
                        if (child instanceof CheckedTreeNode) {
                            collect((CheckedTreeNode)child);
                        }
                    }

                }
            }
        }.collect((CheckedTreeNode)root);

        return nodes;
    }

    public static <T> List<T> getCheckedNodes(final Class<T> nodeType, final CheckedTreeNode root) {
        final ArrayList<T> nodes = new ArrayList<>();
        if (!(root instanceof CheckedTreeNode)) {
            throw new IllegalStateException(
                    "The root must be instance of the " + CheckedTreeNode.class.getName() + ": " + root.getClass().getName());
        }
        new Object() {
            @SuppressWarnings("unchecked")
            public void collect(CheckedTreeNode node) {
                if (node.isLeaf()) {
                    Object userObject = node.getUserObject();
                    if (node.isChecked() && userObject != null && nodeType.isAssignableFrom(userObject.getClass())) {
                        final T value = (T)userObject;
                        nodes.add(value);
                    }
                }
                else {
                    Object userObject = node.getUserObject();
                    if(node.isChecked() && userObject != null && nodeType.isAssignableFrom(userObject.getClass())) {
                        final T value = (T)userObject;
                        nodes.add(value);
                    }
                    for (int i = 0; i < node.getChildCount(); i++) {
                        final TreeNode child = node.getChildAt(i);
                        if (child instanceof CheckedTreeNode) {
                            collect((CheckedTreeNode)child);
                        }
                    }

                }
            }
        }.collect(root);

        return nodes;
    }

    private void createUIComponents() {
        button1 = new JToolbarButton();
        button1.setIcon(AllIcons.General.Add);

        CheckboxTree.CheckboxTreeCellRenderer renderer = new CheckboxTree.CheckboxTreeCellRenderer() {
            @Override
            public void customizeRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

                if (!(value instanceof DefaultMutableTreeNode)) {
                    return;
                }
                value = ((DefaultMutableTreeNode)value).getUserObject();
          //      System.out.println("dsadassa" + value);

//                if (value instanceof JLabel) {
//                    JLabel template = (JLabel)value;
//                    Color fgColor = JBColor.BLUE;
//            //        System.out.println("dsadassa" + template.getText());
//
//                    getTextRenderer().append(template.getText(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, fgColor));
//                    String description = "description";
//                    if (StringUtil.isNotEmpty(description)) {
//                        getTextRenderer().append(" (" + description + ")", SimpleTextAttributes.GRAY_ATTRIBUTES);
//                    }
//                }
                if (value instanceof Mavenize) {
                    Mavenize template = (Mavenize)value;
                    Color fgColor = JBColor.BLACK;
                    //        System.out.println("dsadassa" + template.getText());

                    getTextRenderer().append(template.getDisplayName(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, fgColor));
//                    String description = "description";
//                    if (StringUtil.isNotEmpty(description)) {
//                        getTextRenderer().append(" (" + description + ")", SimpleTextAttributes.GRAY_ATTRIBUTES);
//                    }
                }
            }
        };
        tree1 = new CheckboxTree(renderer, null);
        tree1.setBackground(Color.WHITE);

    }

    class BookInfo {
        String bookName;
        String bookName2;

        public BookInfo(final String bookName, final String bookName2) {
            this.bookName = bookName;
            this.bookName2 = bookName2;
        }
    }

    // Create the tool window content.
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        this.project = project;
        myToolWindow = toolWindow;
        this.currentDateTime();

//        final ActionManager actionManager = ActionManager.getInstance();
//        ActionToolbar actionToolbar = actionManager.createActionToolbar("toolbar", (DefaultActionGroup)actionManager.getAction("Myplugin.ActionsToolbar"), false);
//
//        myToolWindowContent = new SimpleToolWindowPanel(true);
//        ((SimpleToolWindowPanel) myToolWindowContent).setToolbar(actionToolbar.getComponent());



        MavenProjectsManager projectsManager = MavenProjectsManager.getInstance(project);
        System.out.println("--------------");
        CheckedTreeNode root = new CheckedTreeNode(null);
        for(MavenProject mavenProject : projectsManager.getRootProjects()) {
            JLabel label2 = new JLabel(mavenProject.getMavenId().getGroupId() + ":" + mavenProject.getMavenId().getArtifactId());
            CheckedTreeNode rootProject = new CheckedTreeNode(ProjectRoot.of(mavenProject.getDisplayName(), mavenProject.getMavenId(),
                    mavenProject.getDirectoryFile()));
            rootProject.setChecked(false);

            System.out.println(mavenProject.getDisplayName());
            findChildren(mavenProject, projectsManager, "", rootProject);

            root.add(rootProject);

        }
        tree1.setModel(new DefaultTreeModel(root, false));
        System.out.println("--------------");


        CheckboxTree.CheckboxTreeCellRenderer renderer = new CheckboxTree.CheckboxTreeCellRenderer() {
            @Override
            public void customizeRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

                if (!(value instanceof DefaultMutableTreeNode)) {
                    return;
                }
                value = ((DefaultMutableTreeNode)value).getUserObject();
                //      System.out.println("dsadassa" + value);

//                if (value instanceof JLabel) {
//                    JLabel template = (JLabel)value;
//                    Color fgColor = JBColor.BLUE;
//            //        System.out.println("dsadassa" + template.getText());
//
//                    getTextRenderer().append(template.getText(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, fgColor));
//                    String description = "description";
//                    if (StringUtil.isNotEmpty(description)) {
//                        getTextRenderer().append(" (" + description + ")", SimpleTextAttributes.GRAY_ATTRIBUTES);
//                    }
//                }
                if (value instanceof Mavenize) {
                    Mavenize template = (Mavenize)value;
                    Color fgColor = JBColor.BLACK;
                    //        System.out.println("dsadassa" + template.getText());

                    getTextRenderer().append(template.getDisplayName(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, fgColor));
//                    String description = "description";
//                    if (StringUtil.isNotEmpty(description)) {
//                        getTextRenderer().append(" (" + description + ")", SimpleTextAttributes.GRAY_ATTRIBUTES);
//                    }
                }
            }
        };
        tree2 = new CheckboxTree(renderer, null);
        tree2.setBackground(Color.WHITE);
        tree2.setModel(new DefaultTreeModel(root, false));

        final ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar actionToolbar = actionManager.createActionToolbar("EasyMavenBuilderPanel", (DefaultActionGroup)actionManager
                .getAction("EasyMavenBuilder.ActionsToolbar"), true);

        actionToolbar.setTargetComponent(tree2);
     //   actionToolbar.setMinimumButtonSize(new Dimension(20, 20));
     //   actionToolbar.setMiniMode(true);

        myToolWindowContent = new SimpleToolWindowPanel(true, true);
        ((SimpleToolWindowPanel) myToolWindowContent).setToolbar(actionToolbar.getComponent());

        panelContent = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        panelContent.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        favoritePanel = new JPanel();
        favoritePanel.add(new JLabel("dsa"));

        configPanel = new JPanel();
//        GroupLayout layout = new GroupLayout(configPanel);
//        layout.setAutoCreateGaps(true);
//        layout.setAutoCreateContainerGaps(true);
//
//        configPanel.setLayout(layout);
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.PAGE_AXIS));

        runMavenButton = new JButton();
        runMavenButton.setIcon(AllIcons.General.Run);

       // configPanel.add(button1);

        goals = new JBTextField();

        String [] history = {"dsa"};
        this.goalsComboBox = new ComboBox(history);
//        goalComponent = this.goalsComboBox;
//        this.goalsLabel.setLabelFor(this.goalsComboBox);
        this.goalsComboBox.setLightWeightPopupEnabled(false);
        EditorComboBoxEditor editor = new StringComboboxEditor(project, PlainTextFileType.INSTANCE, this.goalsComboBox);
        this.goalsComboBox.setRenderer(new EditorComboBoxRenderer(editor));
        this.goalsComboBox.setEditable(true);
        this.goalsComboBox.setEditor(editor);
        this.goalsComboBox.setFocusable(true);
        this.goalsEditor = editor.getEditorComponent();

        (new MavenArgumentsCompletionProvider(project)).apply(this.goalsEditor);

        JLabel label2 = new JLabel("Goals2");
        JLabel label = new JLabel("Goals");
       // configPanel.add(label);
       // configPanel.add(goalsComboBox);

        CheckBoxList<String> profiles = new CheckBoxList<>();
        profiles.setItems(Lists.newArrayList("prof1", "prof2", "prof4", "prof4", "prof5"), a -> a);
        JScrollPane profilesScrollPane = ScrollPaneFactory.createScrollPane(profiles);
        profilesScrollPane.setMaximumSize(new Dimension(150, 200));
        profilesScrollPane.setMinimumSize(new Dimension(150, 50));

        GridLayout gridLayout = new GridLayout(0, 3);

        JPanel goalsPanel = new JPanel();
        GroupLayout groupLayout = new GroupLayout(goalsPanel);
        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);

        goalsPanel.setLayout(groupLayout);
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
//        GridBagConstraints c2 = new GridBagConstraints();
//        c2.fill = GridBagConstraints.HORIZONTAL;
//        c2.weightx = 1.0;
//        c2.gridx = 0;
//        c2.gridy = 0;
//        goalsPanel.add(label, c2);
//        c2.fill = GridBagConstraints.HORIZONTAL;
//        c2.gridx = 1;
//        c2.gridy = 0;
//        goalsPanel.add(goalsComboBox, c2);
//        c2.fill = GridBagConstraints.BOTH;
//        c2.gridx = 2;
//        c2.gridy = 0;
//        goalsPanel.add(runMavenButton, c2);

        JPanel propertiesPanel = new JPanel();
        JPanel innerPropertiesPanel = new JPanel();
     //   propertiesPanel.add(profiles);

        GroupLayout propertiesGroupLayout = new GroupLayout(propertiesPanel);
        propertiesGroupLayout.setAutoCreateGaps(true);
        propertiesGroupLayout.setAutoCreateContainerGaps(true);

        propertiesPanel.setLayout(propertiesGroupLayout);

        propertiesGroupLayout.setHorizontalGroup(
                propertiesGroupLayout.createSequentialGroup()
                        .addComponent(innerPropertiesPanel)
                        .addComponent(profilesScrollPane)
        );
        propertiesGroupLayout.setVerticalGroup(
                propertiesGroupLayout.createSequentialGroup()
                        .addGroup(propertiesGroupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(innerPropertiesPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(profilesScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        )
        );

        configPanel.add(goalsPanel);
        configPanel.add(propertiesPanel);

//        layout.setHorizontalGroup(
//                layout.createSequentialGroup()
//                        .addComponent(label)
//                        .addComponent(goalsComboBox)
//                        .addComponent(runMavenButton)
//                        .addComponent(profilesScrollPane)
//        );
//        layout.setVerticalGroup(
//                layout.createSequentialGroup()
//                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
//                                .addComponent(label, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                                .addComponent(goalsComboBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                                .addComponent(runMavenButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                                .addComponent(profilesScrollPane)
//                        )
//        );

     //   configPanel.add(goalsEditor);

    //    panelContent.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panelContent.add(configPanel, c);

        c.weightx = 0.0;

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
      //  c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        panelContent.add(ScrollPaneFactory.createScrollPane(tree2), c);

        c.fill = GridBagConstraints.VERTICAL;
        c.weightx = 0.0;
        c.weighty = 1.0;
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        panelContent.add(favoritePanel, c);


        ((SimpleToolWindowPanel) myToolWindowContent).setContent(panelContent);



        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myToolWindowContent, "dsad", false);
        toolWindow.getContentManager().addContent(content);

        System.out.println( ((SimpleToolWindowPanel) myToolWindowContent).isToolbarVisible());
    }


    private void findChildren(MavenProject rootProject, MavenProjectsManager projectsManager, String offset, CheckedTreeNode root) {
        offset = offset + "  ";

        for(MavenProject mavenProject : projectsManager.findInheritors(rootProject)) {
            System.out.println(offset + mavenProject.getDisplayName());
           // CheckedTreeNode projectNode = new CheckedTreeNode(new JLabel(mavenProject.getMavenId().getGroupId() + ":" + mavenProject
           //         .getMavenId().getArtifactId()));
            CheckedTreeNode projectNode = new CheckedTreeNode(ProjectModule.of(mavenProject.getDisplayName(), mavenProject.getMavenId()));
            projectNode.setChecked(false);
            root.add(projectNode);
            findChildren(mavenProject, projectsManager, offset, projectNode);
        }

    }


    public void currentDateTime() {
        // Get current date and time
        Calendar instance = Calendar.getInstance();
        currentDate.setText(
                String.valueOf(instance.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(instance.get(Calendar.MONTH) + 1) + "/" + String
                        .valueOf(instance.get(Calendar.YEAR)));
        currentDate.setIcon(new ImageIcon(getClass().getResource("/myToolWindow/Calendar-icon.png")));
        int min = instance.get(Calendar.MINUTE);
        String strMin;
        if (min < 10) {
            strMin = "0" + String.valueOf(min);
        }
        else {
            strMin = String.valueOf(min);
        }
        currentTime.setText(instance.get(Calendar.HOUR_OF_DAY) + ":" + strMin);
        currentTime.setIcon(new ImageIcon(getClass().getResource("/myToolWindow/Time-icon.png")));
        // Get time zone
        long gmt_Offset = instance.get(Calendar.ZONE_OFFSET); // offset from GMT in milliseconds
        String str_gmt_Offset = String.valueOf(gmt_Offset / 3600000);
        str_gmt_Offset = (gmt_Offset > 0) ? "GMT + " + str_gmt_Offset : "GMT - " + str_gmt_Offset;
        timeZone.setText(str_gmt_Offset);
        timeZone.setIcon(new ImageIcon(getClass().getResource("/myToolWindow/Time-zone-icon.png")));


    }

}
