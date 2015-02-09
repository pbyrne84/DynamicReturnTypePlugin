package com.ptby.dynamicreturntypeplugin.signature_processingv2

import org.junit.Test
import org.mockito.Mockito
import com.jetbrains.php.PhpIndex
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig

public class ChainedSignatureProcessorTest {

    Test fun moo() {

        val phpIndex = Mockito.mock(javaClass<PhpIndex>())
        val dynamicReturnTypeConfig = Mockito.mock(javaClass<DynamicReturnTypeConfig>())
       // val signatureIterator = ChainedSignatureProcessor(phpIndex, dynamicReturnTypeConfig)

        val multiSignature = "#Ђ#M#Ђ#M#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ClassBroker.getClassWithoutMaskª#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker.CLASS_NAME♣.getServiceWithoutMaskª#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\TestService.CLASS_NAME♣"

  //      signatureIterator.parseSignature( multiSignature )



    }

}