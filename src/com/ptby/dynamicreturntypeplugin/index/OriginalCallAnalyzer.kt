package com.ptby.dynamicreturntypeplugin.index

import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomMethodCallSignature
import java.util.*


/**
 * As I haven't found a way to validate whether calls involving inheritance are valid with using the php index
 * calls with duplicate method names can get overridden by the loose getType call so this puts them back into their original state.
 */
public class OriginalCallAnalyzer {

    public fun getFieldInstanceOriginalReturnType(phpIndex: PhpIndex,
                                                  customMethodCallSignature: CustomMethodCallSignature,
                                                  project: Project): Collection<PhpNamedElement> {
        val methodSignatures = phpIndex.getBySignature(customMethodCallSignature.className, null, 0)

        if (methodSignatures.size == 0) {
            return setOf()
        }

        val field = methodSignatures.iterator().next() as Field
        val classToFindOriginalTurnTypeOf = field.type.toString()
        val typeCollection: Collection<PhpNamedElement>? = getMethodCallReturnType(
                phpIndex,
                classToFindOriginalTurnTypeOf,
                customMethodCallSignature.method,
                project
        )

        if ( typeCollection != null ) {
            return typeCollection
        }


        return methodSignatures
    }


    public fun getMethodCallReturnType(phpIndex: PhpIndex,
                                       className: String,
                                       calledMethod: String,
                                       project: Project): Collection<PhpNamedElement>? {

        val anyByFQN = phpIndex.getAnyByFQN(className)
        for (phpClass in anyByFQN) {
            val method = findClassMethodByName(phpClass, calledMethod)
            if (method != null ) {
                val returnType = method.type.toString()
                val registeredTypes = phpIndex.getAnyByFQN(returnType)

                if (registeredTypes.size > 0) {
                    return registeredTypes
                }

                val primitiveList = ArrayList<PhpNamedElement>()
                primitiveList.add(LocalClassImpl(method.type, project))
                return primitiveList
            }
        }

        return null
    }


    private fun findClassMethodByName(phpClass: PhpClass, methodName: String): Method? {
        for (method in phpClass.methods) {
            if (method.name == methodName) {
                return method
            }
        }

        return null
    }


}


