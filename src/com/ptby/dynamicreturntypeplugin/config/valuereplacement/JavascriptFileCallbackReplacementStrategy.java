package com.ptby.dynamicreturntypeplugin.config.valuereplacement;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.ptby.dynamicreturntypeplugin.config.valuereplacement.replacementexecutors.JavascriptReplacementExecutor;

import javax.script.ScriptException;
import java.io.IOException;

import com.intellij.openapi.diagnostic.Logger;

public class JavascriptFileCallbackReplacementStrategy implements ValueReplacementStrategy {
    private final String className;
    private final String methodName;
    private final String javascriptFunctionCall;
    private final String absoluteJavaScriptFileLocationPath;
    private JavascriptReplacementExecutor javascriptReplacementExecutor;


    public JavascriptFileCallbackReplacementStrategy( VirtualFile configFile,
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
        final Logger log = Logger.getInstance( "DynamicReturnTypePlugin" );
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
                            log.warn(
                                    "Could not read javascript call back file " + absoluteJavaScriptFileLocationPath +
                                            "\n" + e.getMessage(),
                                    e
                            );
                        } catch ( ScriptException e ) {
                            log.warn(
                                    "Could not evaluate javascript call back file " + absoluteJavaScriptFileLocationPath +
                                            "\n" + e.getMessage(),
                                    e
                            );
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
