package com.ptby.dynamicreturntypeplugin.signatureconversion;

import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider;
import org.junit.*;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class BySignatureSignatureSplitterTest {
    String parameterSeparator = DynamicReturnTypeProvider.PARAMETER_ITEM_SEPARATOR;

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

        String methodCallSignature = String.format(
                "#M#C\\DynamicReturnTypePluginTestEnvironment\\OverriddenReturnType\\Phockito:verify%s\\DomDocument",
                parameterSeparator
        );
        List<String> actualChainedSignatureList = bySignatureSignatureSplitter
                .createChainedSignatureList( methodCallSignature );
        assertEquals(
                new StringList( methodCallSignature ),
                actualChainedSignatureList
        );
    }


    @Test
    public void test_createChainedSignatureList_multiOverriddenMethodCall_returnsOriginalCall() {
        String methodCallSignature = String.format(
                "#M#Ђ#P#C\\DynamicReturnTypePluginTestEnvironment\\ChainedDynamicReturnTypeTest.classBroker:getClassWithoutMask%s#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker.CLASS_NAME|?:getServiceWithoutMask%s#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\TestService.CLASS_NAME|?",
                parameterSeparator,
                parameterSeparator
        );


        List<String> actualChainedSignatureList = bySignatureSignatureSplitter
                .createChainedSignatureList( methodCallSignature );
        assertEquals(
                new StringList(
                        String.format( "#P#C\\DynamicReturnTypePluginTestEnvironment\\ChainedDynamicReturnTypeTest.classBroker:getClassWithoutMask%s#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker.CLASS_NAME|?",parameterSeparator ),
                        String.format( ":getServiceWithoutMask%s#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\TestService.CLASS_NAME|?", parameterSeparator )
                ),
                actualChainedSignatureList
        );
    }


    @Test
    public void test_createChainedSignatureList_multiOverriddenMethodCall_moo() {
        String methodCallSignature = String.format(
                "#M#Ђ#P#C\\DynamicReturnTypePluginTestEnvironment\\ChainedDynamicReturnTypeTest.classBroker:getClassWithoutMask%s#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker",
                parameterSeparator
        );
        List<String> actualChainedSignatureList = bySignatureSignatureSplitter
                .createChainedSignatureList( methodCallSignature );
        assertEquals(
                new StringList(
                        String.format( "#P#C\\DynamicReturnTypePluginTestEnvironment\\ChainedDynamicReturnTypeTest.classBroker:getClassWithoutMask%s#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker",parameterSeparator )
                ),
                actualChainedSignatureList
        );
    }


}
