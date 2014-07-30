package com.proptiger.app.typeahead.thandlers;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateMap extends HashMap<String, TemplateTypes> {

    private static final long serialVersionUID = 1L;

    private static Logger     logger           = LoggerFactory.getLogger(TemplateMap.class);
    
    public TemplateMap() {
        super();
        fillMap();
    }
    
    public RootTHandler getTemplate(String text) {
        TemplateTypes ttype;
        try {
            if (this.containsKey(text)) {
                ttype = this.get(text);
                RootTHandler rtt = ttype.getClazz().newInstance();
                rtt.setType(ttype);
                return rtt;
            }
            else {
                return null;
            }
        }
        catch (InstantiationException | IllegalAccessException ex) {
            logger.error("Unable to instantiate template class (template_text = " + text + ")", ex);
            return null;
        }
    }

    private void fillMap() {
        TemplateTypes[] allTemplateTypes = TemplateTypes.values();
        for(TemplateTypes ttype : allTemplateTypes)
        {
            this.put(ttype.getText().toLowerCase(), ttype);
        }
    }
 
}
