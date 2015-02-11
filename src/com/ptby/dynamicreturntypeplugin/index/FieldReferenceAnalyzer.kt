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

/**
 * I cannot seem to be able to find the type from a field without looking at the index so final validation on whether to actually ovveride
 * has to be done later
 */
public class FieldReferenceAnalyzer(private val configAnalyser: ConfigAnalyser) : ListReturnPackaging{
    private val classConstantAnalyzer: ClassConstantAnalyzer
    private val originalCallAnalyzer: OriginalCallAnalyzer
    private val methodCallValidator: MethodCallValidator


    {
        classConstantAnalyzer = ClassConstantAnalyzer()
        originalCallAnalyzer = OriginalCallAnalyzer()
        methodCallValidator = MethodCallValidator(configAnalyser)
    }


    public fun getClassNameFromFieldLookup(customMethodCallSignature: CustomMethodCallSignature,
                                           project: Project): Collection<PhpNamedElement> {
        val phpIndex = PhpIndex.getInstance(project)

        var potentiallyNullPhpType = locateType(phpIndex, project, customMethodCallSignature)
        if (potentiallyNullPhpType == null) {
            return originalCallAnalyzer.getFieldInstanceOriginalReturnType(phpIndex, customMethodCallSignature, project)
        }

        var nullSafePhpType = potentiallyNullPhpType as String
        return processNonNullPhpType(project, phpIndex, nullSafePhpType)
    }

    fun processNonNullPhpType(project: Project,
                              phpIndex: PhpIndex,
                              nullSafePhpType: String): Collection<PhpNamedElement> {
        var processedType: String? = nullSafePhpType

        if (requiresListPackaging( nullSafePhpType)) {
            packageList( nullSafePhpType, project )
        }

        if (nullSafePhpType.indexOf("#C") == 0) {
            return phpIndex.getBySignature(processedType, null, 0)
        } else if (classConstantAnalyzer.verifySignatureIsClassConstant(nullSafePhpType)) {
            processedType = classConstantAnalyzer.getClassNameFromConstantLookup(
                    nullSafePhpType,
                    project
            )
        }

        return phpIndex.getAnyByFQN(processedType)
    }

    private fun locateType(phpIndex: PhpIndex, project: Project,
                           customMethodCallSignature: CustomMethodCallSignature): String? {
        val fieldElements = phpIndex.getBySignature(customMethodCallSignature.className, null, 0)
        if (fieldElements.size() == 0) {
            return null
        }

        val fieldElement = fieldElements.iterator().next()
        val phpType = fieldElement.getType()
        val matchingConfig = methodCallValidator.getMatchingConfig(
                phpIndex, project, customMethodCallSignature.method, "#C" + phpType.toString(), fieldElements
        )

        if (matchingConfig == null) {
            return null
        }

        return customMethodCallSignature.desiredParameter
    }
}
