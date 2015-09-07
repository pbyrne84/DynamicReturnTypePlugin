package com.ptby.dynamicreturntypeplugin.signature_processingv2

import com.jetbrains.php.lang.psi.elements.PhpNamedElement

data class ReturnType(val phpNamedElements: Collection<PhpNamedElement>?) {
    private var fqnClassName: String? = null

    fun getClassName(): String {
        if ( fqnClassName == null ) {
            fqnClassName = ""
            if ( phpNamedElements != null && hasFoundReturnType() ) {
                val phpNamedElement = phpNamedElements.iterator().next()
                fqnClassName = if ( phpNamedElement.getFQN() == null ) {
                    ""
                } else {
                    phpNamedElement.getFQN()
                }
            }
        }

        return fqnClassName as String
    }

    fun hasFoundReturnType(): Boolean {
        return phpNamedElements != null && phpNamedElements.size() > 0
    }


    companion object {
        fun empty(): ReturnType {
            return ReturnType(setOf())
        }
    }

}