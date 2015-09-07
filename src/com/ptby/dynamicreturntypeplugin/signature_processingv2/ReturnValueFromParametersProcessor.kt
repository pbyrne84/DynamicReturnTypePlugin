package com.ptby.dynamicreturntypeplugin.signature_processingv2

import com.intellij.openapi.project.Project
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import java.util.ArrayList
import com.ptby.dynamicreturntypeplugin.index.LocalClassImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.PhpIndex
import com.ptby.dynamicreturntypeplugin.signatureconversion.SignatureMatcher
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfigKt
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomSignatureProcessor
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomMethodCallSignature
import com.ptby.dynamicreturntypeplugin.config.ParameterValueFormatter
import com.ptby.dynamicreturntypeplugin.signatureconversion.MaskProcessedSignature

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

        if ( treatedParameter.isMulti) {
            return ReturnType(createMultiTypedFromMask(treatedParameter, project))
        }

        val customMethodCallSignature = CustomMethodCallSignature.new(
                "#M#C" + classCall.fqnClass,
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


    private fun createMultiTypedFromMask(formattedSignature: MaskProcessedSignature, project: Project): Collection<PhpNamedElement>? {
        val customList = ArrayList<PhpNamedElement>()
        formattedSignature.typeWithOutListSuffix.split("\\|".toRegex()).reverse().forEach { type ->
            customList.add(LocalClassImpl(PhpType().add("#C" + type.removePrefix("#K#C").removeSuffix(".")), project))
        }

        return customList
    }

    fun getFunctionReturnValue(parameter: MaskProcessedSignature, phpIndex: PhpIndex, project: Project): ReturnType {
        return ReturnType(customSignatureProcessor.tryFunctionCall(parameter, phpIndex, project))
    }

}
