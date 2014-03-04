package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "wp_term_taxonomy")
public class WordpressTermTaxonomy extends BaseModel {

    private static final long serialVersionUID = 345497454514698605L;

    @Id
    @Column(name = "term_taxonomy_id")
    private long              termTaxonomyId;

    @Column(name = "term_id")
    private long              termId;

    public long getTermTaxonomyId() {
        return termTaxonomyId;
    }

    public void setTermTaxonomyId(long termTaxonomyId) {
        this.termTaxonomyId = termTaxonomyId;
    }

    public long getTermId() {
        return termId;
    }

    public void setTermId(long termId) {
        this.termId = termId;
    }

}
