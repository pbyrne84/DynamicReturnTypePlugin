package com.ptby.dynamicreturntypeplugin.config.valuereplacement.replacementexecutors;

import sun.org.mozilla.javascript.internal.Context;
import sun.org.mozilla.javascript.internal.ErrorReporter;
import sun.org.mozilla.javascript.internal.EvaluatorException;
import sun.org.mozilla.javascript.internal.Function;
import sun.org.mozilla.javascript.internal.ScriptableObject;

public class JavascriptReplacementExecutor {

    private final String javaScript;
    private final String className;
    private final String methodName;
    private JavascriptExecutor javascriptExecutor;

    public JavascriptReplacementExecutor( String className,
                                          String methodName,
                                          String javascript,
                                          String javascriptFunctionCall ) {
        this.javaScript = javascript;
        this.className = className;
        this.methodName = methodName;

        Context context = Context.enter();
        ScriptableObject scope = context.initStandardObjects();
        context.evaluateString( scope, javaScript, "script", 1, null );
        Function fct = ( Function ) scope.get( javascriptFunctionCall, scope );

        javascriptExecutor = new JavascriptExecutor( fct, context, scope );
    }


    public String executeAndReplace( String currentValue ) {
        return javascriptExecutor.executeJavascript( currentValue );
    }


    private class JavascriptExecutor {
        private final Function fct;
        private final Context context;
        private final ScriptableObject scope;


        private JavascriptExecutor( Function fct, Context context, ScriptableObject scope ) {
            this.fct = fct;
            this.context = context;
            this.scope = scope;
        }


        public String executeJavascript( String currentValue ) {
            Object result = fct.call(
                    context, scope, scope, new Object[]{ className, methodName, currentValue }
            );

            return String.valueOf( Context.jsToJava( result, String.class ) );
        }
    }
}
