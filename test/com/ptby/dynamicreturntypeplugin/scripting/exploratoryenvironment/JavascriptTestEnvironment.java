package com.ptby.dynamicreturntypeplugin.scripting.exploratoryenvironment;

import com.ptby.dynamicreturntypeplugin.scripting.ScriptReplacementExecutor;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JavascriptTestEnvironment   {

    public static void main( String[] args ) throws ScriptException, IOException, NoSuchMethodException {
        ScriptEngineManager manager = new ScriptEngineManager();
        String scriptLanguage = ScriptReplacementExecutor.SCRIPT_LANGUAGE_JAVASCRIPT;
        ScriptEngine engine = manager.getEngineByName( scriptLanguage );
        if ( engine == null ) {
            throw new ScriptException(
                    "Script engine '" + scriptLanguage + "' was not created. Relevant jar may not be in classpath."
            );
        }
        engine.eval( readScript() );
        Object o = engine.get( "a" );
        Object b = engine.get( "test" );
        System.out.println( b );

        engine.put( "listener", new JavascriptTestEnvironment() );
        Invocable invocable = ( Invocable ) engine;
        invocable.invokeFunction( "test" );
        invocable.invokeMethod( o, "b" );
        System.out.println();
    }


    private static String readScript() throws IOException {
        String script;
        BufferedReader br = new BufferedReader(
                new FileReader(
                        "C:\\development\\DynamicReturnTypePlugin\\test\\com\\ptby\\dynamicreturntypeplugin\\scripting\\exploratoryenvironment\\Test.js"
                )
        );
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while ( line != null ) {
                sb.append( line );
                sb.append( "\n" );
                line = br.readLine();
            }
            script = sb.toString();
        } finally {
            br.close();
        }
        return script;
    }


    public void testCallBack( Runnable testObject ) {
        System.out.println( testObject );
        testObject.run();
    }


    public void runByInterface( JavascriptTestInterface value ) {
        value.runByInterface("banana");
    }
}
