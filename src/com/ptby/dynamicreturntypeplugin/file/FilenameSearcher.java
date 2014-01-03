package com.ptby.dynamicreturntypeplugin.file;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.ProjectAndLibrariesScope;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FilenameSearcher {

    public void findByFileName( final Project project, final String filename, final FilenameSearchResultsListener resultsListener ) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute( new Runnable() {
            @Override
            public void run() {
                while ( !project.isInitialized() ) {
                    try {
                        Thread.sleep( 10 );
                    } catch ( InterruptedException e ) {
                        //e.printStackTrace();
                    }
                }
                final Collection<VirtualFile> files = ApplicationManager.getApplication().runReadAction(new Computable<Collection<VirtualFile>>() {
                    @Nullable
                    @Override
                    public Collection<VirtualFile> compute() {
                        if (  project.isDisposed()) {
                            return null;
                        }


                        return FilenameIndex.getVirtualFilesByName( project, filename, new ProjectAndLibrariesScope( project, true ) );
                    }
                }
                );

                resultsListener.respondToResults( files );
            }
        }
        );


    }
}
