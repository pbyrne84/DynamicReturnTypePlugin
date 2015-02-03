package com.ptby.dynamicreturntypeplugin.symfony

import org.junit.Test
import kotlin.test.assertTrue
import org.junit.Before
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import com.intellij.openapi.project.Project
import kotlin.test.assertEquals

public class SymfonySignatureTranslatorTest {
    var symfonyContainerLookup: SymfonyContainerLookup? = null
    var symfonySignatureTranslator: SymfonySignatureTranslator? = null
    val project = mock(javaClass<Project>())

    Before fun setup() {
        symfonyContainerLookup = mock(javaClass<SymfonyContainerLookup>())
        symfonySignatureTranslator = SymfonySignatureTranslator(symfonyContainerLookup as SymfonyContainerLookup)
    }

    Test fun classConstantGetsTranslated() {
        val expectedSymfonyContainerCall = "service_broker"
        val signatureFromSymfonyPlugin
                = "#M#Ő#M#C\\TestController.getƀservice_broker:getServiceWithoutMask:#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\TestService.CLASS_NAME"

        `when`(symfonyContainerLookup?.lookup(project, expectedSymfonyContainerCall))
                .thenReturn("DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker")

        val actualSignature = symfonySignatureTranslator?.trySymfonyContainer(project, signatureFromSymfonyPlugin)
        verify(symfonyContainerLookup)?.lookup(project, expectedSymfonyContainerCall)

        assertEquals(
                "#M#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker:getServiceWithoutMask:#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\TestService.CLASS_NAME",
                actualSignature
        )
    }


    Test fun classStringClassNameGetsTranslated_noLeadingSlash() {
        val expectedSymfonyContainerCall = "service_broker"
        val signatureFromSymfonyPlugin
                = "#M#Ő#M#C\\TestController.getƀservice_broker:getServiceWithoutMask:DynamicReturnTypePluginTestEnvironment\\TestClasses\\TestService"

        `when`(symfonyContainerLookup?.lookup(project, expectedSymfonyContainerCall))
                .thenReturn("DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker")

        val actualSignature = symfonySignatureTranslator?.trySymfonyContainer(project, signatureFromSymfonyPlugin)
        verify(symfonyContainerLookup)?.lookup(project, expectedSymfonyContainerCall)

        assertEquals(
                "#M#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker:getServiceWithoutMask:#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\TestService.",
                actualSignature
        )
    }

    Test fun classStringClassNameGetsTranslated_leadingSlash() {
        val expectedSymfonyContainerCall = "service_broker"
        val signatureFromSymfonyPlugin
                = "#M#Ő#M#C\\TestController.getƀservice_broker:getServiceWithoutMask:\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\TestService"

        `when`(symfonyContainerLookup?.lookup(project, expectedSymfonyContainerCall))
                .thenReturn("DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker")

        val actualSignature = symfonySignatureTranslator?.trySymfonyContainer(project, signatureFromSymfonyPlugin)
        verify(symfonyContainerLookup)?.lookup(project, expectedSymfonyContainerCall)

        assertEquals(
                "#M#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker:getServiceWithoutMask:#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\TestService.",
                actualSignature
        )
    }




    Test fun chainedMethodCallGetsTranslated() {
        val expectedSymfonyContainerCall = "service_broker"
        val signatureFromSymfonyPlugin
                = "#M#Ő#M#C\\TestController.getƀservice_broker:getServiceWithoutMask:\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\TestService"

        `when`(symfonyContainerLookup?.lookup(project, expectedSymfonyContainerCall))
                .thenReturn("DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker")

        val actualSignature = symfonySignatureTranslator?.trySymfonyContainer(project, signatureFromSymfonyPlugin)
        verify(symfonyContainerLookup)?.lookup(project, expectedSymfonyContainerCall)

        assertEquals(
                "#M#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\ServiceBroker:getServiceWithoutMask:#K#C\\DynamicReturnTypePluginTestEnvironment\\TestClasses\\TestService.",
                actualSignature
        )
    }






}