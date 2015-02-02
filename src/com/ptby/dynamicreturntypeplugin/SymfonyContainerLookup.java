package com.ptby.dynamicreturntypeplugin;

import com.intellij.openapi.project.Project;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SymfonyContainerLookup {

    private  Class<?> symfonyContainerResolver = null;


    public SymfonyContainerLookup() {
        try {
            symfonyContainerResolver = Class.forName(
                    "fr.adrienbrault.idea.symfony2plugin.stubs.ContainerCollectionResolver"
            );
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
        }

    }


    public boolean isEnabled(){
        return symfonyContainerResolver != null;
    }


    String lookup( Project project,  String serviceName)  {

        if ( !isEnabled() ) {
            return null;
        }

        try {
            Method resolveService = symfonyContainerResolver.getMethod( "resolveService", Project.class, String.class );
            return ( String ) resolveService.invoke( null, project, serviceName );
        } catch ( NoSuchMethodException e ) {
            return null;
        } catch ( IllegalAccessException e ) {
            return null;
        } catch ( InvocationTargetException e ) {
            return null;
        }
    }
}
