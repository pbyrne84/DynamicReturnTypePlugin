package com.ptby.dynamicreturntypeplugin.config.multi;

import com.intellij.openapi.vfs.VirtualFile;
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig;
import com.ptby.dynamicreturntypeplugin.json.JsonToDynamicReturnTypeConfigConverter;

import java.io.IOException;
import java.util.Collection;

public class ProjectConfigBuilder {

    private final JsonToDynamicReturnTypeConfigConverter jsonToDynamicReturnTypeConfigConverter;

    public ProjectConfigBuilder() {
        jsonToDynamicReturnTypeConfigConverter = new JsonToDynamicReturnTypeConfigConverter();
    }

    public DynamicReturnTypeConfig createConfigFromFileList( Collection<VirtualFile> configFiles ) throws IOException {
        DynamicReturnTypeConfig dynamicReturnTypeConfig = new DynamicReturnTypeConfig();

        for ( VirtualFile configFile : configFiles ) {
            DynamicReturnTypeConfig currentConfig = jsonToDynamicReturnTypeConfigConverter.convertJson(
                    configFile
            );

            dynamicReturnTypeConfig.merge( currentConfig );
        }

        return dynamicReturnTypeConfig;
    }
}
