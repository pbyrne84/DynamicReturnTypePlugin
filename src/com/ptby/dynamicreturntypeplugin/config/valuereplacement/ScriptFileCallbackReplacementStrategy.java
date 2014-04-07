package com.ptby.dynamicreturntypeplugin.config.valuereplacement;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.ptby.dynamicreturntypeplugin.scripting.PhpCallReferenceInfo;
import com.ptby.dynamicreturntypeplugin.scripting.CallableScriptConfiguration;
import com.ptby.dynamicreturntypeplugin.scripting.ScriptReplacementExecutor;

import javax.script.ScriptException;
import java.io.IOException;

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
        VirtualFile fileByPath =
                LocalFileSystem.getInstance().findFileByPath( absoluteJavaScriptFileLocationPath );
        if ( fileByPath == null ) {
            String message = "Local file system could not find script  " + absoluteJavaScriptFileLocationPath;

            Notifications.Bus.notify( createWarningNotification( message ) );
            return;
        }

        try {
            String script = new String( fileByPath.contentsToByteArray() );
            Notifications.Bus.notify( createWarningNotification( script ) );

            CallableScriptConfiguration callableScriptConfiguration = new CallableScriptConfiguration(
                    absoluteJavaScriptFileLocationPath,
                    script,
                    javascriptFunctionCall
            );

            scriptReplacementExecutor = new ScriptReplacementExecutor(
                    calculateScriptType(),
                    new PhpCallReferenceInfo( className, methodName ),
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
    public String replaceCalculatedValue( String currentValue ) {
        if ( scriptReplacementExecutor == null ) {
            return currentValue;
        }

        return scriptReplacementExecutor.executeAndReplace( currentValue );
    }


}
