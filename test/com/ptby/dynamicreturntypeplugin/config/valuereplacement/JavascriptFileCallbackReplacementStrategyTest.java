package com.ptby.dynamicreturntypeplugin.config.valuereplacement;

import com.ptby.dynamicreturntypeplugin.TestVirtualFile;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JavascriptFileCallbackReplacementStrategyTest {

    @Test
    public void replaceCalculatedValue_methodDoesNotMatch_returnsSameValue() {
        JavascriptFileCallbackReplacementStrategy javascriptFileCallbackReplacementStrategy
                = new JavascriptFileCallbackReplacementStrategy(
                createConfigFile(),
                "",
                "",
                "JavascriptCallBackTestFile.js",
                "replaceReturnType"
        );

        String actual = javascriptFileCallbackReplacementStrategy.replaceCalculatedValue( "test value" );
        assertEquals( "test value", actual );
    }
    
    
    
    private TestVirtualFile createConfigFile() {
        TestVirtualFile configVirtualFile = new TestVirtualFile();
        TestVirtualFile parentVirtualFile = new TestVirtualFile();
        configVirtualFile.setParent( parentVirtualFile );
        parentVirtualFile.setCanonicalPath("C:/development/DynamicReturnTypePlugin/test/resource"); 
        
        return configVirtualFile;
    }
}
