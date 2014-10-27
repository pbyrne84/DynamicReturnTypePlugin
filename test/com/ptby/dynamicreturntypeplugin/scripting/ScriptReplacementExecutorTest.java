package com.ptby.dynamicreturntypeplugin.scripting;

import org.intellij.lang.annotations.Language;
import org.junit.Test;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.junit.Assert.assertEquals;

public class ScriptReplacementExecutorTest {

    @Test
    public void executeAndReplace_validJavascript() throws ScriptException {
        @Language( "JavaScript" )
        String javascript = "function abc( returnTypeNameSpace, returnTypeClass ) {" +
                "print(returnTypeNameSpace);return returnTypeNameSpace + \"_\" + returnTypeClass ;" +
                "}";

        ScriptReplacementExecutor replacementExecutor = new ScriptReplacementExecutor(
                CustomScriptEngineFactory.OBJECT$.createFactory(
                        new ScriptEngineManager(),
                        ScriptReplacementExecutor.SCRIPT_LANGUAGE_JAVASCRIPT
                ),
                new PhpCallReferenceInfo( "class1", "method1" ),
                new CallableScriptConfiguration( "file location", javascript, "abc" )
        );

        String actual = replacementExecutor
                .executeAndReplace( "#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker" );
        assertEquals( "#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses_ServiceBroker", actual );
    }


    @Test
    public void executeAndReplace_validGroovy() throws ScriptException {
        @Language( "Groovy" )
        String groovy = "def abc( returnTypeNameSpace, returnTypeClass ) {return returnTypeNameSpace + \"_\" + returnTypeClass  ;}";

        ScriptReplacementExecutor replacementExecutor = new ScriptReplacementExecutor(
                CustomScriptEngineFactory.OBJECT$.createFactory(
                        new ScriptEngineManager(),
                        ScriptReplacementExecutor.SCRIPT_LANGUAGE_GROOVY
                ),
                new PhpCallReferenceInfo( "class1", "method1" ),
                new CallableScriptConfiguration( "file location", groovy, "abc" )
        );

        String actual = replacementExecutor
                .executeAndReplace( "#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker" );
        assertEquals( "#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses_ServiceBroker", actual );
    }


    @Test( expected = ScriptException.class )
    public void executeAndReplace_invalidJavascript() throws ScriptException {
        String javascript = "function abc(";

        ScriptReplacementExecutor replacementExecutor = new ScriptReplacementExecutor(
                CustomScriptEngineFactory.OBJECT$.createFactory(
                        new ScriptEngineManager(),
                        ScriptReplacementExecutor.SCRIPT_LANGUAGE_JAVASCRIPT
                ),
                new PhpCallReferenceInfo( "class1", "method1" ),
                new CallableScriptConfiguration( "file location", javascript, "abc" )
        );

        replacementExecutor.executeAndReplace( "calculatedvalue" );
    }


}
