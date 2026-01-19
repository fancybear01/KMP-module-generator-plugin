package com.coding.kmp_module_generator

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

class ApiModuleGenerator(private val project: Project) {
    fun generateModuleStructure(
        moduleDir: VirtualFile,
        moduleName: String,
        packageName: String
    ) {

        val moduleDirApi = moduleDir.createChildDirectory(null, "${moduleName.toLowerName(isFolder = false)}-api")

        val srcDir = moduleDirApi.createChildDirectory(null, "src")
        val commonMainDir = srcDir.createChildDirectory(null, "commonMain")
        val kotlinDir = commonMainDir.createChildDirectory(null, "kotlin")
        var currentDir = kotlinDir

        val parts = packageName.split(".")
        for (part in parts) {
            currentDir = currentDir.createChildDirectory(null, part)
        }

        // Генерируем файлы
        generateFile(
            project = project,
            currentDir = currentDir,
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
}