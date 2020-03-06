package com.ptby.dynamicreturntypeplugin.json

import com.intellij.openapi.project.Project
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfigKt
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfigKt
import com.ptby.dynamicreturntypeplugin.config.ProjectDynamicReturnTypeMap
import com.ptby.dynamicreturntypeplugin.config.multi.OpenProjects
import com.ptby.dynamicreturntypeplugin.config.multi.ProjectsConfigRefresher
import com.ptby.dynamicreturntypeplugin.config.multi.RefreshProjectCallBack

class ConfigAnalyser(private val openProjects: OpenProjects) : ProjectConfigChangeListener,
                                                                      RefreshProjectCallBack {
    private val projectDynamicReturnTypeMap = ProjectDynamicReturnTypeMap()

    private val openProjectsConfigRefresher = ProjectsConfigRefresher()


    fun getCurrentConfig(project: Project): DynamicReturnTypeConfig {
        return projectDynamicReturnTypeMap.get(project)
    }


    fun hasArrayAccessEnabled(project: Project): Boolean {
        return getCurrentConfig(project).hasArrayAccessEnabled()
    }


    fun getCurrentClassMethodConfigs(project: Project): List<ClassMethodConfigKt> {
        return projectDynamicReturnTypeMap.get(project).classMethodConfigs
    }


    fun getCurrentFunctionCallConfigs(project: Project): List<FunctionCallConfigKt> {
        return projectDynamicReturnTypeMap.get(project).functionCallConfigs
    }


    fun notifyProjectIsClosed(project: Project) {
        projectDynamicReturnTypeMap.resetProject(project)
    }


    override fun setConfig(project: Project, config: DynamicReturnTypeConfig) {
        projectDynamicReturnTypeMap.put(project, config)
    }

    override fun refreshAllConfigs() {
        val openProjectsAsArray = openProjects.getOpenProjectsAsArray()
        openProjectsConfigRefresher.refreshProjects(openProjectsAsArray, this)
    }
}
