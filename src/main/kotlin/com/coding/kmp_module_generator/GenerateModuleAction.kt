package com.coding.kmp_module_generator

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager

internal class GenerateModuleAction : AnAction() {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val isFolder = file?.isDirectory == true
        e.presentation.isEnabledAndVisible = isFolder
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val g = GenerateModuleDialog(project)
        g.showAndGet()
        generate(project, project.guessProjectDir()!!)
    }
}