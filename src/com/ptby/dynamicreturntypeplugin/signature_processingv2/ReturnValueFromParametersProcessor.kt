package com.ptby.dynamicreturntypeplugin.signature_processingv2

import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.ptby.dynamicreturntypeplugin.config.ParameterValueFormatter
import com.ptby.dynamicreturntypeplugin.index.LocalClassImpl
import com.ptby.dynamicreturntypeplugin.signature_extension.withClassPrefix
import com.ptby.dynamicreturntypeplugin.signature_extension.withMethodCallPrefix
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomMethodCallSignature
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomSignatureProcessor
import java.util.*

public class ReturnValueFromParametersProcessor(
        private val customSignatureProcessor: CustomSignatureProcessor) {

    fun getMethodReturnValue(project: Project,
                             classMethodConfigKt: ParameterValueFormatter,
                             classCall: ClassCall,
                             phpIndex: PhpIndex): ReturnType {

        if(!classCall.hasParameterAtIndex( classMethodConfigKt.parameterIndex )){
            return ReturnType.empty()
        }

        val selectedParameter = classCall.getParameterAtIndex( classMethodConfigKt.parameterIndex )
        val treatedParameter = classMethodConfigKt.formatBeforeLookup(selectedParameter)

        if ( treatedParameter.contains("|")) {
            return ReturnType(createMultiTypedFromMask(treatedParameter, project))
        }

        val customMethodCallSignature = CustomMethodCallSignature.new(
                classCall.fqnClass.withMethodCallPrefix(),
                classCall.method,
                treatedParameter
        )


        val collection = customSignatureProcessor.processSignature(
                phpIndex,
                customMethodCallSignature,
                project,
                customMethodCallSignature.rawStringSignature
        )

        return ReturnType(collection)
    }


    private fun createMultiTypedFromMask(formattedSignature: String, project: Project): Collection<PhpNamedElement>? {
        val customList = ArrayList<PhpNamedElement>()
        formattedSignature.split("\\|".toRegex()).reversed().forEach { type ->
            customList.add(LocalClassImpl(PhpType().add( type.withClassPrefix() ), project))
        }

        return customList
    }

    fun getFunctionReturnValue(parameter: String, phpIndex: PhpIndex, project: Project): ReturnType {
        return ReturnType(customSignatureProcessor.tryFunctionCall(parameter, phpIndex, project))
    }

}


data class ReturnType(val phpNamedElements: Collection<PhpNamedElement>?) {
    private var fqnClassName: String? = null

    fun getClassName(): String {
        if ( fqnClassName == null ) {
            fqnClassName = ""
            if ( phpNamedElements != null && hasFoundReturnType() ) {
                val phpNamedElement = phpNamedElements.iterator().next()
                // phpNamedElement.getFQN() says it cannot return null from java but does
                fqnClassName = if ( phpNamedElement.getFQN() == null ) {
                    ""
                } else {
                    phpNamedElement.getFQN()
                }
            }
        }

        return fqnClassName as String
    }

    fun hasFoundReturnType(): Boolean {
        return phpNamedElements != null && phpNamedElements.size() > 0
    }


    companion object {
        fun empty(): ReturnType {
            return ReturnType(setOf())
        }
    }

}