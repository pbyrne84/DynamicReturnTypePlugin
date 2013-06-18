package com.ptby.dynamicreturntypeplugin.json;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;

import java.io.IOException;

public interface JsonConfigurationChangeListener {

    public void notifyJsonFileHasChanged( VirtualFile virtualFileEvent ) throws IOException;
}
