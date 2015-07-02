package com.proptiger.columbus.thandlers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.proptiger.columbus.model.TemplateInfo;
import com.proptiger.columbus.util.PropertyKeys;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.util.CorePropertyKeys;
import com.proptiger.core.util.PropertyReader;

@Component
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

    @PostConstruct
    public void initialize() {

        templateInfoProjectsIn = templateInfoDao.findByTemplateType(TemplateTypes.ProjectsIn.name());
        templateInfoUpcoming = templateInfoDao.findByTemplateType(TemplateTypes.UpcomingProjectsIn.name());
        templateInfoNew = templateInfoDao.findByTemplateType(TemplateTypes.NewProjectsIn.name());
        templateInfoUnderConst = templateInfoDao.findByTemplateType(TemplateTypes.UnderConstProjectsIn.name());
        templateInfoReadyToMove = templateInfoDao.findByTemplateType(TemplateTypes.ReadyToMoveProjectsIn.name());
        templateInfoAffordable = templateInfoDao.findByTemplateType(TemplateTypes.AffordableProjectsIn.name());
        templateInfoLuxury = templateInfoDao.findByTemplateType(TemplateTypes.LuxuryProjectsIn.name());
        templateInfoSale = templateInfoDao.findByTemplateType(TemplateTypes.PropertyForSaleIn.name());
        templateInfoResale = templateInfoDao.findByTemplateType(TemplateTypes.PropertyForResaleIn.name());
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
            t = getTypeaheadObject(template, city, cityId, locality);
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
        return getTypeaheadObject(template, city, cityId, null);
    }

    private Typeahead getTypeaheadObject(Typeahead template, String city, int cityId, Locality locality) {

        TemplateInfo templateInfo = getTemplateInfo(template);

        String typeaheadId = getTemplateType(template).toString();
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
        t.setRedirectUrl(redirectUrl);
        t.setRedirectUrlFilters(redirectUrlFilters);
        return t;
    }

    private TemplateInfo getTemplateInfo(Typeahead template) {
        TemplateTypes templateType = getTemplateType(template);
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
        URI uri = URI
                .create(UriComponentsBuilder
                        .fromUriString(
                                PropertyReader.getRequiredPropertyAsString(CorePropertyKeys.PROPTIGER_URL) + PropertyReader
                                        .getRequiredPropertyAsString(CorePropertyKeys.LOCALITY_API_URL)
                                        + "?"
                                        + URLGenerationConstants.Selector
                                        + String.format(
                                                URLGenerationConstants.SELECTOR_GET_LOCALITYNAMES_BY_CITYNAME,
                                                cityName)).build().encode().toString());

        List<Locality> topLocalities = httpRequestUtil.getInternalApiResultAsTypeListFromCache(
                uri,
                PropertyReader.getRequiredPropertyAsInt(PropertyKeys.INTERNAL_API_SLA_MS),
                Locality.class);
        return topLocalities;
    }

    private String getEntityName(String city, int cityId, Locality locality) {
        if (locality == null) {
            return StringUtils.capitalize(city);
        }
        return locality.getLabel();
    }
}
