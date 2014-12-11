package com.proptiger.data.event.model.payload;

import java.math.BigInteger;

import com.proptiger.data.event.model.RawDBEvent;

public class NewsEventTypePayload extends EventTypePayload {

    private static final long serialVersionUID = -1143388818923609238L;

    private Long              postId;
    private Long              termTaxonomyId;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getTermTaxonomyId() {
        return termTaxonomyId;
    }

    public void setTermTaxonomyId(Long termTaxonomyId) {
        this.termTaxonomyId = termTaxonomyId;
    }

    @Override
    public void populatePayloadValues(RawDBEvent rawDBEvent, String attributeName) {
        this.postId = ((BigInteger) rawDBEvent.getNewDBValueMap().get("object_id")).longValue();
        this.termTaxonomyId = ((BigInteger) rawDBEvent.getNewDBValueMap().get("term_taxonomy_id")).longValue();
    }

    @Override
    public Object getPayloadValues() {
        // TODO Auto-generated method stub
        return null;
    }

}
