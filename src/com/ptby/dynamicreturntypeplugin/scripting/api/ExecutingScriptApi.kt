package com.ptby.dynamicreturntypeplugin.scripting.api

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.ptby.dynamicreturntypeplugin.scripting.CallableScriptConfiguration
import com.ptby.dynamicreturntypeplugin.scripting.PhpCallReferenceInfo
import com.ptby.dynamicreturntypeplugin.scripting.ScriptReplacementExecutor

public class ExecutingScriptApi(private val scriptReplacementExecutor: ScriptReplacementExecutor) {

    /**
     * Used by script execution
     *
     * @param message - message to send to event log
     */
    public fun writeToEventLog(message: String) {
        val notification = Notification(
                "DynamicReturnTypePlugin",
                "script debug for " + getCallableScriptConfiguration().fileLocation,
                "\n" + message + "\n\n",
                NotificationType.INFORMATION
        )

        Notifications.Bus.notify(notification)
    }

    SuppressWarnings("unused")
    public fun getCallableScriptConfiguration(): CallableScriptConfiguration {
        return scriptReplacementExecutor.callableScriptConfiguration
    }

}
