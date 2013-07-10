package com.ptby.dynamicreturntypeplugin.json;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;

import java.io.IOException;

public interface JsonConfigurationChangeListener {

    public void notifyJsonFileHasChanged( VirtualFile virtualFileEvent ) throws IOException;

    void notifyJsonFileIsDeleted(  VirtualFileEvent virtualFileEvent );

    void notifyProjectIsClosed( Project project );
}
