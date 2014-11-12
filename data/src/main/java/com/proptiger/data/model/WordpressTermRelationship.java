package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.core.model.BaseModel;

@Entity
@Table(name = "wp_term_relationships")
public class WordpressTermRelationship extends BaseModel {

    private static final long serialVersionUID = 3136863215048437655L;

    @Id
    @Column(name = "object_id")
    private long              objectId;

    @Column(name = "term_taxonomy_id")
    private long              termTaxonomyId;

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public long getTermTaxonomyId() {
        return termTaxonomyId;
    }

    public void setTermTaxonomyId(long termTaxonomyId) {
        this.termTaxonomyId = termTaxonomyId;
    }

}