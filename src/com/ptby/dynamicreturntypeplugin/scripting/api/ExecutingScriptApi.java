package com.ptby.dynamicreturntypeplugin.scripting.api;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.ptby.dynamicreturntypeplugin.scripting.CallableScriptConfiguration;
import com.ptby.dynamicreturntypeplugin.scripting.PhpCallReferenceInfo;
import com.ptby.dynamicreturntypeplugin.scripting.ScriptReplacementExecutor;

public class ExecutingScriptApi {

    private final ScriptReplacementExecutor scriptReplacementExecutor;


    public ExecutingScriptApi( ScriptReplacementExecutor scriptReplacementExecutor ) {
        this.scriptReplacementExecutor = scriptReplacementExecutor;
    }


    /**
     * Used by script execution
     *
     * @param message - message to send to event log
     */
    public void writeToEventLog( String message ) {
        Notification notification = new Notification(
                "DynamicReturnTypePlugin",
                "script debug for " + getCallableScriptConfiguration().getFileLocation(),
                "\n" + message + "\n\n",
                NotificationType.INFORMATION
        );

        Notifications.Bus.notify( notification );
    }


    @SuppressWarnings( "unused" )
    public CallableScriptConfiguration getCallableScriptConfiguration() {
        return scriptReplacementExecutor.getCallableScriptConfiguration();
    }


    @SuppressWarnings( "unused" )
    public PhpCallReferenceInfo getPhpCallReferenceInfo() {
        return scriptReplacementExecutor.getPhpCallReferenceInfo();
    }
}
