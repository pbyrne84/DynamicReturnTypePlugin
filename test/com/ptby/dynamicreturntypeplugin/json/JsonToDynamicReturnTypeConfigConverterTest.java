package com.ptby.dynamicreturntypeplugin.json;

import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfigList;
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig;
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfig;
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfigList;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class JsonToDynamicReturnTypeConfigConverterTest {
    @Test
    public void testConvertJson() throws Exception {


        String json = "{\n" +
                "    \"methodCalls\": [\n" +
                "        {\n" +
                "            \"class\": \"\\\\TaskData\",\n" +
                "            \"method\": \"getObject\",\n" +
                "            \"position\": 1\n" +
                "        },\n" +
                "        {\n" +
                "            \"class\": \"\\\\JE\\\\Test\\\\Phpunit\\\\PhockitoTestCase\",\n" +
                "            \"method\": \"getFullMock\",\n" +
                "            \"position\": 0\n" +
                "        },\n" +
                "        {\n" +
                "            \"class\": \"\\\\JE\\\\Test\\\\Phpunit\\\\PhockitoTestCase\",\n" +
                "            \"method\": \"verify\",\n" +
                "            \"position\": 0\n" +
                "        },\n" +
                "    ],\n" +
                "    \"functionCalls\": [\n" +
                "        {\n" +
                "            \"function\": \"\\\\verify\",\n" +
                "            \"position\": 0\n" +
                "        },\n" +
                "        {\n" +
                "            \"function\": \"\\\\mock\",\n" +
                "            \"position\": 0\n" +
                "        },\n" +
                "    ]\n" +
                "}\n" +
                "\n";

        JsonToDynamicReturnTypeConfigConverter jsonToDynamicReturnTypeConfigConverter = new JsonToDynamicReturnTypeConfigConverter();
        DynamicReturnTypeConfig actualReturnTypeConfig = jsonToDynamicReturnTypeConfigConverter.convertJson( json );

        List<ClassMethodConfig> classMethodConfigs = new ClassMethodConfigList(
                new ClassMethodConfig( "\\TaskData", "getObject", 1, "" ),
                new ClassMethodConfig( "\\JE\\Test\\Phpunit\\PhockitoTestCase", "getFullMock", 0,"" ),
                new ClassMethodConfig( "\\JE\\Test\\Phpunit\\PhockitoTestCase", "verify", 0,"" )
        );

        List<FunctionCallConfig> functionCallConfigs = new FunctionCallConfigList(
                new FunctionCallConfig( "\\verify", 0,"" ),
                new FunctionCallConfig( "\\mock", 0,"" )
        );

        DynamicReturnTypeConfig expectedReturnTypeConfig = new DynamicReturnTypeConfig( classMethodConfigs, functionCallConfigs );

        assertEquals( expectedReturnTypeConfig, actualReturnTypeConfig );

    }
}
