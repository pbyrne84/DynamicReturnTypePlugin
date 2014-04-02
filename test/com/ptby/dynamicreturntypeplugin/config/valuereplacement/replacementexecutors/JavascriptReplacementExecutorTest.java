package com.ptby.dynamicreturntypeplugin.config.valuereplacement.replacementexecutors;

import org.intellij.lang.annotations.Language;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JavascriptReplacementExecutorTest {

    @Test
    public void executeAndReplace_validJavascript() {
        @Language( "JavaScript" )
        String javascript = "function abc( className,methodName,returnType) {return className + \"_\" + methodName + \"_\" +  returnType;}";

        JavascriptReplacementExecutor replacementExecutor = new JavascriptReplacementExecutor(
                "class1",
                "method1",
                javascript,
                "abc"
        );

        String actual = replacementExecutor.executeAndReplace( "calculatedvalue" );
        assertEquals( "class1_method1_calculatedvalue", actual );
    }


    @Test
    public void executeAndReplace_invalidJavascript() {
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
