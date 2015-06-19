package com.ptby.dynamicreturntypeplugin.index

import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.ptby.dynamicreturntypeplugin.callvalidator.MethodCallValidator
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfigKt
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomMethodCallSignature

import java.util.ArrayList
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.signature_processingv2.ListReturnPackaging

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

        if (classConstantAnalyzer.verifySignatureIsClassConstant(signature.desiredParameter)) {
            val classNameFromConstantLookup = classConstantAnalyzer.getClassNameFromConstantLookup(
                    signature.desiredParameter, project
            )


            return formatWithMask(phpIndex, classNameFromConstantLookup, project)
        }

        return formatWithMask(phpIndex, signature.desiredParameter, project)
    }


    private fun formatWithMask(phpIndex: PhpIndex, signature: String?, project: Project): Collection<PhpNamedElement>? {
        val formattedSignature = signature ?: ""

        if ( requiresListPackaging(formattedSignature)) {
            return packageList(formattedSignature, project)
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
