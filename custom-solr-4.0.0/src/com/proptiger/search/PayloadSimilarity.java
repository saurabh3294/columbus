package com.proptiger.search;

import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.Norm;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRef;

public class PayloadSimilarity extends DefaultSimilarity{
	
	@Override
	public float tf(float feq) {
		return 1.0f;
	}
	
	@Override
	public float idf(long docFreq, long numDocs) {
		return 1.0f;
	}
	
//	@Override
//	  public void computeNorm(FieldInvertState state, Norm norm) {
//	    norm.setByte(encodeNormValue(state.getBoost() * ((float) (1.0))));
//	}
	
   @Override 
	 public float scorePayload(int doc, int start, int end, BytesRef payload)
	 {
	 if (payload!=null) {
	 float pscore = PayloadHelper.decodeFloat(payload.bytes,payload.offset);
	 return pscore;
	 }
	 return 1.0f;
	 }
   
  public void test(){
    
  }
}