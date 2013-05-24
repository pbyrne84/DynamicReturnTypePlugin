package com.ptby.dynamicreturntypeplugin;

import java.util.ArrayList;

public class ClassMethodConfigList extends ArrayList<ClassMethodConfig>{
    public ClassMethodConfigList( ClassMethodConfig... classMethodConfigs ){
        for ( ClassMethodConfig classMethodConfig : classMethodConfigs ) {
            add( classMethodConfig );
        }
    }
}
