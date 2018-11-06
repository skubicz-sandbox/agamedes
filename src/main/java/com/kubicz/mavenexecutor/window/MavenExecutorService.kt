package com.kubicz.mavenexecutor.window

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Property
import com.intellij.util.xmlb.annotations.Tag
import java.util.*

@State(name = "mavenExecutorSetting", storages = [Storage("mavenExecutorSetting.xml")])
class MavenExecutorService : PersistentStateComponent<MavenExecutorService> {

    @Property
    var defaultSettings = MavenExecutorSetting()

    @Property
    var currentSettingsLabel: String? = null

    @Property
    var goalsHistory: History = History()

    @Property
    var jvmOptionHistory: History = History()

    @Property
    private var favorite = HashMap<String, MavenExecutorSetting>()

    val favoriteSettings: List<MavenExecutorSetting>
        get() = favorite.values.toList()

    val favoriteSettingsNames: List<String>
        get() = favorite.keys.toList()

    val currentSettings: MavenExecutorSetting
        get() {
            return favorite.getOrDefault(currentSettingsLabel, defaultSettings)
        }

    val isDefaultSettings: Boolean
        get() = currentSettingsLabel == null


    init {
    }

    override fun getState(): MavenExecutorService? {
        return this
    }

    override fun loadState(state: MavenExecutorService) {
        XmlSerializerUtil.copyBean(state, this)
    }

    fun addSettings(settingsName: String, setting: MavenExecutorSetting) {
        favorite[settingsName] = setting
    }

    fun loadSettings(settingsName: String) {
        currentSettingsLabel = settingsName
    }

    fun loadDefaultSettings() {
        currentSettingsLabel = null
    }

    fun removeFavoriteSettings(settingsName: String) {
        favorite.remove(settingsName)
    }

    companion object {
        fun getInstance(project: Project): MavenExecutorService {
            return ServiceManager.getService(project, MavenExecutorService::class.java)
        }
    }

}
