//package com.kubicz.mavenexecutor.window;
//
//import com.kubicz.mavenexecutor.model.Mavenize;
//import com.kubicz.mavenexecutor.model.ProjectRootNode;
//import com.kubicz.mavenexecutor.model.ProjectToBuild;
//import org.jetbrains.idea.maven.model.MavenId;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class MavenProjectsTreeViewListener extends MavenExecutorWindowListenerAdapter {
//
//    private MavenProjectsTreeView treeView;
//
//    public MavenProjectsTreeViewListener(MavenExecutorSetting setting, MavenProjectsTreeView treeView) {
//        super(setting);
//        this.treeView = treeView;
//    }
//
//    @Override
//    protected void update(MavenExecutorSetting setting) {
//        Map<ProjectRootNode, List<Mavenize>> selectedProjects = treeView.findSelectedProjects();
//
//        List<ProjectToBuild> projectsToBuild = selectedProjects.entrySet().stream()
//                .map(this::toProjectToBuild)
//                .collect(Collectors.toList());
//
//        setting.setProjectsToBuild(projectsToBuild);
//    }
//
//    private ProjectToBuild toProjectToBuild(Map.Entry<ProjectRootNode, List<Mavenize>> selectedProjectEntry) {
//        ProjectRootNode projectRootNode = selectedProjectEntry.getKey();
//        List<Mavenize> selectedModule = selectedProjectEntry.getValue();
//
//        ProjectToBuild projectToBuild;
//        if(projectRootNode.isSelected()) {
//            projectToBuild = new ProjectToBuild(projectRootNode.getDisplayName(), projectRootNode.getMavenArtifact(), projectRootNode.getProjectDirectory().getPath());
//        }
//        else {
//            List<MavenId> modules = selectedModule.stream().map(Mavenize::getMavenArtifact).collect(Collectors.toList());
//            projectToBuild = new ProjectToBuild(projectRootNode.getDisplayName(), projectRootNode.getMavenArtifact(), projectRootNode.getProjectDirectory().getPath(), modules);
//        }
//
//        return projectToBuild;
//    }
//
//}
