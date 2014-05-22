package com.proptiger.data.enums;

/**
 * Document types constants
 * 
 * @author Rajeev Pandey
 * 
 */
public enum DocumentType {
    LOCALITY("LOCALITY"), BUILDER("BUILDER"), CITY("CITY"), PROJECT("PROJECT"), LANDMARK("LANDMARK");

    private String type;

    private DocumentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    /**
     * Creating document type filter string
     * 
     * @param documentType
     * @return
     */
    public static String getDocumentTypeFilter(DocumentType documentType) {
        return "DOCUMENT_TYPE:" + documentType.getType();
    }
}
