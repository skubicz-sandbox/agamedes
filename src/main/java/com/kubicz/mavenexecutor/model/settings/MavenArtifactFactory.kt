package com.kubicz.mavenexecutor.model.settings

import com.kubicz.mavenexecutor.model.MavenArtifact
import org.jetbrains.idea.maven.model.MavenId

class MavenArtifactFactory {

    companion object {
        fun from(mavenId: MavenId)  = MavenArtifact(mavenId.groupId ?: "", mavenId.artifactId
                ?: "", mavenId.version ?: "")
    }

}