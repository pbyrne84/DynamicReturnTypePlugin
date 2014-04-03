package com.ptby.dynamicreturntypeplugin.scripting;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScriptSignatureParserTest {

    private ScriptSignatureParser scriptSignatureParser;


    @Before
    public void setup() {
        scriptSignatureParser = new ScriptSignatureParser();
    }


    @Test
    public void parseSignature_withSignatureIdentifier_multipleLevelsOfSeparator() {
        ParsedSignature expectedParsedSignature = new ParsedSignature(
                "#K#C\\",
                "\\DynamicReturnTypePluginTestEnvironment\\TestClasses",
                "ServiceBroker"
        );

        ParsedSignature actualParsedSignature = scriptSignatureParser.parseSignature(
                "#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker"
        );

        assertEquals( expectedParsedSignature, actualParsedSignature );
    }


    @Test
    public void parseSignature_withoutSignatureIdentifier_singleLevelOfSeparator() {
        ParsedSignature expectedParsedSignature = new ParsedSignature( "", "Entity", "User" );

        ParsedSignature actualParsedSignature = scriptSignatureParser.parseSignature(
                "Entity\\User"
        );

        assertEquals( expectedParsedSignature, actualParsedSignature );
    }


    @Test
    public void parseSignature_withoutSignatureIdentifier_noNamespace() {
        ParsedSignature expectedParsedSignature = new ParsedSignature( "", "", "User" );

        ParsedSignature actualParsedSignature = scriptSignatureParser.parseSignature( "User" );

        assertEquals( expectedParsedSignature, actualParsedSignature );
    }


    @Test
    public void parseSignature_emptyValueReturnsNull() {
        ParsedSignature actualParsedSignature = scriptSignatureParser.parseSignature( "" );
        assertEquals( null, actualParsedSignature );
    }


}
