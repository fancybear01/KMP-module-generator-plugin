package com.coding.kmp_module_generator

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.panels.VerticalLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class GenerateModuleDialog(project: Project): DialogWrapper(project) {
    private val panel = JPanel(VerticalLayout(15))
    private val nameField = JTextField()
    private val packageField = JTextField("com.example")

    init {
        title = "Generate KMP Module"
        panel.add(JLabel("Module name (kebab-case):"))
        panel.add(nameField)
        panel.add(JLabel("Package name:"))
        panel.add(packageField)
        init()
    }

    override fun createCenterPanel() = panel

    fun getModuleName() = nameField.text.trim()
    fun getPackageName() = packageField.text.trim()
}