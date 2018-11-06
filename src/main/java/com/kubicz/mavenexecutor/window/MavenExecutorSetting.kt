package com.kubicz.mavenexecutor.window

import com.google.common.collect.Lists
import com.intellij.util.xmlb.annotations.Property
import com.intellij.util.xmlb.annotations.Tag
import com.kubicz.mavenexecutor.model.ProjectToBuild
import java.io.Serializable


class MavenExecutorSetting: Cloneable, Serializable {

    @Property
    var goals: MutableList<String> = ArrayList()

    @Property
    var profiles: MutableList<String> = ArrayList()

    @Property
    var jvmOptions: MutableList<String> = ArrayList()

    @Property
    var optionalJvmOptions: MutableList<String> = ArrayList()

    @Property
    var threadCount: Int? = null

    @Property
    var environmentProperties:MutableMap<String, String> = HashMap()

    @Property
    var isUseOptionalJvmOptions = false

    @Property
    var isOfflineMode = false

    @Property
    var isAlwaysUpdateSnapshot = false

    @Property
    var isSkipTests = false

    @Property
    var projectsToBuild: MutableList<ProjectToBuild> = ArrayList()

    fun goalsAsText(): String {
        return ListTextMapper.listAsText(goals, " ")
    }

    fun optionalJvmOptionsAsText(): String {
        return ListTextMapper.listAsText(optionalJvmOptions, " ")
    }

    fun jvmOptionsAsText(): String {
        return ListTextMapper.listAsText(jvmOptions, " ")
    }

    fun goalsFromText(goalsText: String) {
        if (goalsText.isEmpty()) {
            goals.clear()
        } else {
            goals = Lists.newArrayList(*goalsText.split("\\s".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        }
    }

//    @Attribute("offlineMode")
//    fun isOfflineMode(): Boolean {
//        return offlineMode
//    }

}