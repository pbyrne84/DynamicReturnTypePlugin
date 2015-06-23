package com.ptby.dynamicreturntypeplugin.scanner

import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfigKt
import com.ptby.dynamicreturntypeplugin.gettype.ArrayAccessGetTypeResponse
import com.ptby.dynamicreturntypeplugin.gettype.FunctionReferenceGetTypeResponse
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse

public class MethodCallReturnTypeScanner() {


    public fun getTypeFromMethodCall(classMethodConfigList: List<ClassMethodConfigKt>,
                                     methodReference: MethodReference): FunctionReferenceGetTypeResponse {

        for (classMethodConfig in classMethodConfigList) {
            if (classMethodConfig.equalsMethodReferenceName(methodReference)) {
                return FunctionReferenceGetTypeResponse.newMethod(methodReference)
            }
        }

        return FunctionReferenceGetTypeResponse.createNull()
    }


    public fun getTypeFromArrayAccess(arrayAccessExpression: ArrayAccessExpression): GetTypeResponse {

        return ArrayAccessGetTypeResponse.newArrayAccess(arrayAccessExpression)
    }


}
