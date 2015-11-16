<?php

include_once 'config.php';
include_once dirname(__FILE__) . '/../solrDBConfig.php';

global $gblData;

$gblData['averagePriceLocalityMap'] = computeAveragePriceLocalityMap();

function computeAveragePriceLocalityMap() {
	global $solrDB;
	$result = array();
    $sql = 'SELECT rp.LOCALITY_ID, rpt.UNIT_TYPE, l.AVERAGE_PRICE,
                   SQRT(AVG(POWER(rpt.PRICE_PER_UNIT_AREA - l.AVERAGE_PRICE, 2))) AS STANDARD_DEVIATION
            FROM RESI_PROJECT rp
            JOIN RESI_PROJECT_TYPES rpt
            	ON (rp.PROJECT_ID = rpt.PROJECT_ID)
            JOIN (  SELECT LOCALITY_ID, UNIT_TYPE, AVG(PRICE_PER_UNIT_AREA) AS AVERAGE_PRICE
                    FROM RESI_PROJECT rp
                    JOIN RESI_PROJECT_TYPES rpt
                    	ON (rp.PROJECT_ID = rpt.PROJECT_ID)
                    WHERE rpt.PRICE_PER_UNIT_AREA != 0
                    GROUP BY LOCALITY_ID, UNIT_TYPE
                    ORDER BY AVERAGE_PRICE) l
            	ON (l.LOCALITY_ID = rp.LOCALITY_ID AND l.UNIT_TYPE = rpt.UNIT_TYPE)
            WHERE rpt.PRICE_PER_UNIT_AREA != 0
            GROUP BY rp.LOCALITY_ID, rpt.UNIT_TYPE';

    $documents = array();
    $records = mysql_unbuffered_query($sql, $solrDB);

    if ($records) {
        while ($locality = mysql_fetch_assoc($records)) {
            $result[$locality['LOCALITY_ID']][$locality['UNIT_TYPE']] = $locality;
        }
    }

    return $result;
}

function computePricePriority($property) {
    global $unkPricePriority, $gblData;
    if (!isset($property['PRICE_PER_UNIT_AREA']) || 
         empty($gblData['averagePriceLocalityMap'][$property['LOCALITY_ID']][$property['UNIT_TYPE']]['STANDARD_DEVIATION']))
    {
        return $unkPricePriority;
    }
    else
    {
        $locality = $gblData['averagePriceLocalityMap'][$property['LOCALITY_ID']][$property['UNIT_TYPE']];
        return min(array(100, abs($property['PRICE_PER_UNIT_AREA'] - $locality['AVERAGE_PRICE']) / $locality['STANDARD_DEVIATION']));
    }
}

function computeStaticPriority($property) {
    global $wtTextMatch, $wtCityBoost, $multiplierPossessionPriority, $wtStaticPriority, 
        $wtDynamicPriority, $wtSearchPriority, $wtStaticPriority, $wtDynamicPriority, $wtLocalityPriority,
        $wtBuilderPriority, $wtProjectLiveability, $multProjectLiveability, $wtPricePriority, $wtPossessionPriority, $wtLaunchDatePriority, 
        $wtAvailabilityPriority, $wtLocationPriority, $multPrice, $unkPricePriority, $unkPossessionPriority,
        $multLaunchDate, $unkLaunchDatePriority, $unkLocationPriority, $wtOfferPriority, $priOffer,
        $priNoOfffer, $priNoAvailability, $priAvailability, $wtProjectType;

    $localityPriority = $property['LOCALITY_PRIORITY'];
    $builderPriority = $property['BUILDER_DISPLAY_ORDER'];
    $pricePriority = computePricePriority($property);
    $possessionPriority = computePossessionPriority($property);
    $launchDatePriority = computeLaunchDatePriority($property);
    $availabilityPriority = computeAvailabilityPriority($property);
    $locationPriority = computeLocationPriority($property);
    $offerPriority = computeOfferPriority($property);
    $projectTypePriority = computeProjectTypePriority($property);
    
    /* Livability Overrrides */
    $projectLiveability = $property['PROJECT_LIVABILITY_SCORE'] * $multProjectLiveability;

	$staticPriority = ($wtProjectLiveability * $projectLiveability + $wtLocalityPriority * $localityPriority + 
			$wtBuilderPriority * $builderPriority + $wtPricePriority * $pricePriority + 
			$wtPossessionPriority * $possessionPriority + $wtLaunchDatePriority * $launchDatePriority + 
			$wtAvailabilityPriority * $availabilityPriority + $wtLocationPriority * $locationPriority + 
			$wtOfferPriority * $offerPriority + $wtProjectType * $projectTypePriority) / 
			($wtLocalityPriority + $wtBuilderPriority + $wtPricePriority + $wtPossessionPriority + 
			$wtLaunchDatePriority + $wtAvailabilityPriority + $wtLocationPriority + $wtOfferPriority + 
			$wtProjectType + $wtProjectLiveability);

    return $staticPriority;
}

function projectPriorityAttributes($property){
	global $wtTextMatch, $wtCityBoost, $multiplierPossessionPriority, $wtStaticPriority,
	$wtDynamicPriority, $wtSearchPriority, $wtStaticPriority, $wtDynamicPriority, $wtLocalityPriority,
	$wtBuilderPriority, $wtPricePriority, $wtPossessionPriority, $wtLaunchDatePriority,
	$wtAvailabilityPriority, $wtLocationPriority, $multPrice, $unkPricePriority, $unkPossessionPriority,
	$multLaunchDate, $unkLaunchDatePriority, $unkLocationPriority, $wtOfferPriority, $priOffer,
	$priNoOfffer, $priNoAvailability, $priAvailability, $wtProjectType;

	$localityPriority = $property['LOCALITY_PRIORITY'];
	$builderPriority = $property['BUILDER_DISPLAY_ORDER'];
	$pricePriority = computePricePriority($property);
	$possessionPriority = computePossessionPriority($property);
	$launchDatePriority = computeLaunchDatePriority($property);
	$availabilityPriority = computeAvailabilityPriority($property);
	$locationPriority = computeLocationPriority($property);
	$offerPriority = computeOfferPriority($property);
	$projectTypePriority = computeProjectTypePriority($property);

	$arr = array("wtLocalityPriority"=>$wtLocalityPriority, "localityPriority"=>$localityPriority,
			"wtBuilderPriority"=>$wtBuilderPriority, "builderPriority"=>$builderPriority,
			"wtPricePriority"=>$wtPricePriority, "pricePriority"=>$pricePriority,
			"wtPossessionPriority"=>$wtPossessionPriority, "possessionPriority"=>$possessionPriority,
			"wtLaunchDatePriority"=>$wtLaunchDatePriority, "launchDatePriority"=>$launchDatePriority,
			"wtAvailabilityPriority"=>$wtAvailabilityPriority, "availabilityPriority"=>$availabilityPriority,
			"wtLocationPriority"=>$wtLocationPriority, "locationPriority"=>$locationPriority,
			"wtOfferPriority"=>$wtOfferPriority, "offerPriority"=>$offerPriority,
			"wtProjectType"=>$wtProjectType, "wtProjectType"=>$wtProjectType);

	return json_encode($arr);
}

function computeAvailabilityPriority($property) {
    global $priNoAvailability, $priAvailability;
    if ($property['AVAILABILITY']) {
        return $priAvailability;
    }

    if (!isset($property['AVAILABILITY'])) {
        if (in_array($property['PROJECT_STATUS'], array('Pre Launch','Launch','Under Construction'))) {
            return $priAvailability;
        }
    }

    return $priNoAvailability;
}

function computeLocationPriority($property) {
    global $unkLocationPriority;
    if (isset($property['LATITUDE']) && isset($property['LONGITUDE'])) {
        return 1;
    }

    return $unkLocationPriority;
}

function computeProjectTypePriority($property) {
    if (!empty($property['PROJECT_TYPE']) && substr_count($property['PROJECT_TYPE'], 'PLOT') > 0) {
        return 100;
    }

    return 1;
}

function computePossessionPriority($property) {
    global $multiplierPossessionPriority, $unkPossessionPriority;
    if (!$property['PROMISED_COMPLETION_DATE']) {
        return $unkPossessionPriority;
    }
    $datetime1 = date_create();
    $datetime2 = date_create($property['PROMISED_COMPLETION_DATE']);
    $interval = date_diff($datetime1, $datetime2, true);
    $numMonths = intval($interval->format('%a') / 30);

    if (!$numMonths) {
        return 1;
    }

    return min(100, $numMonths * $multiplierPossessionPriority);
}

function computeLaunchDatePriority($property) {
    global $multLaunchDate, $unkLaunchDatePriority;

    if ('Pre Launch' == $property['PROJECT_STATUS'] && $property['PRE_LAUNCH_DATE'] && $property['PRE_LAUNCH_DATE'] != '0000-00-00 00:00:00') {
        $datetime2 = date_create($property['PRE_LAUNCH_DATE']);
    }
    else {
        if ($property['LAUNCH_DATE'] == '0000-00-00 00:00:00') {
            return $unkLaunchDatePriority;
        }

        $datetime2 = date_create($property['LAUNCH_DATE']);
    }

    $datetime1 = date_create();

    $interval = date_diff($datetime1, $datetime2, true);
    $numMonths = intval($interval->format('%a') / 30);

    if (!$numMonths) {
        return 1;
    }

    return min(100, $numMonths * $multLaunchDate);
}

function computeOfferPriority($property) {
    global $priOffer, $priNoOffer;
    if (!isset($property['OFFER'])) {
        return $priNoOffer;
    }

    return $priOffer;
}

?>
