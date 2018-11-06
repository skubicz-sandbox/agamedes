package com.kubicz.mavenexecutor.window

import com.intellij.ui.CheckboxTreeAdapter
import com.intellij.ui.CheckedTreeNode
import com.intellij.ui.ScrollPaneFactory
import com.kubicz.mavenexecutor.model.Mavenize
import com.kubicz.mavenexecutor.model.ProjectRootNode
import com.kubicz.mavenexecutor.model.ProjectToBuild
import org.jetbrains.idea.maven.project.MavenProjectsManager
import javax.swing.JComponent

class MavenProjectsTreePanel(projectsManager: MavenProjectsManager, settingsService: MavenExecutorService, nodeStateChangedListener: () -> Unit) {

    private val projectsTree = MavenProjectsTree(projectsManager, settingsService.currentSettings.projectsToBuild)

    private val scrollPane = ScrollPaneFactory.createScrollPane(projectsTree.treeComponent)

    private val settingsService = settingsService

    val component
        get() : JComponent = scrollPane

    init {
        projectsTree.addCheckboxTreeListener(object : CheckboxTreeAdapter() {
            override fun nodeStateChanged(node: CheckedTreeNode) {
                val selectedProjects = projectsTree.findSelectedProjects()

                val projectsToBuild = selectedProjects.entries.map { toProjectToBuild(it) }.toMutableList()

                settingsService.currentSettings.projectsToBuild = projectsToBuild

                nodeStateChangedListener()
            }
        })

    }

    fun update() {
        projectsTree.update(settingsService.currentSettings.projectsToBuild)
    }

    fun updateTreeSelection() {
        projectsTree.updateTreeSelection(settingsService.currentSettings.projectsToBuild)
    }

    private fun toProjectToBuild(selectedProjectEntry: Map.Entry<ProjectRootNode, List<Mavenize>>): ProjectToBuild {
        val projectRootNode = selectedProjectEntry.key
        val selectedModule = selectedProjectEntry.value

        val projectToBuild: ProjectToBuild
        if (projectRootNode.isSelected()) {
            projectToBuild = ProjectToBuild(projectRootNode.getDisplayName(), projectRootNode.mavenArtifact, projectRootNode.projectDirectory.path)
        } else {
            val modules = selectedModule.map { it.mavenArtifact }.toList()
            projectToBuild = ProjectToBuild(projectRootNode.getDisplayName(), projectRootNode.mavenArtifact, projectRootNode.projectDirectory.path, modules)
        }

        return projectToBuild
    }
}