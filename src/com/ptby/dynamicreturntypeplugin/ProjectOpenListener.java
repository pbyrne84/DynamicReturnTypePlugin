package com.ptby.dynamicreturntypeplugin;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
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
        ConfigStateContainer.notifyProjectOpened( this.project );
    }


    public void projectClosed() {
        ConfigStateContainer.notifyProjectClosed( this.project );
    }
}
