package com.ptby.dynamicreturntypeplugin.json;

import com.intellij.openapi.vfs.*;

import java.util.ArrayList;
import java.util.List;

public class FileSytemConfigChangeListener implements VirtualFileListener {
    private static final String expectedConfigFileName = "dynamicReturnTypeMeta.json";
    private List<ProjectConfigChangeListener> projectConfigChangeListeners = new ArrayList<ProjectConfigChangeListener>();

    public FileSytemConfigChangeListener(){
        LocalFileSystem instance = LocalFileSystem.getInstance();
        instance.addVirtualFileListener( this );
    }


    @Override
    public void propertyChanged( VirtualFilePropertyEvent event ) {
    }

    public void registerProjectConfigChangeListener( ProjectConfigChangeListener projectConfigChangeListener ) {
        projectConfigChangeListeners.add( projectConfigChangeListener );
    }


    @Override
    public void contentsChanged( VirtualFileEvent event ) {
        refreshIfCorrectFileEvent( event );
    }


    private void refreshIfCorrectFileEvent( VirtualFileEvent virtualFileEvent ) {
        String currentFileName = virtualFileEvent.getFileName();

        if ( currentFileName.equals( expectedConfigFileName ) ) {
            refreshConfigs();
        }
    }


    private void refreshConfigs() {
        for ( ProjectConfigChangeListener projectConfigChangeListener : projectConfigChangeListeners ) {
            projectConfigChangeListener.refreshAllConfigs();
        }
    }


    @Override
    public void fileCreated( VirtualFileEvent event ) {
        refreshIfCorrectFileEvent( event );
    }

    @Override
    public void fileDeleted( VirtualFileEvent event ) {
        refreshIfCorrectFileEvent( event );
    }

    @Override
    public void fileMoved( VirtualFileMoveEvent event ) {
        refreshIfCorrectFileEvent( event );
    }

    @Override
    public void fileCopied( VirtualFileCopyEvent event ) {
        refreshIfCorrectFileEvent( event );
    }

    @Override
    public void beforePropertyChange( VirtualFilePropertyEvent event ) {

    }

    @Override
    public void beforeContentsChange( VirtualFileEvent event ) {

    }

    @Override
    public void beforeFileDeletion( VirtualFileEvent event ) {

    }

    @Override
    public void beforeFileMovement( VirtualFileMoveEvent event ) {

    }
}
