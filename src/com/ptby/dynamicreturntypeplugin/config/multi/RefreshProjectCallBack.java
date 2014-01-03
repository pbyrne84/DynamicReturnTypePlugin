package com.ptby.dynamicreturntypeplugin.config.multi;

import com.intellij.openapi.project.Project;
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig;

public interface RefreshProjectCallBack {

    public void setConfig( Project project, DynamicReturnTypeConfig config );

}
