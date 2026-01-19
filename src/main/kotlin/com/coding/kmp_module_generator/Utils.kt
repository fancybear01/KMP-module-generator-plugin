package com.coding.kmp_module_generator

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import java.io.File

fun String.toLowerName(isFolder: Boolean): String {
    val sb = StringBuilder()
    for (c in this) {
        if (c.isUpperCase()) {
            if (sb.isNotEmpty()) {
                sb.append(if (isFolder) "_" else "-")
            }
            sb.append(c.lowercaseChar())
        } else {
            sb.append(c)
        }
    }
    return sb.toString()
}

fun generateFile(
    project: Project,
    currentDir: VirtualFile,
    templateFile: File,
    fileName: String,
    values: Map<String, String>
) {
    val psiManager = PsiManager.getInstance(project)
    val basePsiDir = psiManager.findDirectory(currentDir) ?: return

    try {
        val content = TemplateLoader.generateFromTemplate(
            templateFile = templateFile,
            values = values
        )

        TemplateLoader.createPsiFile(project, basePsiDir, fileName, content)
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }

}