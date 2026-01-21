package com.coding.kmp_module_generator

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class ApiModuleGenerator(project: Project) : BaseModuleGenerator(project) {
    fun generateModuleStructure(
        moduleDir: VirtualFile,
        moduleName: String,
        packageName: String
    ) {
        val moduleDirApi = createModuleDir(
            moduleDir = moduleDir,
            moduleName = moduleName,
            suffix = "api",
            moduleLabel = "API module"
        ) ?: return
        val packageDir = buildPackageDir(moduleDirApi, packageName) ?: return
        ensureGitignore(moduleDirApi)

        // Генерируем файлы
        generateFile(
            project = project,
            currentDir = packageDir,
            templatePath = "templates/FeatureApi.kt.template",
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
            templatePath = "templates/GradleApi.kts.template",
            fileName = "build.gradle.kts",
            values = mapOf(
                "package" to packageName,
                "feature_name" to moduleName.toLowerName(isFolder = true)
            )
        )
    }
}
