package com.proptiger.columbus.repo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.proptiger.columbus.model.GooglePlace;
import com.proptiger.core.util.UtilityClass;

@Repository
public class GooglePlacesAPIDao {

    @Value("${google.places.api.place.autocomplete.json.url}")
    private String       gpPlaceAutocompleteApiUrl;

    @Value("${google.places.api.place.detail.json.url}")
    private String       gpPlaceDetailApiUrl;

    @Value("${google.places.api.key}")
    private String       gpApiKey;

    private Logger       logger        = LoggerFactory.getLogger(GooglePlacesAPIDao.class);

    public static String CountryFilter = "components=country:in";
    public static String LangFilter    = "language=en";
    public static String KeyFilter     = "key=%s";
    public static String PlaceIdParam  = "placeid=%s";
    public static String QueryParam    = "input=%s";

    private RestTemplate restTemplate  = new RestTemplate();

    public List<GooglePlace> getMatchingPlaces(String query, int rows) {
        String apiUrl = addParamsToURL(
                gpPlaceAutocompleteApiUrl,
                String.format(QueryParam, query),
                CountryFilter,
                LangFilter,
                String.format(KeyFilter, gpApiKey));
        String apiResponse = getResponse(apiUrl);

        List<GooglePlace> placelist = new ArrayList<GooglePlace>();
        try {
            placelist = parsePlaceAutocompleteApiResponse(apiResponse);
        }
        catch (Exception ex) {
            logger.error("Exception while fetching place list from Google Places API. Query= " + query, ex);
        }
        return UtilityClass.getFirstNElementsOfList(placelist, rows);
    }

    public GooglePlace getPlaceDetails(String placeId) {
        String apiUrl = addParamsToURL(
                gpPlaceDetailApiUrl,
                String.format(PlaceIdParam, placeId),
                CountryFilter,
                LangFilter,
                String.format(KeyFilter, gpApiKey));

        String apiResponse = getResponse(apiUrl);
        GooglePlace googlePlace = null;
        try {
            googlePlace = parsePlaceDetailApiResponse(apiResponse);
        }
        catch (Exception ex) {
            logger.error("Exception while fetching place details from Google Places API. PlaceId = " + placeId, ex);
        }
        return googlePlace;
    }

    private String getResponse(String apiUrl) {
        URI uri = null;
        try {
            uri = new URI(apiUrl);
        }
        catch (Exception ex) {
            logger.error("Could not make URO Object {}", apiUrl, ex);
            return null;
        }

        String apiResponse = restTemplate.getForObject(uri, String.class);
        return apiResponse;
    }

    /**
     * @param apiResponse
     * @return GooglePlace object or null in case of some problem.
     * @throws Exception
     *             if any problem while parsing response
     */
    private GooglePlace parsePlaceDetailApiResponse(String apiResponse) throws Exception {
        GooglePlace googlePlace = new GooglePlace();
        JsonParser jsonParser = new JsonParser();
        JsonObject joResponse = jsonParser.parse(apiResponse).getAsJsonObject();
        String status = joResponse.get("status").getAsString();
        if (!status.equals("OK")) {
            throw new Exception("Google Places API returned a NON-OK response : " + status);
        }

        JsonObject joResult = joResponse.get("result").getAsJsonObject();
        JsonObject joLocation = joResult.get("geometry").getAsJsonObject().get("location").getAsJsonObject();

        googlePlace.setPlaceId(joResult.get("place_id").getAsString());
        googlePlace.setPlaceName(joResult.get("name").getAsString());
        googlePlace.setFormattedAddress(joResult.get("formatted_address").getAsString());
        googlePlace.setLatitude(joLocation.get("lat").getAsDouble());
        googlePlace.setLongitude(joLocation.get("lng").getAsDouble());
        return googlePlace;
    }

    /**
     * @param apiResponse
     * @return List of GooglePlace objects.
     * @throws Exception
     *             if any problem while parsing response
     */
    private List<GooglePlace> parsePlaceAutocompleteApiResponse(String apiResponse) throws Exception {

        List<GooglePlace> googlePlacelist = new ArrayList<GooglePlace>();
        JsonParser jsonParser = new JsonParser();
        JsonObject joResponse = jsonParser.parse(apiResponse).getAsJsonObject();
        String status = joResponse.get("status").getAsString();
        if (status.equals("ZERO_RESULTS")) {
            return googlePlacelist;
        }
        else if (!status.equals("OK")) {
            throw new Exception("Google Places API returned a NON-OK response : " + status);
        }

        JsonArray joPredictions = joResponse.get("predictions").getAsJsonArray();
        JsonObject joPrediction;
        GooglePlace googlePlace;
        for (JsonElement je : joPredictions) {
            joPrediction = je.getAsJsonObject();
            googlePlace = new GooglePlace();
            googlePlace.setPlaceId(joPrediction.get("place_id").getAsString());
            googlePlace.setDescription(joPrediction.get("description").getAsString());
            googlePlacelist.add(googlePlace);
        }
        return googlePlacelist;
    }

    private String addParamsToURL(String url, String... filters) {

        String finalUrl = url;
        for (String filter : filters) {
            if (StringUtils.contains(finalUrl, "?")) {
                finalUrl += ("&" + filter);
            }
            else {
                finalUrl += ("?" + filter);
            }
        }
        return finalUrl;
    }

}