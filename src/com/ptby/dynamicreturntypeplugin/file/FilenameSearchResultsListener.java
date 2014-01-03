package com.ptby.dynamicreturntypeplugin.file;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.Collection;

public interface FilenameSearchResultsListener {
    void respondToResults( Collection<VirtualFile> virtualFilesByName );
}
