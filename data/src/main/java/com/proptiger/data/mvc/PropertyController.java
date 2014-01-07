/**
 * 
 */
package com.proptiger.data.mvc;

import java.util.List;
import java.util.Set;

import org.apache.cxf.jaxrs.ext.search.SearchBean;
import org.apache.cxf.jaxrs.ext.search.SearchCondition;
import org.apache.cxf.jaxrs.ext.search.fiql.FiqlParser;
import org.apache.cxf.jaxrs.ext.search.lucene.LuceneQueryVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.Property;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.ImageService;
import com.proptiger.data.service.PropertyService;

/**
 * @author mandeep
 *
 */
@Controller
@RequestMapping
public class PropertyController extends BaseController {
    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private ImageService imageService;
    
    private static Logger logger = LoggerFactory.getLogger(PropertyController.class);

    @RequestMapping(value = "data/v1/entity/property")
    public @ResponseBody ProAPIResponse getProperties(@RequestParam(required=false, value = "selector") String selector) throws Exception {
    	
    	Selector propRequestParam = super.parseJsonToObject(selector, Selector.class);
    	if(propRequestParam == null){
    		propRequestParam = new Selector();
    	}
        List<Property> properties = propertyService.getProperties(propRequestParam);
        Set<String> fieldsSet = propRequestParam.getFields();
        
        return new ProAPISuccessResponse(super.filterFields(properties, fieldsSet));
    }
    
    @RequestMapping(value = "data/v2/entity/property")
    public @ResponseBody ProAPIResponse getV2Properties(@RequestParam(required=false, value = "selector") String selector) throws Exception {
        SearchCondition<SearchBean> filter = new FiqlParser<SearchBean>(SearchBean.class).parse("ct==text");
        LuceneQueryVisitor<SearchBean> lucene = new LuceneQueryVisitor<SearchBean>("ct", "contents");
        lucene.visit(filter);
        org.apache.lucene.search.Query termQuery = lucene.getQuery();
        logger.error(termQuery.toString());
        return null;
    }    
}
