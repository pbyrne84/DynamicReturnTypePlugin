package com.ptby.dynamicreturntypeplugin.config.valuereplacement;

import com.intellij.openapi.project.Project;

public interface ValueReplacementStrategy {

    String replaceCalculatedValue( Project project, String currentValue );
}
