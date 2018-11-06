package com.kubicz.mavenexecutor.window

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.util.ui.JBUI
import java.awt.Color
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class FavoritePanel(settingsService: MavenExecutorService, changeSettingListener: () -> Unit) {

    private var panel = JPanel()

    private val settingsService = settingsService;

    private var defaultSettingsButton = JButton("DEFAULT")

    private val isDefault: JButton.() -> Boolean = {name == "default"}

    private val init: JButton.(Boolean) -> Unit = {selected ->
        background = color(selected)
        maximumSize = Dimension(Integer.MAX_VALUE, maximumSize.getHeight().toInt())

        addActionListener{
            val button = (it.source as JButton)

            if(button.isDefault()) {
                settingsService.loadDefaultSettings()
            }
            else {
                settingsService.loadSettings(button.text)
            }

            refreshSelection()

            changeSettingListener()
        }
    }

    val component
        get() : JComponent = panel

    init {
        initComponents()
    }

    fun refresh() {
        panel.removeAll()

        initComponents()

        panel.updateUI()
        panel.repaint()
    }

    private fun initComponents() {
        panel.layout = BoxLayout(panel, BoxLayout.PAGE_AXIS)

        val currentSettingsLabel = settingsService.currentSettingsLabel

        val isDefaultSettingsSelected = settingsService.isDefaultSettings

        defaultSettingsButton.name = "default"
        defaultSettingsButton.init(isDefaultSettingsSelected)

        panel.add(defaultSettingsButton)

        val favoriteLabel = JLabel("Favorite:", null, SwingConstants.CENTER)
        favoriteLabel.maximumSize = Dimension(Integer.MAX_VALUE, favoriteLabel.maximumSize.getHeight().toInt())
        panel.add(favoriteLabel)

        settingsService.favoriteSettingsNames.forEach { settingName ->
            val button = JButton(settingName)

            button.init(settingName == currentSettingsLabel)

            button.addMouseListener(object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent?) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        showMenu(e?.source as JButton)
                    }
                }
            })

            panel.add(button)
        }

    }

    private fun color(selected: Boolean): Color {
        return if(selected) Color(200, 200, 200) else Color(227, 227, 227)
    }

    private fun refreshSelection() {
        panel.components.forEach {
            if (it is JButton) {
                if(it.isDefault()) {
                    it.background = color(settingsService.isDefaultSettings)
                }
                else {
                    it.background = color(settingsService.currentSettingsLabel == it.text)
                }
            }
        }
    }

    private fun showMenu(button: JButton) {
        val actionManager = ActionManager.getInstance()

        val menu = actionManager.createActionPopupMenu("EasyMavenBuilderPanel", actionManager.getAction("EasyMavenBuilder.FavoriteItemContextMenu") as DefaultActionGroup)
        menu.component.show(button, JBUI.scale(-10), button.height + JBUI.scale(2))
    }

}