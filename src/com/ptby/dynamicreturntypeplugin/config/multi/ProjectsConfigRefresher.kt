package com.ptby.dynamicreturntypeplugin.config.multi

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.ptby.dynamicreturntypeplugin.file.FilenameSearchResultsListener
import com.ptby.dynamicreturntypeplugin.file.FilenameSearcher
import java.io.IOException

class ProjectsConfigRefresher {

    private val filenameSearcher: FilenameSearcher
    private val projectConfigBuilder: ProjectConfigBuilder

    init {
        filenameSearcher = FilenameSearcher()
        projectConfigBuilder = ProjectConfigBuilder()
    }

    fun refreshProjects(projects: Array<Project>, refreshProjectCallBack: RefreshProjectCallBack) {
        for (project in projects) {
            val resultsListener = object : FilenameSearchResultsListener {
                override fun respondToResults(virtualFilesByName: Collection<VirtualFile>) {
                    try {
                        val configFromFileList = projectConfigBuilder.createConfigFromFileList(virtualFilesByName)

                        refreshProjectCallBack.setConfig(project, configFromFileList)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            filenameSearcher.findByFileName(project, "dynamicReturnTypeMeta.json", resultsListener)
        }
    }


}
