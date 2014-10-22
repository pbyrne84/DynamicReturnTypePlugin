package com.ptby.dynamicreturntypeplugin.config

import java.util.ArrayList

public class ClassMethodConfigList(vararg classMethodConfigs: ClassMethodConfigKt) : ArrayList<ClassMethodConfigKt>() {
    {
        for (classMethodConfig in classMethodConfigs) {
            add(classMethodConfig)
        }
    }
}
