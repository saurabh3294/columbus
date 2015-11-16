package com.proptiger.columbus.thandlers;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class TemplateMap extends HashMap<String, TemplateTypes> {

    private static final long serialVersionUID = 1L;

    @PostConstruct
    private void initialize() {
        TemplateTypes[] allTemplateTypes = TemplateTypes.values();
        for (TemplateTypes ttype : allTemplateTypes) {
            this.put(ttype.getText().toLowerCase(), ttype);
        }
    }
}
