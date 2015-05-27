package com.proptiger.columbus.thandlers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import com.proptiger.columbus.model.TemplateInfo;
import com.proptiger.columbus.util.PropertyKeys;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.util.CorePropertyKeys;
import com.proptiger.core.util.PropertyReader;

public class THandlerProjectIn extends RootTHandler {

    private static Logger logger = LoggerFactory.getLogger(THandlerProjectIn.class);

    private TemplateInfo  templateInfoProjectsIn;
    private TemplateInfo  templateInfoUpcoming;
    private TemplateInfo  templateInfoNew;
    private TemplateInfo  templateInfoUnderConst;
    private TemplateInfo  templateInfoReadyToMove;
    private TemplateInfo  templateInfoAffordable;
    private TemplateInfo  templateInfoLuxury;
    private TemplateInfo  templateInfoSale;
    private TemplateInfo  templateInfoResale;

    @Override
    public void initialize() {

        templateInfoProjectsIn = templateInfoDao.findByTemplateType(TemplateTypes.ProjectsIn.getText());
        templateInfoUpcoming = templateInfoDao.findByTemplateType(TemplateTypes.UpcomingProjectsIn.getText());
        templateInfoNew = templateInfoDao.findByTemplateType(TemplateTypes.NewProjectsIn.getText());
        templateInfoUnderConst = templateInfoDao.findByTemplateType(TemplateTypes.UnderConstProjectsIn.getText());
        templateInfoReadyToMove = templateInfoDao.findByTemplateType(TemplateTypes.ReadyToMoveProjectsIn.getText());
        templateInfoAffordable = templateInfoDao.findByTemplateType(TemplateTypes.AffordableProjectsIn.getText());
        templateInfoLuxury = templateInfoDao.findByTemplateType(TemplateTypes.LuxuryProjectsIn.getText());
        templateInfoSale = templateInfoDao.findByTemplateType(TemplateTypes.PropertyForSaleIn.getText());
        templateInfoResale = templateInfoDao.findByTemplateType(TemplateTypes.PropertyForResaleIn.getText());
    }

    @Override
    public List<Typeahead> getResults(String query, Typeahead template, String city, int cityId, int rows) {

        /* restrict results to top 2 localities for now */
        rows = Math.min(rows, 3);

        List<Typeahead> results = new ArrayList<Typeahead>();

        results.add(getTopResult(query, template, city, cityId));

        List<Locality> topLocalities = getTopLocalities(city);

        if (topLocalities == null) {
            logger.error("Could not fetch top localities for city " + city);
            return results;
        }

        Typeahead t;
        for (Locality locality : topLocalities) {
            t = getTypeaheadObject(city, cityId, locality);
            if (t != null) {
                results.add(t);
            }
            if (results.size() == rows) {
                break;
            }
        }

        return results;
    }

    @Override
    public Typeahead getTopResult(String query, Typeahead template, String city, int cityId) {
        return getTypeaheadObject(city, cityId, null);
    }

    private Typeahead getTypeaheadObject(String city, int cityId, Locality locality) {

        TemplateInfo templateInfo = getTemplateInfo();

        String typeaheadId = this.getType().toString();
        String entityName = getEntityName(city, cityId, locality);
        String typeaheadDisplayText = String.format(templateInfo.getDisplayTextFormat() + " " + entityName);

        String redirectUrl = String.format(templateInfo.getRedirectUrlFormat(), city.toLowerCase());

        String entityFilter = URLGenerationConstants.getCityLocalityFilter(cityId, locality);
        String redirectUrlFilters = String.format(templateInfo.getRedirectUrlFilters(), entityFilter);

        if (locality != null) {
            redirectUrl = URLGenerationConstants.addLocalityFilterToRedirectURL(redirectUrl, locality);
        }
        Typeahead t = getTypeaheadObjectByIdTextAndURL(
                typeaheadId,
                typeaheadDisplayText,
                redirectUrl,
                redirectUrlFilters);
        typeaheadDisplayText = (this.getType().getText());
        t.setRedirectUrl(redirectUrl);
        t.setRedirectUrlFilters(redirectUrlFilters);
        return t;
    }

    private TemplateInfo getTemplateInfo() {
        TemplateTypes templateType = this.getType();
        TemplateInfo templateInfo = null;
        switch (templateType) {
            case ProjectsIn:
                templateInfo = templateInfoProjectsIn;
                break;
            case UpcomingProjectsIn:
                templateInfo = templateInfoUpcoming;
                break;
            case NewProjectsIn:
                templateInfo = templateInfoNew;
                break;
            case UnderConstProjectsIn:
                templateInfo = templateInfoUnderConst;
                break;
            case ReadyToMoveProjectsIn:
                templateInfo = templateInfoReadyToMove;
                break;
            case AffordableProjectsIn:
                templateInfo = templateInfoAffordable;
                break;
            case LuxuryProjectsIn:
                templateInfo = templateInfoLuxury;
                break;
            case PropertyForSaleIn:
                templateInfo = templateInfoSale;
                break;
            case PropertyForResaleIn:
                templateInfo = templateInfoResale;
                break;
            default:
                break;
        }
        return templateInfo;
    }

    private List<Locality> getTopLocalities(String cityName) {
        URI uri = URI.create(UriComponentsBuilder
                .fromUriString(
                        PropertyReader.getRequiredPropertyAsString(CorePropertyKeys.PROPTIGER_URL) + PropertyReader
                                .getRequiredPropertyAsString(CorePropertyKeys.LOCALITY_API_URL)
                                + "?"
                                + URLGenerationConstants.Selector
                                + String.format(URLGenerationConstants.SelectorGetLocalityNamesByCityName, cityName))
                .build().encode().toString());

        List<Locality> topLocalities = httpRequestUtil.getInternalApiResultAsTypeListFromCache(
                uri,
                PropertyReader.getRequiredPropertyAsInt(PropertyKeys.INTERNAL_API_SLA_MS),
                Locality.class);
        return topLocalities;
    }

    private String getEntityName(String city, int cityId, Locality locality) {
        if (locality == null) {
            return city;
        }
        return locality.getLabel();
    }
}
