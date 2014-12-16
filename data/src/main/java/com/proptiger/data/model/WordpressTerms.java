package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.proptiger.core.model.BaseModel;

@Entity
@Table(name = "wp_terms")
@NamedQueries({
        @NamedQuery(
                name = "Term.termsByTermTaxonomyId",
                query = "SELECT T FROM WordpressTerms AS T, WordpressTermTaxonomy AS TT WHERE T.termId = TT.termId AND TT.termTaxonomyId = :termTaxonomyId"),
        @NamedQuery(
                name = "Term.categoriesByPostId",
                query = "SELECT T FROM WordpressTerms AS T, WordpressTermTaxonomy AS TT, WordpressTermRelationship AS R " + " WHERE R.objectId = :postId AND "
                        + " R.termTaxonomyId = TT.termTaxonomyId AND "
                        + " TT.taxonomy = 'category' AND "
                        + " TT.termId = T.termId") })
public class WordpressTerms extends BaseModel {

    private static final long serialVersionUID = 806356402805138067L;

    @Id
    @Column(name = "term_id")
    private long              termId;

    @Column(name = "name")
    private String            name;

    public long getTermId() {
        return termId;
    }

    public void setTermId(long termId) {
        this.termId = termId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
