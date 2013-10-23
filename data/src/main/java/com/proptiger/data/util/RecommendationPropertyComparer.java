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
class RecommendationPropertyComparer implements Comparator<SolrResult>{
            int totalCompares;// = 3;
            int viewedPropertyBedroom;// = viewedProperty.getProperty().getBedrooms();
            
            public RecommendationPropertyComparer(int totalCompares, int bedrooms){
                this.totalCompares = totalCompares;
                this.viewedPropertyBedroom = bedrooms;
            }
            
            @Override
            public int compare(SolrResult o1, SolrResult o2) {
                return compareRecursively(o1, o2, 3);
            }
            
            public int compareRecursively(SolrResult o1, SolrResult o2, int compareIndex){
                if(compareIndex >= 3)
                    return 0;
                
                int compare = 0;
                switch(compareIndex)
                {
                    // Project is resale or new.
                    case 0:
                        compare = compareResale(o1.getProject().isIsResale(), o2.getProject().isIsResale());
                        break;
                    // project sorting by display order.
                    case 1:
                        compare = compareDisplayOrder(o1.getProject().getAssignedPriority(), o2.getProject().getAssignedLocalityPriority());
                        break;
                    // project of same bhk given more priority.    
                    case 2:
                        compare = compareBedrooms(o1.getProperty().getBedrooms(), o1.getProperty().getBedrooms());
                        break;
                }
                return 0;
            }
            
            public int compareResale(boolean o1, boolean o2){
                if(o1 == o2)
                    return 0;
                // New projects has more priority.
                else if(o1 == false)
                    return 1;
                else
                    return -1;
            }
            public int compareDisplayOrder(int a, int b){
                if(a<b)
                    return 1;
                else if(a>b)
                    return -1;
                else
                    return 0;
                       
            }
            
            public int compareBedrooms(int bed1, int bed2){
                if(bed1 == bed2)
                    return 0;
                else if(bed1 == viewedPropertyBedroom)
                    return 1;
                else
                    return -1;
            }
                    
}
