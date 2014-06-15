package com.proptiger.data.repo.trend;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.pojo.FIQLSelector;

public class TrendDaoFieldSwitcher {

    private ArrayList<String> markedBaseFieldList;
    private ArrayList<String> markedGroupList;

    public TrendDaoFieldSwitcher() {
        markedBaseFieldList = new ArrayList<String>();
        markedGroupList = new ArrayList<String>();
        fillMarkedBaseFieldList();
        fillMarkedGroupList();
    }

    /**
     * @param selector
     * @return returned map will contain mappings for ALL fields with their
     *         switched versions. If a field is not switched then map will have
     *         key=value=fieldname
     * 
     */
    public HashMap<String, String> getFieldSwitchMap(FIQLSelector selector) {

        HashMap<String, String> fieldSwitchMap = new HashMap<String, String>();

        String groupName = selector.getGroup();

        /* Lists are used because order is important */
        ArrayList<String> switchedfieldList = new ArrayList<String>(selector.getFieldSet());

        /*
         * Traverse the field-list and replace if 1. if a field is marked for
         * manual-override. 2. if a field fulfills the group-based-switch. in
         * the above order.
         */
        for (int i = 0; i < switchedfieldList.size(); i++) {
            String fieldName = switchedfieldList.get(i);

            /* Process manual-override based switch */
            String switchedName = StringUtils.replace(switchedfieldList.get(i), "OnSupply", "OnLtdSupply");

            if (groupName == null || !markedGroupList.contains(groupName)) {
                fieldSwitchMap.put(fieldName, switchedName);
                continue;
            }

            /* Process group-based-override switch */

            String baseFieldName = JPAQueryBuilder.extractActualFieldName(switchedName);
            if (!markedBaseFieldList.contains(baseFieldName)) {
                fieldSwitchMap.put(fieldName, switchedName);
                continue;
            }
            String baseFieldNameCaps = StringUtils.capitalize(baseFieldName);
            switchedName = StringUtils.replace(
                    switchedName,
                    baseFieldNameCaps,
                    baseFieldNameCaps + StringUtils.capitalize(groupName));

            /* put the 'original' field-name and the 'final' field-name in map */
            fieldSwitchMap.put(fieldName, switchedName);
        }

        return fieldSwitchMap;
    }

    private void fillMarkedBaseFieldList() {
        markedBaseFieldList.add("pricePerUnitArea");
        markedBaseFieldList.add("inventory");
        markedBaseFieldList.add("inventoryOverhang");
        markedBaseFieldList.add("ltdLaunchedUnit");
    }

    private void fillMarkedGroupList() {
        markedGroupList.add("quarter");
        markedGroupList.add("year");
        markedGroupList.add("financialYear");
    }

}
