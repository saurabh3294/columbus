
package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.base.Joiner;
import com.proptiger.data.model.City;
import com.proptiger.data.model.Typeahead;
import com.proptiger.data.service.portfolio.CityService;
import com.proptiger.data.service.portfolio.DashboardService;

/**
 * 
 * @author mukand
 * @author hemendra
 * 
 */


@Repository
public class TypeaheadDao {
	
	private static Logger logger = LoggerFactory.getLogger(DashboardService.class);
	
	@Autowired
    private CityService cityService;
	
    @Autowired
    private SolrDao solrDao;
    
	private SolrQuery getSolrQuery(String query, int rows, List<String> filterQueries) {
		SolrQuery solrQuery = this.getQueryParams(query);
        
        for (String fq : filterQueries) {
        	solrQuery.addFilterQuery(fq);
		}

		solrQuery.setRows(rows);
		return solrQuery;
	}

	private SolrQuery getQueryParams(String query) {
		SolrQuery solrQuery = new SolrQuery();
		List<String> queryTokens = this.tokenizeQuery(query);
		List<String> cityList = this.findCities(query);
		query = this.parseCities(query, cityList);
		String ctq = this.getCityQuery(cityList);
		
		double wt = 1.0;
		if (query.trim().isEmpty())
			wt = 1.0 / queryTokens.size();
	
		solrQuery.setQuery(query);
		solrQuery.setParam("defType", "edismax");
		solrQuery.setParam("qf", "tp_engram^5.0 tp_ngram^0.5 tp_phonetic^0.2");
		
		List<String> boostList = new ArrayList<String>();
		for (String qry : queryTokens) {
			if (qry.split(" ").length == 1)
				boostList
						.add("query({!edismax qf='tp_city tp_locality tp_builder tp_suburb tp_project' tie=0.1 v='$q' boost=''}, "+String.format("%.2f", wt)+")");
			else
				boostList
						.add("query({!edismax qf='tp_city tp_locality tp_builder tp_suburb tp_project' tie=0.1 v='\"$q\"' boost=''}, $wt)");
		}

		String boost = new String();
		if (boostList.size() > 1)
			boost = "sum(" + Joiner.on(",").skipNulls().join(boostList) + ")";
		else
			boost = boostList.get(0);

		boost = "product(map(query({!v='TYPEAHEAD_TYPE:CITY'}),0,0,1,1.5),map(query({!v='TYPEAHEAD_TYPE:BUILDER'}),0,0,1,0.8),map(query({!v='TYPEAHEAD_TYPE:PROJECT'}),0,0,1,1.3),map(query({!v='TYPEAHEAD_TYPE:LOCALITY'}),0,0,1,1.3),map(query({!v='TYPEAHEAD_TYPE:SUBURB'}),0,0,1,1.3),"
				+ boost + ")";
		
		if ( cityList.size() > 0 )
			boost = "product(query({!v='"+ctq+"'}, 1.0), "+boost+")";
		
		solrQuery.setParam("boost", boost);
		return solrQuery;
	}

	private String getCityQuery(List<String> cityList) {
		List<String> newCityList = new ArrayList<String>();
		
		for (String city: cityList) {
			newCityList.add("TYPEAHEAD_CITY:"+city.trim());	
		}
		String ctq = Joiner.on(" OR ").skipNulls().join(newCityList);
		return ctq;
	}

	public List<List<String>> powerset(List<String> list) {
		List<List<String>> ps = new ArrayList<List<String>>();
		ps.add(new ArrayList<String>());

		for (String item : list) {
			List<List<String>> newPs = new ArrayList<List<String>>();

			for (List<String> subset : ps) {
				newPs.add(subset);
				List<String> newSubset = new ArrayList<String>(subset);
				newSubset.add(item);
				newPs.add(newSubset);
			}

			ps = newPs;
		}
		return ps;
	}

	private List<String> tokenizeQuery(String query) {
		List<String> qList = new ArrayList<String>();
		List<List<String>> powerset = this.powerset(qList);
		for (List<String> list : powerset) {
			String st = Joiner.on(" ").skipNulls().join(list);
			if (st.trim() != "" && st != null)
				qList.add(st);
		}
		return qList;
	}

	public List<Typeahead> getTypeaheads(String query, int rows,
			List<String> filterQueries) {
		
		SolrQuery solrQuery = this.getSolrQuery(query, rows, filterQueries);
		return solrDao.executeQuery(solrQuery).getBeans(Typeahead.class);
	}
	
	private String parseCities(String query, List<String> queryCities) {		
		String query_new = this.substituteQuery(query, queryCities);
		
		if (query_new.trim().isEmpty())
			return query;
		else
			return query_new;
	}
	
	private List<String> findCities(String query) {
		// TODO Auto-generated method stub
		List<City> cityList = cityService.getCityList(null);
		List<String> cityLabels = new ArrayList<String>();
		
		for (City city : cityList) {
			String label = city.getLabel();
			if (label != "")
				cityLabels.add(city.getLabel());
		}

		List<String> matchedCities = new ArrayList<String>();
		for (String city: cityLabels) {
			if (query.matches("(?i).*"+city+".*")) {
				if (city != "")
					matchedCities.add(city);
			}
		}
		return matchedCities;
	}

	private String substituteQuery(String query, List<String> terms) {
		// replaces terms in query with terms
		for (String term : terms) {
			query = query.replace(term.toLowerCase(), "");
		}
		return query.trim();
	}
}
