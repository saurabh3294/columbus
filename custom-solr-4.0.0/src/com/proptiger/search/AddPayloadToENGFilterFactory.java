package com.proptiger.search;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class AddPayloadToENGFilterFactory extends TokenFilterFactory  {
	@Override
	public TokenStream create(TokenStream ts) {
		return new AddPayloadToENGFilter(ts);
	}
}
