package com.coding.kmp_module_generator

import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
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