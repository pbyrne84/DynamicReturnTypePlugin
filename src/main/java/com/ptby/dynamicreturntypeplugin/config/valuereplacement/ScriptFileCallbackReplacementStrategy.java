package com.ptby.dynamicreturntypeplugin.config.valuereplacement;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.ptby.dynamicreturntypeplugin.scripting.CallableScriptConfiguration;
import com.ptby.dynamicreturntypeplugin.scripting.CustomScriptEngineFactory;
import com.ptby.dynamicreturntypeplugin.scripting.ScriptReplacementExecutor;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;

public class ScriptFileCallbackReplacementStrategy implements ValueReplacementStrategy {
    private final ScriptEngineManager scriptEngineManager;

    private final String className;
    private final String methodName;
    private final String scriptFileName;
    private final String javascriptFunctionCall;
    private final String absoluteJavaScriptFileLocationPath;
    private ScriptReplacementExecutor scriptReplacementExecutor;


    public ScriptFileCallbackReplacementStrategy( ScriptEngineManager scriptEngineManager,
                                                  String scriptParentDirectory,
                                                  String className,
                                                  String methodName,
                                                  String scriptFileName,
                                                  String javascriptFunctionCall ) {
        this.scriptEngineManager = scriptEngineManager;
        this.className = className;
        this.methodName = methodName;
        this.scriptFileName = scriptFileName;
        this.javascriptFunctionCall = javascriptFunctionCall;

        this.absoluteJavaScriptFileLocationPath = scriptParentDirectory + "/" + scriptFileName;

        loadJavascript();
    }


    private void loadJavascript() {
        VirtualFile fileByPath =
                LocalFileSystem.getInstance().findFileByPath( absoluteJavaScriptFileLocationPath );
        if ( fileByPath == null ) {
            String message = "Local file system could not find script  " + absoluteJavaScriptFileLocationPath;

            Notifications.Bus.notify( createWarningNotification( message ) );
            return;
        }

        try {
            String script = new String( fileByPath.contentsToByteArray() );
            CallableScriptConfiguration callableScriptConfiguration = new CallableScriptConfiguration(
                    absoluteJavaScriptFileLocationPath,
                    script,
                    javascriptFunctionCall
            );

            scriptReplacementExecutor = new ScriptReplacementExecutor(
                    CustomScriptEngineFactory.Companion.createFactory( scriptEngineManager, calculateScriptType() ),
                    callableScriptConfiguration
            );

        } catch ( IOException e ) {
            String message = "Could not read javascript call back file " + absoluteJavaScriptFileLocationPath +
                    "\n" + e.getMessage();

            Notifications.Bus.notify( createWarningNotification( message ) );
        } catch ( ScriptException e ) {
            String message = "Could not evaluate javascript call back file " + absoluteJavaScriptFileLocationPath +
                    "\n" + e.getMessage();

            Notifications.Bus.notify( createWarningNotification( message ) );
        }
    }


    @Override
    public String toString() {
        return "ScriptFileCallbackReplacementStrategy{" +
                "\nclassName='" + className + '\'' +
                "\n, methodName='" + methodName + '\'' +
                "\n, scriptFileName='" + scriptFileName + '\'' +
                "\n, javascriptFunctionCall='" + javascriptFunctionCall + '\'' +
                "\n, absoluteJavaScriptFileLocationPath='" + absoluteJavaScriptFileLocationPath + '\'' +
                "\n, scriptReplacementExecutor=" + scriptReplacementExecutor +
                '}';
    }


    private Notification createWarningNotification( String message ) {
        return new Notification(
                "DynamicReturnTypePlugin",
                "Script file error",
                message,
                NotificationType.WARNING
        );
    }


    private String calculateScriptType() {
        String scriptExtension = "";
        int i = scriptFileName.lastIndexOf( '.' );
        if ( i > 0 ) {
            scriptExtension = scriptFileName.substring( i + 1 ).toLowerCase();
        }
        if ( scriptExtension.equals( "groovy" ) ) {
            return ScriptReplacementExecutor.SCRIPT_LANGUAGE_GROOVY;
        }

        return ScriptReplacementExecutor.SCRIPT_LANGUAGE_JAVASCRIPT;
    }


    @Override
    public String replaceCalculatedValue( Project project,  String currentValue ) {
        if ( scriptReplacementExecutor == null ) {
            return currentValue;
        }

        return scriptReplacementExecutor.executeAndReplace( currentValue );
    }


    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ScriptFileCallbackReplacementStrategy ) ) {
            return false;
        }

        ScriptFileCallbackReplacementStrategy that = ( ScriptFileCallbackReplacementStrategy ) o;

        //noinspection ConstantConditions
        if ( absoluteJavaScriptFileLocationPath != null ? !absoluteJavaScriptFileLocationPath
                .equals( that.absoluteJavaScriptFileLocationPath ) : that.absoluteJavaScriptFileLocationPath != null ) {
            return false;
        }
        if ( className != null ? !className.equals( that.className ) : that.className != null ) {
            return false;
        }
        if ( javascriptFunctionCall != null ? !javascriptFunctionCall
                .equals( that.javascriptFunctionCall ) : that.javascriptFunctionCall != null ) {
            return false;
        }
        if ( methodName != null ? !methodName.equals( that.methodName ) : that.methodName != null ) {
            return false;
        }
        if ( scriptFileName != null ? !scriptFileName.equals( that.scriptFileName ) : that.scriptFileName != null ) {
            return false;
        }
        //noinspection RedundantIfStatement
        if ( scriptReplacementExecutor != null ? !scriptReplacementExecutor
                .equals( that.scriptReplacementExecutor ) : that.scriptReplacementExecutor != null ) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        int result = className != null ? className.hashCode() : 0;
        result = 31 * result + ( methodName != null ? methodName.hashCode() : 0 );
        result = 31 * result + ( scriptFileName != null ? scriptFileName.hashCode() : 0 );
        result = 31 * result + ( javascriptFunctionCall != null ? javascriptFunctionCall.hashCode() : 0 );
        result = 31 * result + ( absoluteJavaScriptFileLocationPath != null ? absoluteJavaScriptFileLocationPath
                .hashCode() : 0 );
        result = 31 * result + ( scriptReplacementExecutor != null ? scriptReplacementExecutor.hashCode() : 0 );
        return result;
    }
}
