package com.ptby.dynamicreturntypeplugin;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.ptby.dynamicreturntypeplugin.config.ConfigStateContainer;
import org.jetbrains.annotations.NotNull;

public class ProjectOpenListener implements ProjectComponent {
    private final Project project;


    public ProjectOpenListener( Project project ) {
        this.project = project;
    }


    public void initComponent() {
    }


    public void disposeComponent() {
    }


    @NotNull
    public String getComponentName() {
        return "ProjectOpenerListener";
    }


    public void projectOpened() {
        final StartupManager startupManager = StartupManager.getInstance( project );
        Runnable postInitialisationCallBack = new Runnable() {
            @Override
            public void run() {
                ConfigStateContainer.OBJECT$.notifyProjectOpened( project );
            }
        };

        startupManager.runWhenProjectIsInitialized( postInitialisationCallBack );
    }


    public void projectClosed() {
        ConfigStateContainer.OBJECT$.notifyProjectClosed( this.project );
    }
}
