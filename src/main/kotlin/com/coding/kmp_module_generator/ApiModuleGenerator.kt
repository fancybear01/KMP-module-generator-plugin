package com.coding.kmp_module_generator

import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

class ApiModuleGenerator(private val project: Project) {
    fun generateModuleStructure(
        moduleDir: VirtualFile,
        moduleName: String,
        packageName: String
    ) {
        val moduleDirApi = createModuleApiDir(moduleDir, moduleName) ?: return
        val packageDir = buildPackageDir(moduleDirApi, packageName) ?: return
        ensureGitignore(moduleDirApi)

        // Генерируем файлы
        generateFile(
            project = project,
            currentDir = packageDir,
            templateFile = File("C:\\kotlin\\plugins\\KMP Module Generator\\src\\main\\resources\\templates\\FeatureApi.kt.template"),
            fileName = "${moduleName}Api.kt",
            values = mapOf(
                "package" to packageName,
                "FeatureName" to moduleName,
                "featureName" to moduleName.replaceFirstChar { it.lowercaseChar() },
                "feature_name" to moduleName.toLowerName(isFolder = true)
            )
        )

        generateFile(
            project = project,
            currentDir = moduleDirApi,
            templateFile = File("C:\\kotlin\\plugins\\KMP Module Generator\\src\\main\\resources\\templates\\GradleApi.kts.template"),
            fileName = "build.gradle.kts",
            values = mapOf(
                "package" to packageName,
                "feature_name" to moduleName.toLowerName(isFolder = true)
            )
        )
    }

    private fun createModuleApiDir(moduleDir: VirtualFile, moduleName: String): VirtualFile? {
        val dirName = "${moduleName.toLowerName(isFolder = false)}-api"
        val existing = moduleDir.findChild(dirName)
        if (existing != null) {
            showNotification(
                project = project,
                title = "KMP Module Generator",
                content = "API module '$dirName' already exists. Generation skipped.",
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

    private fun buildPackageDir(moduleDirApi: VirtualFile, packageName: String): VirtualFile? {
        return try {
            val srcDir = moduleDirApi.createChildDirectoryIfNotExists(null, "src")
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

    private fun ensureGitignore(dir: VirtualFile) {
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