package com.ptby.dynamicreturntypeplugin.config;

import java.util.ArrayList;

public class ClassMethodConfigList extends ArrayList<ClassMethodConfigKt>{
    public ClassMethodConfigList( ClassMethodConfigKt... classMethodConfigs ){
        for ( ClassMethodConfigKt classMethodConfig : classMethodConfigs ) {
            add( classMethodConfig );
        }
    }
}
