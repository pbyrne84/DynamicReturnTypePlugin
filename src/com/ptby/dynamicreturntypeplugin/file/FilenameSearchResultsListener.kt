package com.ptby.dynamicreturntypeplugin.file

import com.intellij.openapi.vfs.VirtualFile

interface FilenameSearchResultsListener {
    fun respondToResults(virtualFilesByName: Collection<VirtualFile>)
}
