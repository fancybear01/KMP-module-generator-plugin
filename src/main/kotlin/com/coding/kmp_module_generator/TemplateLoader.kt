package com.coding.kmp_module_generator

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import java.io.File

object TemplateLoader {
    fun generateFromTemplate(
        templateFile: File,
        values: Map<String, String>
    ) : String {
        var content = templateFile.readText()
        for ((key, value) in values) {
            content = content.replace("{{$key}}", value)
        }
        return content.replace("\r\n", "\n")
    }
    fun createPsiFile(
        project: Project,
        directory: PsiDirectory,
        fileName: String,
        content: String
    ) {
        try {
            val fileFactory = PsiFileFactory.getInstance(project)
            val file = fileFactory.createFileFromText(
                fileName,
                FileTypeRegistry
                    .getInstance()
                    .getFileTypeByFileName(fileName),
                content
            )
            val psiFile = directory.add(file) as? PsiFile
            psiFile?.viewProvider?.document?.setText(content)
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}

fun getOrCreateSubDir(parent: PsiDirectory, name: String): PsiDirectory {
    return parent.findSubdirectory(name) ?: parent.createSubdirectory(name)
}

fun generate(project: Project, baseDir: VirtualFile) {
    val psiManager = PsiManager.getInstance(project)
    val basePsiDir = psiManager.findDirectory(baseDir) ?: return

    WriteCommandAction.runWriteCommandAction(project) {
        try {
            val myDir = getOrCreateSubDir(basePsiDir, "feature")

            val content = TemplateLoader.generateFromTemplate(
                File("C:\\kotlin\\plugins\\KMP Module Generator\\src\\main\\resources\\templates\\ScreenApi.kt.template"),
                mapOf("package" to "com.coding", "FeatureName" to "Muuu")
            )

            TemplateLoader.createPsiFile(project, myDir, "Api.kt", content)
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}