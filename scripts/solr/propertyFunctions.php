<?php
require_once __DIR__ . '/projectFunctions.php';

function getPropertyDocumentsFromDB() {
    global $imageTypeCount;
    global $projectEnquiryCountArray, $localityEnquiryCountArray, $suburbEnquiryCountArray, $cityEnquiryCountArray, $builderEnquiryCountArray;
    global $soldOutStatus;
    global $panoramaViewPath, $solrDB, $paramProjectIds, $paramPropertyIds;
    global $logger;
    $imageTypeCount = getImageTypeCount();
    $cmsProperties = array();
    
    $logger->info("Fetching properties from solr.");
    $solrDeleteProperties = getPropertiesFromSolr();
    
    $logger->info("Fetching project video count.");
    $projectVideosCount = getProjectVideoCount();

    $logger->info("Fetching project image count.");
    $projectImagesCount = getProjectImagesCount();
    
    $logger->info("Fetching project flat supply.");
    $projectFlatSupply = getProjectFlatSupply();
    
    // Disabling Coupon System
    $logger->info("Fetching coupons data for properties.");
    list($couponData, $couponProjectData) = getCouponsDataForProperties();
    
    $logger->info("Fetching property sold out status.");
    $soldOutStatus = getPropertySoldOutStatus();
    
    $logger->info("Fetching project attributes :: panorama view path.");
    $panoramaViewPath = getProjectAttributes("resi_project_options", array("PANORAMA_VIEW_PATH"), "");
    
    $projectComputedPriority = array();
    $distinctProjectIds = array();
 
    if(empty($projectEnquiryCountArray)){
        list($projectEnquiryCountArray, $projectLastEnquiredDate) = loadEnquiryCount('PROJECT');
    }
    if(empty($localityEnquiryCountArray)){
        list($localityEnquiryCountArray, $localityLastEnquiredDate) = loadEnquiryCount('LOCALITY');
    }
    if(empty($cityEnquiryCountArray)){
        list($cityEnquiryCountArray, $cityLastEnquiredDate) = loadEnquiryCount('CITY');
    }
    if(empty($builderEnquiryCountArray)){
        list($builderEnquiryCountArray, $builderLastEnquiredDate) = loadEnquiryCount('BUILDER');
    }
    if(empty($suburbEnquiryCountArray)){
        list($suburbEnquiryCountArray, $suburbLastEnquiredDate) = loadEnquiryCount('SUBURB');
    }

    $condition = "";
    if( !empty($paramProjectIds) ){
        $condition = " WHERE project_id in ($paramProjectIds) ";
    }
    if( !empty($paramPropertyIds) ){
        $propertyCond = " options_id in ($paramPropertyIds) ";
         if(empty($condition))
             $condition = " WHERE ".$propertyCond;
         else
             $condition .= " OR ".$propertyCond;

    }
    $sql = "SELECT MIN(OPTIONS_ID) AS MIN_ID, MAX(OPTIONS_ID) AS MAX_ID FROM cms.resi_project_options $condition";
    $result = mysql_query($sql, $solrDB);
    $logger->info($sql);
    if ($result == FALSE) {
    	$logger->error("Error while fetching Min-Max OptionsIds query : \n ". $sql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }
    $row = mysql_fetch_assoc($result);
    $minPropertyId = (int)$row['MIN_ID'];
    $maxPropertyId = (int)$row['MAX_ID'];
    $limit = PROPERTY_RECORDS_LIMIT;
    $lowerLimit = $minPropertyId;
    do{
    list($numOfRows, $solrDeleteProperties)  = savePropertyDocuments($lowerLimit, $limit, $cmsProperties, $solrDeleteProperties, $projectVideosCount, 
    		$projectImagesCount, $projectFlatSupply, $projectComputedPriority, $distinctProjectIds, $couponData);
    $lowerLimit += $limit;
    }
    while($numOfRows > 0 || $lowerLimit <= $maxPropertyId);
    
    if( !empty($paramPropertyIds)){
        if(sizeof($distinctProjectIds)){
            $paramProjectIds .= ",".implode(",", array_keys($distinctProjectIds));
            $paramProjectIds = trim($paramProjectIds, ",");
        }
    }

    return array(array_keys($solrDeleteProperties), array());
}

function savePropertyDocuments($lowerLimit, $limit, $cmsProperties, $solrDeleteProperties, $projectVideosCount,
    	$projectImagesCount, $projectFlatSupply, &$projectComputedPriority, &$distinctProjectIds, $couponData){
    	
    global $projectEnquiryCountArray, $localityEnquiryCountArray, $suburbEnquiryCountArray, $cityEnquiryCountArray, $builderEnquiryCountArray;
   	global $imageTypeCount;
   	global $projectMinMaxPrice, $logger, $resaleProjectPrices, $resalePropertyPrices, $projectPriceRise;
   	global $projectAndBuilderImages;
   	global $projectDiscussionCount;
    global $projectLastUpdatedTime;
    global $optionsPriceData;
    global $projectOffers;
    global $projectDelay;
    global $projectVerifiedResaleLst;
    global $propertyVerifiedResaleLst;
    global $listingMinPricePerUnitArea;
    global $listingMaxPricePerUnitArea;
    global $paramProjectIds, $paramPropertyIds;
    global $projCmsActiveCondition, $version;
    global $has3DImages;
    global $projectSafetyAndLivabilityData;
    global $localitySafetyAndLivabilityData;
    global $soldOutStatus;
    global $panoramaViewPath, $solrDB;
    global $projDominantUnitTypes, $logger, $solr;
    global $projectResaleEnquiry;
    global  $projectsCreatedDates;
    global $localityPrices;
    global $projectsWithPrimaryExpandedListings;
    global $projectWithSource;
    global $propertyWithSource;
    //$resaleProjects = getResaleProjects();
    $projectCondition = " rp.STATUS IN ({$projCmsActiveCondition}) ";
    $newCondition = "";
    if( !empty($paramProjectIds) )
    	$newCondition .= " rp.PROJECT_ID IN ($paramProjectIds) ";
    if( !empty($paramPropertyIds) )
        $newCondition .= "OR rpo.options_id in ($paramPropertyIds) ";

    if(!empty($newCondition)){
        $newCondition = "(".ltrim($newCondition, "OR").")";
        $projectCondition .= " AND $newCondition";
    }
    $projectCondition = appendConditionForB2b($projectCondition);

    $maxActualBuliderScore = getMaxBuilderScoreFromDb();

    /*$projectsCreatedDates=getCreatedAtForProject();*/
    $sql = "SELECT CONCAT('PROPERTY-', rpo.OPTIONS_ID) AS id, 'PROPERTY' AS DOCUMENT_TYPE, rp.PROJECT_ID, rp.PROJECT_NAME,
                    rb.BUILDER_NAME, l.LABEL AS LOCALITY, rp.PROMISED_COMPLETION_DATE AS COMPLETION_DATE, rp.DISPLAY_ORDER,
                    rp.DISPLAY_ORDER_LOCALITY,rp.DISPLAY_ORDER_SUBURB,
                    c.LABEL AS CITY, rp.PROJECT_ADDRESS, LOWER(s.LABEL) AS SUBURB, rp.PROJECT_SMALL_IMAGE, rp.PROJECT_URL,
                    rpo.OPTIONS_ID AS TYPE_ID, rpo.BEDROOMS,
		    		rpo.OPTION_NAME AS UNIT_NAME, rpo.OPTION_TYPE AS UNIT_TYPE, rpo.BATHROOMS, rpo.SIZE, rpo.CARPET_AREA,
                    rp.LATITUDE, rp.LONGITUDE,
                    rp.BUILDER_ID, rp.LOCALITY_ID, s.SUBURB_ID, c.CITY_ID, c.NORTH_EAST_LATITUDE, c.NORTH_EAST_LONGITUDE,
                    c.SOUTH_WEST_LATITUDE, c.SOUTH_WEST_LONGITUDE, c.CENTER_LATITUDE, c.CENTER_LONGITUDE, l.PRIORITY AS LOCALITY_PRIORITY,
                    s.PRIORITY AS SUBURB_PRIORITY, rp.FORCE_RESALE,
                    rb.DISPLAY_ORDER AS BUILDER_DISPLAY_ORDER, rp.LAUNCH_DATE, rp.PROMISED_COMPLETION_DATE, rp.D_AVAILABILITY AS AVAILABILITY,
                    psm.display_name AS PROJECT_STATUS, rp.PRE_LAUNCH_DATE, pt.TYPE_NAME AS PROJECT_TYPE,
                    CONCAT(LOWER(l.LABEL), ':', IF(l.PRIORITY IS NULL, 0, l.PRIORITY) ) AS LOCALITY_LABEL_PRIORITY,
                    CONCAT(LOWER(rb.BUILDER_NAME), ':', IF(rb.DISPLAY_ORDER IS NULL, 0, rb.DISPLAY_ORDER) ) AS BUILDER_LABEL_PRIORITY,
                    CONCAT(LOWER(s.LABEL), ':', IF(s.PRIORITY IS NULL, 0, s.PRIORITY) ) AS SUBURB_LABEL_PRIORITY,
                    CONCAT(LOWER(l.LABEL), ':', l.LOCALITY_ID, ':', IF(l.PRIORITY IS NULL, 0, l.PRIORITY) ) AS LOCALITY_LABEL_ID_PRIORITY,
                    CONCAT(LOWER(rb.BUILDER_NAME), ':', rb.BUILDER_ID, ':', IF(rb.DISPLAY_ORDER IS NULL, 0, rb.DISPLAY_ORDER) ) AS BUILDER_LABEL_ID_PRIORITY,
                    CONCAT(LOWER(s.LABEL), ':', s.SUBURB_ID, ':', IF(s.PRIORITY IS NULL, 0, s.PRIORITY) ) AS SUBURB_LABEL_ID_PRIORITY,
                    rp.D_LAST_PRICE_UPDATION_DATE AS SUBMITTED_DATE, l.LATITUDE AS LOCALITY_LATITUDE, l.LONGITUDE AS LOCALITY_LONGITUDE,
                    l.URL AS LOCALITY_URL, rp.STATUS, rb.URL AS BUILDER_URL,
                    rp.SAFETY_SCORE AS PROJECT_SAFETY_SCORE, rb.listed AS IS_BUILDER_LISTED, rp.LIVABILITY_SCORE AS PROJECT_LIVABILITY_SCORE, ifnull(svt.user_popularity_index, 0) AS PROJECT_POPULARITY_INDEX,
                    rp.PROJECT_LOCALITY_SCORE, rp.PROJECT_SOCIETY_SCORE, rp.PROJECT_SIZE, ppi.primary_index PRIMARY_INDEX, ppi.resale_index RESALE_INDEX, c.URL as CITY_URL,
                    l.SAFETY_SCORE AS LOCALITY_SAFETY_SCORE, l.LIVABILITY_SCORE AS LOCALITY_LIVABILITY_SCORE,
		    rpo.BALCONY, rpo.SERVANT_ROOM, rpo.POOJA_ROOM, rpo.STUDY_ROOM, rb.BUILDER_SCORE as BUILDER_SCORE,rpo.created_at as PROPERTY_CREATED_AT,rpo.updated_at as PROPERTY_UPDATED_AT
                    FROM cms.resi_project rp
                    LEFT JOIN cms.project_primary_index ppi
                    ON rp.PROJECT_ID = ppi.id
                    LEFT JOIN cms.resi_project_type pt
                    ON (rp.PROJECT_TYPE_ID = pt.PROJECT_TYPE_ID)
                    LEFT JOIN analytics.object_popularity_table svt
                    ON (rp.PROJECT_ID = svt.object_id AND svt.object_type_id = 1)
                    JOIN cms.resi_project_options rpo
                    ON (rp.PROJECT_ID = rpo.PROJECT_ID AND rpo.OPTION_CATEGORY = 'Actual' and rpo.OPTIONS_ID >= $lowerLimit and rpo.OPTIONS_ID <= ($lowerLimit + $limit))
                    JOIN cms.resi_builder rb
                    ON (rb.BUILDER_ID = rp.BUILDER_ID)
                    JOIN cms.locality l
                    ON (l.LOCALITY_ID = rp.LOCALITY_ID)
		   		    JOIN cms.suburb s
                    ON (s.SUBURB_ID = l.SUBURB_ID)
                    JOIN cms.city c
                    ON (c.CITY_ID = s.CITY_ID)
	            	JOIN cms.project_status_master psm
		    		ON (psm.id = rp.PROJECT_STATUS_ID)
                    WHERE $projectCondition AND rp.VERSION = $version AND rp.RESIDENTIAL_FLAG ='Residential'
                    GROUP BY rpo.OPTIONS_ID";
    $deleteDocuments = array();
    $documents = array();
    $result = mysql_unbuffered_query($sql, $solrDB);
    
    if ($result) {
        while ($document = mysql_fetch_assoc($result)) {
            unset($solrDeleteProperties[$document['id']]);
            if(!empty($paramPropertyIds)){
                $distinctProjectIds[$document['PROJECT_ID']] = 1;
            }

            $typeId = $document['TYPE_ID'];
            $document['IS_SOLD_OUT'] = getSoldOutStatus($document);
            $document['IS_PROPERTY_SOLD_OUT'] = $document['IS_SOLD_OUT'];
            if (! empty($soldOutStatus[$document['TYPE_ID']])) {
                $document['IS_PROPERTY_SOLD_OUT'] = $soldOutStatus[$document['TYPE_ID']];
            }
            
            
            if ($document['IS_PROPERTY_SOLD_OUT']) {
                unset($optionsPriceData[$typeId]);
            }
            
            $projectRandomImageIncrement = 0;
            $project_id = $document['PROJECT_ID'];
            
            if (isset($projectsCreatedDates[$project_id])) {
                $document['CREATED_LIVE_DATE'] =  $projectsCreatedDates[$project_id];
                checkValidAndSetDate($document, "CREATED_LIVE_DATE");
            }

            $unit_type = strtolower($document['UNIT_TYPE']);
            $document['LOCALITY_OR_SUBURB'] = array($document['LOCALITY'], $document['SUBURB']);
            $document['LOCALITY_OR_SUBURB_ID'] = array($document['LOCALITY_ID'], $document['SUBURB_ID']);
            $size = $document['SIZE'];
            
            $sizeOrCarpetArea = $document['SIZE'];
            if (!isset($document['SIZE']) || $document['SIZE'] == 0) {
            	$sizeOrCarpetArea = $document['CARPET_AREA'];
            }
			
            $latitude = $document['LATITUDE'];
            $longitude = $document['LONGITUDE'];
            
            if (!isset($document['CARPET_AREA']) || $document['CARPET_AREA'] == 0) {
            	unset($document['CARPET_AREA']);
            }

            if (empty ( $document ['PRIMARY_INDEX'] )) {
                $document ['PRIMARY_INDEX'] = 0;
            }

            if (empty ( $document ['RESALE_INDEX'] )) {
                $document ['RESALE_INDEX'] = 0;
            }
            
            // Disabling Coupon System
            if(isset($couponData[$typeId]))
            {
                $couponPropertyData = $couponData[$typeId];
                if($couponPropertyData['productType'] == 'DiwaliMela'){
                    $couponPropertyData['discountPricePerUnitArea'] = round($couponPropertyData['discount']/$size);
                    $document['COUPON_CATALOGUE_OBJECT'] = json_encode($couponPropertyData);    
                    $document['PROPERTY_COUPON_AVAILABLE'] = true;
                }
                else if($couponPropertyData['productType'] == 'Non4DSale'){
                    //$document['PROPERTY_COUPON_DISCOUNT'] = $couponPropertyData['discount'];
                    $couponPropertyData['discountPricePerUnitArea'] = round($couponPropertyData['discount']/$size);
                    $document['COUPON_CATALOGUE_OBJECT'] = json_encode($couponPropertyData);    
                    $document['HAS_PRIMARY_EXPANDED_LISTING_NEW'] = 2;
                }

            }
            if (isValidGeo($latitude, $longitude))
            {
                $document['GEO'] = "$latitude, $longitude";
                $document['HAS_GEO'] = 1;
                $document['PROCESSED_LATITUDE'] = $document['LATITUDE'];
                $document['PROCESSED_LONGITUDE'] = $document['LONGITUDE'];
            }
            else {
                if (isValidGeo($document['LOCALITY_LATITUDE'], $document['LOCALITY_LONGITUDE'])) {
                    $document['PROCESSED_LATITUDE'] = $document['LOCALITY_LATITUDE'];
                    $document['PROCESSED_LONGITUDE'] = $document['LOCALITY_LONGITUDE'];
                }

                unset($document['LATITUDE']);
                unset($document['LONGITUDE']);
                $document['HAS_GEO'] = 0;
            }

            if ( !empty( $imageTypeCount ) && !empty( $imageTypeCount[ $project_id ] ) ) {
                $document[ 'IMAGE_TYPE_COUNT' ] = json_encode( $imageTypeCount[ $project_id ] );
            }

            if ( !empty( $projectResaleEnquiry ) && !empty( $projectResaleEnquiry[ $project_id ] ) ) {
            	$document[ 'RESALE_ENQUIRY' ] = $projectResaleEnquiry[ $project_id ];
            }
            
            if( isset($projectAndBuilderImages["builder"][ $document['BUILDER_ID'] ]['PATH']) )
            {
                $document['BUILDER_LOGO_IMAGE'] = $projectAndBuilderImages["builder"][ $document['BUILDER_ID'] ]['PATH'];
            }
            if( isset($projectAndBuilderImages["project"][$project_id]['PATH']) )
            {
                $document['PROJECT_MAIN_IMAGE'] = $projectAndBuilderImages["project"][$project_id]['PATH'];
                $projectRandomImageIncrement = 0;
            }
            else
            {
                $document['PROJECT_MAIN_IMAGE'] = getProjectMainImageRandomly($document['PROJECT_ID']);
                $projectRandomImageIncrement = 1;
            }
			
            if ( isset ($projectAndBuilderImages['builder'][$document ['BUILDER_ID']]['ALTTEXT']) ) {
            	$document ['BUILDER_IMAGE_ALTTEXT'] = $projectAndBuilderImages['builder'][$document ['BUILDER_ID']]['ALTTEXT'];
            }
            	
            if ( isset ($projectAndBuilderImages['builder'][$document ['BUILDER_ID']]['TITLE']) ) {
            	$document ['BUILDER_IMAGE_TITLE'] = $projectAndBuilderImages['builder'][$document ['BUILDER_ID']]['TITLE'];
            }
            
            if (isset ( $projectAndBuilderImages ['project'] [$project_id] ['ALTTEXT'] )) {
            	$document ['PROJECT_IMAGE_ALTTEXT'] = $projectAndBuilderImages ['project'] [$project_id] ['ALTTEXT'];
            }
            	
            if (isset ( $projectAndBuilderImages ['project'] [$project_id] ['TITLE'] )) {
            	$document ['PROJECT_IMAGE_TITLE'] = $projectAndBuilderImages ['project'] [$project_id] ['TITLE'];
            }
            
            if( isset($projectDiscussionCount[$project_id]) )
            {
                $document['NUMBER_OF_PROJECT_DISCUSSION'] = $projectDiscussionCount[$project_id];
            }
            if( isset($projectLastUpdatedTime[$project_id]) )
            {
                $document['PROJECT_LAST_UPDATED_TIME'] = $projectLastUpdatedTime[$project_id]["ATTRIBUTE_VALUE"];
                checkValidAndSetDate($document, "PROJECT_LAST_UPDATED_TIME");
            }       
            if( isset($projectOffers[$project_id]) )
            {
                $document['PROJECT_OFFER'] = $projectOffers[$project_id]['OFFER'];
            }

            if( isset($projectFlatSupply[$project_id]) )
            {
            	$document['TOTAL_UNITS'] = $document['PROJECT_SUPPLY'] = $projectFlatSupply[$project_id]['TOTAL_UNITS'];
            }
            
            if( empty($document['SERVANT_ROOM']) ){
                unset($document['SERVANT_ROOM']);
            }
            if( empty($document['POOJA_ROOM']) ){
                unset($document['POOJA_ROOM']);
            }
            if( empty($document['STUDY_ROOM']) ){
                unset($document['STUDY_ROOM']);
            }

            /*if( isset($cmsProperties[$typeId]) )
            {
                if( isset($cmsProperties[$typeId]['SERVANT_ROOM']) )
                    $document['SERVANT_ROOM'] = $cmsProperties[$typeId]['SERVANT_ROOM'];
                if( isset($cmsProperties[$typeId]['POOJA_ROOM']) )
                    $document['POOJA_ROOM'] = $cmsProperties[$typeId]['POOJA_ROOM'];
                if (isset($cmsProperties[$typeId]['STUDY_ROOM']))
                	$document['STUDY_ROOM'] = $cmsProperties[$typeId]['STUDY_ROOM'];
            }*/

            if( isset($projectImagesCount[$project_id]) )
            {
                $document['PROJECT_IMAGES_COUNT'] = $projectImagesCount[$project_id] + $projectRandomImageIncrement;
            }
            else
            {
                $document['PROJECT_IMAGES_COUNT'] = $projectRandomImageIncrement;
            }

            if( isset($projectVideosCount[$project_id]) )
            {
                $document['PROJECT_VIDEOS_COUNT'] = $projectVideosCount[$project_id];
            }
            
            if( isset($optionsPriceData[$typeId]) && !empty($sizeOrCarpetArea)) {
            	$document['PRICE_PER_UNIT_AREA'] = $optionsPriceData[$typeId]['price_per_unit_area'];
		$document['PROJECT_ID_PRICE_PER_UNIT_AREA'] = $document['PROJECT_ID']."-".$document['PRICE_PER_UNIT_AREA'];
            	$document['BUDGET'] = $document['PRICE'] = $document['PRICE_PER_UNIT_AREA'] * $sizeOrCarpetArea;
            }
            else {
                unset($document['PRICE_PER_UNIT_AREA']);
                unset($document['BUDGET']);
                unset($document['PRICE']);
            }

            if(isset($document['BUDGET'])) {
            	$document['PRICE']        = makePriceUserReadable($document['BUDGET']);
            }
            if(isset($projectMinMaxPrice[$document['PROJECT_ID']]['MINPRICE'])){
            	$document['MINPRICE']     = $projectMinMaxPrice[$document['PROJECT_ID']]['MINPRICE'];
            }
            if(isset($projectMinMaxPrice[$document['PROJECT_ID']]['MINPRICE'])){
            	$document['MAXPRICE']     = $projectMinMaxPrice[$document['PROJECT_ID']]['MAXPRICE'];
            }
            
            if (isset($projectMinMaxPrice[$document['PROJECT_ID']])) {
            	$document['ALL_BEDROOMS'] = $projectMinMaxPrice[$document['PROJECT_ID']]['ALL_BEDROOMS'];
            }
	    if( empty($document['BALCONY']) ){
		unset($document['BALCONY']);
	    }
            $document["LOCALITY_ID_PROJECT_STATUS"] = $document['LOCALITY_ID'].":".$document['PROJECT_STATUS'];
            $document['PRIMARY_OR_RESALE_BUDGET'] = array();
            $document['PRIMARY_OR_RESALE_PRICE_PER_UNIT_AREA'] = array();

            $document['IS_PRIMARY'] = getPrimaryStatus($document);

            // setting resale prices.
            if( isset($resalePropertyPrices[$typeId]) ){
            	$document['RESALE_PRICE_PER_UNIT_AREA'] = $resalePropertyPrices[$typeId];
            }
            else if ( isset($resaleProjectPrices[$project_id]) && isset($resaleProjectPrices[$project_id][$unit_type]) ){	 
            	$document['RESALE_PRICE_PER_UNIT_AREA'] = $resaleProjectPrices[$project_id][$unit_type];
        	}        	
        	if(isset($document['RESALE_PRICE_PER_UNIT_AREA'])){
            	$document['PRIMARY_OR_RESALE_PRICE_PER_UNIT_AREA'][] = $document['RESALE_PRICE_PER_UNIT_AREA'];
            	if(!empty($sizeOrCarpetArea)) {
            		$document['RESALE_PRICE'] = $document['RESALE_PRICE_PER_UNIT_AREA'] * $sizeOrCarpetArea;
            		$document['PRIMARY_OR_RESALE_BUDGET'][] = $document['RESALE_PRICE'];
            	}
            }

            if(isset($document['BUDGET'])){
            	$document['MIN_BUDGET'] = $document['BUDGET'];
            }            
            if(isset($document['BUDGET'])){
            	$document['MAX_BUDGET'] = $document['BUDGET'];
            }
            $typeId = $document['TYPE_ID'];
            $bedrooms = ($document['BEDROOMS'] ? $document['BEDROOMS'] : '') . 'bhk';
            $projectURL = $document['PROJECT_URL'];
            $document['HAS_BUDGET'] = 1;
            $document['HAS_SIZE'] = 1;
            $document['HAS_PRICE_PER_UNIT_AREA'] = 1;

            if (!isset($document['BUDGET'])) {
                $document['HAS_BUDGET'] = 0;
                unset($document['BUDGET']);
                unset($document['MIN_BUDGET']);
                unset($document['MAX_BUDGET']);
            }
            else {
                $document['PRIMARY_OR_RESALE_BUDGET'][] = $document['BUDGET'];
            }

            if (!isset($document['SIZE']) || $document['SIZE'] == 0) {
                $document['HAS_SIZE'] = 0;
                unset($document['SIZE']);
            }

            if (!isset($document['PRICE_PER_UNIT_AREA'])) {
                $document['HAS_PRICE_PER_UNIT_AREA'] = 0;
                unset($document['PRICE_PER_UNIT_AREA']);
            }
            else {
                $document['PRICE_PER_UNIT_AREA'] = round($document['PRICE_PER_UNIT_AREA'], 2);
                $document['PRIMARY_OR_RESALE_PRICE_PER_UNIT_AREA'][] = $document['PRICE_PER_UNIT_AREA'];
            }

			if($document['COMPLETION_DATE']) {
				$document['COMPLETION_DATE'] = date("M Y", strtotime($document['COMPLETION_DATE']));
			}


            if (empty($document['NORTH_EAST_LATITUDE'])) {
                unset($document['NORTH_EAST_LATITUDE']);
	    }
	    if(empty($document['NORTH_EAST_LONGITUDE'])){
                unset($document['NORTH_EAST_LONGITUDE']);
	    }		
	    if(empty($document['SOUTH_WEST_LATITUDE'])){
                unset($document['SOUTH_WEST_LATITUDE']);
            }
	    if(empty($document['SOUTH_WEST_LONGITUDE'])){
                unset($document['SOUTH_WEST_LONGITUDE']);
	    }
	    
            if(empty($document['CENTER_LATITUDE'])){
                unset($document['CENTER_LATITUDE']);
            }
            if(empty($document['CENTER_LONGITUDE'])){
                unset($document['CENTER_LONGITUDE']);
            }
            
            if( !isset($projectComputedPriority[$project_id]) ){
                $priorities = computePropertyPriority($document);
                $projectComputedPriority[$project_id] = array();
                $projectComputedPriority[$project_id]['PROJECT_PRIORITY']   = $priorities['nonEditorialPriority'];
                $projectComputedPriority[$project_id]['DISPLAY_ORDER']  = $priorities['editorialPriority'];
        
            }
            $document['PROJECT_PRIORITY']   = $projectComputedPriority[$project_id]['PROJECT_PRIORITY'];
            $document['DISPLAY_ORDER']  = $projectComputedPriority[$project_id]['DISPLAY_ORDER'];

            if ($document['PROJECT_STATUS'] == 'On Hold') {
                $document['PROMISED_COMPLETION_DATE'] = null;
            }

            if(isset($projectVerifiedResaleLst[$document['PROJECT_ID']])){
                $document['PROJECT_RESALE_LISTING_COUNT'] = $projectVerifiedResaleLst[$document['PROJECT_ID']];
            }
            
            // formatting date in solr date format
            checkValidAndSetDate($document, 'PROMISED_COMPLETION_DATE');
            checkValidAndSetDate($document, 'SUBMITTED_DATE');
            
            checkValidAndSetDate($document, 'PROPERTY_CREATED_AT');
            checkValidAndSetDate($document, 'PROPERTY_UPDATED_AT');



            checkValidAndSetDate($document, 'LAUNCH_DATE');
            checkValidAndSetDate($document, 'PRE_LAUNCH_DATE');

            $document['IS_RESALE'] = getReSaleStatus($document);
            
            // valid launch date. It should be called after checkValidAndSetDate
            // function call to launch date and pre launch date.
            setValidLaunchDate($document);
            

            if(empty($document['PROJECT_STATUS']))
            {
                unset($document['PROJECT_STATUS']);
                unset($document['VALID_LAUNCH_DATE']);
            }
            else if($document['BEDROOMS'] > 0 )
            {
                $document['PROJECT_STATUS_BEDROOM'] = "{$document['PROJECT_STATUS']},{$document['BEDROOMS']}";
                $document['PROJECT_ID_BEDROOM'] = $document['PROJECT_ID']."-".$document['BEDROOMS'];
            }

            if(!isset($document['IS_RESALE']))
                unset($document['IS_RESALE']);
            
            if( !isset($document['LOCALITY_URL']) )
                unset( $document['LOCALITY_URL'] );
            
            if( !isset($document['BUILDER_URL']) )
            	unset( $document['BUILDER_URL'] );
            
            if( empty($document['PROJECT_AVG_PRICE_PER_UNIT_AREA']) )
                unset( $document['PROJECT_AVG_PRICE_PER_UNIT_AREA'] );
            $document ['PROJECT_DOMINANT_UNIT_TYPE'] = DEFAULT_DOMINANT_TYPE;
            if (isset($projectPriceRise[$document['PROJECT_ID']])) {
                $projectDoc = $projectPriceRise[$document['PROJECT_ID']];
                $document['PROJECT_DOMINANT_UNIT_TYPE']  = empty($projectDoc['unit_type']) ? DEFAULT_DOMINANT_TYPE : $projectDoc['unit_type'];
                
                if (!empty($projectDoc['average_price_per_unit_area'])) {
                    $document['PROJECT_AVG_PRICE_PER_UNIT_AREA'] = $projectDoc['average_price_per_unit_area'];
                }
                
                if ($document['PROJECT_STATUS'] != 'On Hold' && !empty($projectDoc['RISE_PERCENT'])) {
                    $document['PROJECT_PRICE_RISE'] = $projectDoc['RISE_PERCENT'];
                    $document['PROJECT_PRICE_RISE_TIME'] = $projectDoc['RISE_PERIOD_MONTHS'];
                }
            }
	    else {
		if ( !empty($projDominantUnitTypes[$document['PROJECT_ID']])) {
			$document ['PROJECT_DOMINANT_UNIT_TYPE'] = $projDominantUnitTypes[$document['PROJECT_ID']];
		}
            }
            resetPriceFields($document);

            $price = 0;
            $resalePrice = 0;
            if( !empty($document['HAS_PRICE_PER_UNIT_AREA']) && !empty($sizeOrCarpetArea) )
                $price = (int)$sizeOrCarpetArea * (int)$document['PRICE_PER_UNIT_AREA'];
            if( !empty($document['RESALE_PRICE']) )
                $resalePrice = $document['RESALE_PRICE'];
                
            $document['MAX_RESALE_OR_PRIMARY_PRICE'] = max($price, isset($resalePropertyPrices['max_price'][$typeId]) ? $sizeOrCarpetArea * $resalePropertyPrices['max_price'][$typeId] : $resalePrice);
            $document['MIN_RESALE_OR_PRIMARY_PRICE'] = min($price, isset($resalePropertyPrices[$typeId]) ? $sizeOrCarpetArea * $resalePropertyPrices[$typeId] : $resalePrice);
            if ($document['MIN_RESALE_OR_PRIMARY_PRICE'] == 0) {
            	$document['MIN_RESALE_OR_PRIMARY_PRICE'] = $document['MAX_RESALE_OR_PRIMARY_PRICE'];
            }
            
            if(isset($projectEnquiryCountArray[$document['PROJECT_ID']])){
            	$document['PROJECT_ENQUIRY_COUNT']   = $projectEnquiryCountArray[$document['PROJECT_ID']];
            }
            if(isset($localityEnquiryCountArray[$document['LOCALITY_ID']])){
            	$document['LOCALITY_ENQUIRY_COUNT']   = $localityEnquiryCountArray[$document['LOCALITY_ID']];
            }
            if(isset($cityEnquiryCountArray[$document['CITY_ID']])){
            	$document['CITY_ENQUIRY_COUNT']       = $cityEnquiryCountArray[$document['CITY_ID']];
            }
            if(isset($suburbEnquiryCountArray[$document['SUBURB_ID']])){
            	$document['SUBURB_ENQUIRY_COUNT']     = $suburbEnquiryCountArray[$document['SUBURB_ID']];
            }
            if(isset($builderEnquiryCountArray[$document['BUILDER_ID']])){
            	$document['BUILDER_ENQUIRY_COUNT']    = $builderEnquiryCountArray[$document['BUILDER_ID']];
            }
            if(isset($projectDelay[$document['PROJECT_ID']])){
                $document['PROJECT_DELAY'] = $projectDelay[$document['PROJECT_ID']];
            }
            if(isset($propertyVerifiedResaleLst[$document['TYPE_ID']])){
                $document['PROPERTY_RESALE_LISTING_COUNT'] = $propertyVerifiedResaleLst[$document['TYPE_ID']];
            }
            if(isset($listingMinPricePerUnitArea[$document['TYPE_ID']])){
                $document['PROPERTY_RESALE_MIN_PRICE_PER_UNIT_AREA'] = $listingMinPricePerUnitArea[$document['TYPE_ID']];
            }
            if(isset($listingMaxPricePerUnitArea[$document['TYPE_ID']])){
                $document['PROPERTY_RESALE_MAX_PRICE_PER_UNIT_AREA'] = $listingMaxPricePerUnitArea[$document['TYPE_ID']];
            }

            if(isset($localityPrices[$document['LOCALITY_ID']]['average_price_per_unit_area'])){
                $document['LOCALITY_PRICE_PER_UNIT_AREA'] = $localityPrices[$document['LOCALITY_ID']]['average_price_per_unit_area'];
            }
            $document['PROJECT_VIEW_COUNT']  = 0;
            $document['LOCALITY_VIEW_COUNT'] = 0;
            $document['CITY_VIEW_COUNT']     = 0;
            $document['BUILDER_VIEW_COUNT']  = 0;
            $document['SUBURB_VIEW_COUNT']   = 0;
            
            if( empty($document['MIN_RESALE_OR_PRIMARY_PRICE']) || $document['MIN_RESALE_OR_PRIMARY_PRICE'] == 0)
                unset($document['MIN_RESALE_OR_PRIMARY_PRICE']);
            if( empty($document['MAX_RESALE_OR_PRIMARY_PRICE']) || $document['MAX_RESALE_OR_PRIMARY_PRICE'] == 0)
                unset($document['MAX_RESALE_OR_PRIMARY_PRICE']);
            if(!isset($document['AVAILABILITY'])) {
                unset($document['AVAILABILITY']);
            }
            if(!isset($document['PROJECT_SIZE'])){
                unset($document['PROJECT_SIZE']);
            }
            if(!isset($document['TOTAL_UNITS'])){
                $document['TOTAL_UNITS'] ="0";
            }
            if( empty($document['PROJECT_SAFETY_SCORE']))
            	unset($document['PROJECT_SAFETY_SCORE']);
            if( empty($document['PROJECT_LIVABILITY_SCORE']))
            	unset($document['PROJECT_LIVABILITY_SCORE']);
            if( empty($document['PROJECT_LOCALITY_SCORE']))
            	unset($document['PROJECT_LOCALITY_SCORE']);
            if( empty($document['PROJECT_SOCIETY_SCORE']))
            	unset($document['PROJECT_SOCIETY_SCORE']);
            if( empty($document['LOCALITY_SAFETY_SCORE']))
            	unset($document['LOCALITY_SAFETY_SCORE']);
            if( empty($document['LOCALITY_LIVABILITY_SCORE']))
            	unset($document['LOCALITY_LIVABILITY_SCORE']);
            
            unset($document['LOCALITY_LATITUDE']);
            unset($document['LOCALITY_LONGITUDE']);

            unset($document['OLDEST_PRICE_PER_UNIT_AREA']);
            unset($document['OLDEST_PRICE_PER_UNIT_AREA_DATE']);
            unset($document['LATEST_PRICE_PER_UNIT_AREA']);
            unset($document['LATEST_PRICE_PER_UNIT_AREA_DATE']);
            unset($document['LOCALITY_PRIORITY']);
            unset($document['BUILDER_DISPLAY_ORDER']);
            unset($document['PROJECT_TYPE']);
            unset($document['PRE_LAUNCH_DATE']);
            unset($document['FORCE_RESALE']);
            unset($document['SUBURB_PRIORITY']);
            unset($document['STATUS']);
            
            if (isset ($projectSafetyAndLivabilityData['SAFETY_RANK'][$document['LOCALITY_ID']][$document['PROJECT_ID']])){
            	$document['PROJECT_SAFETY_RANK'] = $projectSafetyAndLivabilityData['SAFETY_RANK'][$document['LOCALITY_ID']][$document['PROJECT_ID']];
            }
            if (isset ($projectSafetyAndLivabilityData['LIVABILITY_RANK'][$document['LOCALITY_ID']][$document['PROJECT_ID']])){
            	$document['PROJECT_LIVABILITY_RANK'] = $projectSafetyAndLivabilityData['LIVABILITY_RANK'][$document['LOCALITY_ID']][$document['PROJECT_ID']];
            }
            if (isset ($projectSafetyAndLivabilityData[$document['LOCALITY_ID']]['MIN_SAFETY_SCORE'])) {
            	$document['PROJECT_MIN_SAFETY_SCORE'] = $projectSafetyAndLivabilityData[$document['LOCALITY_ID']]['MIN_SAFETY_SCORE'];
            }
            if (isset ($projectSafetyAndLivabilityData[$document['LOCALITY_ID']]['MAX_SAFETY_SCORE'])) {
            	$document['PROJECT_MAX_SAFETY_SCORE'] = $projectSafetyAndLivabilityData[$document['LOCALITY_ID']]['MAX_SAFETY_SCORE'];
            }
            if (isset ($projectSafetyAndLivabilityData[$document['LOCALITY_ID']]['MIN_LIVABILITY_SCORE'])) {
            	$document['PROJECT_MIN_LIVABILITY_SCORE'] = $projectSafetyAndLivabilityData[$document['LOCALITY_ID']]['MIN_LIVABILITY_SCORE'];
            }
            if (isset ($projectSafetyAndLivabilityData[$document['LOCALITY_ID']]['MAX_LIVABILITY_SCORE'])) {
            	$document['PROJECT_MAX_LIVABILITY_SCORE'] = $projectSafetyAndLivabilityData[$document['LOCALITY_ID']]['MAX_LIVABILITY_SCORE'];
            }
            
            if (isset ($localitySafetyAndLivabilityData['SAFETY_RANK'][$document['CITY_ID']][$document['LOCALITY_ID']])){
            	$document['LOCALITY_SAFETY_RANK'] = $localitySafetyAndLivabilityData['SAFETY_RANK'][$document['CITY_ID']][$document['LOCALITY_ID']];
            }
            if (isset ($localitySafetyAndLivabilityData['LIVABILITY_RANK'][$document['CITY_ID']][$document['LOCALITY_ID']])){
            	$document['LOCALITY_LIVABILITY_RANK'] = $localitySafetyAndLivabilityData['LIVABILITY_RANK'][$document['CITY_ID']][$document['LOCALITY_ID']];
            }
            if (isset ($localitySafetyAndLivabilityData[$document['CITY_ID']]['MIN_SAFETY_SCORE'])) {
            	$document['LOCALITY_MIN_SAFETY_SCORE'] = $localitySafetyAndLivabilityData[$document['CITY_ID']]['MIN_SAFETY_SCORE'];
            }
            if (isset ($localitySafetyAndLivabilityData[$document['CITY_ID']]['MAX_SAFETY_SCORE'])) {
            	$document['LOCALITY_MAX_SAFETY_SCORE'] = $localitySafetyAndLivabilityData[$document['CITY_ID']]['MAX_SAFETY_SCORE'];
            }
            if (isset ($localitySafetyAndLivabilityData[$document['CITY_ID']]['MIN_LIVABILITY_SCORE'])) {
            	$document['LOCALITY_MIN_LIVABILITY_SCORE'] = $localitySafetyAndLivabilityData[$document['CITY_ID']]['MIN_LIVABILITY_SCORE'];
            }
            if (isset ($localitySafetyAndLivabilityData[$document['CITY_ID']]['MAX_LIVABILITY_SCORE'])) {
            	$document['LOCALITY_MAX_LIVABILITY_SCORE'] = $localitySafetyAndLivabilityData[$document['CITY_ID']]['MAX_LIVABILITY_SCORE'];
            }
            
            if (! empty ( $has3DImages [$document ['PROJECT_ID']] ) && $has3DImages [$document ['PROJECT_ID']]) {
            	$document ['HAS_3D_IMAGES'] = $has3DImages [$document ['PROJECT_ID']];
            }

            if (! empty ($panoramaViewPath[$document['TYPE_ID']]) && isset ($panoramaViewPath[$document['TYPE_ID']]['ATTRIBUTE_VALUE'])) {
            	$document ['PANORAMA_VIEW_PATH'] = $panoramaViewPath[$document['TYPE_ID']]['ATTRIBUTE_VALUE'];
            }
            
            if (empty ($document['BUILDER_SCORE'])){
		      unset($document['BUILDER_SCORE']);
            }
            else{
                $document['BUILDER_SCORE'] = scaleBuilderScore($document['BUILDER_SCORE'], $maxActualBuliderScore);
            }
           
            if (isset($projectsWithPrimaryExpandedListings[$document['PROJECT_ID']])){
                $document['HAS_PRIMARY_EXPANDED_LISTING_NEW'] = 1;
            }

            if(isset($projectWithSource[$document['PROJECT_ID']]['SOURCE_ID'])){
                $document['PROJECT_SOURCE_ID'] = $projectWithSource[$document['PROJECT_ID']]['SOURCE_ID'];
            }
            if(isset($projectWithSource[$document['PROJECT_ID']]['SOURCE_DOMAIN'])){
                $document['PROJECT_SOURCE_DOMAIN'] = $projectWithSource[$document['PROJECT_ID']]['SOURCE_DOMAIN'];
            }
            if(isset($propertyWithSource[$document['TYPE_ID']]['SOURCE_ID'])){
                $document['PROPERTY_SOURCE_ID'] = $propertyWithSource[$document['TYPE_ID']]['SOURCE_ID'];
            }
            if (isset($propertyWithSource[$document['TYPE_ID']]['SOURCE_DOMAIN'])){
                $document['PROPERTY_SOURCE_DOMAIN'] = $propertyWithSource[$document['TYPE_ID']]['SOURCE_DOMAIN'];
            }

            array_push($documents, $document);
        }
    }
    
    else {
    		$logger->error("Error while fetching Property Data using query : \n ". $sql."\n");
    		$logger->error("Mysql error : \n". mysql_error());
    		die();
    }
    $logger->info("Property Documents retrived.");
    // As Mysql unbuffered does not allow mysql_query to be executed untill all rows of
    // mysql_unbuffered_query has been fetched.
    $len = count($documents);
    for($i=0; $i<$len; $i++)
    {
        $document = &$documents[$i];
	$size = isset($documents[$i]['SIZE'])?$documents[$i]['SIZE']:(isset($document['CARPET_AREA']) ? $document['CARPET_AREA'] : 0);
        $document['PROPERTY_URL'] = getPropertyUrl($documents[$i]['TYPE_ID'], "", $documents[$i]['PROJECT_NAME'],
                        $documents[$i]['BUILDER_NAME'], $documents[$i]['CITY'],
                        $documents[$i]['LOCALITY'], $documents[$i]['BEDROOMS'], $documents[$i]['UNIT_TYPE'], 
                        $size, $documents[$i]['BATHROOMS'] );
        $localityOverviewUrl = url_lib_locality_url($document);
        $suburbOverviewUrl = url_lib_suburb_url($document);
        $cityOverviewUrl = url_lib_city_url($document);
        unset($document['CITY_URL']);

        if( isset($cityOverviewUrl) ){
            $document['CITY_OVERVIEW_URL'] = $cityOverviewUrl;

        }
        if( isset($suburbOverviewUrl) ){
            $document['SUBURB_OVERVIEW_URL'] = $suburbOverviewUrl;

        }
        if( isset($localityOverviewUrl) )
            $document['LOCALITY_OVERVIEW_URL'] = $localityOverviewUrl;
    }

    // XXX - Due to pagination need to index solr right from here
    handleDocumentsToSolr(array(array(), $documents), $solr);
    return array($len, $solrDeleteProperties);
}

function getPropertiesFromSolr()
{
    global $solr, $logger, $paramProjectIds, $paramPropertyIds;
    try{

    	/* Making solr query elements */
    	$query = "DOCUMENT_TYPE:PROPERTY";
        $fq = getSolrFilterQuery($paramProjectIds, $paramPropertyIds);
        $solrQueryParams = array('fq' => $fq,
        		'fl' => "id, TYPE_ID, PROJECT_ID",
        		'wt' =>  "json",
        		'sort' =>  "TYPE_ID asc, id asc");
        
        /* Fetching count to check in the end. */
        $totalDocsPresent = getDocumentCountFromSolrWithRetries($solr, $query, array('fq' => $fq, 'wt' =>  "json"), GLOBAL_SOLR_GET_RETRY_COUNT);
        
        /* Fetching all property documents using cursor and retries*/
        $documentList  = getAllDocumentsFromSolrWithCursorAndRetries( $solr, $query, PROPERTY_FETCH_ROWS, $solrQueryParams, GLOBAL_SOLR_GET_RETRY_COUNT );
        $totalDocsFetched = count($documentList);
        
        /* Processing property documents. */
        $properties = array();
        $distinctProjectIds = array();
        foreach($documentList as $doc) {
        	$properties[$doc->id] = 1;
        	$distinctProjectIds[$doc->PROJECT_ID] = 1;
        }
        if( !empty($paramPropertyIds) ) {
        	$paramProjectIds .= ",".implode(",", array_keys($distinctProjectIds));
        	$paramProjectIds = trim($paramProjectIds, ",");
        }
        
    }catch(Exception $e){
    	$errorMsg = "Error while fetching properties from solr : " . getExceptionLogMessage($e);
        $logger->error($errorMsg);
        trigger_error($errorMsg, E_USER_ERROR);
        die();
    }
    
    validatePropertyCount($totalDocsPresent, $totalDocsFetched);
    return $properties;
}

function getSolrFilterQuery($paramProjectIds, $paramPropertyIds){
	$fq = "";
	if( !empty($paramProjectIds) ) {
		$fq = "PROJECT_ID:(".str_replace(",", " OR ", $paramProjectIds)." ) ";
	}
	if( !empty($paramPropertyIds) ) {
		$fq .= "OR TYPE_ID:(".str_replace(",", " OR ", $paramPropertyIds)." )";
	}
	$fq = ltrim($fq, "OR");
	return $fq;
}


function validatePropertyCount($totalDocsPresent, $totalDocsFetched){
	global $logger;
	if($totalDocsPresent != $totalDocsFetched){
		$errorMsg = "Total count of properties present and fetched are not equal: totalDocsPresent: $totalDocsPresent , totalDocsFetched: $totalDocsFetched";
		$logger->error($errorMsg);
		trigger_error($errorMsg, E_USER_WARNING);
	}	
}

// Deprecated. Not Needed
function getCMSProperties()
{
   global $logger, $paramProjectIds, $projCmsActiveCondition, $version, $solrDB;
   $logger->info("Fetching the CMS Properties.");
    global $logger;
    
    $projectCondition = "";
    if( !empty($paramProjectIds) )
        $projectCondition = " AND rpo.PROJECT_ID IN ($paramProjectIds) ";

    $sql = <<<QRY
        SELECT OPTIONS_ID AS TYPE_ID, rpo.PROJECT_ID, SERVANT_ROOM, POOJA_ROOM, STUDY_ROOM FROM cms.resi_project_options rpo JOIN cms.resi_project rp
            ON (rpo.PROJECT_ID=rp.PROJECT_ID) WHERE rp.STATUS IN ({$projCmsActiveCondition}) $projectCondition AND rp.VERSION = $version 
QRY;
    $rs = mysql_unbuffered_query($sql, $solrDB);
    if ($rs == FALSE) {
    	$logger->error("Error while Executing CMS property table query : \n ". $sql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }    
    $cmsProperties = array();
    while( ($row = mysql_fetch_assoc($rs) ) != FALSE)
    {
        $typeID = $row['TYPE_ID'];
        unset($row['TYPE_ID']);
        $cmsProperties[$typeID] = $row;
    }

    return $cmsProperties;
}

// Deprecated , Don not use this function
function getCouponsDataForProperties(){
    global $projCmsActiveCondition, $version, $logger, $solrDB;

    $sql = <<<SQL
        SELECT cc.id, cc.option_id as propertyId, cc.coupon_price as couponPrice, cc.discount, 
            cc.purchase_expiry_at as purchaseExpiryAt, cc.total_inventory as totalInventory,
            cc.redeem_expiry_hours as redeemExpiryHours, cc.created_at as createdAt, cc.updated_at as updatedAt,
            cc.product_type as productType, rpo.project_id as projectId, cc.inventory_left as inventoryLeft
            FROM cms.coupon_catalogue cc JOIN cms.resi_project_options rpo ON (cc.option_id = rpo.options_id) 
                JOIN cms.resi_project rp ON (rp.project_id = rpo.project_id) WHERE rp.STATUS IN ({$projCmsActiveCondition}) 
                    AND rp.version = $version AND cc.purchase_expiry_at > now() AND cc.inventory_left > 0 AND rp.RESIDENTIAL_FLAG = 'Residential'
                    AND cc.product_type = 'Non4DSale'
SQL;
    $result = mysql_query($sql, $solrDB);
    if ($result == FALSE) {
    	$logger->error("Error while fetching Coupons data using query : \n ". $sql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }
    $couponData = array();
    $couponProjectData = array();
    while( $row = mysql_fetch_assoc($result))
    {
        $row['purchaseExpiryAt'] = getDateInSolrFormat(strtotime($row['purchaseExpiryAt']));
        $row['createdAt'] = getDateInSolrFormat(strtotime($row['createdAt']));
        $row['updatedAt'] = getDateInSolrFormat(strtotime($row['updatedAt']));
        $couponData[$row['propertyId']] = $row;
        $couponProjectData[$row['projectId']] = $row;
    }
    
    return array($couponData, $couponProjectData);
}

function getPropertySoldOutStatus() {
	global $logger, $solrDB;
	$sql = <<<SQL
        select l.option_id, group_concat(distinct(l.booking_status_id))
from cms.listings l 
join (
      select l.option_id, if(group_concat(distinct(rpp.phase_type)) = 'Logical', _latin1'Logical', _latin1'Actual') as phase_type 
	  from cms.listings l 
      join cms.resi_project_phase rpp on (rpp.phase_id = l.phase_id)
      group by l.option_id) rppd on (rppd.option_id = l.option_id)
join cms.resi_project_phase rpp on (rpp.phase_id = l.phase_id and rpp.phase_type = rppd.phase_type)
where l.status = 'Active' 
and l.listing_category = 'Primary'
group by l.option_id
having group_concat(distinct(l.booking_status_id)) = '2' 
SQL;
	$result = mysql_query($sql, $solrDB);
	if ($result == FALSE) {
		$logger->error("Error while fetching property sold out status using query : \n ". $sql."\n");
		$logger->error("Mysql error : \n". mysql_error());
		die();
	}
	
	$soldOutStatus = array();
	while ( $row = mysql_fetch_assoc($result)) {
		$soldOutStatus[$row['option_id']] = true;
	}
	return $soldOutStatus;
}

function getProjectIdFromProperty(){
    global $paramPropertyIds, $paramProjectIds, $logger;
    
    if(empty($paramPropertyIds)){
        return;
    }
    $sql = <<<SQL
        SELECT project_id, options_id from cms.resi_project_options where options_id in ($paramPropertyIds) GROUP BY project_id
SQL;
    $rs = mysql_query($sql);
    if ($rs == FALSE) {
    	$logger->error("Error while fetching projectId from property using query : \n ". $sql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }
    while( ($row=mysql_fetch_assoc($rs)) != FALSE){
        $paramProjectIds .= ",".$row['project_id'];
    }
    $paramProjectIds = ltrim($paramProjectIds);
}

function getPropertyUrl($propertyId, $type, $projectName, $builderName, $cityName, $localityName, $beds, $unitType, $size, $bathrooms) {
	if (empty($beds)) {
		$beds = '';
	}
	$unitType = strtolower($unitType);
	$url = hyphonate("$cityName/$builderName-$projectName-$localityName-$propertyId/");	
	$size = floor($size);	
	switch($unitType){
		case "apartment":
		case "villa":
			if(!empty($beds)){
				$url .= $beds."bhk-";
			}
			if(!empty($bathrooms)){
				$url .= $bathrooms."t-";
			}
			break;
	}
	if(!empty($size)&&$size>0){
		$url .= $size."-sqft-";
	}
	$url .= $unitType;
	return $url;
}

?>
