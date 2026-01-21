package com.coding.kmp_module_generator

import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory

object TemplateLoader {
    fun loadTemplate(resourcePath: String): String {
        val stream = this::class.java.classLoader.getResourceAsStream(resourcePath)
            ?: error("Template '$resourcePath' not found in resources")
        return stream.bufferedReader().use { it.readText() }
    }

    fun generateFromTemplate(
        templateContent: String,
        values: Map<String, String>
    ) : String {
        var content = templateContent
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