package com.coding.kmp_module_generator

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.components.panels.VerticalLayout
import java.awt.Dimension
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.event.DocumentEvent

class GenerateModuleDialog(project: Project): DialogWrapper(project) {

    private val panel = JPanel(VerticalLayout(15))
    private val nameField = JTextField("MyMainScreen")
    private val packageField = JTextField("com.example")

    init {
        title = "Generate KMP Module"
        panel.preferredSize = Dimension(350, 150)
        panel.add(JLabel("Module name:"))
        panel.add(nameField)
        panel.add(JLabel("Package name:"))
        panel.add(packageField)

        nameField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                initValidation()
            }
        })

        packageField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                initValidation()
            }
        })

        init()
    }

    override fun createCenterPanel() = panel

    override fun doValidate(): ValidationInfo? {
        val name = getModuleName()
        val packageName = getPackageName()

        if (name.isEmpty()) {
            return ValidationInfo("Module name cannot be empty", nameField)
        }

        if (packageName.isEmpty()) {
            return ValidationInfo("Package name cannot be empty", packageField)
        }

        return null
    }

    fun getModuleName() = nameField.text.trim()
    fun getPackageName() = packageField.text.trim()
}