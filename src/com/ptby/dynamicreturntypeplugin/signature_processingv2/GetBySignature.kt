package com.ptby.dynamicreturntypeplugin.signature_processingv2

import com.intellij.openapi.project.Project
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.PhpIndex
import java.util.ArrayList
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.signatureconversion.SignatureMatcher
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer
import com.jetbrains.php.lang.psi.elements.PhpClass

class GetBySignature(  private val signatureMatcher: SignatureMatcher, private val  classConstantAnalyzer: ClassConstantAnalyzer ) {

    fun getBySignature(signature: String, project: Project): Collection<PhpNamedElement>? {
        val firstSignature = if ( signature.contains(DynamicReturnTypeProvider.PLUGIN_IDENTIFIER_KEY_STRING) )
            signature.substring(0, signature.indexOf(DynamicReturnTypeProvider.PLUGIN_IDENTIFIER_KEY_STRING) - 1)
        else signature

        //#M#P#C\DynamicReturnTypePluginTestEnvironment\FieldCallTest.phockito.verifyª#?#M#C\DynamicReturnTypePluginTestEnvironment\OverriddenReturnType\Phockito.mockª\DynamicReturnTypePluginTestEnvironment\TestClasses\TestEntity

        val signatureWithoutParameter = firstSignature.substring(0,
                                                                 firstSignature.indexOf(DynamicReturnTypeProvider.PARAMETER_SEPARATOR))
        val parameter = firstSignature.substring(firstSignature.indexOf(DynamicReturnTypeProvider.PARAMETER_SEPARATOR) + 1)

        println("signatureWithoutParameter " + signatureWithoutParameter)
        println("parameter " + parameter)

        val phpIndex = PhpIndex.getInstance(project)
        val mutableCollection = phpIndex.getBySignature(signatureWithoutParameter)
        if ( mutableCollection.size() > 0 ) {
            val phpNamedElement = mutableCollection.iterator().next()
            println("phpNamedElement.getFQN() " + phpNamedElement.getFQN())

        }



        println("mutableCollection.size() " + mutableCollection.size())

        return convertParameter( phpIndex,parameter, project)
    }



    private fun convertParameter( phpIndex : PhpIndex, parameter : String, project: Project  ): MutableCollection<PhpClass> {
        if( signatureMatcher.verifySignatureIsClassConstantFunctionCall( parameter )){
            return phpIndex.getAnyByFQN(classConstantAnalyzer.getClassNameFromConstantLookup(parameter, project))
        }

        return ArrayList()
    }
}