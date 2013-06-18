package com.ptby.dynamicreturntypeplugin.json;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileCopyEvent;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileMoveEvent;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;
import com.intellij.openapi.project.Project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonFileSystemChangeListener implements VirtualFileListener {

    private List<JsonConfigurationChangeListener> jsonConfigurationChangeListeners = new ArrayList<JsonConfigurationChangeListener>();
    private Project currentProject;


    public JsonFileSystemChangeListener(  ) {
        LocalFileSystem instance = LocalFileSystem.getInstance();
        instance.addVirtualFileListener( this );
    }


    public void registerChangeListener( JsonConfigurationChangeListener jsonConfigurationChangeListener ) {
        jsonConfigurationChangeListeners.add( jsonConfigurationChangeListener );
    }

    public void setCurrentProject( Project currentProject ){
        if( this.currentProject != null ){
            return;
        }

        VirtualFile metaFile = LocalFileSystem.getInstance().findFileByPath( currentProject
                .getBasePath() + File.separatorChar + "dynamicReturnTypeMeta.json"
        );

        notifyOfConfigUpdate( metaFile );
        this.currentProject = currentProject;
    }


    @Override
    public void propertyChanged( VirtualFilePropertyEvent virtualFilePropertyEvent ) {

    }


    @Override
    public void contentsChanged( VirtualFileEvent virtualFileEvent ) {
        if( virtualFileEvent.getFileName().equals( "dynamicReturnTypeMeta.json" ) ) {
            notifyOfConfigUpdate( virtualFileEvent.getFile() );
        }
    }


    private void notifyOfConfigUpdate( VirtualFile virtualFile ) {
        for ( JsonConfigurationChangeListener jsonConfigurationChangeListener : jsonConfigurationChangeListeners ) {
            try {
                jsonConfigurationChangeListener.notifyJsonFileHasChanged( virtualFile);
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void fileCreated( VirtualFileEvent virtualFileEvent ) {

    }


    @Override
    public void fileDeleted( VirtualFileEvent virtualFileEvent ) {

    }


    @Override
    public void fileMoved( VirtualFileMoveEvent virtualFileMoveEvent ) {

    }


    @Override
    public void fileCopied( VirtualFileCopyEvent virtualFileCopyEvent ) {

    }


    @Override
    public void beforePropertyChange( VirtualFilePropertyEvent virtualFilePropertyEvent ) {

    }


    @Override
    public void beforeContentsChange( VirtualFileEvent virtualFileEvent ) {

    }


    @Override
    public void beforeFileDeletion( VirtualFileEvent virtualFileEvent ) {

    }


    @Override
    public void beforeFileMovement( VirtualFileMoveEvent virtualFileMoveEvent ) {

    }
}
