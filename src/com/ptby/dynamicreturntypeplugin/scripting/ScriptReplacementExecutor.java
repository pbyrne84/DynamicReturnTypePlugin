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

    private final String fileLocation;
    private final String script;
    private final String className;
    private final String methodName;
    private final String javascriptFunctionCall;
    private final Invocable invocable;
    private ScriptSignatureParser scriptSignatureParser;


    public ScriptReplacementExecutor( String scriptLanguage,
                                      String className,
                                      String methodName,
                                      String fileLocation,
                                      String script,
                                      String scriptFunctionCall ) throws ScriptException {
        this.fileLocation = fileLocation;
        this.script = script;
        this.className = className;
        this.methodName = methodName;
        this.javascriptFunctionCall = scriptFunctionCall;

        ExecutingScriptApi executingScriptApi = new ExecutingScriptApi( fileLocation );

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName( scriptLanguage );
        if ( engine == null ) {
            throw new ScriptException(
                    "Script engine '" + scriptLanguage + "' was not created. Relevant jar may not be in classpath."
            );
        }

        engine.put( "api", executingScriptApi );

        engine.eval( this.script );
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
                    javascriptFunctionCall,
                    parsedSignature.getNamespace(),
                    parsedSignature.getReturnClassName(),
                    className,
                    methodName
            );

            return parsedSignature.getPrefix() + String.valueOf( result );
        } catch ( ScriptException e ) {
            String message = "Error executing " + javascriptFunctionCall + " in " + fileLocation + "\n" + e
                    .getMessage();
            Notifications.Bus.notify( createWarningNotification( message ) );
        } catch ( NoSuchMethodException e ) {
            String message = "No such method " + javascriptFunctionCall + " in " + fileLocation + "\n" + e.getMessage();
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
