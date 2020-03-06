package com.ptby.dynamicreturntypeplugin.config

import com.intellij.openapi.project.Project

interface ParameterValueFormatter {
    fun formatBeforeLookup(project : Project, passedType: String?): String

    fun getParameterIndex(): Int
}