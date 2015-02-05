package com.ptby.dynamicreturntypeplugin.scanner

import com.jetbrains.php.lang.psi.elements.MethodReference
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfigKt
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse

public class MethodCallReturnTypeScanner() {


    public fun getTypeFromMethodCall(classMethodConfigList: List<ClassMethodConfigKt>,
                                     methodReference: MethodReference): GetTypeResponse {

        for (classMethodConfig in classMethodConfigList) {
            if (classMethodConfig.equalsMethodReferenceName(methodReference)) {
                val getTypeResponse = GetTypeResponse.newMethod( methodReference )
                if (!getTypeResponse.isNull()) {
                    return getTypeResponse
                }
            }
        }

        return GetTypeResponse.createNull()
    }


}
