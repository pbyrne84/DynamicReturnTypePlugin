package com.ptby.dynamicreturntypeplugin.callvalidator

import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfigKt
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser

public class MethodCallValidator(private val configAnalyser: ConfigAnalyser) {


    public fun getMatchingConfig(phpIndex: PhpIndex,
                                 project: Project,
                                 calledMethod: String,
                                 cleanedVariableSignature: String,
                                 fieldElements: Collection<PhpNamedElement>): ClassMethodConfigKt? {

        for (fieldElement in fieldElements) {
            val currentConfig = configAnalyser.getCurrentConfig(project)
            val fieldElementType = fieldElement.type
            for (classMethodConfig in currentConfig.classMethodConfigs) {
                if (classMethodConfig.methodCallMatches(fieldElementType.toString(), calledMethod)) {
                    return classMethodConfig
                }

                val hasConfiguredSuperClassForMethod = attemptSuperLookup(
                        phpIndex,
                        calledMethod,
                        cleanedVariableSignature,
                        classMethodConfig
                )

                if (hasConfiguredSuperClassForMethod) {
                    return classMethodConfig
                }
            }
        }

        return null
    }


    private fun attemptSuperLookup(phpIndex: PhpIndex,
                                   calledMethod: String,
                                   cleanedVariableSignature: String,
                                   classMethodConfig: ClassMethodConfigKt): Boolean {

        if (!classMethodConfig.equalsMethodName(calledMethod)) {
            return false
        }

        val actualFqnClassName = cleanedVariableSignature.substring(2)
        val expectedFqnClassName = classMethodConfig.fqnClassName

        return PhpType.findSuper(expectedFqnClassName, actualFqnClassName, phpIndex)
    }
}