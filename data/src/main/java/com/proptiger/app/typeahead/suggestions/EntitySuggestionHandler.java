package com.proptiger.app.typeahead.suggestions;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.model.Typeahead;

@Component
public class EntitySuggestionHandler {

    private Logger              logger                   = LoggerFactory.getLogger(EntitySuggestionHandler.class);

    private float               suggestionScoreThreshold = 15.0f;

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
    
    @Autowired
    private LandmarkSuggestions landmarkSuggestions ;
    
    public List<Typeahead> getEntityBasedSuggestions(List<Typeahead> results, int count) {

        List<Typeahead> suggestions = new ArrayList<Typeahead>();

        /* If top-result is not relevant enough then don't give suggestions. */
        if (results == null || results.isEmpty() || results.get(0).getScore() < suggestionScoreThreshold) {
            return suggestions;
        }

        Typeahead topResult = results.get(0);

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

        String redirectUrl = topResult.getRedirectUrl();
        String label = topResult.getLabel();

        switch (idTokens[1]) {
            case "PROJECT":
                suggestions = projectSuggestions.getSuggestions(entityId, label, redirectUrl, count);
                break;
            case "CITY":
                suggestions = citySuggestions.getSuggestions(entityId, label, redirectUrl, count);
                break;
            case "BUILDER":
                suggestions = builderSuggestions.getSuggestions(entityId, label, redirectUrl, count);
                break;
            case "SUBURB":
                suggestions = suburbSuggestions.getSuggestions(entityId, label, redirectUrl, topResult.getCity(), count);
                break;
            case "LOCALITY":
                String cityName = topResult.getCity();
                String localityName = topResult.getLocality();
                suggestions = localitySuggestions.getSuggestions(entityId, label, redirectUrl, cityName, localityName, count);
                break;
            case "LANDMARK":
                //suggestions = landmarkSuggestions.getSuggestions(entityId, topResult, count);
                break;
            default:
                break;
        }

        return suggestions;
    }

}
