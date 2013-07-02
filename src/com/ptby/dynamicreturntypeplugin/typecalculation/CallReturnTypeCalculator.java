package com.ptby.dynamicreturntypeplugin.typecalculation;

import com.jetbrains.php.lang.psi.elements.ClassReference;
import com.jetbrains.php.lang.psi.elements.FieldReference;
import com.jetbrains.php.lang.psi.elements.PhpExpression;
import com.jetbrains.php.lang.psi.elements.Variable;
import com.jetbrains.php.lang.psi.elements.impl.ClassReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.FieldReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.VariableImpl;
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse;
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.FieldReferenceAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.VariableAnalyser;
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


    public String calculateTypeFromMethodParameter( MethodReferenceImpl methodReference, int parameterIndex ) {
        PhpExpression classReference = methodReference.getClassReference();
        if ( classReference instanceof FieldReference ) {
            ParameterType parameterType = parameterTypeCalculator
                    .calculateTypeFromParameter( parameterIndex, methodReference.getParameters() );

            return fieldResponsePackager.packageFieldReference( methodReference, parameterType );
        }else if ( classReference instanceof Variable ) {
            ParameterType parameterType = parameterTypeCalculator
                    .calculateTypeFromParameter( parameterIndex, methodReference.getParameters() );

            return variableResponsePackager.packageVariableReference( methodReference, parameterType );
        }else if( classReference instanceof ClassReference ){
            ParameterType parameterType = parameterTypeCalculator
                    .calculateTypeFromParameter( parameterIndex, methodReference.getParameters() );
            return classResponsePackager.packageClassReference( methodReference, parameterType );
        }

        return parameterTypeCalculator.calculateTypeFromParameter( parameterIndex, methodReference.getParameters() )
                                      .toString();
    }


    public GetTypeResponse calculateTypeFromFunctionParameter( FunctionReferenceImpl functionReference, int parameterIndex ) {
        ParameterType functionReturnType = parameterTypeCalculator.calculateTypeFromParameter(
                parameterIndex, functionReference.getParameters()
        );

        return new GetTypeResponse( functionReturnType.toString()  );
    }



}