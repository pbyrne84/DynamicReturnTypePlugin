package com.ptby.dynamicreturntypeplugin.index

import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.ptby.dynamicreturntypeplugin.callvalidator.MethodCallValidator
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfigKt
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser
import com.ptby.dynamicreturntypeplugin.signature_processingv2.ListReturnPackaging
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomMethodCallSignature
import com.ptby.dynamicreturntypeplugin.signatureconversion.MaskProcessedSignature

public class VariableAnalyser(configAnalyser: ConfigAnalyser, private val classConstantAnalyzer: ClassConstantAnalyzer) : ListReturnPackaging {
    private val methodCallValidator: MethodCallValidator
    private val originalCallAnalyzer: OriginalCallAnalyzer


    init {
        this.methodCallValidator = MethodCallValidator(configAnalyser)
        originalCallAnalyzer = OriginalCallAnalyzer()
    }


    public fun getClassNameFromVariableLookup(signature: CustomMethodCallSignature,
                                              project: Project): Collection<PhpNamedElement>? {
        val phpIndex = PhpIndex.getInstance(project)

        val matchingMethodConfig = getMatchingMethodConfig(
                phpIndex, project, signature.className, signature.method
        )

        if (matchingMethodConfig == null) {
            return originalCallAnalyzer.getMethodCallReturnType(
                    phpIndex, signature.className.substring(4), signature.method, project
            )
        }

        if (classConstantAnalyzer.verifySignatureIsClassConstant(signature.maskProcessedSignature.typeWithOutListSuffix)) {
            val classNameFromConstantLookup = classConstantAnalyzer.getClassNameFromConstantLookup(
                    signature.maskProcessedSignature.typeWithOutListSuffix, project
            )

            val maskProcessedSignature = if( classNameFromConstantLookup == null ){
                null
            }else{
                MaskProcessedSignature( classNameFromConstantLookup )
            }

            return formatWithMask(phpIndex, maskProcessedSignature, project)
        }

        return formatWithMask(phpIndex, signature.maskProcessedSignature, project)
    }


    private fun formatWithMask(phpIndex: PhpIndex, signature: MaskProcessedSignature?, project: Project): Collection<PhpNamedElement>? {
        val formattedSignature = signature ?: MaskProcessedSignature( "")

        if ( formattedSignature.wouldLikeList) {
            return packageList(formattedSignature, project)
        }

        if ( formattedSignature.typeWithOutListSuffix.startsWith("#F")) {
            return phpIndex.getBySignature(formattedSignature.typeWithOutListSuffix)
        }

        val createdType = "#C" + formattedSignature
        return phpIndex.getBySignature(createdType)
    }


    private fun getMatchingMethodConfig(phpIndex: PhpIndex,
                                        project: Project,
                                        variableSignature: String,
                                        calledMethod: String): ClassMethodConfigKt? {
        val cleanedVariableSignature = variableSignature.substring(2)
        val fieldElements = phpIndex.getBySignature(cleanedVariableSignature, null, 0)

        return methodCallValidator.getMatchingConfig(
                phpIndex, project, calledMethod, cleanedVariableSignature, fieldElements
        )
    }
}
