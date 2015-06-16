package com.ptby.dynamicreturntypeplugin

import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2
import com.ptby.dynamicreturntypeplugin.config.ConfigState
import com.ptby.dynamicreturntypeplugin.config.ConfigStateContainer
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponseFactory
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer
import com.ptby.dynamicreturntypeplugin.index.FieldReferenceAnalyzer
import com.ptby.dynamicreturntypeplugin.index.LocalClassImpl
import com.ptby.dynamicreturntypeplugin.index.ReturnInitialisedSignatureConverter
import com.ptby.dynamicreturntypeplugin.index.VariableAnalyser
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser
import com.ptby.dynamicreturntypeplugin.scanner.FunctionCallReturnTypeScanner
import com.ptby.dynamicreturntypeplugin.scanner.MethodCallReturnTypeScanner
import com.ptby.dynamicreturntypeplugin.signatureconversion.BySignatureSignatureSplitter
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomSignatureProcessor

import java.util.ArrayList

import com.intellij.openapi.diagnostic.Logger.getInstance
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.impl.FunctionImpl
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.elements.Method
import com.ptby.dynamicreturntypeplugin.signature_processingv2.GetBySignature
import com.ptby.dynamicreturntypeplugin.signatureconversion.SignatureMatcher

public class DynamicReturnTypeProvider : PhpTypeProvider2 {
    private val classConstantAnalyzer: ClassConstantAnalyzer
    private val getTypeResponseFactory: GetTypeResponseFactory
    private val returnInitialisedSignatureConverter: ReturnInitialisedSignatureConverter
    private val logger = getInstance("DynamicReturnTypePlugin")
    private val fieldReferenceAnalyzer: FieldReferenceAnalyzer
    private val configState = ConfigStateContainer.configState
    private val configAnalyser = configState.configAnalyser

    private var variableAnalyser: VariableAnalyser
    init {
        fieldReferenceAnalyzer = FieldReferenceAnalyzer(configAnalyser)
        classConstantAnalyzer = ClassConstantAnalyzer()
        variableAnalyser = VariableAnalyser(configAnalyser, classConstantAnalyzer)
        returnInitialisedSignatureConverter = ReturnInitialisedSignatureConverter()
        variableAnalyser = VariableAnalyser(configAnalyser, classConstantAnalyzer)
        getTypeResponseFactory = createGetTypeResponseFactory(configAnalyser)
    }

    companion object {
        public val PLUGIN_IDENTIFIER_KEY: Char = "Ђ".toCharArray()[0]
        public val PLUGIN_IDENTIFIER_KEY_STRING: String = String(charArrayOf(PLUGIN_IDENTIFIER_KEY))
        public val PARAMETER_START_SEPARATOR: String = "ª"
        public val PARAMETER_ITEM_SEPARATOR: String = "♠"
        public val PARAMETER_END_SEPARATOR: String = "♣"
    }


    private fun createGetTypeResponseFactory(configAnalyser: ConfigAnalyser): GetTypeResponseFactory {
        val functionCallReturnTypeScanner = FunctionCallReturnTypeScanner()
        val methodCallReturnTypeScanner = MethodCallReturnTypeScanner()

        return GetTypeResponseFactory(configAnalyser, methodCallReturnTypeScanner, functionCallReturnTypeScanner)
    }


    override fun getKey(): Char {
        return PLUGIN_IDENTIFIER_KEY
    }


    override fun getType(psiElement: PsiElement): String? {
        try {
            val dynamicReturnType: GetTypeResponse = getTypeResponseFactory.createDynamicReturnType(psiElement)
            if (dynamicReturnType.isNull()) {
                return null
            }

            return dynamicReturnType.toString()
        } catch (e: Exception) {
            if (e !is ProcessCanceledException) {
                logger.error("Exception", e)
                e.printStackTrace()
            }
        }

        return null
    }

    override fun getBySignature(signature: String, project: Project): Collection<PhpNamedElement>? {
        val customSignatureProcessor = CustomSignatureProcessor(
                returnInitialisedSignatureConverter,
                ClassConstantAnalyzer(),
                fieldReferenceAnalyzer,
                variableAnalyser
        )

        val getBySignature = GetBySignature(
                SignatureMatcher(),
                classConstantAnalyzer,
                customSignatureProcessor,
                configAnalyser,
                fieldReferenceAnalyzer
        )

        return getBySignature.getBySignature(signature, project)
    }


}
