package com.ptby.dynamicreturntypeplugin.index

import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.NamedStub
import com.intellij.util.IncorrectOperationException
import com.intellij.util.ui.EmptyIcon
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.PhpLanguage
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.impl.FieldImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomMethodCallSignature
import org.jetbrains.annotations.NonNls

import javax.swing.*
import java.util.ArrayList
import java.util.Collections


/**
 * As I haven't found a way to validate whether calls involving inheritance are valid with using the php index
 * calls with duplicate method names can get overridden by the loose getType call so this puts them back into their original state.
 */
public class OriginalCallAnalyzer {

    public fun getFieldInstanceOriginalReturnType(phpIndex: PhpIndex,
                                                  customMethodCallSignature: CustomMethodCallSignature,
                                                  project: Project): Collection<PhpNamedElement> {
        val methodSignatures = phpIndex.getBySignature(customMethodCallSignature.className, null, 0)

        if (methodSignatures.size() == 0) {
            return setOf()
        }

        val field = methodSignatures.iterator().next() as FieldImpl
        val classToFindOriginalTurnTypeOf = field.getType().toString()
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
                val returnType = method.getType().toString()
                val registeredTypes = phpIndex.getAnyByFQN(returnType)

                if (registeredTypes.size() > 0) {
                    return registeredTypes
                }

                val primitiveList = ArrayList<PhpNamedElement>()
                primitiveList.add(LocalClassImpl(method.getType(), project))
                return primitiveList
            }
        }

        return null
    }


    private fun findClassMethodByName(phpClass: PhpClass, methodName: String): Method? {
        for (method in phpClass.getMethods()) {
            if (method.getName() == methodName) {
                return method
            }
        }

        return null
    }


}


