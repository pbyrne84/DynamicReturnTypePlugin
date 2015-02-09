package com.ptby.dynamicreturntypeplugin.signature_processingv2

import com.jetbrains.php.PhpIndex
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.intellij.openapi.project.Project


public class ChainedSignatureProcessor(private val phpIndex: PhpIndex,
                                       private val dynamicReturnTypeConfig: DynamicReturnTypeConfig,
                                       private val returnValueFromParametersProcessor: ReturnValueFromParametersProcessor) {


    fun parseSignature(signature: String, project: Project): Collection<PhpNamedElement>? {
        val preparedSignature = prepareSignature(signature)
        val chainedCalls = preparedSignature.split(DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR)

        var lastClassType = ""
        var lastReturnType : ReturnType? = null

        var callIndex = 0

        for ( singleCall in chainedCalls ) {
            val callFromSignature =  getCallFromSignature( lastClassType, singleCall )
            if( callFromSignature.fqnClass == "" ){
                return setOf()

            }

            val parameters = getParameters(singleCall)
            val classMethodConfigKt = dynamicReturnTypeConfig.locateClassMethodConfig(phpIndex,
                                                                                      callFromSignature.fqnClass,
                                                                                      callFromSignature.method)

            if ( classMethodConfigKt == null ) {
                return setOf()
            }


            val returnType = returnValueFromParametersProcessor.getReturnValue(project,
                                                                               classMethodConfigKt,
                                                                               callFromSignature,
                                                                               parameters,
                                                                               phpIndex)

            if( !returnType.hasFoundReturnType() ){
                return setOf()
            }

            lastClassType = returnType.getClassName()
            lastReturnType = returnType

            callIndex += 1
        }

        if( lastReturnType != null && lastReturnType?.hasFoundReturnType() as Boolean ){
            return lastReturnType?.phpNamedElements
        }

        return setOf()
    }

    private fun prepareSignature(signature: String): String {
        var preparedSignature = cleanParameterEndSignature(signature ).trimLeading("#M#" + DynamicReturnTypeProvider.PLUGIN_IDENTIFIER_KEY_STRING)



        return preparedSignature
    }

    private fun cleanParameterEndSignature( signature : String ) : String{
        var cleanSignature = signature
        while( cleanSignature != cleanSignature.trimTrailing(DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR)){
            cleanSignature = cleanSignature.trimTrailing(DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR)
        }

        return cleanSignature

    }


    private fun getParameters(singleCall: String): Array<String> {
        val parameterSignature = singleCall.substring(singleCall.indexOf(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR) + 1)

        return parameterSignature.split(
                DynamicReturnTypeProvider.PARAMETER_ITEM_SEPARATOR)
    }


    private fun getCallFromSignature(lastClassType : String, singleCall: String): ClassCall {
        val indexOfParameterStart = singleCall.indexOf(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR)
        if( indexOfParameterStart == -1 ){
            throw RuntimeException( "Single call has no parameters " + singleCall)
        }
        val callSignature = singleCall.substring(0, indexOfParameterStart)

        val mutableCollection = if( callSignature.indexOf(".") == 0){
            val chainedSignature = "#M#C" + lastClassType + callSignature
            phpIndex.getBySignature(chainedSignature)
        }else{
            phpIndex.getBySignature(callSignature)
        }


        if( mutableCollection.size() == 0 ){
            return ClassCall("", "" )
        }

        val phpNamedElement = mutableCollection.iterator().next()

        val fqn = phpNamedElement.getFQN()
        val indexOfMethod = fqn.indexOf(".")

        if( indexOfMethod == -1 ){
            return ClassCall("", "" )
        }


        return ClassCall( fqn.substring(0, indexOfMethod), fqn.substring( indexOfMethod + 1) )
    }

}

data class ClassCall(val fqnClass: String, val  method: String)