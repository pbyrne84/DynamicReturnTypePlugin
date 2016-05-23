package com.ptby.dynamicreturntypeplugin.config

import com.intellij.openapi.project.Project

class ConfigStateContainer {
    companion object {
        val configState : ConfigState = ConfigState()


        fun notifyProjectOpened(project: Project) {
            configState.openProjects.addProject(project)
            configState.configAnalyser.refreshAllConfigs()
        }


        fun notifyProjectClosed(project: Project) {
            configState.openProjects.removeProject(project)
            configState.configAnalyser.notifyProjectIsClosed(project)
        }
    }
}
