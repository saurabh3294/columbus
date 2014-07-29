package com.proptiger.search;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.util.BytesRef;

public class AddPayloadToENGFilter  extends TokenFilter{
	
	private CharTermAttribute charTermAttr = addAttribute(CharTermAttribute.class);
	private OffsetAttribute offsetAttr = addAttribute(OffsetAttribute.class) ;
	private PayloadAttribute payloadAttr = addAttribute(PayloadAttribute.class);
	
	protected AddPayloadToENGFilter(TokenStream ts) {
		super(ts);
	}

	@Override
	public boolean incrementToken() throws IOException {
		if (!input.incrementToken()) {
			return false;
		}		
		
		float pld = (float) ((10.0/(offsetAttr.startOffset()+1))*Math.sqrt(charTermAttr.length()));
        byte[] data = PayloadHelper.encodeFloat(pld);
        BytesRef payload = new BytesRef(data);
        payloadAttr.setPayload(payload);

		return true;
	}
}
