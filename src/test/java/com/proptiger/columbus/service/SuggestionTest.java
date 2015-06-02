package com.proptiger.columbus.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.proptiger.core.model.Typeahead;

@Component
public interface SuggestionTest {

    public void test(List<Typeahead> suggestions);

}
