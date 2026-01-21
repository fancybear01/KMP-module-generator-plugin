package com.coding.kmp_module_generator

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager

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
    templatePath: String,
    fileName: String,
    values: Map<String, String>
) {
    require(fileName.isNotBlank()) { "fileName cannot be blank" }

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
        val templateContent = TemplateLoader.loadTemplate(templatePath)
        val content = TemplateLoader.generateFromTemplate(
            templateContent = templateContent,
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

fun addModulesToSettings(
    project: Project,
    projectDir: VirtualFile,
    moduleName: String
) {
    try {
        val settingsFile = projectDir.findFile("settings.gradle.kts") ?: return
        val documentManager = FileDocumentManager.getInstance()
        val document = documentManager.getDocument(settingsFile) ?: return
        val modulePath = ":features:${moduleName.toLowerName(isFolder = false)}:${moduleName.toLowerName(isFolder = false)}"
        val lineToAddApi = "include(\"${modulePath}-api\")\n"
        val lineToAddImpl = "include(\"${modulePath}-impl\")\n"
        document.insertString(document.textLength, lineToAddApi)
        document.insertString(document.textLength, lineToAddImpl)
        documentManager.saveDocument(document)
    } catch (e: Exception) {
        showNotification(project, "settngs.gradle.kts", "No modules added to settngs.gradle.kts", NotificationType.WARNING)
    }
}
