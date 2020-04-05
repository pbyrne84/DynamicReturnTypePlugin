package com.ptby.dynamicreturntypeplugin.signatureconversion

import com.intellij.openapi.diagnostic.Logger.getInstance
import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.ptby.dynamicreturntypeplugin.index.ClassConstantWalker
import com.ptby.dynamicreturntypeplugin.index.FieldReferenceAnalyzer
import com.ptby.dynamicreturntypeplugin.index.ReturnInitialisedSignatureConverter
import com.ptby.dynamicreturntypeplugin.index.VariableAnalyser
import com.ptby.dynamicreturntypeplugin.signature_extension.startsWithClassConstantPrefix
import com.ptby.dynamicreturntypeplugin.signature_processingv2.ListReturnPackaging

class CustomSignatureProcessor(private val returnInitialisedSignatureConverter: ReturnInitialisedSignatureConverter,
                                      private val classConstantWalker: ClassConstantWalker,
                                      private val fieldReferenceAnalyzer: FieldReferenceAnalyzer,
                                      private val variableAnalyser: VariableAnalyser) : ListReturnPackaging {
    private val logger = getInstance("DynamicReturnTypePlugin")


    fun processSignature(phpIndex: PhpIndex,
                         customMethodCallSignature: CustomMethodCallSignature,
                         project: Project,
                         signature: String): Collection<PhpNamedElement>? {

        var processedCustomMethodCallSignature = customMethodCallSignature
        val signatureMatcher = SignatureMatcher()
        if (signatureRequiresConversion(processedCustomMethodCallSignature, signatureMatcher)) {
            processedCustomMethodCallSignature = returnInitialisedSignatureConverter.convertSignatureToClassSignature(
                    processedCustomMethodCallSignature, project
            )
        }

        if (signatureMatcher.verifySignatureIsClassConstantFunctionCall(processedCustomMethodCallSignature)) {
            return phpIndex.getAnyByFQN(classConstantWalker.walkThroughConstants(
                    project,
                    processedCustomMethodCallSignature.rawStringSignature
            )
            )
        } else if (signatureMatcher.verifySignatureIsFieldCall(processedCustomMethodCallSignature)) {
            return fieldReferenceAnalyzer.getClassNameFromFieldLookup(processedCustomMethodCallSignature, project)
        } else if (signatureMatcher.verifySignatureIsMethodCall(processedCustomMethodCallSignature)) {
            return variableAnalyser.getClassNameFromVariableLookup(processedCustomMethodCallSignature, project)
        }

        return tryToDeferToDefaultType(processedCustomMethodCallSignature, signature, phpIndex)
    }

    private fun signatureRequiresConversion(processedCustomMethodCallSignature: CustomMethodCallSignature,
                                            signatureMatcher: SignatureMatcher): Boolean {
        return signatureMatcher.verifySignatureIsDeferredGlobalFunctionCall(processedCustomMethodCallSignature) ||
                signatureMatcher.verifySignatureIsFromReturnInitialiasedLocalObject(processedCustomMethodCallSignature)
    }


    fun tryFunctionCall(parameter: String,
                        phpIndex: PhpIndex,
                        project: Project): Collection<PhpNamedElement> {
        val signatureMatcher = SignatureMatcher()
        if (signatureMatcher.verifySignatureIsClassConstantFunctionCall(parameter)) {
            return phpIndex.getAnyByFQN(
                    classConstantWalker.walkThroughConstants( project, parameter)
            )
        }

        if ( requiresListPackaging(parameter)) {
            return packageList(parameter, project)
        }

        return tryToDeferToDefaultType(null, parameter, phpIndex)
    }


    private fun tryToDeferToDefaultType(customMethodCallSignature: CustomMethodCallSignature?,
                                        signature: String,
                                        phpIndex: PhpIndex): Collection<PhpNamedElement> {
        var cleanedSignature = signature.replace("\\\\", "\\")
        if (cleanedSignature.indexOf("#") != 0) {
            if (cleanedSignature.indexOf("\\") != 0) {
                cleanedSignature = "\\" + cleanedSignature
            }
            return phpIndex.getAnyByFQN(cleanedSignature)
        }

        cleanedSignature = cleanConstant(cleanedSignature)

        try {
            return phpIndex.getBySignature(cleanedSignature, null, 0)
        } catch (e: RuntimeException) {
            var signatureMessage = "function call"
            if (customMethodCallSignature != null) {
                signatureMessage = customMethodCallSignature.toString()
            }

            logger.warn(
                    "CustomSignatureProcessor.tryToDeferToDefaultType cannot decode " +
                            cleanedSignature + "\n"
                            + signatureMessage
            )

            return setOf()
        }
    }


    /**
     * Indicates something is wrong from one of the tests in the test environment but no inspections are raised.
     * Just a warning in idea. Will need further looking into.
     *
     * @param signature
     * @return
     */
    private fun cleanConstant(signature: String): String {
        var cleanedSignature = signature
        if (cleanedSignature.startsWithClassConstantPrefix() && !cleanedSignature.contains("|?")) {
            cleanedSignature += ".|?"
        }
        return cleanedSignature
    }
}
