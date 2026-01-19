package com.coding.kmp_module_generator

import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.guessProjectDir

internal class GenerateModuleAction : AnAction() {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val isFolder = file?.isDirectory == true
        e.presentation.isEnabledAndVisible = isFolder
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val generateModuleDialog = GenerateModuleDialog(project)

        if (generateModuleDialog.showAndGet()) {
            try {
                WriteCommandAction.runWriteCommandAction(project) {

                    val moduleName = generateModuleDialog.getModuleName()
                    val packageName = generateModuleDialog.getPackageName()

                    val projectDir = project.guessProjectDir()!!
                    val featureDir = projectDir.createChildDirectory(null, "features")
                    val moduleDir = featureDir.createChildDirectory(null, moduleName.toLowerName(isFolder = false))

                    val generatorApi = ApiModuleGenerator(project)

                    generatorApi.generateModuleStructure(
                        moduleDir = moduleDir,
                        moduleName = moduleName,
                        packageName = packageName
                    )

                    val generatorImpl = ImplModuleGenerator(project)
                    generatorImpl.generateModuleStructure(
                        moduleDir = moduleDir,
                        moduleName = moduleName,
                        packageName = packageName
                    )

                    showNotification(
                        project = project,
                        title = "KMP Module Generator",
                        content = "Module '$moduleName' has been generated successfully.",
                        type = NotificationType.INFORMATION
                    )
                }
            } catch (e: Exception) {
                showNotification(
                    project = project,
                    title = "KMP Module Generator",
                    content = "Module has not been generated: ${e.message}",
                    type = NotificationType.ERROR
                )
            }
        } else {
            generateModuleDialog.doCancelAction()
        }
    }
}