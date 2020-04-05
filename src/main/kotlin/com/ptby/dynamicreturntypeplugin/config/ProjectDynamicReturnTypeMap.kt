package com.ptby.dynamicreturntypeplugin.config

import com.intellij.openapi.project.Project

import java.util.concurrent.ConcurrentHashMap

class ProjectDynamicReturnTypeMap : ConcurrentHashMap<String, DynamicReturnTypeConfig>() {

    fun get(project: Project): DynamicReturnTypeConfig {
        val key = project.baseDir.toString()
        val dynamicReturnTypeConfig = get(key)
                ?: return DynamicReturnTypeConfig.newEmpty()

        return dynamicReturnTypeConfig
    }


    fun put(project: Project, dynamicReturnTypeConfig: DynamicReturnTypeConfig) {
        val key = project.baseDir.toString()
        put(key, dynamicReturnTypeConfig)
    }


    fun resetProject(project: Project) {
        val key = project.baseDir.toString()
        put(key, DynamicReturnTypeConfig.newEmpty())
    }
}
