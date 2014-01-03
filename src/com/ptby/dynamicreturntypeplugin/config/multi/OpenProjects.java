package com.ptby.dynamicreturntypeplugin.config.multi;

import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

public class OpenProjects {

    private final List<Project> projects = new ArrayList<Project>();

    private boolean isAccessing = false;


    public void addProject( Project project ) {
        waitUntilAvailable();
        isAccessing = true;
        projects.add( project );
        isAccessing = false;
    }


    private void waitUntilAvailable() {
        int maxIterations = 100;
        int currentIteration = 0;

        while ( isAccessing ) {
            try {
                Thread.sleep( 1 );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }

            if ( ++currentIteration == maxIterations ) {
                throw new RuntimeException( "Exceeding lock time" );
            }
        }
    }


    public Project[] getOpenProjectsAsArray() {
        waitUntilAvailable();
        isAccessing = true;
        Project[] projectsArray = projects.toArray( new Project[ projects.size() ] );
        isAccessing = false;

        return projectsArray;
    }


    public void removeProject( Project project ) {
        waitUntilAvailable();
        isAccessing = true;
        projects.remove( project );
        isAccessing = false;
    }
}
