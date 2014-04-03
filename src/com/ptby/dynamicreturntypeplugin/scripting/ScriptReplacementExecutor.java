// require(url:'https://scripting.dev.java.net', jar:'groovy-engine.jar')
package com.ptby.dynamicreturntypeplugin.scripting;

import com.intellij.openapi.diagnostic.Logger;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptReplacementExecutor {

    public static final String SCRIPT_LANGUAGE_JAVASCRIPT = "JavaScript";
    public static final String SCRIPT_LANGUAGE_GROOVY     = "groovy";

    private final String script;
    private final String className;
    private final String methodName;
    private final String javascriptFunctionCall;
    private final Invocable invocable;
    private ScriptSignatureParser scriptSignatureParser;
    private Logger log;

    public ScriptReplacementExecutor( String scriptLanguage,
                                      String className,
                                      String methodName,
                                      String script,
                                      String scriptFunctionCall ) throws ScriptException {
        this.script = script;
        this.className = className;
        this.methodName = methodName;
        this.javascriptFunctionCall = scriptFunctionCall;

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName( scriptLanguage );
        if ( engine == null ) {
            throw new ScriptException(
                    "Script engine '" + scriptLanguage + "' was not created. Relevant jar may not be in classpath."
            );
        }

        engine.eval( this.script );
        invocable = ( Invocable ) engine;
        scriptSignatureParser = new ScriptSignatureParser();
        log = Logger.getInstance( "DynamicReturnTypePlugin" );
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
            log.warn(
                    "Error executing " + javascriptFunctionCall + "\n" + e.getMessage(),
                    e
            );
        } catch ( NoSuchMethodException e ) {
            log.warn(
                    "No such method " + javascriptFunctionCall + "\n" + e.getMessage(),
                    e
            );
        }

        return "";
    }
}
