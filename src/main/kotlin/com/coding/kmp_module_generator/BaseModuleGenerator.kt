package com.coding.kmp_module_generator

import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

abstract class BaseModuleGenerator(
    protected val project: Project
) {
    protected fun createModuleDir(
        moduleDir: VirtualFile,
        moduleName: String,
        suffix: String,
        moduleLabel: String
    ): VirtualFile? {
        val dirName = "${moduleName.toLowerName(isFolder = false)}-$suffix"
        val existing = moduleDir.findChild(dirName)
        if (existing != null) {
            showNotification(
                project = project,
                title = "KMP Module Generator",
                content = "$moduleLabel '$dirName' already exists. Generation skipped.",
                type = NotificationType.WARNING
            )
            return null
        }

        return try {
            moduleDir.createChildDirectory(null, dirName)
        } catch (e: Exception) {
            showNotification(
                project = project,
                title = "KMP Module Generator",
                content = "Failed to create directory '$dirName': ${e.message}",
                type = NotificationType.ERROR
            )
            null
        }
    }

    protected fun buildPackageDir(
        rootDir: VirtualFile,
        packageName: String
    ): VirtualFile? {
        return try {
            val srcDir = rootDir.createChildDirectoryIfNotExists(null, "src")
            val commonMainDir = srcDir.createChildDirectoryIfNotExists(null, "commonMain")
            val kotlinDir = commonMainDir.createChildDirectoryIfNotExists(null, "kotlin")
            packageName.split('.').fold(kotlinDir) { acc, part ->
                acc.createChildDirectoryIfNotExists(null, part)
            }
        } catch (e: Exception) {
            showNotification(
                project = project,
                title = "KMP Module Generator",
                content = "Failed to prepare package structure: ${e.message}",
                type = NotificationType.ERROR
            )
            null
        }
    }

    protected fun ensureGitignore(dir: VirtualFile) {
        val name = ".gitignore"
        if (dir.findChild(name) != null) return
        try {
            dir.createChildData(null, name).setBinaryContent("/build".toByteArray())
        } catch (e: Exception) {
            showNotification(
                project = project,
                title = "KMP Module Generator",
                content = "Failed to create $name: ${e.message}",
                type = NotificationType.ERROR
            )
        }
    }
}
