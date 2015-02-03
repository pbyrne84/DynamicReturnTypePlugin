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

public class VariableAnalyser(configAnalyser: ConfigAnalyser, private val classConstantAnalyzer: ClassConstantAnalyzer) {
    private val methodCallValidator: MethodCallValidator
    private val originalCallAnalyzer: OriginalCallAnalyzer


    {
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

        if (classConstantAnalyzer.verifySignatureIsClassConstant(signature.parameter)) {
            val classNameFromConstantLookup = classConstantAnalyzer.getClassNameFromConstantLookup(
                    signature.parameter, project
            )


            return formatWithMask(phpIndex, matchingMethodConfig, classNameFromConstantLookup, project)
        }

        return formatWithMask(phpIndex, matchingMethodConfig, signature.parameter, project)
    }


    private fun formatWithMask(phpIndex: PhpIndex, config: ClassMethodConfigKt, signature: String?, project: Project): Collection<PhpNamedElement>? {
        val formattedSignature = config.formatBeforeLookup(signature)

        if (formattedSignature.contains("[]")) {
            val customList = ArrayList<PhpNamedElement>()
            customList.add(LocalClassImpl(PhpType().add(formattedSignature), project))
            return customList
        }

        if ( !formattedSignature.contains("|")) {
            val createdType = "#C" + formattedSignature
            return phpIndex.getBySignature(createdType)
        }

        return createMultiTypedFromMask(formattedSignature, project)
    }

    private fun createMultiTypedFromMask(formattedSignature: String, project: Project): Collection<PhpNamedElement>? {
        val customList = ArrayList<PhpNamedElement>()
        formattedSignature.split("\\|").forEach { type ->
            customList.add(LocalClassImpl(PhpType().add("#C" + type), project))
        }

        return customList
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

    class object {
        public fun packageForGetTypeResponse(intellijReference: String?,
                                             methodName: String?,
                                             returnType: String?): String {
            return intellijReference + ":" + methodName + DynamicReturnTypeProvider.PARAMETER_SEPARATOR + returnType
        }
    }
}
