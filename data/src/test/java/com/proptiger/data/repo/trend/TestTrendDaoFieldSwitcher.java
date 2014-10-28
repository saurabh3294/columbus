package com.proptiger.data.repo.trend;

import static org.testng.AssertJUnit.assertEquals;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.data.model.filter.JPAQueryBuilder;

public class TestTrendDaoFieldSwitcher {

    @Test
    public void testExtractActualFieldName() {
        String testMsg = "Method = extractActualFieldName,fieldname = ";
        String fieldName = "avgPricePerUnitArea";
        assertEquals(testMsg + fieldName, "pricePerUnitArea", JPAQueryBuilder.extractActualFieldName(fieldName));

        fieldName = "wavgPricePerUnitAreaOnSupply";
        assertEquals(testMsg + fieldName, "pricePerUnitArea", JPAQueryBuilder.extractActualFieldName(fieldName));

        fieldName = "wavgPricePerUnitAreaOnUnitsSold";
        assertEquals(testMsg + fieldName, "pricePerUnitArea", JPAQueryBuilder.extractActualFieldName(fieldName));

    }

    @Test
    public void TestGetFieldSwitchMap() {
        String testMsg = "Method = getFieldSwitchMap";

        TrendDaoFieldSwitcher tdfw = new TrendDaoFieldSwitcher();

        FIQLSelector selector = new FIQLSelector();
        selector.addField("avgPricePerUnitArea");
        selector.addField("wavgPricePerUnitAreaOnSupply");
        selector.addField("wavgPricePerUnitAreaOnUnitsSold");
        selector.addField("avgInventory");
        selector.addField("wavgInventoryOnSupply");
        selector.addField("avgInventoryOverhang");
        selector.addField("wavgInventoryOverhangOnSupply");
        selector.addField("wavgInventoryOverhangOnSupply");
        selector.addField("constructionStatus");
        selector.setGroup("quarter");

        HashMap<String, String> fieldSwitchMap = tdfw.getFieldSwitchMapForSelectorFields(selector);

        assertEquals(testMsg, "avgPricePerUnitAreaQuarter", fieldSwitchMap.get("avgPricePerUnitArea"));
        assertEquals(testMsg, "wavgPricePerUnitAreaQuarterOnLtdSupply", fieldSwitchMap.get("wavgPricePerUnitAreaOnSupply"));
        assertEquals(testMsg, "wavgPricePerUnitAreaQuarterOnUnitsSold", fieldSwitchMap.get("wavgPricePerUnitAreaOnUnitsSold"));
        assertEquals(testMsg, "avgInventoryQuarter", fieldSwitchMap.get("avgInventory"));
        assertEquals(testMsg, "wavgInventoryQuarterOnLtdSupply", fieldSwitchMap.get("wavgInventoryOnSupply"));
        assertEquals(testMsg, "avgInventoryOverhangQuarter", fieldSwitchMap.get("avgInventoryOverhang"));
        assertEquals(testMsg, "wavgInventoryOverhangQuarterOnLtdSupply", fieldSwitchMap.get("wavgInventoryOverhangOnSupply"));
        assertEquals(testMsg, "constructionStatusQuarter", fieldSwitchMap.get("constructionStatus"));
        
        selector = new FIQLSelector();
        selector.addField("avgPricePerUnitArea");
        selector.addField("wavgPricePerUnitAreaOnSupply");
        selector.addField("wavgPricePerUnitAreaOnUnitsSold");
        selector.addField("avgInventory");
        selector.addField("wavgInventoryOnSupply");
        selector.addField("avgInventoryOverhang");
        selector.addField("wavgInventoryOverhangOnSupply");
        selector.addField("wavgInventoryOverhangOnSupply");
        selector.addField("constructionStatus");
        selector.setGroup("month");

        fieldSwitchMap = tdfw.getFieldSwitchMapForSelectorFields(selector);

        assertEquals(testMsg, "avgPricePerUnitArea", fieldSwitchMap.get("avgPricePerUnitArea"));
        assertEquals(testMsg, "wavgPricePerUnitAreaOnLtdSupply", fieldSwitchMap.get("wavgPricePerUnitAreaOnSupply"));
        assertEquals(testMsg, "wavgPricePerUnitAreaOnUnitsSold", fieldSwitchMap.get("wavgPricePerUnitAreaOnUnitsSold"));
        assertEquals(testMsg, "avgInventory", fieldSwitchMap.get("avgInventory"));
        assertEquals(testMsg, "wavgInventoryOnLtdSupply", fieldSwitchMap.get("wavgInventoryOnSupply"));
        assertEquals(testMsg, "avgInventoryOverhang", fieldSwitchMap.get("avgInventoryOverhang"));
        assertEquals(testMsg, "wavgInventoryOverhangOnLtdSupply", fieldSwitchMap.get("wavgInventoryOverhangOnSupply"));
        assertEquals(testMsg, "constructionStatus", fieldSwitchMap.get("constructionStatus"));
        
        assertEquals(testMsg, selector.getFieldSet().size(), fieldSwitchMap.size());
        
    }

}
