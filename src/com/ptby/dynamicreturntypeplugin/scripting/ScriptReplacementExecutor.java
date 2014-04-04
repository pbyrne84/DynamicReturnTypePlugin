// require(url:'https://scripting.dev.java.net', jar:'groovy-engine.jar')
package com.ptby.dynamicreturntypeplugin.scripting;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.ptby.dynamicreturntypeplugin.scripting.api.ExecutingScriptApi;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptReplacementExecutor {

    public static final String SCRIPT_LANGUAGE_JAVASCRIPT = "JavaScript";
    public static final String SCRIPT_LANGUAGE_GROOVY = "groovy";

    private final PhpCallReferenceInfo phpCallReferenceInfo;
    private final CallableScriptConfiguration callableScriptConfiguration;
    private final Invocable invocable;
    private ScriptSignatureParser scriptSignatureParser;


    public ScriptReplacementExecutor( String scriptLanguage,
                                      PhpCallReferenceInfo phpCallReferenceInfo,
                                      CallableScriptConfiguration callableScriptConfiguration ) throws ScriptException {
        this.phpCallReferenceInfo = phpCallReferenceInfo;
        this.callableScriptConfiguration = callableScriptConfiguration;

        ExecutingScriptApi executingScriptApi = new ExecutingScriptApi( callableScriptConfiguration.getScriptFileLocation() );

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName( scriptLanguage );
        if ( engine == null ) {
            throw new ScriptException(
                    "Script engine '" + scriptLanguage + "' was not created. Relevant jar may not be in classpath."
            );
        }

        engine.put( "api", executingScriptApi );

        engine.eval( callableScriptConfiguration.getScriptCode() );
        invocable = ( Invocable ) engine;
        scriptSignatureParser = new ScriptSignatureParser();
    }


    public String executeAndReplace( String currentValue ) {
        ParsedSignature parsedSignature = scriptSignatureParser.parseSignature( currentValue );
        if ( parsedSignature == null ) {
            return "";
        }

        try {
            Object result = invocable.invokeFunction(
                    callableScriptConfiguration.getScriptCall(),
                    parsedSignature.getNamespace(),
                    parsedSignature.getReturnClassName()
            );

            return parsedSignature.getPrefix() + String.valueOf( result );
        } catch ( ScriptException e ) {
            String message = "Error executing " + callableScriptConfiguration.getScriptCall() + " in " + callableScriptConfiguration
                    .getScriptFileLocation() + "\n" + e.getMessage();
            Notifications.Bus.notify( createWarningNotification( message ) );
        } catch ( NoSuchMethodException e ) {
            String message = "No such method " + callableScriptConfiguration.getScriptCall() + " in " + callableScriptConfiguration
                    .getScriptFileLocation() + "\n" + e.getMessage();

            Notifications.Bus.notify( createWarningNotification( message ) );
        }

        return "";
    }


    private Notification createWarningNotification( String message ) {
        return new Notification(
                "DynamicReturnTypePlugin",
                "Script file error",
                message,
                NotificationType.WARNING
        );
    }
}
