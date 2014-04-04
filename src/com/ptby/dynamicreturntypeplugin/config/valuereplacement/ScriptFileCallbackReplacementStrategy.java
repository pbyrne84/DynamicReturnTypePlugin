package com.ptby.dynamicreturntypeplugin.config.valuereplacement;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.ptby.dynamicreturntypeplugin.scripting.ScriptReplacementExecutor;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;

import com.intellij.openapi.diagnostic.Logger;

public class ScriptFileCallbackReplacementStrategy implements ValueReplacementStrategy {
    private final String className;
    private final String methodName;
    private final String scriptFileName;
    private final String javascriptFunctionCall;
    private final String absoluteJavaScriptFileLocationPath;
    private ScriptReplacementExecutor scriptReplacementExecutor;


    public ScriptFileCallbackReplacementStrategy( VirtualFile configFile,
                                                  String className,
                                                  String methodName,
                                                  String scriptFileName,
                                                  String javascriptFunctionCall ) {
        this.className = className;
        this.methodName = methodName;
        this.scriptFileName = scriptFileName;
        this.javascriptFunctionCall = javascriptFunctionCall;

        this.absoluteJavaScriptFileLocationPath = configFile.getParent().getCanonicalPath() + "/" + scriptFileName;

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
                            scriptReplacementExecutor = new ScriptReplacementExecutor(
                                    calculateScriptType(),
                                    className,
                                    methodName,
                                    absoluteJavaScriptFileLocationPath,
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


    private String calculateScriptType() {
        String scriptExtension = "";
        int i = scriptFileName.lastIndexOf('.');
        if (i > 0) {
             scriptExtension = scriptFileName.substring(i+1).toLowerCase();
        }
        if ( scriptExtension.equals( "groovy" ) ) {
            return ScriptReplacementExecutor.SCRIPT_LANGUAGE_GROOVY;
        }

        return ScriptReplacementExecutor.SCRIPT_LANGUAGE_JAVASCRIPT;
    }


    @Override
    public String replaceCalculatedValue( String currentValue ) {
        if ( scriptReplacementExecutor == null ) {
            return currentValue;
        }

        return scriptReplacementExecutor.executeAndReplace( currentValue );
    }


}
