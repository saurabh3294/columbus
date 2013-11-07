/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.util;

import com.proptiger.data.model.SolrResult;
import java.util.Comparator;

/**
 *
 * @author mukand
 */
public class PropertyComparer implements Comparator<SolrResult>{
    int totalCompares;
    int viewedPropertyBedroom;
    Double viewedPropertyBudget;
    int numberOfComparison = 4;

    public PropertyComparer(int totalCompares, int bedrooms, Double viewPropertyBudget) {
        this.totalCompares = totalCompares;
        this.viewedPropertyBedroom = bedrooms;
        this.viewedPropertyBudget = viewPropertyBudget;
    }

    @Override
    public int compare(SolrResult o1, SolrResult o2) {
    	System.out.println(" in ");
        return compareRecursively(o1, o2, 0);
    }

    public int compareRecursively(SolrResult o1, SolrResult o2, int compareIndex) {
        if (compareIndex >= numberOfComparison) {
            return 0;
        }
        System.out.println(o1.getProperty().getPropertyId() + " "+o2.getProperty().getPropertyId()+" "+compareIndex);
        int compare = 0;
        switch (compareIndex) {
            // Project is resale or new.
            case 0:
                compare = compareResale(o1.getProject().isIsResale(), o2.getProject().isIsResale());
                break;
            // project sorting by display order.
            case 1:
                compare = compareDisplayOrder(o1.getProject().getAssignedPriority(), o2.getProject().getAssignedPriority());
                break;
            // project of same bhk given more priority.    
            case 2:
                compare = compareBedrooms(o1.getProperty().getBedrooms(), o2.getProperty().getBedrooms());
                break;
                
            case 3:
            	compare = compareBudget(o1.getProperty().getBudget(), o2.getProperty().getBudget());
            	break;
        }
        System.out.println(" COMPARE RESULT "+compare);
        if(compare == 0)
        	return compareRecursively(o1, o2, compareIndex+1);
        return compare;
    }

    public int compareResale(boolean o1, boolean o2) {
        if (o1 == o2) {
            return 0;
        } // New projects has more priority.
        else if (o1 == false) {
            return -1;
        } else {
            return 1;
        }
    }

    public int compareDisplayOrder(int a, int b) {
        if (a < b) {
            return -1;
        } else if (a > b) {
            return 1;
        } else {
            return 0;
        }

    }

    public int compareBedrooms(int bed1, int bed2) {
    	System.out.println("BEDROOMS BED1: "+bed1+" NEXT PROJECT BEDS: "+bed2);
        if (bed1 == bed2) {
            return 0;
        } else if (bed1 == viewedPropertyBedroom) {
            return -1;
        } else {
            return 1;
        }
    }
    
    public int compareBudget(Double budget1, Double budget2){
    	System.out.println("BUDGET");
    	System.out.println(" BUDGET 1: "+budget1+" BUDGET 2: "+budget2+" VIEWED "+viewedPropertyBudget);
    	if(viewedPropertyBudget == null || viewedPropertyBudget <1 || (budget1 == null && budget2 == null) )
    		return 0;
    	
    	if(budget1 != null && budget2 == null)
    		return -1;
    	else if(budget2 != null && budget1 == null)
    		return 1;
    	
    	Double diff1 = Math.abs(budget1 - viewedPropertyBudget);
    	Double diff2 = Math.abs(budget2 - viewedPropertyBudget);
    	System.out.println(" DIFF 1 : "+diff1+" DIFF 2: "+diff2);
    	if(diff1 < diff2)
    		return -1;
    	else if(diff1 > diff2)
    		return 1;
    	
    	return 0;
    }

}
