package com.coding.kmp_module_generator

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import java.io.File

private const val NOTIFICATION_GROUP_ID = "GenerateModuleAction.Notifications"

fun String.toLowerName(isFolder: Boolean): String {
    require(isNotBlank()) { "Name cannot be blank" }
    val separator = if (isFolder) "_" else "-"
    val sepChar = separator.first()
    val sb = StringBuilder()
    for (c in trim()) {
        when {
            c.isUpperCase() -> {
                if (sb.isNotEmpty() && sb.last() != sepChar) sb.append(separator)
                sb.append(c.lowercaseChar())
            }
            c.isLowerCase() || c.isDigit() -> sb.append(c)
            c == ' ' || c == '-' || c == '_' || c == '.' -> {
                if (sb.isNotEmpty() && sb.last() != sepChar) sb.append(separator)
            }
            else -> continue
        }
    }
    while (sb.isNotEmpty() && sb.last() == sepChar) sb.deleteCharAt(sb.lastIndex)
    require(sb.isNotEmpty()) { "Name contains no valid characters" }
    return sb.toString()
}

fun generateFile(
    project: Project,
    currentDir: VirtualFile,
    templateFile: File,
    fileName: String,
    values: Map<String, String>
) {
    require(fileName.isNotBlank()) { "fileName cannot be blank" }
    if (!templateFile.exists()) {
        showNotification(project, "File generation failed", "Template file ${templateFile.name} not found", NotificationType.ERROR)
        return
    }

    val psiManager = PsiManager.getInstance(project)
    val basePsiDir = psiManager.findDirectory(currentDir)
    if (basePsiDir == null) {
        showNotification(project, "File generation failed", "Target directory is not available", NotificationType.ERROR)
        return
    }
    if (basePsiDir.findFile(fileName) != null) {
        showNotification(project, "File already exists", "$fileName already exists in the target directory", NotificationType.WARNING)
        return
    }

    try {
        val content = TemplateLoader.generateFromTemplate(
            templateFile = templateFile,
            values = values
        )

        TemplateLoader.createPsiFile(project, basePsiDir, fileName, content)
    } catch (e: Exception) {
        showNotification(project, "File generation failed", e.message ?: "Unexpected error", NotificationType.ERROR)
    }
}

fun showNotification(project: Project?, title: String, content: String, type: NotificationType) {
    val notification = try {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP_ID)
            .createNotification(title, content, type)
    } catch (_: Exception) {
        Notification(NOTIFICATION_GROUP_ID, title, content, type)
    }
    Notifications.Bus.notify(notification, project)
}

fun VirtualFile.createChildDirectoryIfNotExists(
    requestor: Any?,
    name: String
): VirtualFile {
    return this.findChild(name) ?: this.createChildDirectory(requestor, name)
}