/**
 * 
 */
package com.proptiger.data.model;

/**
 * @author mandeep
 *
 */
public class SolrResult extends Property {
    Property property;
    Project project;

    public SolrResult() {
        property = new Property();
        project = new Project();
        property.setProject(project);
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
