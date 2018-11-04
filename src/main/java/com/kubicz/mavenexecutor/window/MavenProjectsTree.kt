package com.kubicz.mavenexecutor.window

import com.intellij.ui.*
import com.kubicz.mavenexecutor.model.*
import org.jetbrains.idea.maven.project.MavenProject
import org.jetbrains.idea.maven.project.MavenProjectsManager
import java.util.*
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeModel

class MavenProjectsTree(projectsManager: MavenProjectsManager, selectedNodes: List<ProjectToBuild>) {

    private val tree: MyCheckboxTree

    private val projectsManager = projectsManager

    private val findProject: List<ProjectToBuild>.(MavenArtifact) -> ProjectToBuild? = {
        searchedProject -> firstOrNull { it.mavenArtifact.equalsGroupAndArtifactId(searchedProject) }
    }

    private val renderer = object : MyCheckboxTreeBase.CheckboxTreeCellRendererBase() {
        override fun customizeRenderer(tree: JTree, value: Any, selected: Boolean, expanded: Boolean, leaf: Boolean, row: Int, hasFocus: Boolean) {
            var userObject = (value as DefaultMutableTreeNode).userObject

            if (userObject is Mavenize) {
                val fgColor = JBColor.BLACK

                textRenderer.append(userObject.displayName, SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, fgColor))
            }
        }
    }

    val treeComponent: JTree
        get() = tree

    init {
        this.tree = MyCheckboxTree(renderer, null)

        this.tree.addCheckboxTreeListener(object : CheckboxTreeAdapter() {
            override fun nodeStateChanged(node: CheckedTreeNode) {
           //     (node.userObject as? ProjectRootNode)?.isSelected = node.isChecked
            }
        })

        update(projectsManager, selectedNodes)
    }

    fun update(projectsManager: MavenProjectsManager, selectedNodes: List<ProjectToBuild>) {
        val root = CheckedTreeNode(null)
        for (mavenProject in projectsManager.rootProjects) {
            val rootMavenArtifact = MavenArtifactFactory.from(mavenProject.mavenId)
            val rootProjectNode = CheckedTreeNode(ProjectRootNode.of(mavenProject.displayName, rootMavenArtifact, false,
                    mavenProject.directoryFile))

            val project = selectedNodes.findProject(rootMavenArtifact)
            rootProjectNode.isChecked = project?.buildEntireProject() ?: false

            createChildrenNodes(mavenProject, rootProjectNode)

            root.add(rootProjectNode)
        }
        tree.model = DefaultTreeModel(root)

        updateTreeSelection(selectedNodes)

        expandAll()
    }

//    private fun findProject1(projects: List<ProjectToBuild>, searchedProject: MavenArtifact): ProjectToBuild? {
//        return projects.firstOrNull { projectToBuild -> projectToBuild.mavenArtifact.equalsGroupAndArtifactId(searchedProject) }
//    }

    private fun expandAll() {
        var size = tree.rowCount
        var i = 0
        while (i < size) {
            tree.expandRow(i)

            i++
            size = tree.rowCount // returns only visible nodes
        }
    }

    fun updateTreeSelection(selectedNodes: List<ProjectToBuild>) {
        val root = this.tree.model.root as CheckedTreeNode

        val childCount = root.childCount

        for (i in 0 until childCount) {
            val childNode = root.getChildAt(i) as CheckedTreeNode

            val mavenize = childNode.userObject as Mavenize
            val selectedProject = selectedNodes
                    .firstOrNull { projectToBuild -> projectToBuild.mavenArtifact.equalsGroupAndArtifactId(mavenize.mavenArtifact) }

            val buildEntireProject = selectedProject?.buildEntireProject() ?: false
            if (buildEntireProject) {
                childNode.isChecked = true
                checkedAllTreeNode(childNode)
            } else {
                if (childNode.isLeaf) {
                    childNode.isChecked = selectedProject?.buildEntireProject() ?: false
                } else {
                    updateTreeNode(childNode, selectedProject?.selectedModules ?: ArrayList())
                }
            }
        }

        tree.repaint()
    }

    private fun checkedAllTreeNode(node: DefaultMutableTreeNode) {
        val childCount = node.childCount

        for (i in 0 until childCount) {
            val childNode = node.getChildAt(i) as CheckedTreeNode

            if (childNode.isLeaf) {
                childNode.isChecked = true
            } else {
                checkedAllTreeNode(childNode)
            }
        }
    }

    private fun updateTreeNode(node: DefaultMutableTreeNode, selectedNodes: List<MavenArtifact>) {
        val childCount = node.childCount

        for (i in 0 until childCount) {
            val childNode = node.getChildAt(i) as CheckedTreeNode

            val mavenize = childNode.userObject as Mavenize
            val selectedProject = selectedNodes.stream()
                    .filter { artifact -> artifact.equalsGroupAndArtifactId(mavenize.mavenArtifact) }
                    .findFirst()

            if (childNode.isLeaf) {
                childNode.isChecked = selectedProject.isPresent
            } else {
                updateTreeNode(childNode, selectedNodes)
            }
        }
    }

    private fun containsArtifact(searched: MavenArtifact, listToFilter: List<MavenArtifact>?): Boolean {
        return listToFilter?.any { mavenArtifact -> mavenArtifact.equalsGroupAndArtifactId(searched) } ?: false

    }

    fun addCheckboxTreeListener(checkboxTreeListener: CheckboxTreeListener) {
        this.tree.addCheckboxTreeListener(checkboxTreeListener)
    }

    fun findSelectedProjects(): Map<ProjectRootNode, List<Mavenize>> {
        val projectRootMap = HashMap<ProjectRootNode, List<Mavenize>>()

        val projectRootNodes = findProjectRootNodes(tree.model)

        projectRootNodes.forEach { projectRootNode ->
            val subModules = getCheckedNodes(Mavenize::class.java, projectRootNode)
            if (!subModules.isEmpty()) {
                projectRootMap[projectRootNode.userObject as ProjectRootNode] = subModules
            }
            //            }
        }


        return projectRootMap
    }

    private fun findProjectRootNodes(model: TreeModel): List<CheckedTreeNode> {
        val nodes = ArrayList<CheckedTreeNode>()
        val root = model.root

        if (root !is CheckedTreeNode) {
            throw IllegalStateException(
                    "The root must be instance of the " + CheckedTreeNode::class.java.name + ": " + root.javaClass.name)
        }
        object : Any() {
            fun collect(node: CheckedTreeNode) {
                if (node.isLeaf) {
                    val userObject = node.userObject
                    if (userObject != null && ProjectRootNode::class.java.isAssignableFrom(userObject.javaClass)) {
                        nodes.add(node)
                    }
                } else {
                    val userObject = node.userObject
                    if (userObject != null && ProjectRootNode::class.java.isAssignableFrom(userObject.javaClass)) {
                        nodes.add(node)
                    }
                    for (i in 0 until node.childCount) {
                        val child = node.getChildAt(i)
                        if (child is CheckedTreeNode) {
                            collect(child)
                        }
                    }

                }
            }
        }.collect(root)

        return nodes
    }

    private fun <T> getCheckedNodes(nodeType: Class<T>, root: CheckedTreeNode): List<T> {
        val nodes = ArrayList<T>()
        if (root !is CheckedTreeNode) {
            throw IllegalStateException(
                    "The root must be instance of the " + CheckedTreeNode::class.java.name + ": " + root.javaClass.name)
        }
        object : Any() {
            fun collect(node: CheckedTreeNode) {
                if (node.isLeaf) {
                    val userObject = node.userObject
                    if (node.isChecked && userObject != null && nodeType.isAssignableFrom(userObject.javaClass)) {
                        val value = userObject as T
                        nodes.add(value)
                    }
                } else {
                    val userObject = node.userObject
                    if (node.isChecked && userObject != null && nodeType.isAssignableFrom(userObject.javaClass)) {
                        //       if(isChecked(node) && userObject != null && nodeType.isAssignableFrom(userObject.getClass())) {
                        val value = userObject as T
                        nodes.add(value)
                    }
                    for (i in 0 until node.childCount) {
                        val child = node.getChildAt(i)
                        if (child is CheckedTreeNode) {
                            collect(child)
                        }
                    }

                }
            }
        }.collect(root)

        return nodes
    }

    private fun createChildrenNodes(rootProject: MavenProject, root: CheckedTreeNode) {
        for (mavenProject in projectsManager.findInheritors(rootProject)) {
            val nodeMavenArtifact = MavenArtifactFactory.from(mavenProject.mavenId)
            val projectNode = CheckedTreeNode(ProjectModuleNode(mavenProject.displayName, nodeMavenArtifact))

            root.add(projectNode)

            createChildrenNodes(mavenProject, projectNode)
        }
    }

}
