package com.proptiger.search;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;

public class PLDisMaxQParserPlugin extends QParserPlugin {
	  @Override
	  public void init(NamedList args) {
	  }
	@Override
	public QParser createParser(String qstr, SolrParams localParams,
			SolrParams params, SolrQueryRequest req) {
		return new PLDisMaxQParser(qstr, localParams, params, req);
	}
}