package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.service.SeoPageService;
import com.proptiger.data.service.SuburbService;

@Entity
@Table(name = "seo_meta_content_templates")
@JsonInclude(Include.NON_NULL)
public class SeoPage extends BaseModel {
    /**
     * 
     */
    private static final long serialVersionUID = 6065883078087404621L;

    // @TODO get reference to the field Name like getting reference to class.
    /**
     * @Note The token should be in lowercase.
     * @author mukand
     * TODO scope of improvements in the enums. (The field selection heirarchy should be field1.field2.field3 ... instead of field1, field2).
     *       The enum value should contain the fields for adding prefix or suffix for token replacement.
     */
    public enum Tokens {
        /*
         * ENUM_NAME(token name, field1, field2)
         * They will be mapped with CompositeSeoTokenData class. 
         * field1 corresponds to the field name in the above mentioned class.
         * field2 corresponds to the field name in the field1 class. if field1 is null then in above mentioned class.
         */
        Locality("<locality>", "locality", "label"), City("<city>", "city", "label"), Suburb("<suburb>", "suburb",
                "label"), BuiderName("<builder name>", "builder", "name"), ProjectName("<project name>", "project",
                "name"), UnitName("<unit name>", "property", "unitName"), BHK("<bhk>", null, "bedroomsStr"), PriceRange(
                "<price range>", null, "priceRangeStr"), Bathrooms("<t>","property","bathrooms");

        private String value;
        private String fieldName1;
        private String fieldName2;

        Tokens(String value, String className, String fieldName) {
            this.value = value;
            this.fieldName1 = className;
            this.fieldName2 = fieldName;
        }

        public String getToken(Tokens token) {
            return token.value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getFieldName1() {
            return fieldName1;
        }

        public String getFieldName2() {
            return fieldName2;
        }
    }

    @Column(name = "template_name")
    @JsonIgnore
    @Id
    private String templateId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "keywords")
    private String keywords;

    @Column(name = "h1")
    private String h1;

    @Column(name = "h2")
    private String h2;

    @Column(name = "h3")
    private String h3;

    @Column(name = "h4")
    private String h4;

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getH1() {
        return h1;
    }

    public void setH1(String h1) {
        this.h1 = h1;
    }

    public String getH2() {
        return h2;
    }

    public void setH2(String h2) {
        this.h2 = h2;
    }

    public String getH3() {
        return h3;
    }

    public void setH3(String h3) {
        this.h3 = h3;
    }

    public String getH4() {
        return h4;
    }

    public void setH4(String h4) {
        this.h4 = h4;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
}
