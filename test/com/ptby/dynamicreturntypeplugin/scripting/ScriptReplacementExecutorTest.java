package com.ptby.dynamicreturntypeplugin.scripting;

import org.intellij.lang.annotations.Language;
import org.junit.Test;

import javax.script.ScriptException;

import static org.junit.Assert.assertEquals;

public class ScriptReplacementExecutorTest {

    @Test
    public void executeAndReplace_validJavascript() throws ScriptException {
        @Language("JavaScript")
        String javascript = "function abc( returnTypeNameSpace, returnTypeClass, className,methodName ) {return className + \"_\" + methodName + \"_\" +  returnTypeNameSpace + \"_\" + returnTypeClass ;}";

        ScriptReplacementExecutor replacementExecutor = new ScriptReplacementExecutor(
                ScriptReplacementExecutor.SCRIPT_LANGUAGE_JAVASCRIPT,
                "class1",
                "method1",
                javascript,
                "abc"
        );

        String actual = replacementExecutor
                .executeAndReplace( "#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker" );
        assertEquals( "#K#C\\class1_method1_\\DynamicReturnTypePluginTestEnvironment\\TestClasses_ServiceBroker", actual );
    }


    @Test
    public void executeAndReplace_validGroovy() throws ScriptException {
        @Language("Groovy")
        String groovy = "def abc( returnTypeNameSpace, returnTypeClass, className,methodName ) {return className + \"_\" + methodName + \"_\" +  returnTypeNameSpace + \"_\" + returnTypeClass ;}";

        ScriptReplacementExecutor replacementExecutor = new ScriptReplacementExecutor(
                ScriptReplacementExecutor.SCRIPT_LANGUAGE_GROOVY,
                "class1",
                "method1",
                groovy,
                "abc"
        );

        String actual = replacementExecutor
                .executeAndReplace( "#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker" );
        assertEquals( "#K#C\\class1_method1_\\DynamicReturnTypePluginTestEnvironment\\TestClasses_ServiceBroker", actual );
    }


    @Test(expected = ScriptException.class)
    public void executeAndReplace_invalidJavascript() throws ScriptException {
        String javascript = "function abc(";

        ScriptReplacementExecutor replacementExecutor = new ScriptReplacementExecutor(
                ScriptReplacementExecutor.SCRIPT_LANGUAGE_JAVASCRIPT,
                "class1",
                "method1",
                javascript,
                "abc"
        );

        replacementExecutor.executeAndReplace( "calculatedvalue" );
    }


}
