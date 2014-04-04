package com.ptby.dynamicreturntypeplugin.scripting.api;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

public class ExecutingScriptApi {

    private final String scriptFileLocation;


    public ExecutingScriptApi( String scriptFileLocation ) {
        this.scriptFileLocation = scriptFileLocation;
    }


    /**
     * Used by script execution
     * @param message - message to send to event log
     */
    public void writeToEventLog( String message ) {
        Notification notification = new Notification(
                "DynamicReturnTypePlugin",
                "script debug for " + scriptFileLocation,
                "\n" + message + "\n\n",
                NotificationType.INFORMATION
        );


        Notifications.Bus.notify( notification );
    }
}
