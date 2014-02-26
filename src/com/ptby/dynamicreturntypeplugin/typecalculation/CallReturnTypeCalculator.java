package com.ptby.dynamicreturntypeplugin.typecalculation;

import com.jetbrains.php.lang.psi.elements.*;
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse;
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;
import com.ptby.dynamicreturntypeplugin.responsepackaging.ClassResponsePackager;
import com.ptby.dynamicreturntypeplugin.responsepackaging.FieldResponsePackager;
import com.ptby.dynamicreturntypeplugin.responsepackaging.VariableResponsePackager;

public class CallReturnTypeCalculator {

    private final ParameterTypeCalculator parameterTypeCalculator;
    private final FieldResponsePackager fieldResponsePackager;
    private final VariableResponsePackager variableResponsePackager;
    private final ClassResponsePackager classResponsePackager;


    public CallReturnTypeCalculator() {
        parameterTypeCalculator = new ParameterTypeCalculator( new ClassConstantAnalyzer() );
        fieldResponsePackager = new FieldResponsePackager();
        variableResponsePackager = new VariableResponsePackager();
        classResponsePackager = new ClassResponsePackager();
    }


    public GetTypeResponse calculateTypeFromMethodParameter( MethodReference methodReference, int parameterIndex ) {
        PhpExpression classReference = methodReference.getClassReference();
        if ( classReference instanceof FieldReference ) {
            return fieldResponsePackager.packageFieldReference(
                    methodReference, createParameterType( methodReference, parameterIndex )
            );
        } else if ( classReference instanceof Variable ) {
            return variableResponsePackager.packageVariableReference(
                    methodReference, createParameterType( methodReference, parameterIndex )
            );
        } else if ( classReference instanceof ClassReference || classReference instanceof MethodReference ) {
            return classResponsePackager.packageClassReference(
                    methodReference, createParameterType( methodReference, parameterIndex )
            );
        }

        ParameterType parameterType = parameterTypeCalculator
                .calculateTypeFromParameter( parameterIndex, methodReference.getParameters() );

        return new GetTypeResponse( parameterType.toString() );
    }


    private ParameterType createParameterType( MethodReference methodReference, int parameterIndex ) {
        return parameterTypeCalculator
                .calculateTypeFromParameter( parameterIndex, methodReference.getParameters() );
    }


    public GetTypeResponse calculateTypeFromFunctionParameter( FunctionReference functionReference, int parameterIndex ) {
        ParameterType functionReturnType = parameterTypeCalculator.calculateTypeFromParameter(
                parameterIndex, functionReference.getParameters()
        );

        return new GetTypeResponse( functionReturnType.toString() );
    }


}