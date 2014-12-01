package com.proptiger.data.event.model.payload;

import java.math.BigInteger;

import com.proptiger.data.event.model.RawDBEvent;

public class NewsEventTypePayload extends EventTypePayload {

    private static final long serialVersionUID = -1143388818923609238L;

    private String            newsTitle;
    private String            newsBody;
    private Long              postId;
    private Long              termTaxonomyId;

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsBody() {
        return newsBody;
    }

    public void setNewsBody(String newsBody) {
        this.newsBody = newsBody;
    }

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

}
