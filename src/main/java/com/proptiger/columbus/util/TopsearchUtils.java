package com.proptiger.columbus.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.proptiger.columbus.util.Topsearch;
import com.proptiger.core.model.Typeahead;


public class TopsearchUtils<T extends Object> {
	
	private static Gson gson = new Gson();
	
	public static List<Topsearch> typeaheadToTopsearchConverter(List<Typeahead> thList) {
		List<Topsearch> tsList = new ArrayList<Topsearch>();
		int rows = PropertyKeys.TOPSEARCH_ROW_COUNTS;
		for (Typeahead th : thList) {
			Topsearch topsearch = new Topsearch();
			topsearch.setEntityId(TypeaheadUtils.parseEntityIdAsString(th));
			topsearch.setEntityType(th.getType());
			if(th.getTopSearchedSuburb() != null && th.getTopSearchedSuburb().trim() != ""){
				topsearch.setSuburb(stringToList(th.getTopSearchedSuburb(), "TopsearchObjectField".getClass()));
				if (topsearch.getSuburb().size() > 3){
					topsearch.setSuburb(new ArrayList<TopsearchObjectField>(topsearch.getSuburb().subList(0, rows)));
				}
			}
			if(th.getTopSearchedLocality() != null && th.getTopSearchedLocality().trim() != ""){
				topsearch.setLocality(stringToList(th.getTopSearchedLocality(), "TopsearchObjectField".getClass()));
				if (topsearch.getLocality().size() > 3){
					topsearch.setLocality(new ArrayList<TopsearchObjectField>(topsearch.getLocality().subList(0, rows)));
				}
			}
			if(th.getTopSearchedBuilder() != null && th.getTopSearchedBuilder().trim() != ""){
				topsearch.setBuilder(stringToList(th.getTopSearchedBuilder(), "TopsearchObjectField".getClass()));
				if (topsearch.getBuilder().size() > 3){
					topsearch.setBuilder(new ArrayList<TopsearchObjectField>(topsearch.getBuilder().subList(0, rows)));
				}
			}
			if(th.getTopSearchedProject() != null && th.getTopSearchedProject().trim() != ""){
				topsearch.setProject(stringToList(th.getTopSearchedProject(), "TopsearchObjectField".getClass()));
				if (topsearch.getProject().size() > 3){
					topsearch.setProject(new ArrayList<TopsearchObjectField>(topsearch.getProject().subList(0, rows)));
				}
			}
			
			tsList.add(topsearch);
		}
		
		
		return tsList;
		
	}
	
	public static List<TopsearchObjectField> stringToList(String str, Class<?> var) {
		/*Type type = new TypeToken<List<TopsearchObjectField>>() {}.getType();
		List<TopsearchObjectField> navigation = gson.fromJson(str, type);*/
		Type type = new TypeToken<List<TopsearchObjectField>>() {}.getType();
		List<TopsearchObjectField> navigation = gson.fromJson(str, type);
		return navigation;
	}
}



