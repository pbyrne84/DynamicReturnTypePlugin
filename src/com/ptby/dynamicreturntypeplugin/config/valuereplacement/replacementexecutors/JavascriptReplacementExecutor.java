package com.ptby.dynamicreturntypeplugin.config.valuereplacement.replacementexecutors;

import com.intellij.openapi.diagnostic.Logger;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JavascriptReplacementExecutor {

    private final String javascript;
    private final String className;
    private final String methodName;
    private JavaScriptExecutor javaScriptExecutor;


    public JavascriptReplacementExecutor( String className,
                                          String methodName,
                                          String javascript,
                                          String javascriptFunctionCall ) throws ScriptException {
        this.javascript = javascript;
        this.className = className;
        this.methodName = methodName;

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName( "JavaScript" );
        engine.eval( this.javascript );
        Invocable inv = ( Invocable ) engine;
        javaScriptExecutor = new JavaScriptExecutor( inv, javascriptFunctionCall );
    }


    public String executeAndReplace( String currentValue ) {
        if ( javaScriptExecutor == null ) {
            return "";
        }

        return javaScriptExecutor.executeJavascript( currentValue );
    }


    private int calculatePrefixEnd( String currentValue ) {
        if ( !currentValue.contains( "#" ) && currentValue.contains( "\\" ) ) {
            return 0;
        }

        return currentValue.indexOf( "\\" );
    }


    private class JavaScriptExecutor {

        private final Invocable invocable;
        private final String javascriptFunctionCall;


        private JavaScriptExecutor( Invocable invocable, String javascriptFunctionCall ) {
            this.invocable = invocable;
            this.javascriptFunctionCall = javascriptFunctionCall;
        }


        public String executeJavascript( String currentValue ) {
            final Logger log = Logger.getInstance( "DynamicReturnTypePlugin" );

            int prefixEndIndex = calculatePrefixEnd( currentValue );
            String prefix = "";
            String namespace = "";
            String returnClassName = currentValue;
            if ( prefixEndIndex != -1 ) {
                if ( prefixEndIndex != 0 ) {
                    prefix = currentValue.substring( 0, prefixEndIndex ) + "\\";
                }
                int namesSpaceEndIndex = currentValue.lastIndexOf( "\\" );
                namespace = currentValue.substring( prefixEndIndex, namesSpaceEndIndex );

                returnClassName = currentValue.substring( namesSpaceEndIndex + 1 );
            }

            try {
                Object result = invocable.invokeFunction(
                        javascriptFunctionCall,
                        namespace,
                        returnClassName,
                        className,
                        methodName
                );

                return prefix + String.valueOf( result );
            } catch ( ScriptException e ) {
                log.warn(
                        "Error executing " + javascriptFunctionCall + "\n" +  e.getMessage(),
                        e
                );
            } catch ( NoSuchMethodException e ) {
                log.warn(
                        "No such method " + javascriptFunctionCall + "\n" +  e.getMessage(),
                        e
                );
            }

            return "";
        }
    }
}
