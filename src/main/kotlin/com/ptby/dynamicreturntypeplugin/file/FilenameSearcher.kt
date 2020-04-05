package com.ptby.dynamicreturntypeplugin.file

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.ProjectAndLibrariesScope
import java.util.concurrent.Executors

class FilenameSearcher {

    fun findByFileName(project: Project, filename: String, resultsListener: FilenameSearchResultsListener) {
        val executorService = Executors.newSingleThreadExecutor()

        executorService.execute(object : Runnable {
            override fun run() {
                val files = ApplicationManager.getApplication().runReadAction(
                        object : Computable<Collection<VirtualFile>> {
                            override fun compute(): Collection<VirtualFile>? {
                                if (project.isDisposed) {
                                    return null
                                }

                                val virtualFilesByName = FilenameIndex.getVirtualFilesByName(
                                        project,
                                        filename,
                                        ProjectAndLibrariesScope(project)
                                )
                                return virtualFilesByName
                            }
                        })

                resultsListener.respondToResults(files)
            }
        })
    }
}
