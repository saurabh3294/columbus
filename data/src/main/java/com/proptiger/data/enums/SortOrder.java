package com.proptiger.data.enums;

/**
 * @author Rajeev Pandey
 * 
 */
public enum SortOrder {

    ASC("asc"), DESC("desc");

    private String sortOrder;

    private SortOrder(String val) {
        this.sortOrder = val;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return this.sortOrder;
    }

}
