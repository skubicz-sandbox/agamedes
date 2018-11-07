package org.kubicz.mavenexecutor.view.window.panels

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.util.ui.JBUI
import org.kubicz.mavenexecutor.view.window.MavenExecutorService
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

            println(button.background)
            panel.add(button)
        }

    }

    private fun color(selected: Boolean): Color {
        var defaultColor = UIManager.getColor("Button.background")
        return if(selected) brighter(defaultColor) else defaultColor
     //   return if(selected) Color(200, 200, 200) else Color(227, 227, 227)
    }//javax.swing.plaf.ColorUIResource[r=60,g=63,b=65]

    fun darker(color: Color): Color {
        return Color(Math.max((color.red * 0.9).toInt(), 0),
                Math.max((color.green * 0.9).toInt(), 0),
                Math.max((color.blue * 0.9).toInt(), 0),
                color.alpha)
    }

    fun brighter(color: Color): Color {
        var r = color.red
        var g = color.green
        var b = color.blue
        val alpha = color.alpha

        var FACTOR = 0.7
        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        val i = (1.0 / (1.0 - FACTOR)).toInt()
        if (r == 0 && g == 0 && b == 0) {
            return Color(i, i, i, alpha)
        }
        if (r > 0 && r < i) r = i
        if (g > 0 && g < i) g = i
        if (b > 0 && b < i) b = i

        return Color(Math.min((r / FACTOR).toInt(), 255),
                Math.min((g / FACTOR).toInt(), 255),
                Math.min((b / FACTOR).toInt(), 255),
                alpha)
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