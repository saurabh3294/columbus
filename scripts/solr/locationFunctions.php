<?php
include_once "apiConfig.php";
function getCitiesFromDB() {
	global $solrDB, $logger, $paramProjectIds, $citiesOnProjectIds, $cityEnquiryCountArray, $projCmsActiveCondition, $version;
	global $localitySafetyAndLivabilityData;
	global $solrCitiesList, $paramCityIds, $heroshotImages;
	global $resaleCountData;
	global $resaleListingCountData;
	global $cityWithSource;
	$cities = loadPriceRiseInfo ( 'CITY' );
    $cityDefaultUnitType = getDomainDefaultUnitType('city');
    $couponData = getCouponsDiscount("city");

	if (empty ( $cityEnquiryCountArray ))
		list($cityEnquiryCountArray, $cityEnquiryLastEnquired) = loadEnquiryCount ( 'CITY' );

	$projectCondition = " RP.STATUS IN ({$projCmsActiveCondition}) ";

	$cityProjectPropertyCounts = loadProjectCountInfo("CITY");
	$projectCondition = appendConditionForB2b($projectCondition);
	$paramConditionArray = array();
	if(!empty($paramProjectIds)){
		$paramConditionArray[] = " RP.PROJECT_ID IN ($paramProjectIds)";
	}
	if(!empty($paramCityIds)){
		$paramConditionArray[] = " C.CITY_ID IN ($paramCityIds) ";
	}
	if(count($paramConditionArray) > 0){
		$paramCondition = implode(" OR ", $paramConditionArray);
		$projectCondition .= " AND ( $paramCondition )";
	}

	$sql = "SELECT 'CITY' AS DOCUMENT_TYPE, CONCAT('CITY-', C.CITY_ID) AS id, C.CITY_ID,
                    C.LABEL AS CITY, C.NORTH_EAST_LATITUDE, C.NORTH_EAST_LONGITUDE,
                    C.SOUTH_WEST_LATITUDE, C.SOUTH_WEST_LONGITUDE,
                    C.CENTER_LATITUDE, C.CENTER_LONGITUDE, IFNULL(C.SHOW_POLYGON, 0) AS SHOW_POLYGON,
                    C.DISPLAY_ORDER AS CITY_DISPLAY_ORDER, C.DESCRIPTION AS CITY_DESCRIPTION, C.URL AS CITY_URL,
                    C.DISPLAY_PRIORITY AS DISPLAY_PRIORITY,C.MIN_ZOOM_LEVEL AS CITY_MIN_ZOOM_LEVEL,
                    C.MAX_ZOOM_LEVEL AS CITY_MAX_ZOOM_LEVEL,
                    C.CITY_POPULATION AS CITY_POPULATION, C.CITY_TAG_LINE AS CITY_TAG_LINE, C.POPULATION_SURVEY_DATE AS POPULATION_SURVEY_DATE,
                    C.IS_SERVING AS CITY_IS_SERVING, C.IS_SERVING_RESALE AS CITY_IS_SERVING_RESALE
                    FROM cms.city C JOIN cms.resi_project RP JOIN cms.suburb S
                    JOIN cms.locality L JOIN cms.resi_project_options RPO ON
                    (L.SUBURB_ID=S.SUBURB_ID AND RP.LOCALITY_ID = L.LOCALITY_ID AND C.CITY_ID = S.CITY_ID AND RPO.PROJECT_ID = RP.PROJECT_ID)
		    		JOIN cms.project_status_master psm ON (psm.id = RP.project_status_id)
                    WHERE $projectCondition AND RP.VERSION = $version AND RP.RESIDENTIAL_FLAG ='Residential' AND RPO.OPTION_CATEGORY = 'Actual'
                    	GROUP BY C.CITY_ID";
	$documents = array ();
	$result = mysql_unbuffered_query ( $sql, $solrDB );
	if ($result) {
		while ( $document = mysql_fetch_assoc ( $result ) ) {
			$cityId = $document['CITY_ID'];
			unset($solrCitiesList[$cityId]);
            $document ['CITY_DESCRIPTION'] = removeNonAsciiCharacters ( $document ['CITY_DESCRIPTION'] );
			if (empty ( $document ['NORTH_EAST_LATITUDE'] ))
				unset ( $document ['NORTH_EAST_LATITUDE'] );
			if (empty ( $document ['NORTH_EAST_LONGITUDE'] ))
				unset ( $document ['NORTH_EAST_LONGITUDE'] );
			if (empty ( $document ['SOUTH_WEST_LATITUDE'] ))
				unset ( $document ['SOUTH_WEST_LATITUDE'] );
			if (empty ( $document ['SOUTH_WEST_LONGITUDE'] ))
				unset ( $document ['SOUTH_WEST_LONGITUDE'] );
			if (empty ( $document ['CENTER_LATITUDE'] ))
				unset ( $document ['CENTER_LATITUDE'] );
			if (empty ( $document ['CENTER_LONGITUDE'] ))
				unset ( $document ['CENTER_LONGITUDE'] );
			if (empty ( $document ['DISPLAY_PRIORITY'] ))
				unset ( $document ['DISPLAY_PRIORITY'] );
			if (empty ( $document ['CITY_MIN_ZOOM_LEVEL'] ))
				unset ( $document ['CITY_MIN_ZOOM_LEVEL'] );
			if (empty ( $document ['CITY_MAX_ZOOM_LEVEL'] ))
				unset ( $document ['CITY_MAX_ZOOM_LEVEL'] );
			if (empty ( $document ['CITY_LOCALITY_COUNT'] ))
				unset ( $document ['CITY_LOCALITY_COUNT']);
			if (empty ( $document ['CITY_POPULATION'] ))
				unset ( $document ['CITY_POPULATION']);
			if (empty ( $document ['CITY_TAG_LINE'] ))
				unset ( $document ['CITY_TAG_LINE']);

			if(!empty($cityProjectPropertyCounts[$cityId])){
				$document['CITY_PROJECT_COUNT'] = $cityProjectPropertyCounts[$cityId]['PROJECT_COUNT'];
				$document['CITY_PROPERTY_COUNT'] = $cityProjectPropertyCounts[$cityId]['PROPERTY_COUNT'];
				$document['CITY_LOCALITY_COUNT'] = $cityProjectPropertyCounts[$cityId]['LOCALITY_COUNT'];
			}

			checkValidAndSetDate ( $document, "POPULATION_SURVEY_DATE" );

		    $document ['CITY_DOMINANT_UNIT_TYPE'] = getDominantUnitType($cities, $cityDefaultUnitType, $document['CITY_ID']);//$cityDoc ['unit_type'];
            if( isset($couponData[$cityId]) ){
                $document['CITY_COUPON_MAX_DISCOUNT'] = $couponData[$cityId];
                $document['CITY_COUPON_AVAILABLE'] = true;
            }
			if (isset ( $cities [$document ['CITY_ID']] )) {
				$cityDoc = $cities [$document ['CITY_ID']];

				if (! empty ( $cityDoc ['average_price_per_unit_area'] )) {
					$document ['CITY_PRICE_PER_UNIT_AREA'] = $cityDoc ['average_price_per_unit_area'];
				}
				if (! empty ( $cityDoc ['Apartment']['average_price_per_unit_area'] )) {
					$document ['CITY_PRICE_PER_UNIT_AREA_APARTMENT'] = $cityDoc ['Apartment']['average_price_per_unit_area'];
				}
				if (! empty ( $cityDoc ['Plot']['average_price_per_unit_area'] )) {
					$document ['CITY_PRICE_PER_UNIT_AREA_PLOT'] = $cityDoc ['Plot']['average_price_per_unit_area'];
				}
				if (! empty ( $cityDoc ['Villa']['average_price_per_unit_area'] )) {
					$document ['CITY_PRICE_PER_UNIT_AREA_VILLA'] = $cityDoc ['Villa']['average_price_per_unit_area'];
				}

				if (! empty ( $cityDoc ['RISE_PERCENT'] )) {
					$document ['CITY_PRICE_RISE'] = $cityDoc ['RISE_PERCENT'];
					$document ['CITY_PRICE_RISE_TIME'] = $cityDoc ['RISE_PERIOD_MONTHS'];
				}
				if (! empty ( $cityDoc ['Apartment']['RISE_PERCENT'] )) {
					$document ['CITY_PRICE_RISE_APARTMENT'] = $cityDoc ['Apartment']['RISE_PERCENT'];
				}
				if (! empty ( $cityDoc ['Plot']['RISE_PERCENT'] )) {
					$document ['CITY_PRICE_RISE_PLOT'] = $cityDoc ['Plot']['RISE_PERCENT'];
				}
				if (! empty ( $cityDoc ['Villa']['RISE_PERCENT'] )) {
					$document ['CITY_PRICE_RISE_VILLA'] = $cityDoc ['Villa']['RISE_PERCENT'];
				}
			}

			if (isset ( $cityEnquiryCountArray [$document ['CITY_ID']] )) {
				$document ['CITY_ENQUIRY_COUNT'] = $cityEnquiryCountArray [$document ['CITY_ID']];
			}
			if (isset ( $localitySafetyAndLivabilityData [$document ['CITY_ID']] ['MAX_SAFETY_SCORE'] ))
				$document ['LOCALITY_MAX_SAFETY_SCORE'] = $localitySafetyAndLivabilityData [$document ['CITY_ID']] ['MAX_SAFETY_SCORE'];
			if (isset ( $localitySafetyAndLivabilityData [$document ['CITY_ID']] ['MIN_SAFETY_SCORE'] ))
				$document ['LOCALITY_MIN_SAFETY_SCORE'] = $localitySafetyAndLivabilityData [$document ['CITY_ID']] ['MIN_SAFETY_SCORE'];
			if (isset ( $localitySafetyAndLivabilityData [$document ['CITY_ID']] ['MAX_LIVABILITY_SCORE'] ))
				$document ['LOCALITY_MAX_LIVABILITY_SCORE'] = $localitySafetyAndLivabilityData [$document ['CITY_ID']] ['MAX_LIVABILITY_SCORE'];
			if (isset ( $localitySafetyAndLivabilityData [$document ['CITY_ID']] ['MIN_LIVABILITY_SCORE'] ))
				$document ['LOCALITY_MIN_LIVABILITY_SCORE'] = $localitySafetyAndLivabilityData [$document ['CITY_ID']] ['MIN_LIVABILITY_SCORE'];

			if(isset($cityWithSource[$document['CITY_ID']]['SOURCE_ID'])){
					$document['CITY_SOURCE_ID'] = $cityWithSource[$document['CITY_ID']]['SOURCE_ID'];
			}
			if (isset($cityWithSource[$document['CITY_ID']]['SOURCE_DOMAIN'])){
					$document['CITY_SOURCE_DOMAIN'] = $cityWithSource[$document['CITY_ID']]['SOURCE_DOMAIN'];
			}

			$document ['CITY_VIEW_COUNT'] = 0;
			if(isset ( $heroshotImages[ $document ['CITY_ID']])){
				$document ['CITY_HEROSHOT_IMAGE_URL'] = $heroshotImages[$document ['CITY_ID']];
			}
			array_push ( $documents, $document );
		}
	} else {
			$logger->error("Error while fetching City data using query : \n ". $sql."\n");
			$logger->error("Mysql error : \n". mysql_error());
			die();
	}

	$urlTypes = getUrlTypes();
	$len = count ( $documents );
	for($i = 0; $i < $len; $i ++) {
		$document = &$documents [$i];
		$cityOverviewUrl = url_lib_city_url($document);

		if (isset ( $cityOverviewUrl )) {
			$document ['CITY_OVERVIEW_URL'] = $cityOverviewUrl;
		}

		foreach ( $urlTypes as $urlType => $propertyType ) {
			if ( $urlType == 'newLaunch' ) {
				$taxonomyUrls [$urlType] = url_lib_city_new_launch_url ($document);
			}
			else if(!($urlType == 'resaleApartmentUrl' || $urlType == 'resalePropertyUrl') || (
				($urlType == 'resalePropertyUrl' && isset($resaleListingCountData[$document['CITY_ID']])) ||
				($urlType == 'resaleApartmentUrl' && isset($resaleCountData[$document['CITY_ID']])))) {
				$taxonomyUrls [$urlType] = url_lib_city_list_url ($document, $propertyType);
			}
			else{
				unset($taxonomyUrls [$urlType]);
			}
		}
		$document ['CITY_TAXONOMY_URL'] = json_encode ( $taxonomyUrls );
	}

	$solrCityDelete = array();
	foreach($solrCitiesList as $key => $value){
		$solrCityDelete[] = "CITY-".$key;
	}
	return array (
			$solrCityDelete, //$citiesOnProjectIds ['deleteIds'],
			$documents
	);
}
function getSuburbFromDB() {
	global $solrDB, $logger, $paramProjectIds, $suburbsOnProjectIds, $suburbEnquiryCountArray, $projCmsActiveCondition;
	global $version, $paramSuburbIds, $paramCityIds;
	global $solrSuburbsList, $heroshotImages, $resaleCountData, $resaleListingCountData;
	global $suburbWithSource;

	$suburbs = loadPriceRiseInfo ( 'SUBURB' );
    $suburbDefaultUnitType = getDomainDefaultUnitType('suburb');
	$suburbProjCountDoc = loadProjectCountInfo ( 'SUBURB' );
    $couponData = getCouponsDiscount("suburb");
	if (empty ( $suburbEnquiryCountArray ))
		list($suburbEnquiryCountArray, $suburbLastEnquiredDate) = loadEnquiryCount ( 'SUBURB' );

	$projectCondition = " RP.STATUS IN ({$projCmsActiveCondition}) ";

	$projectCondition = appendConditionForB2b($projectCondition);
	$paramConditionArray = array();
	if(!empty($paramProjectIds)){
		$paramConditionArray[] = " RP.PROJECT_ID IN ($paramProjectIds)";
	}
	if(!empty($paramSuburbIds)){
		$paramConditionArray[] = " S.SUBURB_ID IN ($paramSuburbIds) ";
	}
	if(count($paramConditionArray) > 0){
		$paramCondition = implode(" OR ", $paramConditionArray);
		$projectCondition .= " AND ( $paramCondition )";
	}

	$sql = "SELECT 'SUBURB' AS DOCUMENT_TYPE, CONCAT('SUBURB-', S.SUBURB_ID) AS id,
                    S.LABEL SUBURB, C.CITY_ID, S.SUBURB_ID, S.DESCRIPTION SUBURB_DESCRIPTION,
                    S.PRIORITY SUBURB_PRIORITY, S.URL AS SUBURB_URL, S.SUBURB_TAG_LINE AS SUBURB_TAG_LINE,
                    C.LABEL as CITY, C.DESCRIPTION as CITY_DESCRIPTION, C.DISPLAY_ORDER as CITY_DISPLAY_ORDER,
                    C.CENTER_LATITUDE, C.CENTER_LONGITUDE, C.NORTH_EAST_LATITUDE, C.NORTH_EAST_LONGITUDE,
                    C.SOUTH_WEST_LATITUDE, C.SOUTH_WEST_LONGITUDE, C.URL AS CITY_URL, C.DISPLAY_PRIORITY
                    ,CASE WHEN S.SUBURB_ID  IN (SELECT (S.suburb_id) as suburb_id FROM cms.suburb S left join cms.table_attributes TA ON (TA.attribute_name = 'DESC_CONTENT_FLAG'
                    and S.suburb_id = TA.table_id and TA.table_name = 'suburb') where (TA.attribute_value = 1)
                    and S.STATUS='Active')  THEN 1
                    ELSE 0 END AS SUBURB_IS_DESCRIPTION_VERIFIED,
				    S.LATITUDE AS SUBURB_LATITUDE, S.LONGITUDE AS SUBURB_LONGITUDE
                    FROM cms.suburb S JOIN cms.city C JOIN cms.locality L JOIN cms.resi_project RP
                    JOIN cms.resi_project_options RPO ON
                    (RP.LOCALITY_ID=L.LOCALITY_ID AND S.CITY_ID=C.CITY_ID AND L.SUBURB_ID=S.SUBURB_ID AND RPO.PROJECT_ID = RP.PROJECT_ID)
                    JOIN cms.project_status_master psm ON (psm.id = RP.project_status_id)
                    WHERE $projectCondition AND RP.VERSION = $version AND RP.RESIDENTIAL_FLAG ='Residential' AND RPO.OPTION_CATEGORY = 'Actual' group by S.SUBURB_ID";

	$documents = array ();
	$priceAndDemadRanksCityWise = array();
	$distinctCities = array();
	$result = mysql_unbuffered_query ( $sql, $solrDB );
	if ($result == FALSE) {
		$logger->error("Error while fetching Suburb Data using query : \n ". $sql."\n");
		$logger->error("Mysql error : \n". mysql_error());
		die();
	}

		while ( $document = mysql_fetch_assoc ( $result ) ) {

			$document ['SUBURB_DESCRIPTION'] = removeNonAsciiCharacters ( $document ['SUBURB_DESCRIPTION'] );
			$suburbId = $document['SUBURB_ID'];
			$cityId = $document['CITY_ID'];
			if (! empty ( $suburbProjCountDoc [$document ['SUBURB_ID']] )) {
				$document ["SUBURB_PROJECT_COUNT"] = $suburbProjCountDoc [$document ['SUBURB_ID']]['PROJECT_COUNT'];
				$document ["SUBURB_PROPERTY_COUNT"] = $suburbProjCountDoc [$document ['SUBURB_ID']]['PROPERTY_COUNT'];
				$document ["SUBURB_LOCALITY_COUNT"] = $suburbProjCountDoc [$document ['SUBURB_ID']]['LOCALITY_COUNT'];
			}
			$distinctCities[$cityId] = 1;
			 unset($solrSuburbsList[$suburbId]);
			if(!isset($priceAndDemadRanksCityWise[$cityId])){
				$priceAndDemadRanksCityWise[$cityId] = array();
				$priceAndDemadRanksCityWise[$cityId]['totalLocalityCount'] = 0;
				$priceAndDemadRanksCityWise[$cityId]['priceRise'] = array();
				$priceAndDemadRanksCityWise[$cityId]['demandCount'] = array();
				$priceAndDemadRanksCityWise[$cityId]['locCount'] = array();
				$priceAndDemadRanksCityWise[$cityId]['totalDemandCount'] = 0;
			}
			$priceAndDemadRanksCityWise[$cityId]['locCount'][$suburbId] = $document['SUBURB_LOCALITY_COUNT'];
			$priceAndDemadRanksCityWise[$cityId]['totalLocalityCount'] += $document['SUBURB_LOCALITY_COUNT'];

			if (empty ( $document ['LATITUDE'] ))
				unset ( $document ['LATITUDE'] );
			if (empty ( $document ['LONGITUDE'] ))
				unset ( $document ['LONGITUDE'] );
			if (empty ( $document ['NORTH_EAST_LATITUDE'] ))
				unset ( $document ['NORTH_EAST_LATITUDE'] );
			if (empty ( $document ['NORTH_EAST_LONGITUDE'] ))
				unset ( $document ['NORTH_EAST_LONGITUDE'] );
			if (empty ( $document ['SOUTH_WEST_LATITUDE'] ))
				unset ( $document ['SOUTH_WEST_LATITUDE'] );
			if (empty ( $document ['SOUTH_WEST_LONGITUDE'] ))
				unset ( $document ['SOUTH_WEST_LONGITUDE'] );
			if (empty ( $document ['CENTER_LATITUDE'] ))
				unset ( $document ['CENTER_LATITUDE'] );
			if (empty ( $document ['CENTER_LONGITUDE'] ))
				unset ( $document ['CENTER_LONGITUDE'] );
			if (empty ( $document ['DISPLAY_PRIORITY'] ))
				unset ( $document ['DISPLAY_PRIORITY'] );
			if (empty ( $document ['SUBURB_TAG_LINE'] ))
				unset ( $document ['SUBURB_TAG_LINE'] );
			if( !isValidGeo($document['SUBURB_LATITUDE'], $document['SUBURB_LONGITUDE']) ){
				unset($document['SUBURB_LATITUDE']);
				unset($document['SUBURB_LONGITUDE']);
			}
		    $document ['SUBURB_DOMINANT_UNIT_TYPE'] = getDominantUnitType($suburbs, $suburbDefaultUnitType, $document['SUBURB_ID']);#$suburbDoc ['unit_type'];
            if( isset($couponData[$suburbId]) ){
                $document['SUBURB_COUPON_MAX_DISCOUNT'] = $couponData[$suburbId];
                $document['SUBURB_COUPON_AVAILABLE'] = true;
            }
			if (isset ( $suburbs [$document ['SUBURB_ID']] )) {
				$suburbDoc = $suburbs [$document ['SUBURB_ID']];

				if (! empty ( $suburbDoc ['average_price_per_unit_area'] )) {
					$document ['SUBURB_PRICE_PER_UNIT_AREA'] = $suburbDoc ['average_price_per_unit_area'];
				}
				if (! empty ( $suburbDoc ['Apartment']['average_price_per_unit_area'] )) {
					$document ['SUBURB_PRICE_PER_UNIT_AREA_APARTMENT'] = $suburbDoc ['Apartment']['average_price_per_unit_area'];
				}
				if (! empty ( $suburbDoc ['Plot']['average_price_per_unit_area'] )) {
					$document ['SUBURB_PRICE_PER_UNIT_AREA_PLOT'] = $suburbDoc ['Plot']['average_price_per_unit_area'];
				}
				if (! empty ( $suburbDoc ['Villa']['average_price_per_unit_area'] )) {
					$document ['SUBURB_PRICE_PER_UNIT_AREA_VILLA'] = $suburbDoc ['Villa']['average_price_per_unit_area'];
				}

				if (! empty ( $suburbDoc ['RISE_PERCENT'] )) {
					$priceAndDemadRanksCityWise[$cityId]['priceRise'][$suburbId] =	$suburbDoc['RISE_PERCENT'];
					$document ['SUBURB_PRICE_RISE'] = $suburbDoc ['RISE_PERCENT'];
					$document ['SUBURB_PRICE_RISE_TIME'] = $suburbDoc ['RISE_PERIOD_MONTHS'];
				}
				if (! empty ( $suburbDoc ['Apartment']['RISE_PERCENT'] )) {
					$document ['SUBURB_PRICE_RISE_APARTMENT'] = $suburbDoc ['Apartment']['RISE_PERCENT'];
				}
				if (! empty ( $suburbDoc ['Plot']['RISE_PERCENT'] )) {
					$document ['SUBURB_PRICE_RISE_PLOT'] = $suburbDoc ['Plot']['RISE_PERCENT'];
				}
				if (! empty ( $suburbDoc ['Villa']['RISE_PERCENT'] )) {
					$document ['SUBURB_PRICE_RISE_VILLA'] = $suburbDoc ['Villa']['RISE_PERCENT'];
				}
			}


			if (isset ( $suburbEnquiryCountArray [$document ['SUBURB_ID']] )) {
				$document ['SUBURB_ENQUIRY_COUNT'] = $suburbEnquiryCountArray [$document ['SUBURB_ID']];
				$priceAndDemadRanksCityWise[$cityId]['demandCount'][$suburbId] = $document['SUBURB_ENQUIRY_COUNT'];
				$priceAndDemadRanksCityWise[$cityId]['totalDemandCount'] += $document['SUBURB_ENQUIRY_COUNT'];
			}
			$document ['SUBURB_VIEW_COUNT'] = 0;

			if(isset ( $heroshotImages[ $document ['SUBURB_ID']])){
				$document ['SUBURB_HEROSHOT_IMAGE_URL'] = $heroshotImages[$document ['SUBURB_ID']];
			}

			if(isset ( $heroshotImages[ $document ['CITY_ID']])){
				$document ['CITY_HEROSHOT_IMAGE_URL'] = $heroshotImages[$document ['CITY_ID']];
			}

			if(isset($suburbWithSource[$document['SUBURB_ID']]['SOURCE_ID'])){
					$document['SUBURB_SOURCE_ID'] = $suburbWithSource[$document['SUBURB_ID']]['SOURCE_ID'];
			}
			if (isset($suburbWithSource[$document['SUBURB_ID']]['SOURCE_DOMAIN'])){
					$document['SUBURB_SOURCE_DOMAIN'] = $suburbWithSource[$document['SUBURB_ID']]['SOURCE_DOMAIN'];
			}

			array_push ( $documents, $document );
		}

	foreach($priceAndDemadRanksCityWise as $cityId => $values){
		arsort($values['priceRise']);
		arsort($values['demandCount']);

		$relativeRank = 1;
		$totalLocalityCount = $values['totalLocalityCount'];
		foreach($values['priceRise'] as $suburbId => $priceRise){
			$values['priceRise'][$suburbId] = array();
			$localitiesCountExcludeOwn = $values['totalLocalityCount'] - $values['locCount'][$suburbId] + 1;
			$values['priceRise'][$suburbId]['priceRiseRankPercentage'] =  ceil(($relativeRank/$localitiesCountExcludeOwn)*100);
			$values['priceRise'][$suburbId]['value'] = $priceRise;
			$relativeRank += $values['locCount'][$suburbId];
		}

		$relativeRank = 1;
		$rank = 1;

		$totalLocalityCount = $values['totalLocalityCount'];
		foreach($values['demandCount'] as $suburbId => $demandCount){
			$values['demandCount'][$suburbId] = array();
			$localitiesCountExcludeOwn = $values['totalLocalityCount'] - $values['locCount'][$suburbId] + 1;
			$values['demandCount'][$suburbId]['demandCountRankPercentage'] =  ceil(($relativeRank/$localitiesCountExcludeOwn)*100);
			$values['demandCount'][$suburbId]['demandPercentage'] = ceil(($demandCount/$values['totalDemandCount'])*100);
			$values['demandCount'][$suburbId]['value'] = $demandCount;
			$relativeRank += $values['locCount'][$suburbId];
		}

		$priceAndDemadRanksCityWise[$cityId]['priceRise'] = $values['priceRise'];
		$priceAndDemadRanksCityWise[$cityId]['demandCount'] = $values['demandCount'];
	}
	$len = count ( $documents );
	$urlTypes = getUrlTypes();
	for($i = 0; $i < $len; $i ++) {
		$document = &$documents [$i];
		$suburbOverviewUrl = url_lib_suburb_url($document);
		$cityOverviewUrl = url_lib_city_url($document);
		$cityId = $document['CITY_ID'];
		$suburbId = $document['SUBURB_ID'];
		@$priceRiseData = $priceAndDemadRanksCityWise[$cityId]['priceRise'][$suburbId];
		@$demandCountData = $priceAndDemadRanksCityWise[$cityId]['demandCount'][$suburbId];

		if(isset($priceRiseData)){
			$document['SUBURB_PRICE_RISE_RANK_PERCENTAGE'] = $priceRiseData['priceRiseRankPercentage'];
		}

		if(isset($demandCountData)){
			$document['SUBURB_ENQUIRY_PERCENTAGE'] = $demandCountData['demandPercentage'];
			$document['SUBURB_ENQUIRY_RANK_PERCENTAGE'] = $demandCountData['demandCountRankPercentage'];
		}

		if (isset ( $cityOverviewUrl )) {
			$document ['CITY_OVERVIEW_URL'] = $cityOverviewUrl;
		}
		if (isset ( $suburbOverviewUrl )) {
			$document ['SUBURB_OVERVIEW_URL'] = $suburbOverviewUrl;
		}
		foreach ( $urlTypes as $urlType => $propertyType ) {
			if ( $urlType == 'newLaunch' ) {
				$cityTaxonomyUrls [$urlType] = url_lib_city_new_launch_url($document);
				continue;
			}
			if(!($urlType == 'resaleApartmentUrl' || $urlType == 'resalePropertyUrl') || (
				($urlType == 'resalePropertyUrl' && isset($resaleListingCountData[$document['CITY_ID']])) ||
				($urlType == 'resaleApartmentUrl' && isset($resaleCountData[$document['CITY_ID']])))){
				$cityTaxonomyUrls [$urlType] = url_lib_city_list_url($document, $propertyType);
			}
			else{
				unset($cityTaxonomyUrls [$urlType]);
			}
			if(!($urlType == 'resaleApartmentUrl' || $urlType == 'resalePropertyUrl') || (
				($urlType == 'resalePropertyUrl' && isset($resaleListingCountData[$document['SUBURB_ID']])) ||
				($urlType == 'resaleApartmentUrl' && isset($resaleCountData[$document['SUBURB_ID']])))){
				$suburbTaxonomyUrls [$urlType] = url_lib_suburb_list_url($document, $propertyType);
			}
			else{
				unset($suburbTaxonomyUrls [$urlType]);
			}
		}

		$document['CITY_TAXONOMY_URL'] = json_encode ( $cityTaxonomyUrls );
		$document['SUBURB_TAXONOMY_URL'] = json_encode ( $suburbTaxonomyUrls );
	}

	$solrSuburbDelete = array();
	foreach($solrSuburbsList as $key => $value){
		$solrSuburbDelete[] = "SUBURB-".$key;
	}
	if(!empty($paramSuburbIds)&&count($distinctCities) >0){
		$paramCityIds .= ",".implode(",", array_keys($distinctCities));
	}
	$paramCityIds = ltrim($paramCityIds, ",");
	return array (
			$solrSuburbDelete,//$suburbsOnProjectIds ['deleteIds'],
			$documents
	);
}
function removeNonAsciiCharacters($desc) {
	// due to non ascii characters in description it was not updated in solr, so remove non ascii chars
	$nonAsciiCharRegex = '/[^(\\x00-\\x7F)]*/';
	if ($desc) {
		$desc = preg_replace ( $nonAsciiCharRegex, '', $desc );
	}
	return $desc;
}
function getLocalitiesFromDB() {
	global $solrDB, $logger, $paramProjectIds, $localitiesOnProjectIds, $localityEnquiryCountArray, $projCmsActiveCondition;
	global $version, $projectSafetyAndLivabilityData, $localitySafetyAndLivabilityData, $paramLocalityIds, $paramSuburbIds;
	global $resaleLocalityPrices,$resaleLocalityPricesFromListings, $solrLocalityList, $heroshotImages, $resaleCountData;
	global $localityPrices, $resaleListingCountData;
	global $localityWithSource;
    $localities = $localityPrices;
    $localityDefaultUnitType = getDomainDefaultUnitType('locality');
	$localityProjectStatusCount = loadProjectStatusCountInfo("LOCALITY");
	$localityPrimaryPriceUnitType = loadPrimaryPriceForLocalityByUnitType();
	$localitiesUnitsInfo = getLocalitiesUnitsInfo();

	$cityLocalityCounts = loadCityLocalityCount();
	$cities = loadPriceRiseInfo ( 'CITY' );
	$localityProjectPropertyCounts = loadProjectCountInfo("LOCALITY");
    $couponData = getCouponsDiscount("locality");
	if (empty ( $localityEnquiryCountArray ))
		list($localityEnquiryCountArray, $localityLastEnquiredDate) = loadEnquiryCount ( 'LOCALITY' );

	$projectCondition = " RP.STATUS IN ({$projCmsActiveCondition}) ";

	$projectCondition = appendConditionForB2b($projectCondition);
	$paramConditionArray = array();
	if(!empty($paramProjectIds)){
		$paramConditionArray[] = " RP.PROJECT_ID IN ($paramProjectIds)";
	}
	if(!empty($paramLocalityIds)){
		$paramConditionArray[] = " L.LOCALITY_ID IN ($paramLocalityIds) ";
	}
	if(count($paramConditionArray) > 0){
		$paramCondition = implode(" OR ", $paramConditionArray);
		$projectCondition .= " AND ( $paramCondition )";
	}
	$distinctSuburb = array();
	$sql = "SELECT 'LOCALITY' AS DOCUMENT_TYPE, CONCAT('LOCALITY-', L.LOCALITY_ID) AS id,
                    L.LOCALITY_ID, L.LABEL LOCALITY,
                    CONCAT(S.LABEL, ' ', L.LABEL)   AS NEWS_TAG,
                    C.CITY_ID, S.SUBURB_ID, L.LATITUDE AS LOCALITY_LATITUDE,
                    L.LONGITUDE AS LOCALITY_LONGITUDE, L.URL as LOCALITY_URL, L.PRIORITY AS LOCALITY_PRIORITY,
                    L.DESCRIPTION LOCALITY_DESCRIPTION, L.SAFETY_SCORE AS LOCALITY_SAFETY_SCORE,
                     L.LIVABILITY_SCORE AS LOCALITY_LIVABILITY_SCORE,
                    S.DESCRIPTION SUBURB_DESCRIPTION, S.PRIORITY SUBURB_PRIORITY, S.LABEL SUBURB, S.URL AS SUBURB_URL,
                    C.LABEL as CITY, C.DESCRIPTION as CITY_DESCRIPTION, C.DISPLAY_ORDER as CITY_DISPLAY_ORDER,
                    C.CENTER_LATITUDE, C.CENTER_LONGITUDE, C.NORTH_EAST_LATITUDE, C.NORTH_EAST_LONGITUDE,
                    C.SOUTH_WEST_LATITUDE, C.SOUTH_WEST_LONGITUDE, C.URL AS CITY_URL, C.DISPLAY_PRIORITY, L.TAG_LINE AS LOCALITY_TAG_LINE,
                    L.encoded_polygon AS ENCODED_POLYGON
                    ,CASE WHEN L.LOCALITY_ID  IN (SELECT L.locality_id as locality_id FROM cms.locality L left join cms.table_attributes TA ON (TA.attribute_name = 'DESC_CONTENT_FLAG'
                    and L.locality_id = TA.table_id and TA.table_name = 'locality') where (TA.attribute_value = 1)
                    and L.STATUS='Active')  THEN '1'
                    ELSE '0' END AS LOCALITY_IS_DESCRIPTION_VERIFIED
                    FROM cms.locality L JOIN cms.resi_project RP ON RP.LOCALITY_ID=L.LOCALITY_ID
                    JOIN cms.suburb S ON L.SUBURB_ID=S.SUBURB_ID JOIN cms.city C ON S.CITY_ID=C.CITY_ID
                    JOIN cms.resi_project_options RPO ON RPO.PROJECT_ID = RP.PROJECT_ID
                    JOIN cms.project_status_master psm ON (psm.id = RP.project_status_id)
                    WHERE $projectCondition AND RP.VERSION = $version AND RP.RESIDENTIAL_FLAG ='Residential' AND RPO.OPTION_CATEGORY = 'Actual'
					group by L.locality_id ";

	$documents = array ();
	$localityFieldsCityWise = array();
	$result = mysql_unbuffered_query ( $sql, $solrDB );
	if ($result) {
		while ( $document = mysql_fetch_assoc ( $result ) ) {
			$localityId = $document ['LOCALITY_ID'];
			$latitude = $document ['LOCALITY_LATITUDE'];
			$longitude = $document ['LOCALITY_LONGITUDE'];
			$cityId = $document['CITY_ID'];
			$distinctSuburb[$document['SUBURB_ID']] = 1;
			$document ['LOCALITY_DESCRIPTION'] = removeNonAsciiCharacters ( $document ['LOCALITY_DESCRIPTION'] );
			populateLocalityUnitsInfo ($document, $localitiesUnitsInfo);

            unset($solrLocalityList[$localityId]);
            if( !isset($localityFieldsCityWise[$cityId]) ){
            	$localityFieldsCityWise[$cityId] = array();
            	$localityFieldsCityWise[$cityId]['locCount'] = 0;
            	$localityFieldsCityWise[$cityId]['priceRise'] = array();
            	$localityFieldsCityWise[$cityId]['enquiryCountData'] = array();
            	$localityFieldsCityWise[$cityId]['enquiryCount'] = 0;

            }
            $localityFieldsCityWise[$cityId]['locCount']++;

            if( isset($couponData[$localityId]) ){
                $document['LOCALITY_COUPON_MAX_DISCOUNT'] = $couponData[$localityId];
                $document['LOCALITY_COUPON_AVAILABLE'] = true;
            }

            if( isset($localityProjectStatusCount[$localityId]) ){
            	$document['LOCALITY_PROJECT_STATUS_COUNT'] = json_encode($localityProjectStatusCount[$localityId]);
            }
            if(!empty($localityProjectPropertyCounts[$localityId])){
            	$document['LOCALITY_PROJECT_COUNT'] = $localityProjectPropertyCounts[$localityId]['PROJECT_COUNT'];
            }
			if (isValidGeo ( $latitude, $longitude )) {
				$document ['GEO'] = "$latitude, $longitude";
				$document ['HAS_GEO'] = 1;

				$maxRadius = getLocalityMaxRadius ( $localityId, $latitude, $longitude );
				if (! empty ( $maxRadius )) {
					$maxRadius = round ( $maxRadius, 3, PHP_ROUND_HALF_UP );
					$maxRadius = $maxRadius + 0.05; // adding 50 meters
					$document ['LOCALITY_MAX_RADIUS'] = $maxRadius;
				} else {
					unset ( $document ['LOCALITY_MAX_RADIUS'] );
				}
			} else {
				unset ( $document ['LOCALITY_LATITUDE'] );
				unset ( $document ['LOCALITY_LONGITUDE'] );
			}
			if(isset($resaleLocalityPrices[$localityId])){
				$document['LOCALITY_MIN_RESALE_PRICE'] = $resaleLocalityPrices[$localityId]['min'];
				$document['LOCALITY_MAX_RESALE_PRICE'] = $resaleLocalityPrices[$localityId]['max'];
			}

			if(isset($resaleLocalityPrices[$localityId]) || isset($resaleLocalityPricesFromListings[$localityId])){
				$arrSolrFieldName = array(0 =>'LOCALITY_AVG_RESALE_PRICE_PER_UNIT_AREA_APARTMENT',1 => 'LOCALITY_AVG_RESALE_PRICE_PER_UNIT_AREA_PLOT' , 2=>'LOCALITY_AVG_RESALE_PRICE_PER_UNIT_AREA_VILLA');
				$arrKeyName = array(0 => 'apartment' , 1 => 'plot' , 2 => 'villa');

				$countNoOfValuesField = array(0 => 0 , 1 => 0, 2 => 0);

				for($i=0;$i<3;$i++)
				{
							if(!empty($resaleLocalityPrices[$localityId][$arrKeyName[$i]]) && !empty($resaleLocalityPrices[$localityId][$arrKeyName[$i]]['avg']))
							{
								$document[$arrSolrFieldName[$i]] = $resaleLocalityPrices[$localityId][$arrKeyName[$i]]['avg'];
								$countNoOfValuesField[$i] =1;
							}

							if(!empty($resaleLocalityPricesFromListings[$localityId][$arrKeyName[$i]]['avg']))
							{
								if($countNoOfValuesField[$i] ==1)
								{
									$countNoOfValuesField[$i]++;
									$document[$arrSolrFieldName[$i]] += $resaleLocalityPricesFromListings[$localityId][$arrKeyName[$i]]['avg'];

									$document[$arrSolrFieldName[$i]] = ($document[$arrSolrFieldName[$i]]/$countNoOfValuesField[$i]);
								}
								else
								{
									$document[$arrSolrFieldName[$i]] = $resaleLocalityPricesFromListings[$localityId][$arrKeyName[$i]]['avg'];
								}
							}
				}

			}

			if(empty($document['LOCALITY_MIN_RESALE_PRICE'])){
				unset($document['LOCALITY_MIN_RESALE_PRICE']);
			}
			if(empty($document['LOCALITY_MAX_RESALE_PRICE'])){
				unset($document['LOCALITY_MAX_RESALE_PRICE']);
			}
			if (empty ( $document ['NORTH_EAST_LATITUDE'] ))
				unset ( $document ['NORTH_EAST_LATITUDE'] );
			if (empty ( $document ['NORTH_EAST_LONGITUDE'] ))
				unset ( $document ['NORTH_EAST_LONGITUDE'] );
			if (empty ( $document ['SOUTH_WEST_LATITUDE'] ))
				unset ( $document ['SOUTH_WEST_LATITUDE'] );
			if (empty ( $document ['SOUTH_WEST_LONGITUDE'] ))
				unset ( $document ['SOUTH_WEST_LONGITUDE'] );
			if (empty ( $document ['CENTER_LATITUDE'] ))
				unset ( $document ['CENTER_LATITUDE'] );
			if (empty ( $document ['CENTER_LONGITUDE'] ))
				unset ( $document ['CENTER_LONGITUDE'] );
			if (empty ( $document ['DISPLAY_PRIORITY'] ))
				unset ( $document ['DISPLAY_PRIORITY'] );
			if (empty ( $document ['LOCALITY_SAFETY_SCORE'] ))
				unset ( $document ['LOCALITY_SAFETY_SCORE'] );
			if (empty ( $document ['LOCALITY_LIVABILITY_SCORE'] ))
				unset ( $document ['LOCALITY_LIVABILITY_SCORE'] );
			if (empty ( $document ['LOCALITY_TAG_LINE'] ))
				unset ( $document ['LOCALITY_TAG_LINE'] );
			if (empty ( $document ['ENCODED_POLYGON'] ))
				unset ( $document ['ENCODED_POLYGON'] );

			if(isset($localityWithSource[$document['LOCALITY_ID']]['SOURCE_ID'])){
					$document['LOCALITY_SOURCE_ID'] = $localityWithSource[$document['LOCALITY_ID']]['SOURCE_ID'];
			}
			if (isset($localityWithSource[$document['LOCALITY_ID']]['SOURCE_DOMAIN'])){
					$document['LOCALITY_SOURCE_DOMAIN'] = $localityWithSource[$document['LOCALITY_ID']]['SOURCE_DOMAIN'];
			}

			if (! empty ( $cities[$document['CITY_ID']]['average_price_per_unit_area'] )) {
				$document ['CITY_PRICE_PER_UNIT_AREA'] = $cities[$document['CITY_ID']]['average_price_per_unit_area'];
			}

            if( isset($localityDefaultUnitType[$document['LOCALITY_ID']]) ){
                $document['LOCALITY_UNIT_TYPES'] = $localityDefaultUnitType[$document['LOCALITY_ID']]['unit_types'];
            }
            else {
                $document['LOCALITY_UNIT_TYPES'] = 'Apartment';
            }

            /*if(isset($localityPrimaryPriceUnitType[$document['LOCALITY_ID']])){
            	foreach($localityPrimaryPriceUnitType[$document['LOCALITY_ID']] as $key => $value){
            		if(isset($value)){
            			$document['LOCALITY_PRICE_PER_UNIT_AREA_'.strtoupper($key)] = $value;
            		}
            		else{
            			$logger->info("LOCALITY_PRICE_PER_UNIT_AREA not set for " . $key . " in LocalityID:" . $document['LOCALITY_ID']);
            		}
            	}
            }*/

		    $document['LOCALITY_DOMINANT_UNIT_TYPE'] = getDominantUnitType($localities, $localityDefaultUnitType, $document['LOCALITY_ID']);#$localityDoc ['unit_type'];
			if (isset ( $localities [$document ['LOCALITY_ID']] )) {
				$localityDoc = $localities [$document ['LOCALITY_ID']];

				if (! empty ( $localityDoc ['average_price_per_unit_area'] )) {
					$document ['LOCALITY_PRICE_PER_UNIT_AREA'] = $localityDoc ['average_price_per_unit_area'];
					$document ['LOCALITY_PRICE_PER_UNIT_AREA_APARTMENT'] = $localityDoc ['average_price_per_unit_area'];
				}
				if (! empty ( $localityDoc ['Apartment']['average_price_per_unit_area'] )) {
					$document ['LOCALITY_PRICE_PER_UNIT_AREA_APARTMENT'] = $localityDoc ['Apartment']['average_price_per_unit_area'];
				}
				if (! empty ( $localityDoc ['Plot']['average_price_per_unit_area'] )) {
					$document ['LOCALITY_PRICE_PER_UNIT_AREA_PLOT'] = $localityDoc ['Plot']['average_price_per_unit_area'];
				}
				if (! empty ( $localityDoc ['Villa']['average_price_per_unit_area'] )) {
					$document ['LOCALITY_PRICE_PER_UNIT_AREA_VILLA'] = $localityDoc ['Villa']['average_price_per_unit_area'];
				}

				if (! empty ( $localityDoc ['RISE_PERCENT'] )) {
					$document ['LOCALITY_PRICE_RISE'] = $localityDoc ['RISE_PERCENT'];
					$document ['LOCALITY_PRICE_RISE_TIME'] = $localityDoc ['RISE_PERIOD_MONTHS'];
					$document ['LOCALITY_PRICE_APPRECIATION_RATE'] = ( float ) $localityDoc ['RISE_PERCENT'] / $localityDoc ['RISE_PERIOD_MONTHS'];
					$localityFieldsCityWise[$cityId]['priceRise'][$localityId] = $document['LOCALITY_PRICE_RISE'];
				}
				if (! empty ( $localityDoc ['Apartment']['RISE_PERCENT'] )) {
					$document ['LOCALITY_PRICE_RISE_APARTMENT'] = $localityDoc ['Apartment']['RISE_PERCENT'];
				}
				if (! empty ( $localityDoc ['Plot']['RISE_PERCENT'] )) {
					$document ['LOCALITY_PRICE_RISE_PLOT'] = $localityDoc ['Plot']['RISE_PERCENT'];
				}
				if (! empty ( $localityDoc ['Villa']['RISE_PERCENT'] )) {
					$document ['LOCALITY_PRICE_RISE_VILLA'] = $localityDoc ['Villa']['RISE_PERCENT'];
				}

				if (! empty ( $localityDoc ['LOCALITY_PRICE_RISE_6MONTHS'] )) {
					$document ['LOCALITY_PRICE_RISE_6MONTHS'] = $localityDoc ['LOCALITY_PRICE_RISE_6MONTHS'];
				}
			}

			if(empty($document ['LOCALITY_PRICE_PER_UNIT_AREA_VILLA'])){
				unset($document ['LOCALITY_PRICE_PER_UNIT_AREA_VILLA']);
			}

			if(empty($document ['LOCALITY_PRICE_PER_UNIT_AREA_APARTMENT'])){
				unset($document ['LOCALITY_PRICE_PER_UNIT_AREA_APARTMENT']);
			}

			if(empty($document ['LOCALITY_PRICE_PER_UNIT_AREA_PLOT'])){
				unset($document ['LOCALITY_PRICE_PER_UNIT_AREA_PLOT']);
			}

			if (isset ( $localityEnquiryCountArray [$document ['LOCALITY_ID']] )) {
				$document ['LOCALITY_ENQUIRY_COUNT'] = $localityEnquiryCountArray [$document ['LOCALITY_ID']];
				$localityFieldsCityWise[$cityId]['enquiryCount'] += $document['LOCALITY_ENQUIRY_COUNT'];
				$localityFieldsCityWise[$cityId]['enquiryCountData'][$localityId] = $document['LOCALITY_ENQUIRY_COUNT'];
			}
			if (isset ( $localitySafetyAndLivabilityData ['SAFETY_RANK'] [$document ['CITY_ID']] [$document ['LOCALITY_ID']] )) {
				$document ['LOCALITY_SAFETY_RANK'] = $localitySafetyAndLivabilityData ['SAFETY_RANK'] [$document ['CITY_ID']] [$document ['LOCALITY_ID']];
			}
			if (isset ( $localitySafetyAndLivabilityData ['LIVABILITY_RANK'] [$document ['CITY_ID']] [$document ['LOCALITY_ID']] )) {
				$document ['LOCALITY_LIVABILITY_RANK'] = $localitySafetyAndLivabilityData ['LIVABILITY_RANK'] [$document ['CITY_ID']] [$document ['LOCALITY_ID']];
			}
			if (isset ( $localitySafetyAndLivabilityData [$document ['CITY_ID']] ['MIN_SAFETY_SCORE'] )) {
				$document ['LOCALITY_MIN_SAFETY_SCORE'] = $localitySafetyAndLivabilityData [$document ['CITY_ID']] ['MIN_SAFETY_SCORE'];
			}
			if (isset ( $localitySafetyAndLivabilityData [$document ['CITY_ID']] ['MAX_SAFETY_SCORE'] )) {
				$document ['LOCALITY_MAX_SAFETY_SCORE'] = $localitySafetyAndLivabilityData [$document ['CITY_ID']] ['MAX_SAFETY_SCORE'];
			}
			if (isset ( $localitySafetyAndLivabilityData [$document ['CITY_ID']] ['MIN_LIVABILITY_SCORE'] )) {
				$document ['LOCALITY_MIN_LIVABILITY_SCORE'] = $localitySafetyAndLivabilityData [$document ['CITY_ID']] ['MIN_LIVABILITY_SCORE'];
			}
			if (isset ( $localitySafetyAndLivabilityData [$document ['CITY_ID']] ['MAX_LIVABILITY_SCORE'] )) {
				$document ['LOCALITY_MAX_LIVABILITY_SCORE'] = $localitySafetyAndLivabilityData [$document ['CITY_ID']] ['MAX_LIVABILITY_SCORE'];
			}

			if (isset ( $projectSafetyAndLivabilityData [$document ['LOCALITY_ID']] ['MAX_SAFETY_SCORE'] ))
				$document ['PROJECT_MAX_SAFETY_SCORE'] = $projectSafetyAndLivabilityData [$document ['LOCALITY_ID']] ['MAX_SAFETY_SCORE'];
			if (isset ( $projectSafetyAndLivabilityData [$document ['LOCALITY_ID']] ['MIN_SAFETY_SCORE'] ))
				$document ['PROJECT_MIN_SAFETY_SCORE'] = $projectSafetyAndLivabilityData [$document ['LOCALITY_ID']] ['MIN_SAFETY_SCORE'];
			if (isset ( $projectSafetyAndLivabilityData [$document ['LOCALITY_ID']] ['MAX_LIVABILITY_SCORE'] ))
				$document ['PROJECT_MAX_LIVABILITY_SCORE'] = $projectSafetyAndLivabilityData [$document ['LOCALITY_ID']] ['MAX_LIVABILITY_SCORE'];
			if (isset ( $projectSafetyAndLivabilityData [$document ['LOCALITY_ID']] ['MIN_LIVABILITY_SCORE'] ))
				$document ['PROJECT_MIN_LIVABILITY_SCORE'] = $projectSafetyAndLivabilityData [$document ['LOCALITY_ID']] ['MIN_LIVABILITY_SCORE'];

			// Optimize Removal of City Locality Query.
			/*if (isset ($cityLocalityCounts[$document['CITY_ID']])) {
				if (!isset ($document['CITY_LOCALITY_COUNT']))
					$document['CITY_LOCALITY_COUNT'] = $cityLocalityCounts[$document['CITY_ID']];
			}*/

			$document ['LOCALITY_VIEW_COUNT'] = 0;

			if(isset ( $heroshotImages[ $document ['LOCALITY_ID']])){
				$document ['LOCALITY_HEROSHOT_IMAGE_URL'] = $heroshotImages[$document ['LOCALITY_ID']];
			}

			if(isset ( $heroshotImages[ $document ['CITY_ID']])){
				$document ['CITY_HEROSHOT_IMAGE_URL'] = $heroshotImages[$document ['CITY_ID']];
			}

			array_push ( $documents, $document );
		}
	} else {
			$logger->error("Error while fetching Locality Data using query : \n ". $sql."\n");
			$logger->error("Mysql error : \n". mysql_error());
			die();
	}

	$amenityTypes = getAmenityTypesFromDB();
	$amenityTypeLen = count($amenityTypes);
	$urlTypes = getUrlTypes();

	// sorting the price rise, enquiryCount of localities based on city Id.
	foreach($localityFieldsCityWise as $cityId => $values){
		arsort($values['priceRise']);
		arsort($values['enquiryCountData']);

		$rank = 1;
		foreach($values['priceRise'] as $locId => $priceRiseValue){
			$values['priceRise'][$locId] = array();
			$values['priceRise'][$locId]['rank'] = $rank;
			$values['priceRise'][$locId]['rankPercent'] = ceil(($rank/$values['locCount'])*100);
			$values['priceRise'][$locId]['value'] = $priceRiseValue;
			$rank++;
		}

		$rank = 1;
		foreach($values['enquiryCountData'] as $locId => $enquiryCount){
			$values['enquiryCountData'][$locId] = array();
			$values['enquiryCountData'][$locId]['rank'] = $rank;
			$values['enquiryCountData'][$locId]['rankPercent'] = ceil(($rank/$values['locCount'])*100);
			$values['enquiryCountData'][$locId]['value'] = $enquiryCount;
			$values['enquiryCountData'][$locId]['percentage'] = ceil(($enquiryCount/$values['enquiryCount'])*100);

			$rank++;
		}

		$localityFieldsCityWise[$cityId]['priceRise'] = $values['priceRise'];
		$localityFieldsCityWise[$cityId]['enquiryCountData'] = $values['enquiryCountData'];

	}

	$len = count ( $documents );
	for($i = 0; $i < $len; $i ++) {
		$document = &$documents [$i];
		$localityOverviewUrl = url_lib_locality_url($document);
		$suburbOverviewUrl = url_lib_suburb_url($document);
		$cityOverviewUrl = url_lib_city_url($document);
		$cityId = $document['CITY_ID'];
		$localityId = $document['LOCALITY_ID'];
		$priceData = $localityFieldsCityWise[$cityId]['priceRise'];
		$enquiryData = $localityFieldsCityWise[$cityId]['enquiryCountData'];
		$cityLocalityCount = $localityFieldsCityWise[$cityId]['locCount'];

		$document['CITY_LOCALITY_COUNT'] = $cityLocalityCount;
		if(!empty($enquiryData) && isset($document['LOCALITY_ENQUIRY_COUNT'])){
			$document['LOCALITY_ENQUIRY_PERCENTAGE'] = $enquiryData[$localityId]['percentage'];//ceil(($document['LOCALITY_ENQUIRY_COUNT']/$enquiryData)*100);
			$document['LOCALITY_ENQUIRY_RANK'] = $enquiryData[$localityId]['rank'];
			$document['LOCALITY_ENQUIRY_RANK_PERCENTAGE'] = $enquiryData[$localityId]['rankPercent'];
		}
		if(isset($priceData[$localityId])){
			$document['LOCALITY_PRICE_RISE_RANK'] = $priceData[$localityId]['rank'];
			$document['LOCALITY_PRICE_RISE_RANK_PERCENTAGE'] = $priceData[$localityId]['rankPercent'];
		}
		if(isset($document['LOCALITY_LIVABILITY_RANK'])){
			$document['LOCALITY_LIVABILITY_RANK_PERCENTAGE'] = ceil(($document['LOCALITY_LIVABILITY_RANK']/$cityLocalityCount)*100);
		}
		for($j = 0; $j < $amenityTypeLen; $j ++) {
			$urls[$j] = $amenityTypes[$j];
			$urls[$j][] = url_lib_locality_url_area_type($document, str_replace('_', '-', $amenityTypes [$j][2]).'s');

		}
		if (isset ($urls)) {
			$document['AMENITIES_URLS'] = json_encode($urls);
		}

		if (isset ( $cityOverviewUrl )) {
			$document ['CITY_OVERVIEW_URL'] = $cityOverviewUrl;
		}
		if (isset ( $suburbOverviewUrl )) {
			$document ['SUBURB_OVERVIEW_URL'] = $suburbOverviewUrl;
		}
		if (isset ( $localityOverviewUrl )) {
			$document ['LOCALITY_OVERVIEW_URL'] = $localityOverviewUrl;
		}

		foreach ( $urlTypes as $urlType => $propertyType ) {
			if ( $urlType == 'newLaunch' ) {
				$cityTaxonomyUrls [$urlType] = url_lib_city_new_launch_url($document);
				continue;
			}
			if(!($urlType == 'resaleApartmentUrl' || $urlType == 'resalePropertyUrl') || (
				($urlType == 'resalePropertyUrl' && isset($resaleListingCountData[$document['LOCALITY_ID']])) ||
				($urlType == 'resaleApartmentUrl' && isset($resaleCountData[$document['LOCALITY_ID']])))){
				$localityTaxonomyUrls [$urlType] = url_lib_locality_list_url($document, $propertyType);
			}
			else{
				unset($localityTaxonomyUrls [$urlType]);
			}
			if(!($urlType == 'resaleApartmentUrl' || $urlType == 'resalePropertyUrl') || (
				($urlType == 'resalePropertyUrl' && isset($resaleListingCountData[$document['CITY_ID']])) ||
				($urlType == 'resaleApartmentUrl' && isset($resaleCountData[$document['CITY_ID']])))){
				$cityTaxonomyUrls [$urlType] = url_lib_city_list_url($document, $propertyType);
			}
			else{
				unset($cityTaxonomyUrls [$urlType]);
			}
			if(!($urlType == 'resaleApartmentUrl' || $urlType == 'resalePropertyUrl') || (
				($urlType == 'resalePropertyUrl' && isset($resaleListingCountData[$document['SUBURB_ID']])) ||
				($urlType == 'resaleApartmentUrl' && isset($resaleCountData[$document['SUBURB_ID']])))){
				$suburbTaxonomyUrls [$urlType] = url_lib_suburb_list_url($document, $propertyType);
			}
			else{
				unset($suburbTaxonomyUrls [$urlType]);
			}
		}
		$document['CITY_TAXONOMY_URL'] = json_encode ( $cityTaxonomyUrls );
		$document['SUBURB_TAXONOMY_URL'] = json_encode ( $suburbTaxonomyUrls );
		$document['LOCALITY_TAXONOMY_URL'] = json_encode ( $localityTaxonomyUrls );
	}

	$solrLocalityDelete = array();
	foreach($solrLocalityList as $key => $value){
		$solrLocalityDelete[] = "LOCALITY-".$key;
	}
	if(!empty($paramLocalityIds)&&count($distinctSuburb) >0){
		$paramSuburbIds .= ",".implode(",", array_keys($distinctSuburb));
	}
	$paramSuburbIds = ltrim($paramSuburbIds, ",");
	return array (
			$solrLocalityDelete,//$localitiesOnProjectIds ['deleteIds'],
			$documents
	);
}
function getBuildersFromDB() {
	global $solrDB, $projectAndBuilderImages, $buildersOnProjectIds, $paramProjectIds;
	global $builderEnquiryCountArray, $projCmsActiveCondition, $version;
	global $solrBuilderList,$logger, $paramBuilderIds;
	global $builderWithSource;

	$builderLocCountDoc = builderLocalityCount();
	$builderProjCountDoc = loadProjectCountInfo ( 'BUILDER' );
    $couponData = getCouponsDiscount("builder");
	if (empty ( $builderEnquiryCountArray ))
		list($builderEnquiryCountArray, $builderLastEnquiredDate) = loadEnquiryCount ( 'BUILDER' );
	$maxActualBuliderScore = getMaxBuilderScoreFromDb();

	$projectCondition = " RP.STATUS IN ({$projCmsActiveCondition}) ";
	/*if (! empty ( $buildersOnProjectIds ) && strlen ( $buildersOnProjectIds ['addIds'] ) > 0) {
		$projectCondition = " RP.BUILDER_ID IN ({$buildersOnProjectIds['addIds']})  ";
	} else if (! empty ( $paramProjectIds ) || !empty($paramBuilderIds) ) {
		return array (
				$buildersOnProjectIds ['deleteIds'],
				array ()
		);
	}*/
	$projectCondition = appendConditionForB2b($projectCondition);
	$paramConditionArray = array();
	if(!empty($paramProjectIds)){
		$paramConditionArray[] = " RP.PROJECT_ID IN ($paramProjectIds)";
	}
	if(!empty($paramBuilderIds)){
		$paramConditionArray[] = " B.BUILDER_ID IN ($paramBuilderIds) ";
	}
	if(count($paramConditionArray) > 0){
		$paramCondition = implode(" OR ", $paramConditionArray);
		$projectCondition .= " AND ( $paramCondition )";
	}
	$qry = <<<QRY
        SELECT 'BUILDER' AS DOCUMENT_TYPE, CONCAT("BUILDER-", B.BUILDER_ID) AS id, B.BUILDER_ID,
            B.BUILDER_NAME, B.DESCRIPTION BUILDER_DESCRIPTION, B.URL BUILDER_URL,
            B.DISPLAY_ORDER BUILDER_PRIORITY, B.ESTABLISHED_DATE BUILDER_ESTABLISHED_DATE,
            B.WEBSITE AS BUILDER_WEBSITE, B.ADDRESS AS BUILDER_ADDRESS,
            B.BUILDER_SCORE as BUILDER_SCORE, B.listed AS IS_BUILDER_LISTED
            FROM cms.resi_builder B
            JOIN cms.resi_project RP ON (B.BUILDER_ID = RP.BUILDER_ID )
            JOIN cms.locality L ON RP.LOCALITY_ID = L.LOCALITY_ID
			JOIN cms.suburb S ON L.SUBURB_ID = S.SUBURB_ID
			JOIN cms.city C ON S.CITY_ID = C.CITY_ID
			JOIN cms.resi_project_options RPO ON RPO.PROJECT_ID = RP.PROJECT_ID
                        JOIN cms.project_status_master psm ON psm.id = RP.project_status_id
            WHERE $projectCondition AND RP.VERSION = $version AND RP.RESIDENTIAL_FLAG ='Residential' AND RPO.OPTION_CATEGORY = 'Actual'
            GROUP BY B.BUILDER_ID

QRY;
	$documents = array ();
	$rs = mysql_unbuffered_query ( $qry, $solrDB );
	if ($rs == FALSE) {
		$logger->error("Error while fetching Builder Data using query : \n ". $qry."\n");
		$logger->error("Mysql error : \n". mysql_error());
		die();
	}

	while ( ($document = mysql_fetch_assoc ( $rs )) != FALSE ) {
        $builderId = $document['BUILDER_ID'];
        unset($solrBuilderList[$builderId]);
        if( isset($couponData[$builderId]) ){
            $document['BUILDER_COUPON_MAX_DISCOUNT'] = $couponData[$builderId];
            $document['BUILDER_COUPON_AVAILABLE'] = true;
        }
		// unset( $buildersOnProjectIds[$document['id']] );
		checkValidAndSetDate ( $document, "BUILDER_ESTABLISHED_DATE" );
		$document['BUILDER_DESCRIPTION'] = removeNonAsciiCharacters($document['BUILDER_DESCRIPTION']);
		if (isset ( $projectAndBuilderImages ["builder"] [$document ['BUILDER_ID']]['PATH'] )) {
			$document ['BUILDER_LOGO_IMAGE'] = $projectAndBuilderImages ["builder"] [$document ['BUILDER_ID']]['PATH'];
		}

		if ( isset ($projectAndBuilderImages['builder'][$document ['BUILDER_ID']]['ALTTEXT']) ) {
			$document ['BUILDER_IMAGE_ALTTEXT'] = $projectAndBuilderImages['builder'][$document ['BUILDER_ID']]['ALTTEXT'];
		}

		if ( isset ($projectAndBuilderImages['builder'][$document ['BUILDER_ID']]['TITLE']) ) {
			$document ['BUILDER_IMAGE_TITLE'] = $projectAndBuilderImages['builder'][$document ['BUILDER_ID']]['TITLE'];
		}
		if (! empty ( $builderProjCountDoc [$document ['BUILDER_ID']] )) {
			$document ["BUILDER_PROJECT_COUNT"] = $builderProjCountDoc [$document ['BUILDER_ID']]['PROJECT_COUNT'];
			$document['BUILDER_CITIES'] = explode(",", $builderProjCountDoc[$document ['BUILDER_ID']]['CITIES_LABEL']);
		}
		if (isset ( $builderEnquiryCountArray [$document ['BUILDER_ID']] )) {
			$document ['BUILDER_ENQUIRY_COUNT'] = $builderEnquiryCountArray [$document ['BUILDER_ID']];
		}
		if (! empty ( $builderLocCountDoc [$document ['BUILDER_ID']] )) {
			$document ["BUILDER_LOCALITY_COUNT"] = $builderLocCountDoc [$document ['BUILDER_ID']];
		}
	/*	if (! empty ($document ['BUILDER_CITIES'])) {
			$document['BUILDER_CITIES'] = explode(",", $document ['BUILDER_CITIES']);
		}*/
		if(isset($builderWithSource[$document['BUILDER_ID']]['SOURCE_ID'])){
				$document['BUILDER_SOURCE_ID'] = $builderWithSource[$document['BUILDER_ID']]['SOURCE_ID'];
		}
		if (isset($builderWithSource[$document['BUILDER_ID']]['SOURCE_DOMAIN'])){
				$document['BUILDER_SOURCE_DOMAIN'] = $builderWithSource[$document['BUILDER_ID']]['SOURCE_DOMAIN'];
		}
		if (empty($document['BUILDER_PRIORITY'])){
				unset($document['BUILDER_PRIORITY']);
		}
		if (empty ($document['BUILDER_SCORE'])){
			unset($document['BUILDER_SCORE']);
		}
		else{
            $document['BUILDER_SCORE'] = scaleBuilderScore($document['BUILDER_SCORE'], $maxActualBuliderScore);
        }

		$document ['BUILDER_VIEW_COUNT'] = 0;
		array_push ( $documents, $document );
	}
	$deleteDomainOnProjectIds = array();
	foreach($solrBuilderList as $key => $value){
		$deleteDomainOnProjectIds[] = "BUILDER-".$key;
	}
	return array (
			$deleteDomainOnProjectIds, //$buildersOnProjectIds ['deleteIds'],
			$documents
	);
}
function getCitiesOnProjectIds($projectIds) {
	return getDomainsOnProjectIds ( $projectIds, "CITY" );
}
function getLocalitiesOnProjectIds($projectIds) {
	return getDomainsOnProjectIds ( $projectIds, "LOCALITY" );
}
function getSuburbsOnProjectIds($projectIds) {
	return getDomainsOnProjectIds ( $projectIds, "SUBURB" );
}
function getBuildersOnProjectIds($projectIds) {
	return getDomainsOnProjectIds ( $projectIds, "BUILDER" );
}
function getProjectsOnProjectIds($projectIds) {
	return getDomainsOnProjectIds ( $projectIds, "PROJECT" );
}
function getDomainsOnProjectIds($projectIds, $domain) {
	global $solrDB, $logger, $projCmsActiveCondition, $locationFunctionProjActiveCondition, $version;
    global $paramLocalityIds, $paramSuburbIds, $paramCityIds, $paramBuilderIds;

	$joinCondition = "";
	$logger->info ( "Fetching the {$domain} based on given projectIds" );
	$domain = strtoupper ( $domain );
	$domainIdStr = $domain . "_ID";
	$domainId = $domainIdStr;

    $paramDomainCondition = "";
    $fields = "";
    $useField = "";
    $paramFields = "";
	if ($domain == 'CITY'){
		$domainIdStr = "C.CITY_ID";
		$joinCondition = " RIGHT JOIN cms.locality L on (RP.LOCALITY_ID = L.LOCALITY_ID AND RP.VERSION = $version)
						   INNER JOIN cms.suburb S on L.SUBURB_ID = S.SUBURB_ID INNER JOIN cms.city C on S.CITY_ID = C.CITY_ID " ;
        if(!empty($paramCityIds)){
            $paramDomainCondition = "OR C.city_id in ($paramCityIds) ";
        }

	}
	else if ($domain == 'SUBURB'){
		$domainIdStr = "S.SUBURB_ID";
		$joinCondition = " RIGHT JOIN cms.locality L on (RP.LOCALITY_ID = L.LOCALITY_ID AND RP.VERSION = $version)
						   INNER JOIN cms.suburb S on L.SUBURB_ID = S.SUBURB_ID ";
        if(!empty($paramSuburbIds)){
            $paramDomainCondition = "OR S.suburb_id in ($paramSuburbIds) ";
            $fields = ",S.CITY_ID";
            $useField = "CITY_ID";
        }

	}
	else if ($domain == 'LOCALITY'){
		$domainIdStr = "L.LOCALITY_ID";
		$joinCondition = " RIGHT JOIN cms.locality L on (L.LOCALITY_ID = RP.LOCALITY_ID AND RP.VERSION = $version) ";
        if(!empty($paramLocalityIds)){
            $paramDomainCondition = "OR L.locality_id in ($paramLocalityIds) ";
            $fields = ",L.SUBURB_ID";
            $useField = "SUBURB_ID";
        }

	}
	else if ($domain == 'BUILDER'){
		$domainIdStr = "RB.BUILDER_ID";
		$joinCondition = " RIGHT JOIN cms.resi_builder RB on (RB.BUILDER_ID = RP.BUILDER_ID AND RP.VERSION = $version) ";
        if(!empty($paramBuilderIds)){
            $paramDomainCondition = "OR RB.builder_id in ($paramBuilderIds) ";
        }
	}
	else {
		$domainIdStr = "RP.PROJECT_ID";
	}

	$projectCondition = "";
	if (! empty ( $projectIds ))
		$projectCondition = " WHERE RP.VERSION = $version AND (RP.PROJECT_ID IN ($projectIds)  $paramDomainCondition) ";
    else if( !empty($paramDomainCondition) ){
        $projectCondition = " WHERE RP.VERSION = $version AND ".ltrim($paramDomainCondition, "OR");
    }

	$sql = <<<SQL
        SELECT sum( IF((RP.STATUS IN ({$projCmsActiveCondition})), 1, 0) ) as SUM, $domainIdStr as $domainId, sum( IF((RP.RESIDENTIAL_FLAG IN ('Residential')), 1, 0)) as SUM_RESI,
        sum( IF((RPO.OPTION_CATEGORY IN ('Actual')), 1, 0)) as SUM_RESI_OPTION $fields FROM cms.resi_project RP LEFT JOIN cms.resi_project_options RPO ON (RPO.PROJECT_ID = RP.PROJECT_ID)
        $joinCondition $projectCondition GROUP BY $domainIdStr
SQL;
	$rs = mysql_unbuffered_query ( $sql, $solrDB );
    $fields = ltrim($fields, ",");
	if ($rs == FALSE) {
		$logger->error("Error while fetching Domains Data using query : \n ". $sql."\n");
		$logger->error("Mysql error : \n". mysql_error());
		die();
	}

	$addDocuments = array ();
	$deleteDocuments = array ();
    $distinctFields = array();
	while ( ($row = mysql_fetch_assoc ( $rs )) != FALSE ) {
        if(!empty($fields)){
            $distinctFields[$row[$useField]] = 1;
        }
		if ($row ['SUM'] == 0 || $row['SUM_RESI'] == 0 || $row['SUM_RESI_OPTION'] == 0)
			$deleteDocuments [$row [$domainId]] = 1;
		else
			$addDocuments [] = $row [$domainId];
	}
    switch($domain){
        case 'LOCALITY':
            $paramSuburbIds .= ",".implode(",", array_keys($distinctFields));
            $paramSuburbIds = trim($paramSuburbIds, ",");
            break;
        case 'SUBURB':
            $paramCityIds .= ",".implode(",", array_keys($distinctFields));
            $paramCityIds = trim($paramCityIds, ",");
            break;
    }

	if (count ( $deleteDocuments ) > 0 && ! empty ( $projectIds )) {
		$deleteStr = implode ( ",", array_keys ( $deleteDocuments ) );
		$sql = <<<SQL
        SELECT SUM(CASE WHEN RP.STATUS IN ({$projCmsActiveCondition}) THEN 1 ELSE 0 END) AS SUM, $domainIdStr as $domainId, sum( IF((RP.RESIDENTIAL_FLAG IN ('Residential')), 1, 0)) as SUM_RESI, sum( IF((RPO.OPTION_CATEGORY IN ('Actual')), 1, 0)) as SUM_RESI_OPTION
        FROM cms.resi_project RP JOIN cms.resi_project_options RPO ON (RP.PROJECT_ID = RPO.PROJECT_ID) $joinCondition WHERE RP.PROJECT_ID NOT IN ($projectIds) AND RP.VERSION = $version AND RP.STATUS IN ({$locationFunctionProjActiveCondition})
        AND $domainIdStr IN ($deleteStr) GROUP BY $domainIdStr HAVING SUM > 0 AND SUM_RESI > 0 AND SUM_RESI_OPTION > 0
SQL;
		$rs = mysql_unbuffered_query ( $sql, $solrDB );
		if ($rs == FALSE) {
			$logger->error("Error while fetching Domains Data using query : \n ". $sql."\n");
			$logger->error("Mysql error : \n". mysql_error());
			die();
		}

		while ( ($row = mysql_fetch_assoc ( $rs )) != FALSE ) {
			unset ( $deleteDocuments [$row [$domainId]] );
			$addDocuments [] = $row [$domainId];
		}
	}

	$delete = array ();
	foreach ( $deleteDocuments as $key => $value ) {
		$delete [] = $domain . "-" . $key;
	}

	return array (
			'deleteIds' => $delete,
			"addIds" => implode ( ",", $addDocuments )
	);
}
function getLocalityMaxRadius($localityId, $latitude, $longitude) {
	global $solr, $logger, $solrDB;
	$radius = null;
	try {
		$query = "LOCALITY_ID:{$localityId}";
		$statsResponse = $solr->search ( $query, 0, 1, array (
				'stats' => 'true',
				'fq' => array (
						"DOCUMENT_TYPE:PROJECT",
						"HAS_GEO:1"
				),
				'pt' => "$latitude,$longitude",
				'sfield' => 'GEO',
				'sort' => array (
						"geodist() desc"
				),
				'fl' => array (
						"* __RADIUS__:geodist()"
				)
		) );
		$res = json_decode ( $statsResponse->getRawResponse () );
		if (! empty ( $res->response->docs [0] )) {
			$radius = $res->response->docs [0]->__RADIUS__;
		}
	} catch ( Exception $e ) {
		$logger->error ( ERR, "Error while fetching locality max radius : " . $e->getMessage () );
		die();
	}
	return $radius;
}
function getSolrDocumentsOfDomain($type){
	global $solr, $logger, $solrDB, $paramProjectIds, $paramCityIds,
		$paramLocalityIds, $paramSuburbIds, $paramBuilderIds;

	$query = "DOCUMENT_TYPE:(PROJECT OR ";
	$fq = "";
	$fqArray = array();
	if(!empty($paramProjectIds)){
		$fq = " PROJECT_ID:(".str_replace(',', ' OR ', $paramProjectIds).")";
	}

	$field = "";
	switch ($type) {
		case 'locality':
			$query .= "LOCALITY)";
			$field = "LOCALITY_ID";
			if(!empty($paramLocalityIds)){
				$fq .= " OR LOCALITY_ID:(".str_replace(',', ' OR ', $paramLocalityIds).")";
			}
			break;
		case 'suburb':
			$query .= "SUBURB)";
			$field = "SUBURB_ID";
			if(!empty($paramSuburbIds)){
				$fq .= " OR SUBURB_ID:(".str_replace(',', ' OR ', $paramSuburbIds).")";
			}
			break;
		case 'city':
			$query .= "CITY)";
			$field = "CITY_ID";
			if(!empty($paramCityIds)){
				$fq .= " OR CITY_ID:(".str_replace(',', ' OR ', $paramCityIds).")";
			}
			break;
		case 'builder':
			$query .= "BUILDER)";
			$field = "BUILDER_ID";
			if(!empty($paramBuilderIds)){
				$fq .= " OR BUILDER_ID:(".str_replace(',', ' OR ', $paramBuilderIds).")";
			}
			break;
	}
	$fqArray[] = ltrim($fq, " OR");
	$fqArray[] = "{!collapse field=$field}";
	$fl = array("$field");
	$solrQueryParams = array ('fq' => $fqArray, 'fl' => $fl, 'sort' =>  "id asc");

	$rows = 1000;
	$documentList = array();
	try {
		$documentList = getAllDocumentsFromSolrWithCursorAndRetries($solr, $query, $rows, $solrQueryParams, GLOBAL_SOLR_GET_RETRY_COUNT);
	}
	catch(Exception $e){
		$errorMsg = "Error while fetching " . $type . " objects from solr : " . getExceptionLogMessage($e);
		$logger->error($errorMsg);
		trigger_error($errorMsg, E_USER_ERROR);
		die();
	}

	$data = array();
	$len = sizeof($documentList);
	for($i=0; $i<$len; $i++){
		$responseDoc = $documentList[$i];
		$data[$responseDoc->$field] = 1;
	}
	return $data;
}

function getSolrDocumentsOfTypeahead($solr){
	global $logger, $solrDB, $paramProjectIds, $paramCityIds,
	$paramLocalityIds, $paramSuburbIds, $paramBuilderIds;
	$typeaheadIdList = array();
	if(!empty($paramProjectIds)){
		$typeaheadIdList = array_merge($typeaheadIdList, getTypeaheadIdListFromEntityIdList($paramProjectIds, "PROJECT"));
	}
	if(!empty($paramLocalityIds)){
		$typeaheadIdList = array_merge($typeaheadIdList, getTypeaheadIdListFromEntityIdList($paramLocalityIds, "LOCALITY"));
	}
	if(!empty($paramSuburbIds)){
		$typeaheadIdList = array_merge($typeaheadIdList, getTypeaheadIdListFromEntityIdList($paramSuburbIds, "SUBURB"));
	}
	if(!empty($paramCityIds)){
		$typeaheadIdList = array_merge($typeaheadIdList, getTypeaheadIdListFromEntityIdList($paramCityIds, "CITY"));
	}
	if(!empty($paramBuilderIds)){
		$typeaheadIdList = array_merge($typeaheadIdList, getTypeaheadIdListFromEntityIdList($paramBuilderIds, "BUILDER"));
	}

	$fq = "";
	foreach ($typeaheadIdList as $typeaheadId){
		$fq .= (" OR ". $typeaheadId);
	}
	$fq = ltrim($fq, " OR");
	if(!empty($fq)){
		$fq = "id:(" . $fq . ")";
	}

	$query = "DOCUMENT_TYPE:(TYPEAHEAD)";
	$field = "id";
	$fqArray = array();
	$fqArray[] = $fq;
	$fl = array("$field");

	$solrQueryParams = array ('fq' => $fqArray, 'fl' => $fl, 'sort' =>  "id asc");
	$rows = 1000;
	$documentList = array();
	try {
		$documentList = getAllDocumentsFromSolrWithCursorAndRetries($solr, $query, $rows, $solrQueryParams, GLOBAL_SOLR_GET_RETRY_COUNT);
	}
	catch(Exception $e){
		$errorMsg = "Error while fetching typeahead objects from solr : " . getExceptionLogMessage($e);
		$logger->error($errorMsg);
		trigger_error($errorMsg, E_USER_ERROR);
		die();
	}

	$data = array();
	$len = sizeof($documentList);
	for($i=0; $i<$len; $i++){
		$responseDoc = $documentList[$i];
		$data[$responseDoc->$field] = 1;
	}
	return $data;
}

function getTypeaheadIdListFromEntityIdList($csvIdList, $type){
	$idArray = explode(",",$csvIdList);
	$typeaheadIdList = array();
	foreach ($idArray as $id){
		if($id > 0){
			$typeaheadIdList[] = ("TYPEAHEAD-" . $type . "-" . $id);
		}
	}
	return $typeaheadIdList;
}

function loadProjectStatusCountInfo($objectType){
	global $projCmsActiveCondition, $version, $solrDB, $logger;
	$objectTypeId = 'SUBURB_ID';
	$completeObjectTypeId = 'S.SUBURB_ID';

	switch ($objectType) {
		case 'BUILDER' :
			$completeObjectTypeId = 'RB.BUILDER_ID';
			$objectTypeId = 'BUILDER_ID';
			break;
		case 'LOCALITY' :
			$completeObjectTypeId = 'L.LOCALITY_ID';
			$objectTypeId = 'LOCALITY_ID';
			break;
	}

	$query = "SELECT $completeObjectTypeId, PSM.project_status, COUNT(PSM.id) AS PROJECT_STATUS_COUNT
	FROM cms.resi_project RP
	JOIN cms.resi_project_options RPO ON (RP.PROJECT_ID = RPO.PROJECT_ID )
	JOIN cms.resi_builder RB ON (RB.BUILDER_ID = RP.BUILDER_ID)
	JOIN cms.locality L ON (RP.LOCALITY_ID = L.LOCALITY_ID)
	JOIN cms.suburb S ON (L.SUBURB_ID = S.SUBURB_ID)
	JOIN cms.project_status_master PSM ON (RP.project_status_id = PSM.id)
	WHERE RP.STATUS IN ({$projCmsActiveCondition}) AND RP.VERSION = $version AND RP.RESIDENTIAL_FLAG ='Residential'
	AND RPO.OPTION_CATEGORY = 'Actual' GROUP BY $completeObjectTypeId, PSM.id";

	$result = mysql_unbuffered_query ( $query, $solrDB );
	$projStatusCountDoc = array ();
	if ($result == FALSE) {
		$logger->error("Error while fetching Project Status Count Info using query : \n ". $query."\n");
		$logger->error("Mysql error : \n". mysql_error());
		die();
	}
	while ( $doc = mysql_fetch_assoc ( $result ) ) {
			if(!isset($projStatusCountDoc[$doc[$objectTypeId]])){
				$projStatusCountDoc[$doc[$objectTypeId]] = array();
			}
			$projStatusCountDoc [$doc [$objectTypeId]][$doc['project_status']] = $doc ['PROJECT_STATUS_COUNT'];
	}

	return $projStatusCountDoc;
}

function loadProjectCountInfo($objectType) {
	global $projCmsActiveCondition, $version, $solrDB;
	$objectTypeId = 'S.SUBURB_ID';
	$completeObjectTypeId = 'S.SUBURB_ID';

	switch ($objectType) {
		case 'BUILDER' :
			$completeObjectTypeId = 'RB.BUILDER_ID';
			$objectTypeId = 'BUILDER_ID';
			break;
		case 'LOCALITY' :
			$completeObjectTypeId = 'L.LOCALITY_ID';
			$objectTypeId = 'LOCALITY_ID';
			break;
		case 'SUBURB':
			$completeObjectTypeId = 'S.SUBURB_ID';
			$objectTypeId = 'SUBURB_ID';
			break;
		case 'CITY':
			$completeObjectTypeId = 'C.CITY_ID';
			$objectTypeId = 'CITY_ID';
			break;
	}

	$query = "SELECT $completeObjectTypeId, COUNT(DISTINCT RP.project_id) AS PROJECT_COUNT ,
		COUNT(DISTINCT RPO.OPTIONS_ID) AS PROPERTY_COUNT, COUNT(DISTINCT RP.LOCALITY_ID) AS LOCALITY_COUNT,
		COUNT(DISTINCT L.SUBURB_ID) AS SUBURB_COUNT, COUNT(DISTINCT S.CITY_ID) AS CITY_COUNT,
		GROUP_CONCAT(DISTINCT C.LABEL) AS CITIES_LABEL
	FROM cms.resi_project RP
	JOIN cms.resi_project_options RPO ON (RP.PROJECT_ID = RPO.PROJECT_ID )
	JOIN cms.resi_builder RB ON (RB.BUILDER_ID = RP.BUILDER_ID)
	JOIN cms.locality L ON (RP.LOCALITY_ID = L.LOCALITY_ID)
	JOIN cms.suburb S ON (L.SUBURB_ID = S.SUBURB_ID)
	JOIN cms.city C ON (S.CITY_ID=C.CITY_ID)
	WHERE RP.STATUS IN ({$projCmsActiveCondition}) AND RP.VERSION = $version AND RP.RESIDENTIAL_FLAG ='Residential'
	AND RPO.OPTION_CATEGORY = 'Actual' GROUP BY $completeObjectTypeId";

	$result = mysql_unbuffered_query ( $query, $solrDB );
	$projCountDoc = array ();
	if ($result) {
		while ( $doc = mysql_fetch_assoc ( $result ) ) {
			$projCountDoc [$doc [$objectTypeId]] = $doc;
		}
	}
	else{
		$logger->error("Error while fetching Project Count using query : \n ". $query."\n");
		$logger->error("Mysql error : \n". mysql_error());
		die();
	}

	return $projCountDoc;
}

function getLocalitiesUnitsInfo(){
	global $logger, $solrDB;
	$priceTrendAPIUrl = 'data/v1/trend/hitherto?filters=unitType==Apartment&fields=sumLaunchedUnit,sumUnitsSold,sumUnitsDelivered&group=localityId&monthDuration=6&sort=localityId&rows=50000';
	$localitiesProjectUnitsInfo = json_decode ( file_get_contents ( API_URL . $priceTrendAPIUrl ), true );
	$localitiesUnitsInfo = $localitiesProjectUnitsInfo ["data"];
	if($localitiesUnitsInfo == NULL || empty($localitiesUnitsInfo)){
		$logger->error("Error while fetching localities units info from price-trend API. Logging and moving on.");
		return array();
	}
	return $localitiesUnitsInfo;
}

function populateLocalityUnitsInfo(&$document, $localitiesUnitsInfo) {
	$localityId = $document ['LOCALITY_ID'];
	if (isset($localitiesUnitsInfo[$localityId]) && !empty ( $localitiesUnitsInfo [$localityId] )) {
		$extraAttributes = $localitiesUnitsInfo [$localityId] [0] ["extraAttributes"];
		if (! empty ( $extraAttributes ["sumLaunchedUnit"] )) {
			$document ['TYPEAHEAD_LOCALITY_UNITS_LAUNCHED_6MONTHS'] = $extraAttributes ["sumLaunchedUnit"];
		}
		if (! empty ( $extraAttributes ["sumUnitsSold"] )) {
			$document ['TYPEAHEAD_LOCALITY_UNITS_SOLD_6MONTHS'] = $extraAttributes ["sumUnitsSold"];
		}
		if (! empty ( $extraAttributes ["sumUnitsDelivered"] )) {
			$document ['TYPEAHEAD_LOCALITY_UNITS_DELIVERED_6MONTHS'] = $extraAttributes ["sumUnitsDelivered"];
		}
	}
}

function loadEnquiryCount($objectType) {
	global $solrDB, $logger, $paramIds, $citiesOnProjectIds, $suburbsOnProjectIds, $localitiesOnProjectIds, $buildersOnProjectIds, $paramProjectIds, $version;
	$joinCondition;
	$paramCondition = "";

	switch ($objectType) {
		case 'LOCALITY' :
			$documentId = $objectTypeId = 'LOCALITY_ID';
			$paramIds = $localitiesOnProjectIds ['addIds'];
			break;
		case 'CITY' :
			$documentId = $objectTypeId = 'CITY_ID';
			$paramIds = $citiesOnProjectIds ['addIds'];
			break;
		case 'PROJECT' :
			$documentId = $objectTypeId = 'PROJECT_ID';
			$paramIds = $paramProjectIds;
			break;
		case 'SUBURB' :
			$documentId = 'SUBURB_ID';
			$objectTypeId = 'S.SUBURB_ID';
			$paramIds = $suburbsOnProjectIds ['addIds'];
			$joinCondition = " INNER JOIN cms.locality L ON L.LOCALITY_ID = E.LOCALITY_ID
							   INNER JOIN cms.suburb S ON S.SUBURB_ID = L.SUBURB_ID";
			break;
		case 'BUILDER' :
			$documentId = $objectTypeId = 'BUILDER_ID';
			$paramIds = $buildersOnProjectIds ['addIds'];
			$joinCondition = " INNER JOIN cms.resi_project R ON R.PROJECT_ID=E.PROJECT_ID AND R.VERSION = '$version' ";
			break;
	}
	$logger->info ( "Loading enquiry count from database start.." );
	$enquiryCountToConsiderInLastWeeks = 8;
	$unixTimeStamp = time () - ($enquiryCountToConsiderInLastWeeks * 7 * 24 * 60 * 60);
	$datetime = date ( "Y-m-d H:i:s", $unixTimeStamp );
	$enquiryCountArray = array ();
	$enquiryLastEnquiredArray = array ();

	if (! empty ( $paramIds ))
		$paramCondition = " AND $objectTypeId IN ($paramIds) ";
	$sql = "SELECT $objectTypeId, count(*) as ENQUIRY_COUNT, max(created_date) as lastEnquiredDate from proptiger.ENQUIRY E ";
	if (! empty ( $joinCondition ))
		$sql = $sql . $joinCondition;

	$sql = $sql . " where E.created_date >='$datetime' $paramCondition group by $objectTypeId ";
	$result = mysql_unbuffered_query ( $sql, $solrDB );
	if ($result == FALSE) {
		$logger->error("Error while fetching enquiry count using query : \n ". $sql."\n");
		$logger->error("Mysql error : \n". mysql_error());
		die();
	}

	while ( $document = mysql_fetch_assoc ( $result ) ) {
		$enquiryCountArray [$document [$documentId]] = $document ['ENQUIRY_COUNT'];
		$enquiryLastEnquiredArray[$document[$documentId]] = $document['lastEnquiredDate'];
	}
	$logger->info ( "Loading enquiry count from database end.." );
	return array($enquiryCountArray, $enquiryLastEnquiredArray);
}
function builderLocalityCount() {
	global $version, $solrDB;
	$sql = "SELECT RP.BUILDER_ID, COUNT(DISTINCT(RP.LOCALITY_ID)) AS BUILDER_LOCALITY_COUNT FROM cms.resi_project RP JOIN cms.resi_project_options RPO ON (RPO.PROJECT_ID = RP.PROJECT_ID)
	WHERE VERSION = $version AND RP.RESIDENTIAL_FLAG ='Residential' AND RPO.OPTION_CATEGORY = 'Actual' GROUP BY RP.BUILDER_ID";
	$builderLocalityCount = array ();
	$result = mysql_unbuffered_query ( $sql, $solrDB );
	if ($result == FALSE) {
		$logger->error("Error while fetching Builder Locality count using query : \n ". $sql."\n");
		$logger->error("Mysql error : \n". mysql_error());
		die();
	}

	while ( $document = mysql_fetch_assoc ( $result ) ) {
		$builderLocalityCount [$document ["BUILDER_ID"]] = $document ['BUILDER_LOCALITY_COUNT'];
	}
	return $builderLocalityCount;
}

function getDomainDefaultUnitType($domainType){
    global $version, $solrDB;
    $groupField = "";
    switch($domainType)
    {
        case "locality":
            $groupField = " l.locality_id ";
            break;
        case "suburb":
            $groupField = " s.suburb_id ";
            break;
        case "city":
            $groupField = " c.city_id ";
            break;
        case "builder":
            $groupField = " rpo.builder_id ";
            break;
    }
    $query = <<<SQL
            SELECT $groupField, count(*) as unitCount, rpo.option_type FROM cms.resi_project_options rpo
                    JOIN cms.resi_project rp ON (rpo.PROJECT_ID=rp.project_id)
                    JOIN cms.locality l ON (rp.locality_id=l.locality_id)
                    JOIN cms.suburb s ON (l.suburb_id = s.suburb_id)
                    JOIN cms.city c ON (s.city_id = c.city_id)
            WHERE rpo.option_type IN ('apartment', 'villa', 'plot') AND rp.version = $version AND rp.RESIDENTIAL_FLAG ='Residential' AND rpo.OPTION_CATEGORY = 'Actual'
            GROUP BY $groupField, rpo.option_type ORDER BY $groupField, unitCount DESC
SQL;

    $domainArray = array();
    $result = mysql_unbuffered_query($query, $solrDB);
    if ($result == FALSE) {
    	$logger->error("Error while fetching domain unit info using query : \n ". $query."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }

    while( $document = mysql_fetch_row($result) ) {
        if( !isset($domainArray[$document[0]]) )
        {
            $domainArray[$document[0]] = array();
            $domainArray[$document[0]]['default_dominant_type'] = $document[2];
            $domainArray[$document[0]]['unit_types'] = array();
        }
        $domainArray[$document[0]]['unit_types'][] = $document[2];
    }
    return $domainArray;
}

function getDominantUnitType($domainsData, $domainDefaultUnitType, $domainId){
    @$defaultUnitTypeData = $domainDefaultUnitType[$domainId];
    @$domainData = $domainsData[$domainId];
    if( isset($domainData) && !empty($domainData['unit_type']) )
        return $domainData['unit_type'];
    else if ( !empty($defaultUnitTypeData) )
        return $defaultUnitTypeData['default_dominant_type'];
    else
        return 'Apartment';
}

function getCouponsDiscount($domainType){
    global $version, $logger, $projCmsActiveCondition, $solrDB;
    $logger->info(" getting the coupons data for $domainType");
    $groupField = "";
    switch($domainType)
    {
        case "locality":
            $groupField = " l.locality_id ";
            break;
        case "suburb":
            $groupField = " s.suburb_id ";
            break;
        case "city":
            $groupField = " c.city_id ";
            break;
        case "builder":
            $groupField = " rp.builder_id ";
            break;
    }
    $query = <<<QRY
            SELECT max(cc.discount) as maxDiscount, $groupField FROM cms.resi_project_options rpo
                    JOIN cms.coupon_catalogue cc ON (cc.option_id = rpo.options_id)
                    JOIN cms.resi_project rp ON (rpo.PROJECT_ID=rp.project_id)
                    JOIN cms.locality l ON (rp.locality_id=l.locality_id)
                    JOIN cms.suburb s ON (l.suburb_id = s.suburb_id)
                    JOIN cms.city c ON (s.city_id = c.city_id)
            WHERE rp.version =  $version AND rp.status IN ($projCmsActiveCondition) AND rp.RESIDENTIAL_FLAG ='Residential' AND
            cc.inventory_left > 0 AND cc.purchase_expiry_at > now()
            GROUP BY $groupField
QRY;
    $result = mysql_unbuffered_query($query, $solrDB);
    if ($result == FALSE) {
    	$logger->error("Error while fetching Coupon Discount info using query : \n ". $query."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }

    $couponData = array();
    while( $row = mysql_fetch_row($result) ){
        $couponData[$row[1]] = $row[0];
    }

    return $couponData;
}

function getAmenityTypesFromDB() {
	global $logger, $solrDB;
	$amenityTypes = array();
	$sql = <<<QRY
		SELECT id, name, display_name, description from cms.landmark_types where page_active = 1
QRY;
	$res = mysql_unbuffered_query($sql, $solrDB);
	if ($res == FALSE) {
		$logger->error("Error while fetching Amenities using query : \n ". $sql."\n");
		$logger->error("Mysql error : \n". mysql_error());
		die();
	}
	while($row = mysql_fetch_row($res) ) {
			$amenityTypes[] = $row;
	}
	return $amenityTypes;
}

function getUrlTypes() {
	$urlTypes = array (
			'apartments' => 'apartments-flats-sale',
			'propertysale' => 'property-sale',
			'plots' => 'sites-plots-sale',
			'villas' => 'villas-sale',
			'newLaunch' => 'new-launch',
			'resaleApartmentUrl' => 'resale-apartments',
			'resalePropertyUrl' => 'resale-property',
			'readyToMoveFlatsUrl' => 'ready-to-move-flats',
			'luxuryProjectsUrl' => 'luxury-projects',
			'affordableFlatsUrl' => 'affordable-flats',
			'underConstructionPropertyUrl' => 'under-construction-property',
			'upcomingFlatsForSaleUrl' => 'upcoming-flats-for-sale',
			'upcomingPropertyUrl' => 'upcoming-property',
			'newAppartmentsForSaleUrl' => 'new-apartments-for-sale'
	);
	return $urlTypes;
}

function hyphonate($string) {
	return preg_replace('/ +/', '-', strtolower(trim($string)));
}

function url_lib_locality_url($document) {
	return hyphonate($document['CITY']) . '-real-estate/' .  hyphonate($document['LOCALITY']) . '-overview-' . $document['LOCALITY_ID'];
}

function url_lib_suburb_url($document) {
	return hyphonate($document['CITY']) . '-real-estate/' .  hyphonate($document['SUBURB']) . '-overview-' . $document['SUBURB_ID'];
}

function url_lib_city_url($document) {
	return hyphonate($document['CITY']) . '-real-estate';
}

function url_lib_city_new_launch_url( $document ) {
	return hyphonate('new-launch-in-' . $document['CITY']);
}

function url_lib_city_list_url($document, $propertyType) {
	return hyphonate($document['CITY'] . '/' . $propertyType);
}

function url_lib_suburb_list_url($document, $propertyType) {
    return url_lib_locality_or_suburb_list_url($document['CITY'], $propertyType, $document['SUBURB'], $document['SUBURB_ID']);
}

function url_lib_locality_list_url($document, $propertyType) {
    return url_lib_locality_or_suburb_list_url($document['CITY'], $propertyType, $document['LOCALITY'], $document['LOCALITY_ID']);
}

function url_lib_locality_or_suburb_list_url( $cityName, $propertyType, $localityOrSuburbName, $localityOrSuburbId ) {
    if ( in_array( $propertyType, array( 'villas-sale', 'sites-plots-sale', 'property-sale', 'apartments-flats-sale' ) ) ) {
        //  no 'in' in the url in these cases
        return hyphonate($cityName . '/' . $propertyType . '-' . $localityOrSuburbName . '-' . $localityOrSuburbId);
    }
    else {
        return hyphonate($cityName . '/' . $propertyType . '-in-' . $localityOrSuburbName . '-' . $localityOrSuburbId);
    }
}

function url_lib_locality_url_area_type($document, $areaType) {
	return hyphonate($document['CITY'] . '/' . $document['LOCALITY'] . '-' . $document['LOCALITY_ID'] . "/$areaType");
}

function getHeroshotImages(){
	global $logger, $solrDB;
	$sql = "SELECT I.object_id, concat(I.path, I.seo_name) AS IMAGE_URL from Image I JOIN ImageType IT ON (I.ImageType_id = IT.id) WHERE IT.type = 'heroShot' AND I.active = 1";

	$rs = mysql_unbuffered_query ( $sql, $solrDB );
	if ($rs == FALSE) {
		$logger->error("Error while Executing Heroshot images query : \n ". $sql."\n");
		$logger->error("Mysql error : \n". mysql_error());
		die();
	}

	$heroshotImages = array ();
	while ( ($row = mysql_fetch_assoc ( $rs )) != FALSE ) {
		if (isset($row['object_id'])) {
			$heroshotImages[$row['object_id']] = $row['IMAGE_URL'];
		}
	}
	return $heroshotImages;
}

function getLocalityIdToPopularityMap(){
	global $logger, $solrDB;

	$sql = <<<QRY
	 SELECT object_id AS LOCALITY_ID, user_popularity_index as USER_POPULARITY_INDEX FROM analytics.object_popularity_table where object_type_id = 4
QRY;

	$rs = mysql_unbuffered_query($sql, $solrDB);
	if ($rs == FALSE) {
		$logger->info("Error in Executing Locality Popularity Query (analtytics.object_popularity_table).\n " . $sql."\n" );
		$logger->error("Mysql error : \n". mysql_error());
		exit();
	}

	$mapLocalityIdToPopularity = array();

	while (($row = mysql_fetch_assoc($rs)) != FALSE) {
		if(isset($row['LOCALITY_ID']) && isset($row['USER_POPULARITY_INDEX'])){
			$mapLocalityIdToPopularity[$row['LOCALITY_ID']] = $row['USER_POPULARITY_INDEX'];
		}
	}

	return $mapLocalityIdToPopularity;
}

function getMaxBuilderScoreFromDb(){
	$maxBuliderScoreQuery = "select max(builder_score) as max_builder_score from cms.resi_builder";
    $result = mysql_query($maxBuliderScoreQuery);
    $data = mysql_fetch_assoc($result);
    $maxActualBuliderScore = $data['max_builder_score'];
    return $maxActualBuliderScore;
}
function scaleBuilderScore($builder_score, $maxActualBuliderScore){
	$min_max_builder_score = max(MIN_MAX_BUILDER_SCORE, $maxActualBuliderScore);
    $builder_score = ($builder_score*(($min_max_builder_score-MIN_BUILDER_SCORE)/$maxActualBuliderScore) + MIN_BUILDER_SCORE)*10;
    return $builder_score;

}

function getSuburbCityWithSource($localityWithSource){
	$sql = "SELECT
    city.CITY_ID, suburb.SUBURB_ID, locality.LOCALITY_ID
FROM
    cms.city city
        LEFT JOIN
    cms.suburb suburb ON suburb.CITY_ID = city.city_id
        LEFT JOIN
    cms.locality locality ON locality.SUBURB_ID = suburb.suburb_id
WHERE
    suburb.STATUS = 'Active'
        AND locality.STATUS = 'Active'
        AND city.status = 'Active'
GROUP BY CITY_ID , SUBURB_ID , LOCALITY_ID	";

$rs = mysql_unbuffered_query($sql);
      if(empty($rs)){
              $logger->info("Error in fetching projects and property with source \n " . $sql."\n" );
              $logger->error("Mysql error : \n". mysql_error());
              exit();
      }

			$suburbWithSource = array();
			$cityWithSource = array();
			while($row = mysql_fetch_assoc($rs)){
					isset($suburbWithSource[$row['SUBURB_ID']]) ?: $suburbWithSource[$row['SUBURB_ID']] = array("SOURCE_ID" => null, "SOURCE_DOMAIN" => null);
					isset($cityWithSource[$row['CITY_ID']]) ?: $cityWithSource[$row['CITY_ID']] = array("SOURCE_ID" => null, "SOURCE_DOMAIN" => null);

					if( isset($localityWithSource[$row['LOCALITY_ID']]['SOURCE_ID'])){
							isset($suburbWithSource[$row['SUBURB_ID']]['SOURCE_ID']) ?: $suburbWithSource[$row['SUBURB_ID']]['SOURCE_ID'] = array();
							isset($cityWithSource[$row['CITY_ID']]['SOURCE_ID']) ?: $cityWithSource[$row['CITY_ID']]['SOURCE_ID'] = array();

							$suburbWithSource[$row['SUBURB_ID']]['SOURCE_ID'] = $suburbWithSource[$row['SUBURB_ID']]['SOURCE_ID'] + $localityWithSource[$row['LOCALITY_ID']]['SOURCE_ID'];
							$cityWithSource[$row['CITY_ID']]['SOURCE_ID'] = $cityWithSource[$row['CITY_ID']]['SOURCE_ID'] + $localityWithSource[$row['LOCALITY_ID']]['SOURCE_ID'];
					}
					if (isset($localityWithSource[$row['LOCALITY_ID']]['SOURCE_DOMAIN'])){
						isset($suburbWithSource[$row['SUBURB_ID']]['SOURCE_DOMAIN']) ?: $suburbWithSource[$row['SUBURB_ID']]['SOURCE_DOMAIN'] = array();
						isset($cityWithSource[$row['CITY_ID']]['SOURCE_DOMAIN']) ?: $cityWithSource[$row['CITY_ID']]['SOURCE_DOMAIN'] = array();

						$suburbWithSource[$row['SUBURB_ID']]['SOURCE_DOMAIN'] = $suburbWithSource[$row['SUBURB_ID']]['SOURCE_DOMAIN'] + $localityWithSource[$row['LOCALITY_ID']]['SOURCE_DOMAIN'];
						$cityWithSource[$row['CITY_ID']]['SOURCE_DOMAIN'] = $cityWithSource[$row['CITY_ID']]['SOURCE_DOMAIN'] + $localityWithSource[$row['LOCALITY_ID']]['SOURCE_DOMAIN'];
					}
			}

			return array('SUBURB_SOURCE_ARRAY' => $suburbWithSource, 'CITY_SOURCE_ARRAY' => $cityWithSource );
}
?>
