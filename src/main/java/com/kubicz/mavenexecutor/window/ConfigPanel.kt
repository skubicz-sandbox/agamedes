package com.kubicz.mavenexecutor.window

import com.google.common.collect.Lists
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.EditorComboBoxRenderer
import com.intellij.ui.EditorTextField
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.StringComboboxEditor
import com.intellij.ui.components.fields.IntegerField
import org.jetbrains.idea.maven.execution.MavenArgumentsCompletionProvider
import org.jetbrains.idea.maven.project.MavenProjectsManager
import java.awt.Dimension
import java.awt.GridBagLayout
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import javax.swing.*

class ConfigPanel(private var project: Project,
                  private var parentComponent: MavenExecutorToolWindow) {

    private var panel = JPanel()

    private var goalsSubPanel = JPanel()

    private var propertiesSubPanel = JPanel()

    private var optionalJvmOptionsSubPanel = JPanel()

    private var goalsComboBox = ComboBox<String>()

    private var runMavenButton = JButton()

    private var offlineModeCheckBox = JCheckBox("Offline")

    private var skipTestCheckBox = JCheckBox("Skip tests")

    private var alwaysUpdateModeCheckBox = JCheckBox("Update snapshots")

    private var threadsLabel = JLabel("Threads:")

    private var threadsTextField = IntegerField(null, 0, 99)

    private var optionalJvmOptionsCheckBox = JCheckBox("JVM options:")

    private var optionalJvmOptionsComboBox = ComboBox<String>()

    private var profiles: CustomCheckBoxList = CustomCheckBoxList()

    private var settingsService: MavenExecutorService = parentComponent.settingsService

    private val setHistory: ComboBox<String>.(History) -> Unit = {history -> model = DefaultComboBoxModel<String>(history.asArray())}

    private val initEditor: ComboBox<String>.(String) -> Unit = {
        val comboEditor = StringComboboxEditor(project, PlainTextFileType.INSTANCE, this)
        renderer = EditorComboBoxRenderer(editor)
        isLightWeightPopupEnabled = false
        isEditable = true
        editor = comboEditor
        isFocusable = true
        editor.item = it
    }

    val component
        get() : JComponent = panel

    init {
        panel.layout = BoxLayout(panel, BoxLayout.PAGE_AXIS)

        createGoalsSubPanel()

        createPropertiesSubPanel()

        createSkipPluginSubPanel()

        panel.add(goalsSubPanel)
        panel.add(propertiesSubPanel)
        panel.add(optionalJvmOptionsSubPanel)
    }

    private fun canExecute(): Boolean {
        return !settingsService.currentSettings.goals.isEmpty() && !settingsService.currentSettings.projectsToBuild.isEmpty()
    }

    private fun createGoalsSubPanel() {
        runMavenButton.icon = AllIcons.General.Run
        runMavenButton.addActionListener(RunMavenActionListener(project))
        runMavenButton.isEnabled = canExecute()

        goalsComboBox.setHistory(settingsService.goalsHistory)
        goalsComboBox.initEditor("")

        val goalsEditor = goalsComboBox.editor.editorComponent as EditorTextField
        MavenArgumentsCompletionProvider(project).apply(goalsEditor)

        goalsComboBox.editor.item = settingsService.currentSettings.goalsAsText()

        goalsEditor.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent?) {
                settingsService.currentSettings.goalsFromText(goalsComboBox.editor.item.toString())

                runMavenButton.isEnabled = canExecute()
            }
        })

        goalsEditor.addFocusListener(object : FocusAdapter() {
            override fun focusLost(e: FocusEvent?) {
                val current = settingsService.currentSettings.goalsAsText()
                settingsService.goalsHistory.add(current)

                // refresh comboBox values
                goalsComboBox.setHistory(settingsService.goalsHistory)
                goalsComboBox.model.selectedItem = current
            }
        })

        val label = JLabel("Goals")

        val groupLayout = GroupLayout(goalsSubPanel)
        groupLayout.autoCreateGaps = true
        groupLayout.autoCreateContainerGaps = true

        goalsSubPanel.layout = groupLayout

        groupLayout.setHorizontalGroup(
                groupLayout.createSequentialGroup()
                        .addComponent(label)
                        .addComponent(goalsComboBox)
                        .addComponent(runMavenButton)
        )
        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(label, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, java.lang.Short.MAX_VALUE.toInt())
                                .addComponent(goalsComboBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, java.lang.Short.MAX_VALUE.toInt())
                                .addComponent(runMavenButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, java.lang.Short.MAX_VALUE.toInt())
                        )
        )
    }

    private fun createPropertiesSubPanel() {
        val innerPropertiesPanel = JPanel(GridBagLayout())

        offlineModeCheckBox.isSelected = settingsService.currentSettings.isOfflineMode
        offlineModeCheckBox.addActionListener { event ->
            settingsService.currentSettings.isOfflineMode = offlineModeCheckBox.isSelected
        }
        innerPropertiesPanel.add(offlineModeCheckBox, GridBagConstraintsBuilder().fillHorizontal().build())

        alwaysUpdateModeCheckBox.isSelected = settingsService.currentSettings.isAlwaysUpdateSnapshot
        alwaysUpdateModeCheckBox.addActionListener { settingsService.currentSettings.isAlwaysUpdateSnapshot = alwaysUpdateModeCheckBox.isSelected }
        innerPropertiesPanel.add(alwaysUpdateModeCheckBox, GridBagConstraintsBuilder().fillHorizontal().insetLeft(20).gridx(1).gridy(0).build())

        skipTestCheckBox.isSelected = settingsService.currentSettings.isSkipTests
        innerPropertiesPanel.add(skipTestCheckBox, GridBagConstraintsBuilder().fillHorizontal().gridx(0).gridy(1).build())

        skipTestCheckBox.addActionListener { settingsService.currentSettings.isSkipTests = skipTestCheckBox.isSelected }

        innerPropertiesPanel.add(threadsLabel, GridBagConstraintsBuilder().anchorWest().fillNone().insetLeft(20).gridx(1).gridy(1).build())

        threadsTextField.columns = 2
        threadsTextField.isCanBeEmpty = true
        if (settingsService.currentSettings.threadCount != null) {
            threadsTextField.value = settingsService.currentSettings.threadCount
        }
        threadsTextField.addCaretListener {
            try {
                threadsTextField.validateContent()

                settingsService.currentSettings.threadCount = threadsTextField.value
            } catch (e: ConfigurationException) {
                settingsService.currentSettings.threadCount = null
            }
        }
        innerPropertiesPanel.add(threadsTextField, GridBagConstraintsBuilder().anchorEast().fillNone().gridx(1).gridy(1).build())

        innerPropertiesPanel.maximumSize = Dimension(200, 50)

        val projectsManager = MavenProjectsManager.getInstance(project)
        //profiles.setItems(Lists.newArrayList(projectsManager.getAvailableProfiles()), a -> a);
        projectsManager.availableProfiles.forEach { profile -> profiles.addItem(profile, profile, settingsService.getCurrentSettings().getProfiles().contains(profile)) }
        profiles.setCheckBoxListListener { _, _ -> settingsService.currentSettings.profiles = profiles.selectedItemNames }

        val profilesScrollPane = ScrollPaneFactory.createScrollPane(profiles)
        profilesScrollPane.maximumSize = Dimension(1000, 80)
        profilesScrollPane.minimumSize = Dimension(0, 80)

        val propertiesGroupLayout = GroupLayout(propertiesSubPanel)
        propertiesGroupLayout.autoCreateGaps = true
        propertiesGroupLayout.autoCreateContainerGaps = true

        propertiesSubPanel.layout = propertiesGroupLayout

        propertiesGroupLayout.setHorizontalGroup(
                propertiesGroupLayout.createSequentialGroup()
                        .addComponent(innerPropertiesPanel)
                        .addComponent(profilesScrollPane)
        )
        propertiesGroupLayout.setVerticalGroup(
                propertiesGroupLayout.createSequentialGroup()
                        .addGroup(propertiesGroupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(innerPropertiesPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, java.lang.Short.MAX_VALUE.toInt())
                                .addComponent(profilesScrollPane)
                        )
        )
    }

    private fun createSkipPluginSubPanel() {
        val optionalJvmOptionsLayout = GroupLayout(optionalJvmOptionsSubPanel)
        optionalJvmOptionsLayout.autoCreateGaps = true
        optionalJvmOptionsLayout.autoCreateContainerGaps = true
        optionalJvmOptionsSubPanel.layout = optionalJvmOptionsLayout

        optionalJvmOptionsCheckBox.isSelected = settingsService.currentSettings.isUseOptionalJvmOptions

        optionalJvmOptionsCheckBox.addActionListener { event ->
            optionalJvmOptionsComboBox.isEnabled = optionalJvmOptionsCheckBox.isSelected
            settingsService.currentSettings.isUseOptionalJvmOptions = optionalJvmOptionsCheckBox.isSelected

        }

        optionalJvmOptionsComboBox.setHistory(settingsService.jvmOptionHistory)
        optionalJvmOptionsComboBox.initEditor(settingsService.currentSettings.optionalJvmOptionsAsText())
        optionalJvmOptionsComboBox.isEnabled = optionalJvmOptionsCheckBox.isSelected


        val optionalJvmOptionsEditor = optionalJvmOptionsComboBox.editor.editorComponent as EditorTextField

        optionalJvmOptionsEditor.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent?) {
                settingsService.currentSettings.optionalJvmOptions = Lists.newArrayList(*optionalJvmOptionsComboBox.editor.item.toString().split("\\s".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            }
        })

        optionalJvmOptionsEditor.addFocusListener(object : FocusAdapter() {
            override fun focusLost(e: FocusEvent?) {
                val current = settingsService.currentSettings.optionalJvmOptionsAsText()
                settingsService.jvmOptionHistory.add(current)

                // refresh comboBox values
                optionalJvmOptionsComboBox.setHistory(settingsService.jvmOptionHistory)
                optionalJvmOptionsComboBox.model.selectedItem = current
            }
        })

        optionalJvmOptionsLayout.setHorizontalGroup(
                optionalJvmOptionsLayout.createSequentialGroup()
                        .addComponent(optionalJvmOptionsCheckBox)
                        .addComponent(optionalJvmOptionsComboBox)
        )
        optionalJvmOptionsLayout.setVerticalGroup(
                optionalJvmOptionsLayout.createSequentialGroup()
                        .addGroup(optionalJvmOptionsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(optionalJvmOptionsCheckBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Int.MAX_VALUE)
                                .addComponent(optionalJvmOptionsComboBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Int.MAX_VALUE)
                        )
        )
    }

    fun updateRunButton() {
        runMavenButton.isEnabled = canExecute()
    }

    fun update() {
        updateAlwaysUpdateMode()
        updateGoals()
        updateOfflineOption()
        updateOptionalJvmOptions()
        updateProfile()
        updateThreads()
        updateSkipTestsOption()
    }

    private fun updateThreads() {
        val threadCount = settingsService.currentSettings.threadCount

        threadsTextField.value = threadCount ?: 0
    }

    private fun updateAlwaysUpdateMode() {
        alwaysUpdateModeCheckBox.isSelected = settingsService.currentSettings.isAlwaysUpdateSnapshot
    }

    private fun updateSkipTestsOption() {
        skipTestCheckBox.isSelected = settingsService.currentSettings.isSkipTests
    }

    private fun updateOfflineOption() {
        offlineModeCheckBox.isSelected = settingsService.currentSettings.isOfflineMode
    }

    private fun updateGoals() {
        goalsComboBox.editor.item = settingsService.currentSettings.goalsAsText()
    }

    private fun updateOptionalJvmOptions() {
        optionalJvmOptionsCheckBox.isSelected = settingsService.currentSettings.isUseOptionalJvmOptions
        optionalJvmOptionsComboBox.isEnabled = settingsService.currentSettings.isUseOptionalJvmOptions
        optionalJvmOptionsComboBox.editor.item = settingsService.currentSettings.optionalJvmOptionsAsText()
    }

    fun updateProfile() {
        val projectsManager = MavenProjectsManager.getInstance(project)

        profiles.clear()

        projectsManager.availableProfiles.forEach { profile -> profiles.addItem(profile, profile, settingsService.currentSettings.profiles.contains(profile)) }
    }
}
