package com.ptby.dynamicreturntypeplugin.signature_processingv2

import com.intellij.openapi.project.Project
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.PhpIndex
import java.util.ArrayList
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.signatureconversion.SignatureMatcher
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomSignatureProcessor
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomMethodCallSignature
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.Function

class GetBySignature(private val signatureMatcher: SignatureMatcher,
                     private val  classConstantAnalyzer: ClassConstantAnalyzer,
                     private val customSignatureProcessor: CustomSignatureProcessor) {

    fun getBySignature(signature: String, project: Project): Collection<PhpNamedElement>? {
        val firstSignature = if ( signature.contains(DynamicReturnTypeProvider.PLUGIN_IDENTIFIER_KEY_STRING) )
            signature.substring(0, signature.indexOf(DynamicReturnTypeProvider.PLUGIN_IDENTIFIER_KEY_STRING) - 1)
        else signature

        //#M#P#C\DynamicReturnTypePluginTestEnvironment\FieldCallTest.phockito.verifyª#?#M#C\DynamicReturnTypePluginTestEnvironment\OverriddenReturnType\Phockito.mockª\DynamicReturnTypePluginTestEnvironment\TestClasses\TestEntity
        println("***************************************")
        println("signature " + signature)


        var parameter = firstSignature
        val signatureWithoutParameter = if (firstSignature.contains(DynamicReturnTypeProvider.PARAMETER_SEPARATOR)) {
            parameter = firstSignature.substring(firstSignature.indexOf(DynamicReturnTypeProvider.PARAMETER_SEPARATOR) + 1)
            firstSignature.substring(
                    0, firstSignature.indexOf(DynamicReturnTypeProvider.PARAMETER_SEPARATOR)
            )

        } else {
            firstSignature
        }


        println("signatureWithoutParameter " + signatureWithoutParameter)
        println("parameter " + parameter)

        val phpIndex = PhpIndex.getInstance(project)
        val mutableCollection = phpIndex.getBySignature(signatureWithoutParameter)
        if ( mutableCollection.size() > 0 ) {
            val phpNamedElement = mutableCollection.iterator().next()
            println("phpNamedElement.javaClass " + phpNamedElement.javaClass)
            println("phpNamedElement " + phpNamedElement)
            if ( phpNamedElement is Method) {
                return tryMethod(
                        phpNamedElement,
                        phpIndex,
                        parameter,
                        project)
            } else if ( phpNamedElement is Function ) {
                return customSignatureProcessor.tryFunctionCall(parameter, phpIndex, project)

            }
        }


        return setOf()
    }


    private fun tryMethod(method: Method,
                          phpIndex: PhpIndex,
                          parameter: String,
                          project: Project): Collection<PhpNamedElement>? {
        val fullQualifiedName = method.getFQN()
        println("phpNamedElement.getFQN() " + fullQualifiedName)
        val indexOfMethodSeparator = fullQualifiedName.indexOf(".")
        val className = fullQualifiedName.substring(0, indexOfMethodSeparator)
        val methodName = fullQualifiedName.substring(indexOfMethodSeparator + 1)
        val customMethodCallSignature = CustomMethodCallSignature.new(
                "#M#C" + className,
                methodName,
                parameter
        )

        val collection = customSignatureProcessor.processSignature(phpIndex,
                                                                   customMethodCallSignature,
                                                                   project,
                                                                   customMethodCallSignature.rawStringSignature)

        println("Found collection size collection.size()" + collection?.size())
        return collection
    }


    private fun convertParameter(phpIndex: PhpIndex, parameter: String, project: Project): Collection<PhpNamedElement> {
        if ( signatureMatcher.verifySignatureIsClassConstantFunctionCall(parameter)) {
            return phpIndex.getAnyByFQN(classConstantAnalyzer.getClassNameFromConstantLookup(parameter, project))
        }

        return setOf()
    }
    /*


        private fun processSingleSignature(signature: String, project: Project): Collection<PhpNamedElement>? {
            val bySignature: Collection<PhpNamedElement>?
            val customSignatureProcessor = CustomSignatureProcessor(
                    returnInitialisedSignatureConverter,
                    classConstantAnalyzer,
                    fieldReferenceAnalyzer,
                    variableAnalyser
            )

            bySignature = customSignatureProcessor.getBySignature(signature, project)
            return bySignature
        }
    */

}