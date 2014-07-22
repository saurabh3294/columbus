package com.proptiger.data.repo.trend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
    public HashMap<String, String> getFieldSwitchMapForSelectorFields(FIQLSelector selector) {

        Set<String> groupSet = selector.getGroupSet();
        String groupName = getMarkedGroupFromGroupSet(groupSet);

        /* Lists are used because order is important */
        ArrayList<String> switchedfieldList = new ArrayList<String>(selector.getFieldSet());

        HashMap<String, String> fieldSwitchMap = getFieldSwitchMap(switchedfieldList, groupName);

        return fieldSwitchMap;
    }

    /**
     * @param selector
     * @return 'sorter' with fields switched.
     */
    public String getFieldSwitchedSorter(FIQLSelector selector) {

        Set<String> groupSet = selector.getGroupSet();
        String groupName = getMarkedGroupFromGroupSet(groupSet);

        String sorterOrg = selector.getSort();
        if (sorterOrg == null || sorterOrg.isEmpty()) {
            return sorterOrg;
        }
        List<String> sortFields = Arrays.asList(StringUtils.split(sorterOrg, ","));
        List<String> sortFieldsWithoutSign = Arrays.asList(StringUtils.split(
                sorterOrg,
                "," + FIQLSelector.FIQLSortDescSymbol));

        HashMap<String, String> fieldSwitchMap = getFieldSwitchMap(sortFieldsWithoutSign, groupName);

        /* making new sorter with fields switched */
        FIQLSelector newSelector = new FIQLSelector();
        char FIQLSortDescSymbolChar = FIQLSelector.FIQLSortDescSymbol.charAt(0);
        String switchedField;
        for (String sf : sortFields) {
            /* if a field has '-' sign, remove the sign and then lookup */
            if (sf.charAt(0) == FIQLSortDescSymbolChar) {
                switchedField = fieldSwitchMap.get(sf.substring(1));
                newSelector.addSortDESC(switchedField);
            }
            else {
                switchedField = fieldSwitchMap.get(sf);
                newSelector.addSortASC(switchedField);
            }
        }

        return newSelector.getSort();
    }

    private HashMap<String, String> getFieldSwitchMap(List<String> switchedfieldList, String groupName) {
        HashMap<String, String> fieldSwitchMap = new HashMap<String, String>();

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

    private String getMarkedGroupFromGroupSet(Set<String> groupSet) {
        for (String groupName : groupSet) {
            if (markedGroupList.contains(groupName)) {
                return groupName;
            }
        }
        return null;
    }

    private void fillMarkedBaseFieldList() {
        markedBaseFieldList.add("pricePerUnitArea");
        markedBaseFieldList.add("inventory");
        markedBaseFieldList.add("inventoryOverhang");
        markedBaseFieldList.add("ltdLaunchedUnit");
        markedBaseFieldList.add("rateOfSale");
        markedBaseFieldList.add("constructionStatus");

    }

    private void fillMarkedGroupList() {
        markedGroupList.add("quarter");
        markedGroupList.add("year");
        markedGroupList.add("financialYear");
    }

}
