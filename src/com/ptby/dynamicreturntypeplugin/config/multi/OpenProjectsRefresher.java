package com.ptby.dynamicreturntypeplugin.config.multi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig;
import com.ptby.dynamicreturntypeplugin.file.FilenameSearchResultsListener;
import com.ptby.dynamicreturntypeplugin.file.FilenameSearcher;

import java.io.IOException;
import java.util.Collection;

public class OpenProjectsRefresher {

    private final FilenameSearcher filenameSearcher;
    private final ProjectConfigBuilder projectConfigBuilder;

    public OpenProjectsRefresher() {
        filenameSearcher = new FilenameSearcher();
        projectConfigBuilder = new ProjectConfigBuilder();
    }

    public void refreshProjects( Project[] projects, final RefreshProjectCallBack refreshProjectCallBack ) {
        for ( final Project project : projects ) {
            filenameSearcher.findByFileName( project, "dynamicReturnTypeMeta.json", new FilenameSearchResultsListener() {
                @Override
                public void respondToResults( Collection<VirtualFile> projectConfigFiles ) {
                    try {
                        DynamicReturnTypeConfig configFromFileList = projectConfigBuilder.createConfigFromFileList(
                                projectConfigFiles
                        );

                        refreshProjectCallBack.setConfig( project, configFromFileList );
                    } catch ( IOException e ) {
                        e.printStackTrace();
                    }
                }
            } );
        }
    }


}
