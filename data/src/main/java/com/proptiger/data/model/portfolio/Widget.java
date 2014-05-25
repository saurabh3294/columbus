package com.proptiger.data.model.portfolio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

/**
 * Model object for widget resource
 * 
 * @author Rajeev Pandey
 * 
 */
@Entity
@Table(name = "widgets")
public class Widget{

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer    id;

    @Column(name = "name")
    private String     name;

    @Column(name = "tag")
    private String     tag;

    @Column(name = "content")
    private String     content;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private WidgetType type;

    @Column(name = "created_at")
    private Date       createdAt;

    @Column(name = "updated_at")
    private Date       updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param tag
     *            the tag to set
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content
     *            the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the type
     */
    public WidgetType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(WidgetType type) {
        this.type = type;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Date();
    }

    @PrePersist
    public void prePersist() {
        createdAt = new Date();
        updatedAt = createdAt;
    }

}
