package myToolWindow

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.JavaParameters
import com.intellij.openapi.project.Project
import com.kubicz.mavenexecutor.window.MavenAdditionalParameters
import org.jetbrains.idea.maven.execution.MavenRunConfiguration

class MyMavenRunConfiguration(project: Project, factory: ConfigurationFactory, name: String) : MavenRunConfiguration(project, factory, name) {

    var additionalParameters = MavenAdditionalParameters()

    @Throws(ExecutionException::class)
    override fun createJavaParameters(project: Project?): JavaParameters {
        val javaParameters = super.createJavaParameters(project)

        if (!additionalParameters.projects.isEmpty()) {
            javaParameters.programParametersList.add("-pl", additionalParameters.projects)
        }

        return javaParameters
    }
}