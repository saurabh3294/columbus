package com.proptiger.columbus.suggestions;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.core.model.Typeahead;
import com.proptiger.columbus.model.TypeaheadConstants;

@Component
public class EntitySuggestionHandler {

    private Logger              logger = LoggerFactory.getLogger(EntitySuggestionHandler.class);

    @Autowired
    private CitySuggestions     citySuggestions;

    @Autowired
    private LocalitySuggestions localitySuggestions;

    @Autowired
    private BuilderSuggestions  builderSuggestions;

    @Autowired
    private ProjectSuggestions  projectSuggestions;

    @Autowired
    private SuburbSuggestions   suburbSuggestions;

    public List<Typeahead> getEntityBasedSuggestions(List<Typeahead> results, int count) {

        List<Typeahead> suggestions = new ArrayList<Typeahead>();

        if (results == null || results.isEmpty()) {
            return suggestions;
        }

        Typeahead topResult = results.get(0);

        /*
         * No suggestions for Google Place results or if top-result is not
         * relevant enough
         */
        if (topResult.isGooglePlace() || topResult.getScore() < TypeaheadConstants.suggestionScoreThreshold) {
            return suggestions;
        }

        /*
         * Should be of the form TYPEAHEAD-<entity>-<entity-id> if an entity is
         * hit.
         */
        String typeaheadId = topResult.getId();
        String[] idTokens = StringUtils.split(typeaheadId, '-');
        if (idTokens == null || idTokens.length < 3 || !(idTokens[0].equals("TYPEAHEAD"))) {
            return suggestions;
        }

        /* Extract Entity type and Id */

        int entityId = 0;
        try {
            entityId = Integer.parseInt(idTokens[2]);
        }
        catch (NumberFormatException ex) {
            logger.debug("Could not parse entity id in " + typeaheadId, ex);
            return suggestions;
        }

        switch (idTokens[1]) {
            case "PROJECT":
                suggestions = projectSuggestions.getSuggestions(entityId, topResult, count);
                break;
            case "CITY":
                suggestions = citySuggestions.getSuggestions(entityId, topResult, count);
                break;
            case "BUILDER":
                suggestions = builderSuggestions.getSuggestions(entityId, topResult, count);
                break;
            case "SUBURB":
                suggestions = suburbSuggestions.getSuggestions(entityId, topResult, count);
                break;
            case "LOCALITY":
                suggestions = localitySuggestions.getSuggestions(entityId, topResult, count);
                break;
            default:
                break;
        }

        return suggestions;
    }

}
