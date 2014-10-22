package com.ptby.dynamicreturntypeplugin.typecalculation

import com.jetbrains.php.lang.psi.elements.*
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer
import com.ptby.dynamicreturntypeplugin.responsepackaging.ClassResponsePackager
import com.ptby.dynamicreturntypeplugin.responsepackaging.FieldResponsePackager
import com.ptby.dynamicreturntypeplugin.responsepackaging.VariableResponsePackager

public class CallReturnTypeCalculator {

    private val parameterTypeCalculator: ParameterTypeCalculator
    private val fieldResponsePackager: FieldResponsePackager
    private val variableResponsePackager: VariableResponsePackager
    private val classResponsePackager: ClassResponsePackager


    {
        parameterTypeCalculator = ParameterTypeCalculator(ClassConstantAnalyzer())
        fieldResponsePackager = FieldResponsePackager()
        variableResponsePackager = VariableResponsePackager()
        classResponsePackager = ClassResponsePackager()
    }


    public fun calculateTypeFromMethodParameter(methodReference: MethodReference, parameterIndex: Int): GetTypeResponse {
        val classReference = methodReference.getClassReference()
        if (classReference is FieldReference) {
            return fieldResponsePackager.packageFieldReference(methodReference, createParameterType(methodReference, parameterIndex))
        } else if (classReference is Variable) {
            return variableResponsePackager.packageVariableReference(methodReference, createParameterType(methodReference, parameterIndex))
        } else if (classReference is ClassReference || classReference is MethodReference) {
            return classResponsePackager.packageClassReference(methodReference, createParameterType(methodReference, parameterIndex))
        }

        val parameterType = parameterTypeCalculator.calculateTypeFromParameter(parameterIndex, methodReference.getParameters())

        return GetTypeResponse(parameterType.toNullableString())
    }


    private fun createParameterType(methodReference: MethodReference, parameterIndex: Int): ParameterType {
        return parameterTypeCalculator.calculateTypeFromParameter(parameterIndex, methodReference.getParameters())
    }


    public fun calculateTypeFromFunctionParameter(functionReference: FunctionReference, parameterIndex: Int): GetTypeResponse {
        val functionReturnType = parameterTypeCalculator.calculateTypeFromParameter(parameterIndex, functionReference.getParameters())

        return GetTypeResponse(functionReturnType.toNullableString())
    }


}