package com.ptby.dynamicreturntypeplugin.config.valuereplacement.replacementexecutors;

import org.intellij.lang.annotations.Language;
import org.junit.Test;

import javax.script.ScriptException;

import static org.junit.Assert.assertEquals;

public class JavascriptReplacementExecutorTest {

    @Test
    public void executeAndReplace_validJavascript_hasSignatureAtStart() throws ScriptException {
        @Language("JavaScript")
        String javascript = "function abc( returnTypeNameSpace, returnTypeClass, className,methodName ) {return className + \"_\" + methodName + \"_\" +  returnTypeNameSpace + \"_\" + returnTypeClass ;}";

        JavascriptReplacementExecutor replacementExecutor = new JavascriptReplacementExecutor(
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
    public void executeAndReplace_validJavascript_doesNotHaveSignatureAtStart() throws ScriptException {
        @Language("JavaScript")
        String javascript = "function abc( returnTypeNameSpace, returnTypeClass, className, methodName ) {return className + \"_\" + methodName + \"_\" +  returnTypeNameSpace + \"_\" + returnTypeClass ;}";

        JavascriptReplacementExecutor replacementExecutor = new JavascriptReplacementExecutor(
                "class1",
                "method1",
                javascript,
                "abc"
        );

        String actual = replacementExecutor.executeAndReplace( "Entity\\User" );
        assertEquals( "class1_method1_Entity_User", actual );
    }


    @Test(expected = ScriptException.class)
    public void executeAndReplace_invalidJavascript() throws ScriptException {
        String javascript = "function abc(";

        JavascriptReplacementExecutor replacementExecutor = new JavascriptReplacementExecutor(
                "class1",
                "method1",
                javascript,
                "abc"
        );

        replacementExecutor.executeAndReplace( "calculatedvalue" );
    }


}
