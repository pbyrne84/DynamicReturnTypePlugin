package com.ptby.dynamicreturntypeplugin.config.valuereplacement;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.ptby.dynamicreturntypeplugin.config.valuereplacement.replacementexecutors.JavascriptReplacementExecutor;

import java.io.IOException;

public class JavascriptFileCallbackReplaceStrategy implements ValueReplacementStrategy {
    private final String className;
    private final String methodName;
    private final String javascriptFunctionCall;
    private final String absoluteJavaScriptFileLocationPath;
    private JavascriptReplacementExecutor javascriptReplacementExecutor;


    public JavascriptFileCallbackReplaceStrategy( VirtualFile configFile,
                                                  String className,
                                                  String methodName,
                                                  String fileName,
                                                  String javascriptFunctionCall ) {
        this.className = className;
        this.methodName = methodName;
        this.javascriptFunctionCall = javascriptFunctionCall;

        this.absoluteJavaScriptFileLocationPath = configFile.getParent().getCanonicalPath() + "/" + fileName;

        loadJavascript();
    }


    private void loadJavascript() {
        ApplicationManager.getApplication().invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        VirtualFile fileByPath =
                                LocalFileSystem.getInstance().findFileByPath( absoluteJavaScriptFileLocationPath );
                        if ( fileByPath == null ) {
                            return;
                        }

                        try {
                            String script = new String( fileByPath.contentsToByteArray() );
                            javascriptReplacementExecutor = new JavascriptReplacementExecutor(
                                    className,
                                    methodName,
                                    script,
                                    javascriptFunctionCall
                            );

                        } catch ( IOException e ) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }


    @Override
    public String replaceCalculatedValue( String currentValue ) {
        if ( javascriptReplacementExecutor == null ) {
            return currentValue;
        }

        return javascriptReplacementExecutor.executeAndReplace( currentValue );
    }



}
