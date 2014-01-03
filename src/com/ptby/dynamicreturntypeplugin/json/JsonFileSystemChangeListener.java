package com.ptby.dynamicreturntypeplugin.json;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileCopyEvent;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileMoveEvent;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.openapi.diagnostic.Logger.getInstance;

@Deprecated
public class JsonFileSystemChangeListener implements VirtualFileListener {

    private List<JsonConfigurationChangeListener> jsonConfigurationChangeListeners = new ArrayList<JsonConfigurationChangeListener>();
    private com.intellij.openapi.diagnostic.Logger logger = getInstance( "DynamicReturnTypePlugin" );


    public JsonFileSystemChangeListener() {
        LocalFileSystem instance = LocalFileSystem.getInstance();
        instance.addVirtualFileListener( this );
    }


    public void registerChangeListener( JsonConfigurationChangeListener jsonConfigurationChangeListener ) {
        jsonConfigurationChangeListeners.add( jsonConfigurationChangeListener );
    }


    @Override
    public void propertyChanged( VirtualFilePropertyEvent virtualFilePropertyEvent ) {

    }


    @Override
    public void contentsChanged( VirtualFileEvent virtualFileEvent ) {
        notifyOnUpdateIfCorrectFile( virtualFileEvent );
    }


    private void notifyOnUpdateIfCorrectFile( VirtualFileEvent virtualFileEvent ) {
        if ( virtualFileEvent.getFileName().equals( "dynamicReturnTypeMeta.json" ) ) {
            notifyOfConfigUpdate( virtualFileEvent.getFile() );
        }
    }


    private void notifyOfDeletionIfCorrectFile( VirtualFileEvent virtualFileEvent ) {
        if ( virtualFileEvent.getFileName().equals( "dynamicReturnTypeMeta.json" ) ) {
            notifyOfConfigDeletion( virtualFileEvent );
        }
    }


    public void notifyProjectOpened( Project currentProject ) {
        String basePath = currentProject.getBasePath();
        String jsonFilePath = basePath + File.separatorChar + "dynamicReturnTypeMeta.json";
        VirtualFile metaFile = LocalFileSystem.getInstance().findFileByPath( jsonFilePath );
        if ( metaFile == null ) {
            return;
        }
        notifyOfConfigUpdate( metaFile );
    }


    private void notifyOfConfigUpdate( VirtualFile virtualFile ) {
        for ( JsonConfigurationChangeListener jsonConfigurationChangeListener : jsonConfigurationChangeListeners ) {
            try {
                jsonConfigurationChangeListener.notifyJsonFileHasChanged( virtualFile );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }


    private void notifyOfConfigDeletion( VirtualFileEvent virtualFileEvent ) {
        for ( JsonConfigurationChangeListener jsonConfigurationChangeListener : jsonConfigurationChangeListeners ) {
            jsonConfigurationChangeListener.notifyJsonFileIsDeleted( virtualFileEvent );
        }
    }


    @Override
    public void fileCreated( VirtualFileEvent virtualFileEvent ) {
        notifyOnUpdateIfCorrectFile( virtualFileEvent );
    }


    @Override
    public void fileDeleted( VirtualFileEvent virtualFileEvent ) {
        notifyOfDeletionIfCorrectFile( virtualFileEvent );
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


    public void notifyProjectClosed( Project project ) {
        for ( JsonConfigurationChangeListener jsonConfigurationChangeListener : jsonConfigurationChangeListeners ) {
            jsonConfigurationChangeListener.notifyProjectIsClosed( project );
        }
    }
}
