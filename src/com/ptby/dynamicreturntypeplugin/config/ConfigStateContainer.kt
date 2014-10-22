package com.ptby.dynamicreturntypeplugin.config

import com.intellij.openapi.project.Project

public class ConfigStateContainer {
    class object {
        val configState : ConfigState = ConfigState()


        public fun notifyProjectOpened(project: Project) {
            configState.openProjects.addProject(project)
            configState.configAnalyser.refreshAllConfigs()
        }


        public fun notifyProjectClosed(project: Project) {
            configState.openProjects.removeProject(project)
            configState.configAnalyser.notifyProjectIsClosed(project)
        }
    }
}
