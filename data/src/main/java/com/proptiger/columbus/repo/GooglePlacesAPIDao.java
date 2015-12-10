package com.proptiger.columbus.repo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

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
    private String             gpPlaceAutocompleteApiBaseUrl;

    @Value("${google.places.api.place.detail.json.url}")
    private String             gpPlaceDetailApiBaseUrl;

    @Value("${google.places.api.key}")
    private String             gpApiKey;

    private Logger             logger                 = LoggerFactory.getLogger(GooglePlacesAPIDao.class);

    public static final String COUNTRY_FILTER         = "components=country:in";
    public static final String LANG_FILTER            = "language=en";
    public static final String KEY_FILTER             = "key=%s";
    public static final String PLACE_ID_PARAM         = "placeid=%s";
    public static final String QUERY_PARAM            = "input=%s";
    public static final String BOUNDS_FILTER          = "location=%s,%s";
    public static final String RADIUS_FILTER          = "radius=%s";
    public static final String GP_RESP_STATUS_ZERO    = "ZERO_RESULTS";
    public static final String GP_RESP_STATUS_OK      = "OK";

    private RestTemplate       restTemplate           = new RestTemplate();

    private String             gpPlaceDetailUrl;
    private String             gpPlacePredictionUrl;

    private final int          placeNameWordThreshold = 13;

    @PostConstruct
    private void initialize() {
        gpPlaceDetailUrl = addParamsToURL(
                gpPlaceDetailApiBaseUrl,
                PLACE_ID_PARAM,
                COUNTRY_FILTER,
                LANG_FILTER,
                String.format(KEY_FILTER, gpApiKey));

        gpPlacePredictionUrl = addParamsToURL(
                gpPlaceAutocompleteApiBaseUrl,
                QUERY_PARAM,
                COUNTRY_FILTER,
                LANG_FILTER,
                String.format(KEY_FILTER, gpApiKey));
    }

    public List<GooglePlace> getMatchingPlaces(String query, int rows, double[] geoCenter, int radius, boolean clean) {
        List<GooglePlace> orgPlaceList = getMatchingPlaces(query, rows, geoCenter, radius);
        if (!clean) {
            return orgPlaceList;
        }
        List<GooglePlace> newPlaceList = new ArrayList<GooglePlace>();
        int wordCount = 0;
        for (GooglePlace gp : orgPlaceList) {
            wordCount = StringUtils.split(gp.getDescription(), ' ').length;
            if (wordCount < placeNameWordThreshold) {
                newPlaceList.add(gp);
            }
        }
        return newPlaceList;
    }

    private List<GooglePlace> getMatchingPlaces(String query, int rows, double[] geoCenter, int radius) {

        try {
            query = URLEncoder.encode(query, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            logger.error("Unsupported Encoding UTF-8.", e);
        }

        String gpPlacePredictionUrlBounded = getGpPlacePredictionUrlBounded(geoCenter, radius);
        String apiUrl = String.format(gpPlacePredictionUrlBounded, StringUtils.replace(query, " ", "+"));
        String apiResponse = getResponse(apiUrl);

        List<GooglePlace> placelist = new ArrayList<GooglePlace>();
        try {
            placelist = parsePlaceAutocompleteApiResponse(apiResponse);
        }
        catch (IOException ex) {
            logger.error("Exception while fetching place list from Google Places API. Query= " + query, ex);
        }
        return UtilityClass.getFirstNElementsOfList(placelist, rows);
    }

    private String getGpPlacePredictionUrlBounded(double[] bounds, int radius) {
        String gpPlacePredictionUrlBounded = gpPlacePredictionUrl;
        if (bounds != null && bounds.length >= 2 && radius > 0) {
            gpPlacePredictionUrlBounded = addParamsToURL(
                    gpPlacePredictionUrl,
                    String.format(BOUNDS_FILTER, bounds[0], bounds[1]),
                    String.format(RADIUS_FILTER, String.valueOf(radius)));
        }
        return gpPlacePredictionUrlBounded;
    }

    public GooglePlace getPlaceDetails(String placeId) {
        String apiUrl = String.format(gpPlaceDetailUrl, placeId);
        String apiResponse = getResponse(apiUrl);

        GooglePlace googlePlace = null;
        try {
            googlePlace = parsePlaceDetailApiResponse(apiResponse);
        }
        catch (IOException ex) {
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
    private GooglePlace parsePlaceDetailApiResponse(String apiResponse) throws IOException {
        GooglePlace googlePlace = new GooglePlace();
        JsonParser jsonParser = new JsonParser();
        JsonObject joResponse = jsonParser.parse(apiResponse).getAsJsonObject();
        String status = joResponse.get("status").getAsString();
        if (!(GP_RESP_STATUS_OK.equals(status))) {
            throw new IOException("Google Places API returned a NON-OK response : " + status);
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
    private List<GooglePlace> parsePlaceAutocompleteApiResponse(String apiResponse) throws IOException {

        List<GooglePlace> googlePlacelist = new ArrayList<GooglePlace>();
        JsonParser jsonParser = new JsonParser();
        JsonObject joResponse = jsonParser.parse(apiResponse).getAsJsonObject();
        String status = joResponse.get("status").getAsString();
        if (GP_RESP_STATUS_ZERO.equals(status)) {
            return googlePlacelist;
        }
        else if (!(GP_RESP_STATUS_OK.equals(status))) {
            throw new IOException("Google Places API returned a NON-OK response : " + status);
        }

        JsonArray joPredictions = joResponse.get("predictions").getAsJsonArray();
        JsonObject joPrediction;
        GooglePlace googlePlace;
        for (JsonElement je : joPredictions) {
            joPrediction = je.getAsJsonObject();
            googlePlace = new GooglePlace();
            googlePlace.setPlaceId(joPrediction.get("place_id").getAsString());
            googlePlace.setDescription(joPrediction.get("description").getAsString());
            googlePlace.setPlaceName(getGooglePlaceLabel(joPrediction));
            googlePlacelist.add(googlePlace);
        }
        return googlePlacelist;
    }

    private String addParamsToURL(String url, String... filters) {
        String urlparams = StringUtils.join(filters, "&");
        String join = (StringUtils.contains(url, "?") ? "&" : "?");
        return url + join + urlparams;
    }

    private String getGooglePlaceLabel(JsonObject je) {
        JsonArray jaTerms = je.get("terms").getAsJsonArray();
        return jaTerms.get(0).getAsJsonObject().get("value").getAsString();
    }

}