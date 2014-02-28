package com.ptby.dynamicreturntypeplugin.signatureconversion;

import org.junit.*;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class BySignatureSignatureSplitterTest {

    private BySignatureSignatureSplitter bySignatureSignatureSplitter;


    @Before
    public void setup() {
        bySignatureSignatureSplitter = new BySignatureSignatureSplitter();
    }


    @Test
    public void test_createChainedSignatureList_emptyString_returnsEmptyList() {
        List<String> actualChainedSignatureList = bySignatureSignatureSplitter.createChainedSignatureList( "" );
        assertEquals(
                new StringList(),
                actualChainedSignatureList
        );
    }


    @Test
    public void test_createChainedSignatureList_singleMethodCall_returnsOriginalCall() {
        String methodCallSignature = "#M#C\\DynamicReturnTypePluginTestEnvironment\\OverriddenReturnType\\Phockito:verify:\\DomDocument";
        List<String> actualChainedSignatureList = bySignatureSignatureSplitter
                .createChainedSignatureList( methodCallSignature );
        assertEquals(
                new StringList( methodCallSignature ),
                actualChainedSignatureList
        );
    }


    @Test
    public void test_createChainedSignatureList_multiOverriddenMethodCall_returnsOriginalCall() {
        String methodCallSignature = "#M#Ђ#P#C\\DynamicReturnTypePluginTestEnvironment\\ChainedDynamicReturnTypeTest.classBroker:getClassWithoutMask:#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker.CLASS_NAME|?:getServiceWithoutMask:#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\TestService.CLASS_NAME|?\n";
        List<String> actualChainedSignatureList = bySignatureSignatureSplitter
                .createChainedSignatureList( methodCallSignature );
        assertEquals(
                new StringList(
                        "#P#C\\DynamicReturnTypePluginTestEnvironment\\ChainedDynamicReturnTypeTest.classBroker:getClassWithoutMask:#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker.CLASS_NAME|?",
                        ":getServiceWithoutMask:#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\TestService.CLASS_NAME|?"
                ),
                actualChainedSignatureList
        );
    }


}
