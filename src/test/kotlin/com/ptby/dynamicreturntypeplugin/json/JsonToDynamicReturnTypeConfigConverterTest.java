package com.ptby.dynamicreturntypeplugin.json;

import com.ptby.dynamicreturntypeplugin.TestVirtualFile;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfigKt;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfigList;
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig;
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfigKt;
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfigList;
import com.ptby.dynamicreturntypeplugin.config.valuereplacement.PassthruValueReplacementStrategy;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;


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
        DynamicReturnTypeConfig actualReturnTypeConfig = jsonToDynamicReturnTypeConfigConverter.convertJson(
                new TestVirtualFile( json )
        );

        PassthruValueReplacementStrategy replacementStrategy = new PassthruValueReplacementStrategy();
        List<ClassMethodConfigKt> classMethodConfigs = new ClassMethodConfigList(
                new ClassMethodConfigKt( "\\TaskData", "getObject", 1, replacementStrategy ),
                new ClassMethodConfigKt( "\\JE\\Test\\Phpunit\\PhockitoTestCase", "getFullMock", 0, replacementStrategy ),
                new ClassMethodConfigKt( "\\JE\\Test\\Phpunit\\PhockitoTestCase", "verify", 0, replacementStrategy )
        );

        List<FunctionCallConfigKt> functionCallConfigs = new FunctionCallConfigList(
                new FunctionCallConfigKt( "\\verify", 0, replacementStrategy ),
                new FunctionCallConfigKt( "\\mock", 0, replacementStrategy )
        );

        DynamicReturnTypeConfig expectedReturnTypeConfig = new DynamicReturnTypeConfig( classMethodConfigs, functionCallConfigs );

        assertEquals( expectedReturnTypeConfig, actualReturnTypeConfig );
    }



}
