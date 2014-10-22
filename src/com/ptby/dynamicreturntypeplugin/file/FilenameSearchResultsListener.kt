package com.ptby.dynamicreturntypeplugin.file

import com.intellij.openapi.vfs.VirtualFile

public trait FilenameSearchResultsListener {
    public fun respondToResults(virtualFilesByName: Collection<VirtualFile>)
}
