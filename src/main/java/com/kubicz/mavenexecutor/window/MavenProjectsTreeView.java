package com.kubicz.mavenexecutor.window;

import com.intellij.ui.*;
import com.kubicz.mavenexecutor.model.Mavenize;
import com.kubicz.mavenexecutor.model.ProjectModuleNode;
import com.kubicz.mavenexecutor.model.ProjectRootNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MavenProjectsTreeView {

    private CheckboxTree tree;

    private CheckboxTree.CheckboxTreeCellRenderer renderer = new CheckboxTree.CheckboxTreeCellRenderer() {
        @Override
        public void customizeRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            if (!(value instanceof DefaultMutableTreeNode)) {
                return;
            }
            value = ((DefaultMutableTreeNode)value).getUserObject();

            if (value instanceof Mavenize) {
                Mavenize template = (Mavenize)value;
                Color fgColor = JBColor.BLACK;

                getTextRenderer().append(template.getDisplayName(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, fgColor));
            }
        }
    };

    public MavenProjectsTreeView(@NotNull MavenProjectsManager projectsManager) {
        CheckedTreeNode root = new CheckedTreeNode(null);
        for(MavenProject mavenProject : projectsManager.getRootProjects()) {
            CheckedTreeNode rootProject = new CheckedTreeNode(ProjectRootNode.of(mavenProject.getDisplayName(), mavenProject.getMavenId(), false,
                    mavenProject.getDirectoryFile()));
            rootProject.setChecked(false);

            findChildren(mavenProject, projectsManager, "", rootProject);

            root.add(rootProject);

        }

        this.tree = new CheckboxTree(renderer, root);
        this.tree.addCheckboxTreeListener(new CheckboxTreeAdapter() {
            @Override
            public void nodeStateChanged(@NotNull CheckedTreeNode node) {
                Object userObject = node.getUserObject();
                if(node.getUserObject() instanceof ProjectRootNode) {
                    ((ProjectRootNode)userObject).setSelected(node.isChecked());
                }
            }
        });
    }

    public void addFocusLostListener(FocusListener focusListener) {
        this.tree.addFocusListener(focusListener);
    }

    public JTree getTreeComponent() {
        return tree;
    }

    public Map<ProjectRootNode, List<Mavenize>> findSelectedProjects() {
        Map<ProjectRootNode, List<Mavenize>> projectRootMap = new HashMap<>();

        final List<CheckedTreeNode> projectRootNodes = findProjectRootNodes(tree.getModel());

        projectRootNodes.forEach(projectRootNode -> {
                List<Mavenize> subModules = getCheckedNodes(Mavenize.class, projectRootNode);
                if(!subModules.isEmpty()) {
                    projectRootMap.put((ProjectRootNode) projectRootNode.getUserObject(), subModules);
                }
//            }
        });


        return projectRootMap;
    }

    private List<CheckedTreeNode> findProjectRootNodes(final TreeModel model) {
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
                    if (userObject != null && ProjectRootNode.class.isAssignableFrom(userObject.getClass())) {
                        nodes.add(node);
                    }
                }
                else {
                    Object userObject = node.getUserObject();
                    if(userObject != null && ProjectRootNode.class.isAssignableFrom(userObject.getClass())) {
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

    private <T> List<T> getCheckedNodes(final Class<T> nodeType, final CheckedTreeNode root) {
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

    private void findChildren(MavenProject rootProject, MavenProjectsManager projectsManager, String offset, CheckedTreeNode root) {
        offset = offset + "  ";

        for(MavenProject mavenProject : projectsManager.findInheritors(rootProject)) {
            System.out.println(offset + mavenProject.getDisplayName());
            // CheckedTreeNode projectNode = new CheckedTreeNode(new JLabel(mavenProject.getMavenId().getGroupId() + ":" + mavenProject
            //         .getMavenId().getArtifactId()));
            CheckedTreeNode projectNode = new CheckedTreeNode(ProjectModuleNode.of(mavenProject.getDisplayName(), mavenProject.getMavenId()));
            projectNode.setChecked(false);
            root.add(projectNode);
            findChildren(mavenProject, projectsManager, offset, projectNode);
        }
    }

}
