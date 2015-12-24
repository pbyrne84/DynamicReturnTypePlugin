// require(url:'https://scripting.dev.java.net', jar:'groovy-engine.jar')
package com.ptby.dynamicreturntypeplugin.scripting

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import com.ptby.dynamicreturntypeplugin.index.ClassConstantWalker
import com.ptby.dynamicreturntypeplugin.scripting.api.ExecutingScriptApi
import javax.script.Invocable
import javax.script.ScriptException

public class ScriptReplacementExecutor @Throws(ScriptException::class) constructor(customScriptEngineFactory: CustomScriptEngineFactory,
                                                                                   public val callableScriptConfiguration: CallableScriptConfiguration) {
    private val invocable: Invocable
    private val scriptSignatureParser: ScriptSignatureParser
    private val classConstantWalker = ClassConstantWalker()

    init {
        val executingScriptApi = ExecutingScriptApi(this)
        val engine = customScriptEngineFactory.getEngine()
        engine.eval(callableScriptConfiguration.code)
        engine.put("api", executingScriptApi)

        invocable = engine as Invocable
        scriptSignatureParser = ScriptSignatureParser()
    }


    public fun executeAndReplace(project: Project, currentValue: String): String {
        val parsedSignature = parseSignature(project, currentValue)
                ?: return ""

        try {
            val result = invocable.invokeFunction(
                    callableScriptConfiguration.call,
                    parsedSignature.namespace,
                    parsedSignature.returnClassName
            )
            return parsedSignature.prefix + result
        } catch (e: ScriptException) {
            val message = "Error executing " + callableScriptConfiguration.call + " in " +
                    callableScriptConfiguration.fileLocation + "\n" + e.message

            Notifications.Bus.notify(createWarningNotification(message))
        } catch (e: NoSuchMethodException) {
            val message = "No such method " + callableScriptConfiguration.call + " in " +
                    callableScriptConfiguration.fileLocation + "\n" + e.message

            Notifications.Bus.notify(createWarningNotification(message))
        }

        return ""
    }

    private fun parseSignature(project: Project, currentValue: String): ParsedSignature? {
        val signatureToParse = classConstantWalker.walkThroughConstants(project, currentValue) ?:
                currentValue

        return scriptSignatureParser.parseSignature(signatureToParse)

    }


    private fun createWarningNotification(message: String): Notification {
        return Notification("DynamicReturnTypePlugin", "Script file error", message, NotificationType.WARNING)
    }

    companion object {
        const public val SCRIPT_LANGUAGE_JAVASCRIPT: String = "JavaScript"
        const public val SCRIPT_LANGUAGE_GROOVY: String = "groovy"
    }
}
