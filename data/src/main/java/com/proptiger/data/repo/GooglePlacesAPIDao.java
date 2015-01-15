package com.proptiger.data.repo;

import java.net.URI;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.proptiger.data.model.GooglePlace;

@Repository
public class GooglePlacesAPIDao {

    @Value("${google.places.api.place.detail.json.url}")
    private String       gpPlaceDetailApiBaseUrl;

    @Value("${google.places.api.key}")
    private String       gpApiKey;

    private Logger       logger        = LoggerFactory.getLogger(GooglePlacesAPIDao.class);

    public static String CountryFilter = "components=country:in";
    public static String LangFilter    = "language=en";
    public static String KeyFilter     = "key=%s";
    public static String PlaceIdParam  = "placeid=%s";

    private RestTemplate restTemplate  = new RestTemplate();

    private String       gpPlaceDetailUrl;

    @PostConstruct
    private void initialize() {
        gpPlaceDetailUrl = addParamsToURL(
                gpPlaceDetailApiBaseUrl,
                PlaceIdParam,
                CountryFilter,
                LangFilter,
                String.format(KeyFilter, gpApiKey));
    }

    public GooglePlace getPlaceDetails(String placeId) {
        String apiUrl = String.format(gpPlaceDetailUrl, placeId);
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

    private String addParamsToURL(String url, String... filters) {
        String urlparams = StringUtils.join(filters, "&");
        String join = (StringUtils.contains(url, "?") ? "&" : "?");
        return url + join + urlparams;
    }

}