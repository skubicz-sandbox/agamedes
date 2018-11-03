package com.kubicz.mavenexecutor.window

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.kubicz.mavenexecutor.model.MavenArtifact
import com.kubicz.mavenexecutor.model.MavenArtifactFactory
import com.kubicz.mavenexecutor.model.ProjectToBuildBuilder
import org.jetbrains.idea.maven.project.MavenProjectsManager
import java.awt.Dimension
import java.util.*
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class SelectCurrentPanel(private var project: Project,
                         private var parent: MavenExecutorToolWindow) {

    private var panel = JPanel()

    private var selectCurrentButton: JButton = JButton("Select current")

    init {
        panel.layout = BoxLayout(panel, BoxLayout.PAGE_AXIS)
        selectCurrentButton.maximumSize = Dimension(Integer.MAX_VALUE, selectCurrentButton.maximumSize.getHeight().toInt())
        selectCurrentButton.addActionListener {
            actionListener()
        }

        panel.add(selectCurrentButton)
    }

    val component
        get() : JComponent = panel

    private fun actionListener() {
        val projectsManager = MavenProjectsManager.getInstance(project)
        val manager = FileEditorManager.getInstance(project)

        val fileEditors = manager.selectedEditors

        val projectsToBuild = ArrayList<ProjectToBuildBuilder>()

        fileEditors.forEach { fileEditor ->
            fileEditor.file?.let { file ->
                val currentProject = projectsManager.findContainingProject(file)

                currentProject?.let {
                    val currentRootProject = projectsManager.findRootProject(currentProject)

                    currentRootProject?.let {
                        val selectedArtifact = MavenArtifactFactory.from(currentProject.mavenId)
                        val rootArtifact = MavenArtifactFactory.from(currentRootProject.mavenId)

                        val projectToBuildBuilder = projectsToBuild
                                .firstOrNull { item -> item.mavenArtifact.equalsGroupAndArtifactId(rootArtifact) }
                                ?: ProjectToBuildBuilder(currentRootProject.displayName, rootArtifact, currentRootProject.directoryFile.path)

                        if (!projectsToBuild.contains(projectToBuildBuilder)) {
                            projectsToBuild.add(projectToBuildBuilder)
                        }

                        if (selectedArtifactIsNotRoot(selectedArtifact, rootArtifact)) {
                            projectToBuildBuilder.addArtifact(selectedArtifact)

                            projectsManager.findInheritors(currentProject).forEach {inheritorArtifact ->
                                val artifact = MavenArtifactFactory.from(inheritorArtifact.mavenId)

                                projectToBuildBuilder.addArtifact(artifact)
                            }
                        }
                    }

                }

            }
        }

        parent
                .settingsService
                .currentSettings
                .projectsToBuild = projectsToBuild.map(ProjectToBuildBuilder::build)

        parent.updateProjectTree()
    }

    private fun selectedArtifactIsNotRoot(selectedArtifact : MavenArtifact, rootArtifact : MavenArtifact) : Boolean = !rootArtifact.equalsGroupAndArtifactId(selectedArtifact)

}