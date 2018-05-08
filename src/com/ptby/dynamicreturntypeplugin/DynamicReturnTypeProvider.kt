package com.ptby.dynamicreturntypeplugin

import com.intellij.openapi.diagnostic.Logger.getInstance
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.project.Project
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.impl.ClassConstantReferenceImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider3
import com.ptby.dynamicreturntypeplugin.config.ConfigStateContainer
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponseFactory
import com.ptby.dynamicreturntypeplugin.index.ClassConstantWalker
import com.ptby.dynamicreturntypeplugin.index.FieldReferenceAnalyzer
import com.ptby.dynamicreturntypeplugin.index.ReturnInitialisedSignatureConverter
import com.ptby.dynamicreturntypeplugin.index.VariableAnalyser
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser
import com.ptby.dynamicreturntypeplugin.scanner.FunctionCallReturnTypeScanner
import com.ptby.dynamicreturntypeplugin.scanner.MethodCallReturnTypeScanner
import com.ptby.dynamicreturntypeplugin.signature_extension.cleanNestedSignatureMangling
import com.ptby.dynamicreturntypeplugin.signature_processingv2.GetBySignature
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomSignatureProcessor

class DynamicPhpType( signature: String ) : PhpType(){



}

class DynamicReturnTypeProvider : PhpTypeProvider3 {
    private val classConstantWalker: ClassConstantWalker
    private val getTypeResponseFactory: GetTypeResponseFactory
    private val returnInitialisedSignatureConverter: ReturnInitialisedSignatureConverter
    private val logger = getInstance("DynamicReturnTypePlugin")
    private val fieldReferenceAnalyzer: FieldReferenceAnalyzer
    private val configState = ConfigStateContainer.configState
    private val configAnalyser = configState.configAnalyser

    private var variableAnalyser: VariableAnalyser

    init {
        fieldReferenceAnalyzer = FieldReferenceAnalyzer(configAnalyser)
        classConstantWalker = ClassConstantWalker()
        variableAnalyser = VariableAnalyser(configAnalyser, classConstantWalker)
        returnInitialisedSignatureConverter = ReturnInitialisedSignatureConverter()
        variableAnalyser = VariableAnalyser(configAnalyser, classConstantWalker)
        getTypeResponseFactory = createGetTypeResponseFactory(configAnalyser)
    }

    companion object {
        const val PLUGIN_IDENTIFIER_KEY: Char = '☘'
        const val PLUGIN_IDENTIFIER_KEY_STRING: String = "☘"
        const val PARAMETER_START_SEPARATOR: String = "ª"
        const val PARAMETER_ITEM_SEPARATOR: String = "◮"
        const val PARAMETER_END_SEPARATOR: String = "♣"
    }


    private fun createGetTypeResponseFactory(configAnalyser: ConfigAnalyser): GetTypeResponseFactory {
        val functionCallReturnTypeScanner = FunctionCallReturnTypeScanner()
        val methodCallReturnTypeScanner = MethodCallReturnTypeScanner()

        return GetTypeResponseFactory(configAnalyser, methodCallReturnTypeScanner, functionCallReturnTypeScanner)
    }


    override fun getKey(): Char {
        return PLUGIN_IDENTIFIER_KEY
    }


    override fun getType(psiElement: PsiElement):  PhpType? {


        try {
            val dynamicReturnType: GetTypeResponse = getTypeResponseFactory.createDynamicReturnType(psiElement)
            if (dynamicReturnType.isNull()) {
                return null
            }

            return     PhpType.builder().add( "#" + this.key + dynamicReturnType.getSignature()).build()
        } catch (e: Exception) {
            if (e !is ProcessCanceledException) {
                logger.error("Exception", e)
                e.printStackTrace()
            }
        }

        return null
    }


    private fun debug(psiElement: PsiElement) {
        if (PlatformPatterns.psiElement(PhpElementTypes.METHOD_REFERENCE).accepts(psiElement)) {
            val methodRef = psiElement as MethodReference
            if (methodRef.parameters.size == 1) {
                val ref = methodRef.parameters[0] as ClassConstantReferenceImpl

                println(ref.signature)
            }
        }
    }

    override fun getBySignature(signature :String, visited: MutableSet<String>?, depth: Int, project: Project): Collection< PhpNamedElement>? {
        val customSignatureProcessor = CustomSignatureProcessor(
                returnInitialisedSignatureConverter,
                classConstantWalker,
                fieldReferenceAnalyzer,
                variableAnalyser
        )

        val getBySignature = GetBySignature(
                customSignatureProcessor,
                configAnalyser
        )

        return getBySignature.getBySignature(signature.cleanNestedSignatureMangling(), project)
    }




}
