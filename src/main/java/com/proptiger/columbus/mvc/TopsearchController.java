/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.columbus.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.columbus.service.TopsearchService;
import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.exception.ProAPIException;
import com.proptiger.core.meta.DisableCaching;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;

/**
 * 
 * @author Manmohan
 */
@Controller
@DisableCaching
public class TopsearchController extends BaseController {

    @Autowired
    private TopsearchService topsearchService;

    @RequestMapping(value = "app/v1/topsearch")
    @ResponseBody
    public APIResponse getTopsearches(
            @RequestParam int entityId,
            @RequestParam String entityType,
            @RequestParam String requiredEntities,
            @RequestParam(defaultValue = "false") Boolean group,
            @RequestParam(defaultValue = "5") int rows) {

        if (!entityType.equalsIgnoreCase(getEntityTypeFromEntityId(entityId))) {
            throw new ProAPIException(ResponseCodes.BAD_REQUEST, "Invalid entityId for the given entityType");
        }
        List<Typeahead> list = topsearchService.getTopsearches(entityId, entityType, requiredEntities, group, rows);

        return new APIResponse(super.filterFields(list, null), list.size());
    }

    private String getEntityTypeFromEntityId(int entityId) {
        DomainObject dObj = DomainObject.getDomainInstance((long) entityId);
        return dObj.getText();
    }

}
