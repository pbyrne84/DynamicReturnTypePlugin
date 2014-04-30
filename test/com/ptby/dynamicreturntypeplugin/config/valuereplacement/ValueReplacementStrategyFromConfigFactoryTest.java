package com.ptby.dynamicreturntypeplugin.config.valuereplacement;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ptby.dynamicreturntypeplugin.TestVirtualFile;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ValueReplacementStrategyFromConfigFactoryTest {


    private ValueReplacementStrategyFromConfigFactory valueReplacementStrategyFromConfigFactory;
    private TestVirtualFile testConfigFile;


    @Before
    public void setup() {
        valueReplacementStrategyFromConfigFactory = new ValueReplacementStrategyFromConfigFactory();
        testConfigFile = new TestVirtualFile();
    }


    @Test
    public void createFromJson_defaultPassthruConstruction() {
        JsonObject jsonObject = createJsonObject( "" );
        assertTrue( valueReplacementStrategyFromConfigFactory.createFromJson( "", jsonObject )
                        instanceof PassthruValueReplacementStrategy
        );
    }


    private JsonObject createJsonObject( String mask ) {
        String jsonString =
                "{\n" +
                        "\"position\": 0,\n" +
                        "\"mask\"    : \"" + mask + "\"" +
                        "}";

        Gson gson = new Gson();
        return gson.fromJson( jsonString, JsonObject.class );
    }


    @Test
    public void createFromJson_maskConstruction() {
        JsonObject customMaskJsonObject = createJsonObject( "custom%Mask" );
        MaskValueReplacementStrategy expected = new MaskValueReplacementStrategy( "custom%Mask" );
        ValueReplacementStrategy actual = valueReplacementStrategyFromConfigFactory.createFromJson(
                "",
                customMaskJsonObject
        );

        assertEquals( expected, actual );
    }


    @Test
    public void createFromJson_defaultsAsMaskHasNoPercentageSymbol() {
        JsonObject jsonObject = createJsonObject( "customMask" );
        assertTrue( valueReplacementStrategyFromConfigFactory.createFromJson( "", jsonObject )
                        instanceof PassthruValueReplacementStrategy
        );
    }
}
