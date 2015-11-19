<?php
include_once 'locationFunctions.php';
include_once 'propertyFunctions.php';
error_reporting(E_ALL);
function getProjectDocumentsFromDB()
{
    global $projectMinMaxPrice;
    global $logger, $solrDB;
    global $resaleProjectPrices, $resalePropertyPrices;
    global $projectEnquiryCountArray;
    global $projectPriceRise;
    global $projectAndBuilderImages;
    global $projectDiscussionCount;
    global $projectPriceData;
    global $projectLastUpdatedTime;
    global $projectOffers;
    global $paramProjectIds;
    global $projectActiveConditionArray;
    global $version;
    global $solrCollectionType;
    $projectSafetyAndLivabilityRank = array();
    global $imageTypeCount;
    global $has3DImages;
    $projectCondition = "";
    global $projectSafetyAndLivabilityData;
    global $localitySafetyAndLivabilityData;
    global $projDominantUnitTypes;
    global $projectResaleEnquiry;
    global $projectsCreatedDates;
    global $projectDelay;
    global $projectVerifiedResaleLst;
    global $projectsWithPrimaryExpandedListings;
    global $projectWithSource;

    if (! empty($paramProjectIds))
        $projectCondition = " AND rp.PROJECT_ID IN ($paramProjectIds) ";

    $projectCondition = appendConditionForB2b($projectCondition);

    loadProjectData();
    if (empty($projectEnquiryCountArray)) {
        list($projectEnquiryCountArray, $projectLastEnquiredDate) = loadEnquiryCount('PROJECT');
    }
    $projectTowerDetails = getProjectTowerDetails();
    $projectFlatSupply = getProjectFlatSupply();
    $projectVideosCount = getProjectVideoCount();
    $imageTypeCount = getImageTypeCount();
    $deleteDocuments = array();
    $projectsOnProjectIds = getProjectsOnProjectIds($paramProjectIds);
    $deleteDocuments = $projectsOnProjectIds['deleteIds'];
    $IMG_SERVER = IMG_SERVER;
    $projectImagesCount = getProjectImagesCount();
    //$resaleProjects = getResaleProjects();
    $avgLoadingPercentage = getAvgLoadingPercentage();
    $projectReportMap = getProjectsHavingCatchementReport();  
    list($couponData, $couponProjectData) = getCouponsDataForProperties();
    $maxActualBuliderScore = getMaxBuilderScoreFromDb();

    $sql = "SELECT CONCAT('PROJECT-', rp.PROJECT_ID) AS id, 'PROJECT' AS DOCUMENT_TYPE, rp.PROJECT_ID, rp.PROJECT_NAME,
					CONCAT(rb.BUILDER_NAME, ' ', rp.PROJECT_NAME) AS NEWS_TAG,
                    rb.BUILDER_NAME, l.LABEL AS LOCALITY, c.LABEL AS CITY,
                    rp.PROJECT_ADDRESS, s.LABEL AS SUBURB, rp.PROJECT_SMALL_IMAGE, rp.PROJECT_URL,
                    rp.LATITUDE, rp.LONGITUDE,rp.BUILDER_ID, rp.LOCALITY_ID, s.SUBURB_ID, c.CITY_ID, rp.PROMISED_COMPLETION_DATE AS COMPLETION_DATE,
                    rp.DISPLAY_ORDER,rp.DISPLAY_ORDER_LOCALITY, rp.DISPLAY_ORDER_SUBURB,
                    rp.LAUNCH_DATE AS LAUNCH_DATE, rp.D_LAST_PRICE_UPDATION_DATE AS SUBMITTED_DATE, psm.display_name as PROJECT_STATUS,
                    rp.PROJECT_DESCRIPTION, rp.PROJECT_SIZE, rp.FORCE_RESALE,
                    rp.PROMISED_COMPLETION_DATE, rp.D_AVAILABILITY AS AVAILABILITY,  rb.DISPLAY_ORDER AS BUILDER_DISPLAY_ORDER,
                    l.PRIORITY AS LOCALITY_PRIORITY, pt.TYPE_NAME AS PROJECT_TYPE,s.PRIORITY AS SUBURB_PRIORITY,
                    GROUP_CONCAT(DISTINCT(CONCAT(rpo.OPTION_TYPE, '-', rpo.BEDROOMS))) AS ALL_BEDROOMS, rp.PRE_LAUNCH_DATE,
                     CONCAT(LOWER(l.LABEL), ':', IF(l.PRIORITY IS NULL, 0, l.PRIORITY) ) AS LOCALITY_LABEL_PRIORITY,
                     CONCAT(LOWER(s.LABEL), ':', IF(s.PRIORITY IS NULL, 0, s.PRIORITY) ) AS SUBURB_LABEL_PRIORITY,
                    'Apartment' AS UNIT_TYPE, l.LATITUDE AS LOCALITY_LATITUDE, l.LONGITUDE AS  LOCALITY_LONGITUDE,
                    CONCAT(LOWER(l.LABEL), ':', l.LOCALITY_ID, ':', IF(l.PRIORITY IS NULL, 0, l.PRIORITY) ) AS LOCALITY_LABEL_ID_PRIORITY,
                    CONCAT(LOWER(rb.BUILDER_NAME), ':', rb.BUILDER_ID, ':', IF(rb.DISPLAY_ORDER IS NULL, 0, rb.DISPLAY_ORDER) ) AS BUILDER_LABEL_ID_PRIORITY,
                    CONCAT(LOWER(s.LABEL), ':', s.SUBURB_ID, ':', IF(s.PRIORITY IS NULL, 0, s.PRIORITY) ) AS SUBURB_LABEL_ID_PRIORITY,
                    rp.APPLICATION_FORM AS PAYMENT_PLAN_URL, IF(rp.NO_OF_TOWERS <= 0, NULL, rp.NO_OF_TOWERS) AS NO_OF_TOWERS,
                    IF(rp.TOWNSHIP_ID is NULL,0,1) AS HAS_TOWNSHIP, l.URL AS LOCALITY_URL, rp.STATUS, rp.REACHABILITY_INFO,
                    rp.SAFETY_SCORE AS PROJECT_SAFETY_SCORE, rb.listed AS IS_BUILDER_LISTED, rp.LIVABILITY_SCORE AS PROJECT_LIVABILITY_SCORE,  ifnull(svt.user_popularity_index, 0) AS PROJECT_POPULARITY_INDEX,
                    rp.PROJECT_LOCALITY_SCORE, rp.PROJECT_SOCIETY_SCORE, ppi.primary_index PRIMARY_INDEX, ppi.resale_index RESALE_INDEX, rp.RESIDENTIAL_FLAG, GROUP_CONCAT(DISTINCT rpo.OPTION_TYPE) AS UNIT_TYPES,
                    min(IF(rpo.SIZE > 0, rpo.SIZE, rpo.CARPET_AREA)) AS MINSIZE,  max(IF(rpo.SIZE > 0, rpo.SIZE, rpo.CARPET_AREA)) as MAXSIZE, c.URL as CITY_URL,
                    GROUP_CONCAT(DISTINCT rpo.BEDROOMS) AS BEDROOMS_LIST,
                    GROUP_CONCAT(CONCAT(rpo.OPTIONS_ID, '-', rpo.OPTION_TYPE, '-', IF(rpo.SIZE > 0, rpo.SIZE, rpo.CARPET_AREA))) AS UNIT_TYPE_SIZE, rb.BUILDER_SCORE as BUILDER_SCORE
                    FROM cms.resi_project rp
                    LEFT JOIN cms.project_primary_index ppi
                    ON rp.PROJECT_ID = ppi.id
                    LEFT JOIN cms.resi_project_type pt
                    ON (rp.PROJECT_TYPE_ID = pt.PROJECT_TYPE_ID)
                    LEFT JOIN analytics.object_popularity_table svt
                    ON (rp.PROJECT_ID = svt.object_id AND svt.object_type_id = 1)
                    JOIN cms.resi_project_options rpo
                    ON (rp.PROJECT_ID = rpo.PROJECT_ID AND rpo.OPTION_CATEGORY = 'Actual')
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
                    WHERE rp.VERSION = $version $projectCondition
                    GROUP BY rp.PROJECT_ID, rp.PROJECT_NAME, rb.BUILDER_NAME, l.LABEL,
                    c.LABEL, rp.PROJECT_ADDRESS, s.LABEL, rp.PROJECT_SMALL_IMAGE, rp.PROJECT_URL,
                    rp.LATITUDE, rp.LONGITUDE, rp.BUILDER_ID, rp.LOCALITY_ID, s.SUBURB_ID, c.CITY_ID";

    $documents = array();
    $result = mysql_unbuffered_query($sql, $solrDB);
    if ($result) {
        while ($document = mysql_fetch_assoc($result)) {
            if ((! (in_array($document['STATUS'], $projectActiveConditionArray))) || ($document['RESIDENTIAL_FLAG'] != 'Residential')) {
                $deleteDocuments[] = $document['id'];
                unset($document['RESIDENTIAL_FLAG']);
                continue;
            }
            $projectId = $document['PROJECT_ID'];

            if (isset($projectsCreatedDates[$projectId])) {
                $document['CREATED_LIVE_DATE'] =  $projectsCreatedDates[$projectId];
                checkValidAndSetDate($document, "CREATED_LIVE_DATE");
            }

            if ($document['DISPLAY_ORDER'] != 999)
            {
                $document['IS_HOT_PROJECT'] = true;
            }

            $document['IS_SOLD_OUT'] = getSoldOutStatus($document);
            if ($document['IS_SOLD_OUT']) {
                unset($projectPriceData[$document['PROJECT_ID']]);
                unset($projectPriceRise[$document['PROJECT_ID']]);
            }

            if (isset($projectVideosCount[$projectId])) {
                $document['PROJECT_VIDEOS_COUNT'] = $projectVideosCount[$projectId];
            }
            $document['LOCALITY_OR_SUBURB'] = array(
                $document['LOCALITY'],
                $document['SUBURB']
            );

            $document['LOCALITY_OR_SUBURB_ID'] = array(
                $document['LOCALITY_ID'],
                $document['SUBURB_ID']
            );

            $latitude = $document['LATITUDE'];
            $longitude = $document['LONGITUDE'];

            if (empty($document['PRIMARY_INDEX'])) {
                $document['PRIMARY_INDEX'] = 0;
            }

            if (empty($document['RESALE_INDEX'])) {
                $document['RESALE_INDEX'] = 0;
            }

            $document['PROJECT_ADDRESS'] = $document['LOCALITY'] . ", " . $document['CITY'];
            if (isset($projectAndBuilderImages["builder"][$document['BUILDER_ID']]['PATH'])) {
                $document['BUILDER_LOGO_IMAGE'] = $projectAndBuilderImages["builder"][$document['BUILDER_ID']]['PATH'];
            }
            if (isset($projectAndBuilderImages["project"][$projectId]['PATH'])) {
                $document['PROJECT_MAIN_IMAGE'] = $projectAndBuilderImages["project"][$projectId]['PATH'];
            } else {
                $document['PROJECT_MAIN_IMAGE'] = getProjectMainImageRandomly($document['PROJECT_ID']);
            }

            if (isset($projectDiscussionCount[$projectId])) {
                $document['NUMBER_OF_PROJECT_DISCUSSION'] = $projectDiscussionCount[$projectId];
            }
            if (isset($projectLastUpdatedTime[$projectId])) {
                $document['PROJECT_LAST_UPDATED_TIME'] = $projectLastUpdatedTime[$projectId]["ATTRIBUTE_VALUE"];
                checkValidAndSetDate($document, "PROJECT_LAST_UPDATED_TIME");
            }
            if (isset($projectOffers[$projectId])) {
                $document['PROJECT_OFFER'] = $projectOffers[$projectId]['OFFER'];
                $document['OFFER'] = $projectOffers[$projectId]['OFFER_ARRAY'][0]['offer'];
                $document['OFFER_HEADING'] = $projectOffers[$projectId]['OFFER_ARRAY'][0]['offerHeading'];
                $document['OFFER_DESC'] = $projectOffers[$projectId]['OFFER_ARRAY'][0]['offerDesc'];
            }

            if (isset($projectFlatSupply[$projectId])) {
                $document['TOTAL_UNITS'] = $document['PROJECT_SUPPLY'] = $projectFlatSupply[$projectId]['TOTAL_UNITS'];
            }

            if (! empty($imageTypeCount) && ! empty($imageTypeCount[$projectId])) {
                $document['IMAGE_TYPE_COUNT'] = json_encode($imageTypeCount[$projectId]);
            }

            if (isset($projectPriceData[$document['PROJECT_ID']])) {
                $dataHash = $projectPriceData[$document['PROJECT_ID']];
                if (! empty($dataHash['minPrice'])) {
                    $document['MIN_BUDGET'] = $dataHash['minPrice'];
                }

                if (! empty($dataHash['maxPrice'])) {
                    $document['MAX_BUDGET'] = $dataHash['maxPrice'];
                }


                if (! empty($dataHash['minPricePerUnitArea'])) {
                    $document['MIN_PRICE_PER_UNIT_AREA'] = $dataHash['minPricePerUnitArea'];
                }

                if (! empty($dataHash['maxPricePerUnitArea'])) {
                    $document['MAX_PRICE_PER_UNIT_AREA'] = $dataHash['maxPricePerUnitArea'];
                }
            }

            $document['UNIT_TYPE_SIZE'] = strtolower($document['UNIT_TYPE_SIZE']);
            $unitTypeSizeArr = explode(",", $document['UNIT_TYPE_SIZE']);
            $resalePrice = 0;
            $optionId = 0;
            $optionType = 0;
            $optionSize = 0;

            for ($unitI = 0; $unitI < count($unitTypeSizeArr); $unitI ++) {
                $split = explode("-", $unitTypeSizeArr[$unitI]);
                if (count($split) == 3) {
                    $optionId = $split[0];
                    $optionType = $split[1];
                    $optionSize = $split[2];

                    if (isset($resalePropertyPrices[$optionId]) && ! empty($optionSize)) {
                        $resalePrice = $resalePropertyPrices[$optionId] * $optionSize;
                    } else
                        if (isset($resaleProjectPrices[$document['PROJECT_ID']]) && isset($resaleProjectPrices[$document['PROJECT_ID']][$optionType]) && ! empty($optionSize)) {
                            $resalePrice = $resaleProjectPrices[$document['PROJECT_ID']][$optionType] * $optionSize;
                        }

                    if (isset($resalePrice) && $resalePrice != 0) {
                        if (empty($document['MIN_RESALE_PRICE'])) {
                            $document['MIN_RESALE_PRICE'] = $document['MAX_RESALE_PRICE'] = $resalePrice;
                        } else {
                           $document['MIN_RESALE_PRICE'] = min($document['MIN_RESALE_PRICE'], $resalePrice);
                           $document['MAX_RESALE_PRICE'] = max($document['MAX_RESALE_PRICE'], $resalePrice);
                        }
                    }
                }
            }

            $document['PROJECT_MIN_RESALE_OR_PRIMARY_PRICE'] = $resalePrice;
            $document['PROJECT_MAX_RESALE_OR_PRIMARY_PRICE'] = $resalePrice;

            if (isset($document['MIN_RESALE_PRICE']) || isset($document['MIN_BUDGET'])){
                $document['PROJECT_MIN_RESALE_OR_PRIMARY_PRICE'] = isset($document['MIN_BUDGET']) ? $document['MIN_BUDGET']: $document['MIN_RESALE_PRICE'];
                if (isset($document['MIN_BUDGET']) && isset($document['MIN_RESALE_PRICE'])) {
                   $document['PROJECT_MIN_RESALE_OR_PRIMARY_PRICE'] = min($document['MIN_BUDGET'] , $document['MIN_RESALE_PRICE']);
                }
            }
            if(isset($document['MAX_RESALE_PRICE']) || isset($document['MAX_BUDGET'])) {
            	$document['PROJECT_MAX_RESALE_OR_PRIMARY_PRICE'] = isset($document['MAX_BUDGET']) ? $document['MAX_BUDGET']: $document['MAX_RESALE_PRICE'];
                if(isset($document['MAX_BUDGET']) && isset($document['MAX_RESALE_PRICE'])) {
               $document['PROJECT_MAX_RESALE_OR_PRIMARY_PRICE'] = max($document['MAX_BUDGET'] , $document['MAX_RESALE_PRICE']);
                }
            }

            if(isset($projectLastEnquiredDate[$document['PROJECT_ID']])){
            	$document['PROJECT_LAST_ENQUIRED_DATE'] = getDateInSolrFormat(strtotime($projectLastEnquiredDate[$document['PROJECT_ID']]));
            	if(empty($document['PROJECT_LAST_ENQUIRED_DATE'])){
            		unset($document['PROJECT_LAST_ENQUIRED_DATE']);
            	}
            }
            unset($document['UNIT_TYPE_SIZE']);
            if (isset($projectAndBuilderImages['builder'][$document['BUILDER_ID']]['ALTTEXT'])) {
                $document['BUILDER_IMAGE_ALTTEXT'] = $projectAndBuilderImages['builder'][$document['BUILDER_ID']]['ALTTEXT'];
            }

            if (isset($projectAndBuilderImages['builder'][$document['BUILDER_ID']]['TITLE'])) {
                $document['BUILDER_IMAGE_TITLE'] = $projectAndBuilderImages['builder'][$document['BUILDER_ID']]['TITLE'];
            }

            if (isset($projectAndBuilderImages['project'][$projectId]['ALTTEXT'])) {
                $document['PROJECT_IMAGE_ALTTEXT'] = $projectAndBuilderImages['project'][$projectId]['ALTTEXT'];
            }

            if (isset($projectAndBuilderImages['project'][$projectId]['TITLE'])) {
                $document['PROJECT_IMAGE_TITLE'] = $projectAndBuilderImages['project'][$projectId]['TITLE'];
            }



            if (isValidGeo($latitude, $longitude)) {
                $document['GEO'] = "$latitude, $longitude";
                $document['HAS_GEO'] = 1;
                $document['PROCESSED_LATITUDE'] = $document['LATITUDE'];
                $document['PROCESSED_LONGITUDE'] = $document['LONGITUDE'];
            } else {
                if (isValidGeo($document['LOCALITY_LATITUDE'], $document['LOCALITY_LONGITUDE'])) {
                    $document['PROCESSED_LATITUDE'] = $document['LOCALITY_LATITUDE'];
                    $document['PROCESSED_LONGITUDE'] = $document['LOCALITY_LONGITUDE'];
                }

                unset($document['LATITUDE']);
                unset($document['LONGITUDE']);
                $document['HAS_GEO'] = 0;
            }

            if (isset($projectEnquiryCountArray[$document['PROJECT_ID']])) {
                $document['PROJECT_ENQUIRY_COUNT'] = $projectEnquiryCountArray[$document['PROJECT_ID']];
            }
            $document['PROJECT_VIEW_COUNT'] = 0;
            // formatting date to solr date format

            // Setting properties
            $document['UNIT_TYPE'] = 'Apartment';
            $priorities = computePropertyPriority($document);
            $document['PROJECT_PRIORITY'] = $priorities['nonEditorialPriority'];
            $document['DISPLAY_ORDER'] = $priorities['editorialPriority'];
            $document['PROJECT_PRIORITY_ATTRIBUTES'] = projectPriorityAttributes($document);

            $document['PROJECT_DOMINANT_UNIT_TYPE'] = DEFAULT_DOMINANT_TYPE;
            $unitTypeString = $document['UNIT_TYPES'];
            if (isset($projectPriceRise[$document['PROJECT_ID']])) {
                $projectDoc = $projectPriceRise[$document['PROJECT_ID']];
                $document['PROJECT_DOMINANT_UNIT_TYPE'] = empty($projectDoc['unit_type']) ? DEFAULT_DOMINANT_TYPE : $projectDoc['unit_type'];

                if (! empty($projectDoc['average_price_per_unit_area'])) {
                    $document['PROJECT_AVG_PRICE_PER_UNIT_AREA'] = $projectDoc['average_price_per_unit_area'];
                }

                if ($document['PROJECT_STATUS'] != 'On Hold' && ! empty($projectDoc['RISE_PERCENT'])) {
                    $document['PROJECT_PRICE_RISE'] = $projectDoc['RISE_PERCENT'];
                    $document['PROJECT_PRICE_RISE_TIME'] = $projectDoc['RISE_PERIOD_MONTHS'];
                    $document['PROJECT_PRICE_APPRECIATION_RATE'] = (float) $projectDoc['RISE_PERCENT'] / $projectDoc['RISE_PERIOD_MONTHS'];
                }

                if (! empty($projectDoc['PROJECT_PRICE_RISE_6MONTHS'])) {
                    $document['PROJECT_PRICE_RISE_6MONTHS'] = $projectDoc['PROJECT_PRICE_RISE_6MONTHS'];
                }
            } else {
                if (! empty($document['UNIT_TYPES'])) {
                    $unitTypes = explode(',', $document['UNIT_TYPES']);
                    $document['UNIT_TYPES'] = $unitTypes;
                    $document['PROJECT_DOMINANT_UNIT_TYPE'] = $unitTypes[0];
                    $projDominantUnitTypes[$document['PROJECT_ID']] = $unitTypes[0];
               		$projectResalePriceUnitTypeLowerCase = strtolower($unitTypes[0]);
                }
            }

            // RESALE_PRICE_PER_UNIT_AREA_PLOT , RESALE_PRICE_PER_UNIT_AREA_VILLA , RESALE_PRICE_PER_UNIT_AREA_APARTMENT
            if (!empty($document['UNIT_TYPES'])) {
            	$unitTypes = explode(",", $unitTypeString);
                foreach($unitTypes as $unitTypeValue){
               		$unitValueLower = strtolower($unitTypeValue);
               		if(!empty($resaleProjectPrices[$document['PROJECT_ID']][$unitValueLower])){
               			$document["RESALE_PRICE_PER_UNIT_AREA_".strtoupper($unitTypeValue)] = $resaleProjectPrices[$document['PROJECT_ID']][$unitValueLower];
            		}
            	}
        	}
        	
            if (isset($resaleProjectPrices[$document['PROJECT_ID']]) && 
            	isset($resaleProjectPrices[$document['PROJECT_ID']][$projectResalePriceUnitTypeLowerCase])) {
            	$document['RESALE_PRICE_PER_UNIT_AREA'] = $resaleProjectPrices[$document['PROJECT_ID']][$projectResalePriceUnitTypeLowerCase];
            }
            if (! empty($document['BEDROOMS_LIST'])) {
                $document['BEDROOMS_LIST'] = explode(",", $document['BEDROOMS_LIST']);
                sort($document['BEDROOMS_LIST']);
            } else {
                unset($document['BEDROOMS_LIST']);
            }
            if ($document['PROJECT_STATUS'] == 'On Hold') {
                $document['PROMISED_COMPLETION_DATE'] = null;
            }
            if (isset($projectsWithPrimaryExpandedListings[$document['PROJECT_ID']])){
                $document['HAS_PRIMARY_EXPANDED_LISTING_NEW'] = 1;
    	    }
            if(!empty($couponProjectData[$document['PROJECT_ID']]) && $couponProjectData[$document['PROJECT_ID']]['productType']=='Non4DSale'){
                $document['HAS_PRIMARY_EXPANDED_LISTING_NEW'] = 2;
            }

            if(isset($projectVerifiedResaleLst[$document['PROJECT_ID']])) {
                $document['PROJECT_RESALE_LISTING_COUNT'] = $projectVerifiedResaleLst[$document['PROJECT_ID']];
            }

            $document['IS_PRIMARY'] = getPrimaryStatus($document);
            checkValidAndSetDate($document, 'PROMISED_COMPLETION_DATE');
            checkValidAndSetDate($document, 'SUBMITTED_DATE');
            checkValidAndSetDate($document, 'LAUNCH_DATE');
            checkValidAndSetDate($document, 'PRE_LAUNCH_DATE');

            // valid launch date. IT should be called after checkValidAndSetDate
            // function call to launch date and pre launch date.
            setValidLaunchDate($document);

            // setting project resale status
            $document['IS_RESALE'] = getReSaleStatus($document);

            // formatted price
            if (isset($document['MAX_BUDGET'])) {
                $document['MAXPRICE'] = makePriceUserReadable($document['MAX_BUDGET']);
                $projectMinMaxPrice[$document['PROJECT_ID']]['MAXPRICE'] = $document['MAXPRICE'];
            }
            if (isset($document['MIN_BUDGET'])) {
                $document['MINPRICE'] = makePriceUserReadable($document['MIN_BUDGET']);
                $projectMinMaxPrice[$document['PROJECT_ID']]['MINPRICE'] = $document['MINPRICE'];
            }
            $projectMinMaxPrice[$document['PROJECT_ID']]['ALL_BEDROOMS'] = createAllBedroomsString($document['ALL_BEDROOMS']);
            $document['ALL_BEDROOMS'] = $projectMinMaxPrice[$document['PROJECT_ID']]['ALL_BEDROOMS'];

            if (! isset($document['PROJECT_SIZE'])) {
                $document['PROJECT_SIZE'] = "0";
            }
            if (! isset($document['PROJECT_DESCRIPTION'])) {
                $document['PROJECT_DESCRIPTION'] = "";
            }
            if (! isset($document['TOTAL_UNITS'])) {
                $document['TOTAL_UNITS'] = "0";
            }
            if (! isset($document['LOCALITY_URL'])) {
                unset($document['LOCALITY_URL']);
            }
            if (! isset($document['OFFER_HEADING'])) {
                unset($document['OFFER_HEADING']);
            }
            if (! isset($document['OFFER_DESC'])) {
                unset($document['OFFER_DESC']);
            }

            if ($document['COMPLETION_DATE']) {
                $document['COMPLETION_DATE'] = date("M Y", strtotime($document['COMPLETION_DATE']));
            }

            if (! isset($document['MINSIZE'])) {
                unset($document['MINSIZE']);
            }
            if (! isset($document['MAXSIZE'])) {
                unset($document['MAXSIZE']);
            }
            if (! isset($document['MIN_PRICE_PER_UNIT_AREA'])) {
                unset($document['MIN_PRICE_PER_UNIT_AREA']);
            }
            if (! isset($document['MAX_PRICE_PER_UNIT_AREA'])) {
                unset($document['MAX_PRICE_PER_UNIT_AREA']);
            }
            if (! isset($document['MIN_BUDGET'])) {
                unset($document['MIN_BUDGET']);
            }
            if (! isset($document['MAX_BUDGET'])) {
                unset($document['MAX_BUDGET']);
            }
            if (empty($document['PROJECT_MIN_RESALE_OR_PRIMARY_PRICE'])) {
               unset($document['PROJECT_MIN_RESALE_OR_PRIMARY_PRICE']);
            }
            if (empty($document['PROJECT_MAX_RESALE_OR_PRIMARY_PRICE'])) {
               unset($document['PROJECT_MAX_RESALE_OR_PRIMARY_PRICE']);
            }
            if (empty($document['PROJECT_STATUS'])) {
                unset($document['PROJECT_STATUS']);
                unset($document['VALID_LAUNCH_DATE']);
            }
            if (! isset($document['IS_RESALE']))
                unset($document['IS_RESALE']);
            if (! isset($document['AVAILABILITY']))
                unset($document['AVAILABILITY']);
            if (! isset($document['PAYMENT_PLAN_URL']))
                unset($document['PAYMENT_PLAN_URL']);
            if (empty($document['PROJECT_SAFETY_SCORE']))
                unset($document['PROJECT_SAFETY_SCORE']);
            if (empty($document['PROJECT_LIVABILITY_SCORE']))
                unset($document['PROJECT_LIVABILITY_SCORE']);

            if (empty($document['PROJECT_LOCALITY_SCORE']))
                unset($document['PROJECT_LOCALITY_SCORE']);
            if (empty($document['PROJECT_SOCIETY_SCORE']))
                unset($document['PROJECT_SOCIETY_SCORE']);
            if (empty($document['REACHABILITY_INFO']))
            	unset($document['REACHABILITY_INFO']);

            if(isset($document['NO_OF_TOWERS']) || isset($projectTowerDetails[$document['PROJECT_ID']])) {
                // if Tower details contain more no of towers , use that no
                if(isset($projectTowerDetails[$document['PROJECT_ID']])) {
                   $towersFrmTowerDtl = $projectTowerDetails[$document['PROJECT_ID']]['NO_OF_TOWERS'];

                   if(! isset($document['NO_OF_TOWERS']) || ($document['NO_OF_TOWERS'] <= $towersFrmTowerDtl) ) {
                      $document['NO_OF_TOWERS'] = $towersFrmTowerDtl;
                   }

                   if( isset($projectTowerDetails[$document['PROJECT_ID']]['AVG_FLATS_PER_FLOOR']))
                      $document['AVG_FLATS_PER_FLOOR'] = $projectTowerDetails[$document['PROJECT_ID']]['AVG_FLATS_PER_FLOOR'];
                }
            } else{
                unset($document['NO_OF_TOWERS']);
            }
            if(isset($projectWithSource[$document['PROJECT_ID']]['SOURCE_ID'])){
                $document['PROJECT_SOURCE_ID'] = $projectWithSource[$document['PROJECT_ID']]['SOURCE_ID'];
            }
            if(isset($projectWithSource[$document['PROJECT_ID']]['SOURCE_DOMAIN'])){
                $document['PROJECT_SOURCE_DOMAIN'] = $projectWithSource[$document['PROJECT_ID']]['SOURCE_DOMAIN'];
            }
            if(isset($projectReportMap[$document['PROJECT_ID']]))
                $document['HAS_PROJECT_INSIGHT_REPORT'] = true;
            else
                $document['HAS_PROJECT_INSIGHT_REPORT'] = false;

            if(isset($projectDelay[$document['PROJECT_ID']]))
                $document['PROJECT_DELAY'] = $projectDelay[$document['PROJECT_ID']];
            if(isset($avgLoadingPercentage[$document['PROJECT_ID']]))
                $document['AVG_LOADING_PERCENTAGE'] = $avgLoadingPercentage[$document['PROJECT_ID']];
            if(isset($projectVerifiedResaleLst[$document['PROJECT_ID']]))
                $document['PROJECT_RESALE_LISTING_COUNT'] = $projectVerifiedResaleLst[$document['PROJECT_ID']];

            if (isset($projectSafetyAndLivabilityData['SAFETY_RANK'][$document['LOCALITY_ID']][$document['PROJECT_ID']])) {
                $document['PROJECT_SAFETY_RANK'] = $projectSafetyAndLivabilityData['SAFETY_RANK'][$document['LOCALITY_ID']][$document['PROJECT_ID']];
            }
            if (isset($projectSafetyAndLivabilityData['LIVABILITY_RANK'][$document['LOCALITY_ID']][$document['PROJECT_ID']])) {
                $document['PROJECT_LIVABILITY_RANK'] = $projectSafetyAndLivabilityData['LIVABILITY_RANK'][$document['LOCALITY_ID']][$document['PROJECT_ID']];
            }
            if (isset($projectSafetyAndLivabilityData[$document['LOCALITY_ID']]['MIN_SAFETY_SCORE'])) {
                $document['PROJECT_MIN_SAFETY_SCORE'] = $projectSafetyAndLivabilityData[$document['LOCALITY_ID']]['MIN_SAFETY_SCORE'];
            }
            if (isset($projectSafetyAndLivabilityData[$document['LOCALITY_ID']]['MAX_SAFETY_SCORE'])) {
                $document['PROJECT_MAX_SAFETY_SCORE'] = $projectSafetyAndLivabilityData[$document['LOCALITY_ID']]['MAX_SAFETY_SCORE'];
            }
            if (isset($projectSafetyAndLivabilityData[$document['LOCALITY_ID']]['MIN_LIVABILITY_SCORE'])) {
                $document['PROJECT_MIN_LIVABILITY_SCORE'] = $projectSafetyAndLivabilityData[$document['LOCALITY_ID']]['MIN_LIVABILITY_SCORE'];
            }
            if (isset($projectSafetyAndLivabilityData[$document['LOCALITY_ID']]['MAX_LIVABILITY_SCORE'])) {
                $document['PROJECT_MAX_LIVABILITY_SCORE'] = $projectSafetyAndLivabilityData[$document['LOCALITY_ID']]['MAX_LIVABILITY_SCORE'];
            }

            if (isset($localitySafetyAndLivabilityData['SAFETY_RANK'][$document['CITY_ID']][$document['LOCALITY_ID']])) {
                $document['LOCALITY_SAFETY_RANK'] = $localitySafetyAndLivabilityData['SAFETY_RANK'][$document['CITY_ID']][$document['LOCALITY_ID']];
            }
            if (isset($localitySafetyAndLivabilityData['LIVABILITY_RANK'][$document['CITY_ID']][$document['LOCALITY_ID']])) {
                $document['LOCALITY_LIVABILITY_RANK'] = $localitySafetyAndLivabilityData['LIVABILITY_RANK'][$document['CITY_ID']][$document['LOCALITY_ID']];
            }
            if (isset($localitySafetyAndLivabilityData[$document['CITY_ID']]['MIN_SAFETY_SCORE'])) {
                $document['LOCALITY_MIN_SAFETY_SCORE'] = $localitySafetyAndLivabilityData[$document['CITY_ID']]['MIN_SAFETY_SCORE'];
            }
            if (isset($localitySafetyAndLivabilityData[$document['CITY_ID']]['MAX_SAFETY_SCORE'])) {
                $document['LOCALITY_MAX_SAFETY_SCORE'] = $localitySafetyAndLivabilityData[$document['CITY_ID']]['MAX_SAFETY_SCORE'];
            }
            if (isset($localitySafetyAndLivabilityData[$document['CITY_ID']]['MIN_LIVABILITY_SCORE'])) {
                $document['LOCALITY_MIN_LIVABILITY_SCORE'] = $localitySafetyAndLivabilityData[$document['CITY_ID']]['MIN_LIVABILITY_SCORE'];
            }
            if (isset($localitySafetyAndLivabilityData[$document['CITY_ID']]['MAX_LIVABILITY_SCORE'])) {
                $document['LOCALITY_MAX_LIVABILITY_SCORE'] = $localitySafetyAndLivabilityData[$document['CITY_ID']]['MAX_LIVABILITY_SCORE'];
            }

            if (! empty($has3DImages[$document['PROJECT_ID']]) && $has3DImages[$document['PROJECT_ID']]) {
                $document['HAS_3D_IMAGES'] = $has3DImages[$document['PROJECT_ID']];
            }


            if (isset($document['BUILDER_DISPLAY_ORDER'])){
              $document['BUILDER_PRIORITY'] = $document['BUILDER_DISPLAY_ORDER'];
            }
            unset($document['OLDEST_PRICE_PER_UNIT_AREA']);
            unset($document['OLDEST_PRICE_PER_UNIT_AREA_DATE']);
            unset($document['LOCALITY_PRIORITY']);
            unset($document['BUILDER_DISPLAY_ORDER']);
            unset($document['PROJECT_TYPE']);
            unset($document['SUBURB_PRIORITY']);
            unset($document['LOCALITY_LATITUDE']);
            unset($document['LOCALITY_LONGITUDE']);
            unset($document['STATUS']);
            unset($document['RESIDENTIAL_FLAG']);

            if (isset($projectImagesCount[$document['PROJECT_ID']])) {
                $document['PROJECT_IMAGES_COUNT'] = $projectImagesCount[$document['PROJECT_ID']];
            }

            if (empty($document['BUILDER_SCORE'])){
				unset($document['BUILDER_SCORE']);
            }
            else{
                $document['BUILDER_SCORE'] = scaleBuilderScore($document['BUILDER_SCORE'], $maxActualBuliderScore);
            }

            resetPriceFields($document);

            array_push($documents, $document);
        }
    } else {
    		$logger->error("Error while fetching Project data from database using Query : \n ". $sql."\n");
    		$logger->error("Mysql error : \n". mysql_error());
    		die();
    }

    if ($solrCollectionType === "b2b") {
        $availability_map = getB2bAvailability($paramProjectIds);
    }

    $len = count($documents);
    for ($i = 0; $i < $len; $i ++) {
        $document = &$documents[$i];
        $localityOverviewUrl = url_lib_locality_url($document);
        $suburbOverviewUrl = url_lib_suburb_url($document);
        $cityOverviewUrl = url_lib_city_url($document);
        unset($document['CITY_URL']);
        if (isset($cityOverviewUrl)) {
            $document['CITY_OVERVIEW_URL'] = $cityOverviewUrl;
        }
        if (isset($suburbOverviewUrl)) {
            $document['SUBURB_OVERVIEW_URL'] = $suburbOverviewUrl;
        }
        if (isset($localityOverviewUrl))
            $document['LOCALITY_OVERVIEW_URL'] = $localityOverviewUrl;

        if ($solrCollectionType === "b2b") {
            $project_id = $document['PROJECT_ID'];
            if (array_key_exists($project_id, $availability_map)) {
                $document['AVAILABILITY'] = $availability_map[$project_id];
            } else {
                unset($document['AVAILABILITY']);
            }
        }

        if (isset($document['PROJECT_STATUS'])) {
        	if(isset($document['AVAILABILITY'])){
        		$document['RESALE_ENQUIRY'] = ($document['FORCE_RESALE'] == '1' ||
        		($document['FORCE_RESALE'] != '2' && ($document['PROJECT_STATUS'] == 'Completed' || $document['AVAILABILITY'] == '0')));
        	}
        	else{
        		$document['RESALE_ENQUIRY'] = ($document['FORCE_RESALE'] == '1' ||
        		($document['FORCE_RESALE'] != '2' && $document['PROJECT_STATUS'] == 'Completed'));
        	}
        	$projectResaleEnquiry[$document['PROJECT_ID']] = $document['RESALE_ENQUIRY'];
        }
    }

    return array(
        $deleteDocuments,
        $documents
    );
}

function loadProjectData()
{
    return;
}

function isProjectUpcoming($projectStatus)
{
    $projectStatus = strtolower($projectStatus);
    return ($projectStatus == "not launched" || $projectStatus == "pre launch" || $projectStatus == "upcoming");
}

function getPrimaryStatus($document)
{
    if (getSoldOutStatus($document)) {
        return false;
    }

    return true;
}

function removeFromArray(&$array, $value)
{
    if (! empty($array)) {
        $newArray = array();
        foreach ($array as $element) {
            if ($element != $value) {
                $newArray[] = $element;
            }
        }

        $array = $newArray;
    }
}

function resetPriceFields(&$document)
{
    if (! $document['IS_RESALE']) {

        if (isset($document['RESALE_PRICE_PER_UNIT_AREA'])) {
            removeFromArray($document['PRIMARY_OR_RESALE_PRICE_PER_UNIT_AREA'], $document['RESALE_PRICE_PER_UNIT_AREA']);
            unset($document['RESALE_PRICE_PER_UNIT_AREA']);
        }

        if (isset($document['RESALE_PRICE'])) {
            removeFromArray($document['PRIMARY_OR_RESALE_BUDGET'], $document['RESALE_PRICE']);
            unset($document['RESALE_PRICE']);
        }
    }

    if (! $document['IS_PRIMARY'] || $document['PROJECT_STATUS'] == 'On Hold') {
        if (!empty($document['PRICE_PER_UNIT_AREA'])) {
            removeFromArray($document['PRIMARY_OR_RESALE_PRICE_PER_UNIT_AREA'], $document['PRICE_PER_UNIT_AREA']);
        }

        if (! empty($document['BUDGET'])) {
            removeFromArray($document['PRIMARY_OR_RESALE_BUDGET'], $document['PRICE']);
            removeFromArray($document['PRIMARY_OR_RESALE_BUDGET'], $document['BUDGET']);
        }

        $document['HAS_PRICE_PER_UNIT_AREA'] = 0;
        $document['HAS_BUDGET'] = 0;

        unset($document['PROJECT_AVG_PRICE_PER_UNIT_AREA']);
        unset($document['PROJECT_PRICE_RISE']);
        unset($document['PROJECT_PRICE_RISE_TIME']);
        unset($document['PROJECT_PRICE_APPRECIATION_RATE']);
        unset($document['PRICE_PER_UNIT_AREA']);
        unset($document['PRICE']);
        unset($document['BUDGET']);
        unset($document['MIN_PRICE_PER_UNIT_AREA']);
        unset($document['MINPRICE']);
        unset($document['MIN_BUDGET']);
        unset($document['MAX_PRICE_PER_UNIT_AREA']);
        unset($document['MAXPRICE']);
        unset($document['MAX_BUDGET']);
    }

    if (empty($document['PRIMARY_OR_RESALE_PRICE_PER_UNIT_AREA'])) {
        unset($document['PRIMARY_OR_RESALE_PRICE_PER_UNIT_AREA']);
    }

    if (empty($document['PRIMARY_OR_RESALE_BUDGET'])) {
        unset($document['PRIMARY_OR_RESALE_BUDGET']);
    }

    if (empty($document['HAS_PRICE_PER_UNIT_AREA']) && empty($document['MIN_BUDGET'])) {
        unset($document['PROJECT_AVG_PRICE_PER_UNIT_AREA']);
        unset($document['PROJECT_PRICE_RISE']);
        unset($document['PROJECT_PRICE_RISE_TIME']);
        unset($document['PROJECT_PRICE_APPRECIATION_RATE']);
    }
}

function getSoldOutStatus($document)
{
    return ($document['AVAILABILITY'] == '0' || ($document['AVAILABILITY'] === null && in_array($document['PROJECT_STATUS'], array(
        'Ready For Possession',
        'Occupied',
        'Completed'
    ))));
}

function getReSaleStatus($document)
{
    global $resaleProjectPrices;
    return $document['PROJECT_STATUS'] != 'Pre Launch' &&
            $document['PROJECT_STATUS'] != 'Not Launched' &&
           ($document['PROJECT_STATUS'] == 'Completed' ||
            $document['AVAILABILITY'] === '0' ||
            !empty($document['PROJECT_RESALE_LISTING_COUNT']) ||
            ($document['PROJECT_STATUS'] == 'Under Construction' && !empty($document['PROMISED_COMPLETION_DATE']) && strtotime($document['PROMISED_COMPLETION_DATE']) < strtotime("+18 month")));
}

function getProjectPriceRiseInfo($oldest_price, $oldest_date, $latest_price, $latest_date)
{
    // XXX - Removing this feature for now.
    if (empty($oldest_price) || empty($latest_price) || $oldest_price == $latest_price)
        return array();
    $rise_percentage = floor(($latest_price - $oldest_price) * 100 / $oldest_price);
    $time_difference = strtotime($latest_date) - strtotime($oldest_date);
    $time_diff_months = round($time_difference / (24 * 60 * 60 * 30), 0, PHP_ROUND_HALF_UP);

    return array(
        'rise_percentage' => $rise_percentage,
        'rise_months' => $time_diff_months
    );
}

function getResalePrices()
{
    global $logger, $resaleProjectPrices, $resalePropertyPrices, $resaleLocalityPrices, $paramProjectIds, $solrDB, $version;

    // Not fetching resale prices older than 3 months
    $today = time();
    $resalePriceEffectiveThresholdDate = date('Y-m-d', strtotime("-4 months", $today));

    $projectCondition = "";
    if (! empty($paramProjectIds))
        $projectCondition = " WHERE psp.project_id IN ($paramProjectIds) ";

    $sql = <<<SQL
        SELECT psp.project_id, psp.unit_type, min(psp.min_price) AS resale_price,
        rp.locality_id, min(IF(rpo.size is NULL or rpo.size = 0, rpo.carpet_area, rpo.size)) as min_size,
        max(IF(rpo.size is NULL or rpo.size = 0, rpo.carpet_area, rpo.size))  as max_size,
        avg(IF(rpo.size is NULL or rpo.size = 0, rpo.carpet_area, rpo.size))  as avg_size
        FROM cms.project_secondary_price AS psp
        JOIN (SELECT project_id, unit_type, max(effective_date) AS effective_date
              FROM cms.project_secondary_price
              WHERE effective_date > '$resalePriceEffectiveThresholdDate'
              GROUP BY project_id, unit_type) t
          ON (psp.project_id=t.project_id AND psp.unit_type=t.unit_type AND psp.effective_date = t.effective_date)
        JOIN cms.resi_project rp on (rp.project_id = psp.project_id)
        JOIN cms.resi_project_options rpo on (rp.project_id = rpo.project_id)
        $projectCondition
        GROUP BY psp.project_id, psp.unit_type
SQL;
    $rs = mysql_query($sql, $solrDB);
    if (empty($rs)) {
    	$logger->error("Error in project secondary price table query : \n ". $sql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
   }
    $resaleLocalityPrices = array();
    while (($row = mysql_fetch_assoc($rs)) != FALSE) {
        $projectId = $row['project_id'];
        $localityId = $row['locality_id'];
        $unitType = strtolower($row['unit_type']);
        if (! isset($resalePrices[$projectId]))
            $resaleProjectPrices[$projectId] = array();
        if (! isset($resaleLocalityPrices[$localityId])) {
            $resaleLocalityPrices[$localityId] = array();
            $resaleLocalityPrices[$localityId]['min'] = $row['resale_price'] * $row['min_size'];
            $resaleLocalityPrices[$localityId]['max'] = 0;
            $resaleLocalityPrices[$localityId][$unitType]= array();
            $resaleLocalityPrices[$localityId][$unitType]['avg'] = 0;
            $resaleLocalityPrices[$localityId][$unitType]['sum'] = 0;
            $resaleLocalityPrices[$localityId][$unitType]['count'] = 0;
        }
        $resaleProjectPrices[$projectId][$unitType] = $row['resale_price'];
        $resaleLocalityPrices[$localityId]['min'] = min($row['resale_price'] * $row['min_size'], $resaleLocalityPrices[$localityId]['min']);
        $resaleLocalityPrices[$localityId]['max'] = max($row['resale_price'] * $row['max_size'], $resaleLocalityPrices[$localityId]['max']);

        if(!isset($resaleLocalityPrices[$localityId][$unitType]))
        {
            $resaleLocalityPrices[$localityId][$unitType] = array();
            $resaleLocalityPrices[$localityId][$unitType]['avg'] = 0;
            $resaleLocalityPrices[$localityId][$unitType]['count']=0;
            $resaleLocalityPrices[$localityId][$unitType]['sum'] = 0;
        }

            $resaleLocalityPrices[$localityId][$unitType]['count']++;
            $resaleLocalityPrices[$localityId][$unitType]['sum'] += $row['resale_price'];

    }

    foreach ($resaleLocalityPrices as $localityId => $unitTypeSumCountArray) {
        foreach ($unitTypeSumCountArray as $unitTypeFromSumCountArray => $sumCountArray) {

            if(isset($resaleLocalityPrices[$localityId][$unitTypeFromSumCountArray]['sum']) && $resaleLocalityPrices[$localityId][$unitTypeFromSumCountArray]['sum'] != 0)
            {
            $resaleLocalityPrices[$localityId][$unitTypeFromSumCountArray]['avg'] = $resaleLocalityPrices[$localityId][$unitTypeFromSumCountArray]['sum']/(($resaleLocalityPrices[$localityId][$unitTypeFromSumCountArray]['count']==0)?1:$resaleLocalityPrices[$localityId][$unitTypeFromSumCountArray]['count']);
            }
        }
    }


    $mysql = <<<SQL
		select l.price_verified, rpo.project_id, rpo.option_type, l.option_id,rp.LOCALITY_ID,min(IF(rpo.size is NULL or rpo.size = 0, rpo.carpet_area, rpo.size)) as size,
		 min(IF(lp.price_per_unit_area > 0, lp.price_per_unit_area, floor(lp.price / IF(rpo.size > 0, rpo.size, rpo.carpet_area)))) as min_price,
		 max(IF(lp.price_per_unit_area > 0, lp.price_per_unit_area, floor(lp.price / IF(rpo.size > 0, rpo.size, rpo.carpet_area)))) as max_price
  		from cms.listings l
		join cms.listing_prices lp on l.current_price_id = lp.id
		join cms.resi_project_options rpo on (rpo.options_id = l.option_id) and rpo.option_category = 'Actual'
		join cms.resi_project rp on (rp.project_id = rpo.project_id and rp.version = $version)
		where lp.version=$version
		and l.listing_category = 'Resale' and l.status = 'Active'
		and lp.status = 'Active' and (lp.price_per_unit_area > 0 or lp.price > 0)
		group by l.option_id
		order by project_id, option_type, min_price desc
SQL;

    $rs1 = mysql_unbuffered_query($mysql, $solrDB);
    if ($rs1 == FALSE) {
    	$logger->error("Error while fetching listing prices using query : \n ". $mysql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }

    while (($row = mysql_fetch_assoc($rs1)) != FALSE) {
        $unitType = strtolower($row['option_type']);
        $projectId = $row['project_id'];
        $optionId = $row['option_id'];
        $localityId = $row['LOCALITY_ID'];
        unset($resaleProjectPrices[$projectId]);
        $resalePropertyPrices[$optionId] = $row['min_price'];
        $resalePropertyPrices['max_price'][$optionId] = $row['max_price'];

        if ($row['price_verified']) {
            if (! isset($resaleLocalityPricesFromListings)) {
                $resaleLocalityPricesFromListings = array();
            }
            if (! isset($resaleLocalityPricesFromListings[$localityId])) {
                $resaleLocalityPricesFromListings[$localityId] = array();
                $resaleLocalityPricesFromListings[$localityId][$unitType]=array();
                $resaleLocalityPricesFromListings[$localityId][$unitType]["avg"] =0;
                $resaleLocalityPricesFromListings[$localityId][$unitType]["sum"] = 0;
                $resaleLocalityPricesFromListings[$localityId][$unitType]["count"] =0;
            }

            if(!isset($resaleLocalityPricesFromListings[$localityId][$unitType]))
            {
                $resaleLocalityPricesFromListings[$localityId][$unitType]=array();
                $resaleLocalityPricesFromListings[$localityId][$unitType]["avg"] =0;
                $resaleLocalityPricesFromListings[$localityId][$unitType]["sum"] = 0;
                $resaleLocalityPricesFromListings[$localityId][$unitType]["count"]=0;
            }
            $resaleLocalityPricesFromListings[$localityId][$unitType]["sum"] += $row['min_price'];
            $resaleLocalityPricesFromListings[$localityId][$unitType]["count"]++;
        }
    }


    if(!empty($resaleLocalityPricesFromListings))
    {
        foreach ($resaleLocalityPricesFromListings as $localityId => $unitTypeSumArray) {
            foreach ($unitTypeSumArray as $unitType => $value) {
                $resaleLocalityPricesFromListings[$localityId][$unitType]["avg"] = $resaleLocalityPricesFromListings[$localityId][$unitType]["sum"]/$resaleLocalityPricesFromListings[$localityId][$unitType]["count"];
            }
        }
    }

}

function loadProjectOffers()
{
    global $solrDB, $logger, $paramProjectIds, $projCmsActiveCondition, $version;
    $logger->info("Fetching the Project Offers.");

    $projectCondition = "";
    if (! empty($paramProjectIds))
        $projectCondition = " AND PO.PROJECT_ID IN ($paramProjectIds) ";

    $sql = <<<QRY
        SELECT PO.OFFER, PO.OFFER_HEADING, PO.OFFER_DESC, PO.PROJECT_ID AS PROJECT_ID FROM cms.project_offers AS PO JOIN cms.resi_project RP
        ON (PO.PROJECT_ID = RP.PROJECT_ID AND RP.RESIDENTIAL_FLAG = 'Residential') JOIN cms.resi_project_options RPO ON (RPO.PROJECT_ID = RP.PROJECT_ID AND RPO.OPTION_CATEGORY = 'Actual')
        WHERE RP.STATUS IN ({$projCmsActiveCondition})  AND PO.status = "active" AND RP.VERSION = $version
        $projectCondition GROUP BY PO.PROJECT_ID, PO.OFFER, PO.OFFER_HEADING, PO.OFFER_DESC
QRY;
    $rs = mysql_unbuffered_query($sql, $solrDB);
    if ($rs == FALSE) {
    	$logger->error("Error in executing the CMS project offers table query : \n ". $sql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }
    $projectOffers = array();
    while (($row = mysql_fetch_assoc($rs)) != FALSE) {
        $projectId = $row['PROJECT_ID'];
        unset($row['PROJECT_ID']);
        if (! isset($projectOffers[$projectId])) {
            $projectOffers[$projectId] = array();
            $projectOffers[$projectId]['OFFER'] = array();
        }
        $projectOffers[$projectId]['OFFER'][] = json_encode(array(
            "offer" => $row["OFFER"],
            "offerHeading" => $row["OFFER_HEADING"],
            "offerDesc" => $row["OFFER_DESC"]
        ));
        $projectOffers[$projectId]['OFFER_ARRAY'][] = array(
            "offer" => $row["OFFER"],
            "offerHeading" => $row["OFFER_HEADING"],
            "offerDesc" => $row["OFFER_DESC"]
        );
    }
    return $projectOffers;
}

function loadProjectDiscussionCount()
{
    global $logger, $paramProjectIds, $projCmsActiveCondition, $version, $solrDB;
    $logger->info("Fetching the Project Discussion Count.");

    $projectCondition = "";
    if (! empty($paramProjectIds))
        $projectCondition = " AND FUC.OBJECT_ID IN ($paramProjectIds) ";

    $sql = <<<QRY
        SELECT COUNT(DISTINCT FUC.COMMENT_ID) AS DISCUSSION_COUNT, FUC.OBJECT_ID AS PROJECT_ID FROM proptiger.FORUM_USER_COMMENTS AS FUC
            JOIN cms.resi_project RP ON (FUC.OBJECT_ID = RP.PROJECT_ID)
            JOIN cms.resi_project_options RPO ON (RP.PROJECT_ID = RPO.PROJECT_ID AND RPO.OPTION_CATEGORY = 'Actual')
            WHERE RP.STATUS IN ({$projCmsActiveCondition}) AND FUC.object_type_id  = 1 AND FUC.STATUS = '1' AND FUC.PARENT_ID = 0 AND RP.VERSION = $version
            $projectCondition GROUP BY FUC.OBJECT_ID HAVING DISCUSSION_COUNT > 0
QRY;
    $rs = mysql_unbuffered_query($sql, $solrDB);
    if ($rs == FALSE) {
    	$logger->error("Error in executing the Project Discussion table query : \n ". $sql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
        die();
    }

    $projectDiscussion = array();
    while (($row = mysql_fetch_assoc($rs)) != FALSE) {
        $projectId = $row['PROJECT_ID'];
        $projectDiscussion[$projectId] = $row['DISCUSSION_COUNT'];
    }

    return $projectDiscussion;
}

function loadProjectLastUpdatedTime()
{
    global $logger;
    $logger->info("Fetching the Projects Last Updated Time.");
    return getProjectAttributes("resi_project", array(
        TABLE_ATTRIBUTE_PROJECT_UPDATE_TIME
    ), 500000);
}

function getProjectAttributes($tableName, $attributes, $offset)
{
    global $logger, $paramProjectIds, $solrDB;
    $cond = "";
    if (! empty($attributes))
        $cond .= " AND attribute_name IN ('" . implode("','", $attributes) . "')";
    if (! empty($paramProjectIds))
        $cond .= " AND TABLE_ID IN ($paramProjectIds) ";
    $sql = <<<QRY
        SELECT ID, ATTRIBUTE_NAME, ATTRIBUTE_VALUE, TABLE_ID AS TABLE_ID , TABLE_NAME FROM cms.table_attributes
            WHERE table_name = "{$tableName}" $cond
QRY;

    $rs = mysql_unbuffered_query($sql, $solrDB);
    if ($rs == FALSE) {
    	$logger->error("Error in executing the CMS Table Attributes table query : \n ". $sql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }

    $tableAttributes = array();
    while (($row = mysql_fetch_assoc($rs)) != FALSE) {
        $tableId = $row['TABLE_ID'];
        unset($row['TABLE_ID']);
        $tableAttributes[$tableId] = $row;
    }

    return $tableAttributes;
}

function loadProjectAndBuilderImages()
{
    global $logger, $paramProjectIds, $solrDB;
    $logger->info("Fetching the Project And Builder Images");

    $projectCondition = "";
    if (! empty($paramProjectIds))
        $projectCondition = " AND I.OBJECT_ID IN ($paramProjectIds) ";

    $sql = <<<QRY
        (SELECT I.id as IMAGE_ID, PATH, I.OBJECT_ID, IT.type IMAGE_TYPE, OT.type OBJECT_TYPE, I.SEO_NAME, I.ALT_TEXT, I.TITLE, I.priority AS PRIORITY FROM proptiger.Image I JOIN proptiger.ImageType IT JOIN proptiger.ObjectType OT ON
            (I.ImageType_id = IT.id AND IT.ObjectType_id = OT.id) WHERE OT.type = "builder" AND IT.type = "logo" AND I.active = 1 $projectCondition ORDER BY IFNULL(I.priority, 999), I.id DESC)
        UNION
        (SELECT I.id as IMAGE_ID, PATH, I.OBJECT_ID, IT.type IMAGE_TYPE, OT.type OBJECT_TYPE, I.SEO_NAME, I.ALT_TEXT, I.TITLE, I.priority AS PRIORITY FROM proptiger.Image I JOIN proptiger.ImageType IT JOIN proptiger.ObjectType OT ON
            (I.ImageType_id = IT.id AND IT.ObjectType_id = OT.id) WHERE OT.type="project" AND IT.type = "main" AND I.active = 1 $projectCondition ORDER BY IFNULL(I.priority, 999), I.id DESC) ORDER BY IFNULL(PRIORITY, 999), IMAGE_ID DESC
QRY;
    $rs = mysql_unbuffered_query($sql, $solrDB);
    if ($rs == FALSE) {
    	$logger->error("Error in executing the Image table query : \n ". $sql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }

    $images = array();
    $images['project'] = array();
    $images['builder'] = array();

    /*
     * Only the latest created main image should be used for the website. Hence when one image is set then others are discarded. As the data is sorted in descending order by Image Id.
     */
    while (($row = mysql_fetch_assoc($rs)) != FALSE) {
        $domainType = strtolower($row['OBJECT_TYPE']);
        $objectId = $row["OBJECT_ID"];
        if (isset($images[$domainType][$objectId]))
            continue;
        $images[$domainType][$objectId]['PATH'] = $row['PATH'] . $row['SEO_NAME'];
        $images[$domainType][$objectId]['ALTTEXT'] = $row['ALT_TEXT'];
        $images[$domainType][$objectId]['TITLE'] = $row['TITLE'];
    }
    return $images;
}

function loadPriceRiseInfo($objectType)
{
    global $logger, $version, $solrDB;
    $objectTypeId = '';
    $completeObjectTypeId = '';
    $type = '';
    $conditionUnitType = '';
    $defaultUnitType = 'Apartment';
    switch ($objectType) {
        case 'BUILDER':
            $objectTypeId = 'builder_id';
            $completeObjectTypeId = 'rp.builder_id';
            $type = 'BUILDER_';
            break;
        case 'LOCALITY':
            $objectTypeId = 'locality_id';
            $completeObjectTypeId = 'l.locality_id';
            $type = 'LOCALITY_';
            break;
        case 'SUBURB':
            $objectTypeId = 'suburb_id';
            $completeObjectTypeId = 's.suburb_id';
            $type = 'SUBURB_';
            break;
        case 'CITY':
            $objectTypeId = 'city_id';
            $completeObjectTypeId = 's.city_id';
            $type = 'CITY_';
            break;
        case 'PROJECT':
            $objectTypeId = 'project_id';
            $completeObjectTypeId = 'rp.project_id';
            $type = 'PROJECT_';
            $conditionUnitType = " and a.is_dominant_project_unit_type = 'True' ";
            break;
    }

    $query = "select a.$objectTypeId, a.effective_month, a.unit_type,
                        sum(average_price_per_unit_area*ltd_supply)/sum(if(average_price_per_unit_area is null, 0, ltd_supply)) as average_price_per_unit_area
                    from cms.d_inventory_prices a
                    inner join (select $objectTypeId, min(effective_month) m1, max(effective_month) m2, unit_type
                                from cms.d_inventory_prices
                                where average_price_per_unit_area is not null
                                group by $objectTypeId, unit_type) t
                        on a.$objectTypeId = t.$objectTypeId and (a.effective_month = t.m1 or a.effective_month = t.m2)
                        and a.unit_type = t.unit_type $conditionUnitType
                    group by a.$objectTypeId, a.unit_type, a.effective_month
                    order by a.$objectTypeId, a.unit_type, a.effective_month";

    $result = mysql_unbuffered_query($query, $solrDB);
    if($result == FALSE){
    	$logger->error("Error in fetching price rise info using query : \n ". $query."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }
    $objects = array();
    while ($document = mysql_fetch_assoc($result)) {
        if (($objectTypeId == 'project_id' && !empty($objects[$document[$objectTypeId]])) || !empty($objects[$document[$objectTypeId]][$document['unit_type']])) {
        	if($objectTypeId == 'project_id'){
        		$prevDocument = $objects[$document[$objectTypeId]];
        	}
        	else{
        		$prevDocument = $objects[$document[$objectTypeId]][$document['unit_type']];
        	}
            $oldPrice = $prevDocument['average_price_per_unit_area'];
            if ($oldPrice > 0 && $document['average_price_per_unit_area'] > 0) {
                $risePercent = 100 * ($document['average_price_per_unit_area'] - $oldPrice) / $oldPrice;
                $risePercent = number_format((float) $risePercent, 1, '.', '');
                $latestDateDiff = date_diff(date_create($document['effective_month']), date_create($prevDocument['effective_month']));
                $deltaMonths = $latestDateDiff->format('%m') + $latestDateDiff->format('%y') * 12;

                if (abs($risePercent) > 1 && $deltaMonths > 0) {
                    $document['RISE_PERCENT'] = $risePercent;
                    $document['RISE_PERIOD_MONTHS'] = $deltaMonths;

                    if(isset($objects[$document[$objectTypeId]][$defaultUnitType]['RISE_PERCENT'])){
                    	$objects[$document[$objectTypeId]]['RISE_PERCENT'] = $objects[$document[$objectTypeId]][$defaultUnitType]['RISE_PERCENT'];
                    }
                    if(isset($objects[$document[$objectTypeId]][$defaultUnitType]['RISE_PERIOD_MONTHS'])){
                    	$objects[$document[$objectTypeId]]['RISE_PERIOD_MONTHS'] = $objects[$document[$objectTypeId]][$defaultUnitType]['RISE_PERIOD_MONTHS'];
                    }
                }
            }
        }
        if($objectTypeId == 'project_id'){
        	$objects[$document[$objectTypeId]] = $document;
        }
        else{
        	$objects[$document[$objectTypeId]][$document['unit_type']] = $document;
        }
        if(isset($objects[$document[$objectTypeId]][$defaultUnitType]['average_price_per_unit_area'])){
        	$objects[$document[$objectTypeId]]['average_price_per_unit_area'] = $objects[$document[$objectTypeId]][$defaultUnitType]['average_price_per_unit_area'];
        }
    }

    $completeFieldType = $type . 'PRICE_RISE_6MONTHS';

    $query = "select b.value as value from cms.b2b_properties b where b.name = 'end_date'";
    $rs = mysql_query($query, $solrDB);
    if ($rs == FALSE) {
    	$logger->error("Error in executing the CMS Table b2b_properties table query : \n ". $query."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }

    if ($row = mysql_fetch_assoc($rs)) {
        $var = $row['value'];
    }

    $date = date_create($var);
    $curDate = $date->format('Y-m-d');
    $date->modify('-5 month');
    $sixMonthOldDate = $date->format('Y-m-d');

    $query = "select a.$objectTypeId, a.effective_month, a.unit_type,
                        sum(average_price_per_unit_area*ltd_supply)/sum(if(average_price_per_unit_area is null, 0, ltd_supply)) as average_price_per_unit_area
                from cms.d_inventory_prices a
                    inner join (select $objectTypeId, min(effective_month) m1, max(effective_month) m2
                                from cms.d_inventory_prices
                                where average_price_per_unit_area is not null
                                and effective_month in ('$sixMonthOldDate', '$curDate')
                                group by $objectTypeId) t
                       on (a.$objectTypeId = t.$objectTypeId and (a.effective_month = t.m1 or a.effective_month = t.m2))
                    where a.unit_type = 'Apartment'
                    group by a.$objectTypeId, a.effective_month
                    order by a.$objectTypeId, a.effective_month";


    $tmpObj = array();
    $result = mysql_unbuffered_query($query, $solrDB);
    if($result == FALSE){
    	$logger->error("Error while fetching price rise info using query : \n ". $query."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }

    while ($document = mysql_fetch_assoc($result)) {
        if (! empty($tmpObj[$document[$objectTypeId]])) {
            $prevDocument = $tmpObj[$document[$objectTypeId]];
            $oldPrice = $prevDocument['average_price_per_unit_area'];
            if ($oldPrice > 0 && $tmpObj[$document[$objectTypeId]] != null && $document['average_price_per_unit_area'] > 0) {
                $risePercent = 100 * ($document['average_price_per_unit_area'] - $oldPrice) / $oldPrice;
                $risePercent = number_format((float) $risePercent, 1, '.', '');
                if (abs($risePercent) > 1) {
                    if (! empty($objects[$document[$objectTypeId]])) {
                        $prevDocument = $objects[$document[$objectTypeId]];
                    }
                    $prevDocument[$completeFieldType] = $risePercent;
                    $objects[$document[$objectTypeId]] = $prevDocument;
                }
            }
        }
        $tmpObj[$document[$objectTypeId]] = $document;
    }

    $sql = "SELECT $completeObjectTypeId as OBJECT_ID, OPTION_TYPE as unit_type
                    FROM cms.resi_project_options rpo
                    JOIN cms.resi_project rp ON (rp.PROJECT_ID = rpo.PROJECT_ID AND rp.RESIDENTIAL_FLAG = 'Residential')
                    JOIN cms.locality l ON (l.LOCALITY_ID = rp.LOCALITY_ID)
                    JOIN cms.suburb s ON (s.SUBURB_ID = l.SUBURB_ID)
                    JOIN cms.listings li ON (rpo.OPTIONS_ID = li.OPTION_ID)
                    LEFT JOIN cms.project_supplies ps ON (ps.LISTING_ID = li.ID)
                    WHERE li.LISTING_CATEGORY = 'Primary' AND rpo.OPTION_CATEGORY = 'Logical'
                    AND li.STATUS = 'Active' and ps.VERSION = $version and rp.VERSION = $version
                    GROUP BY OBJECT_ID, OPTION_TYPE
                    ORDER BY OBJECT_ID, SUM(ps.SUPPLY) DESC";

    $result = mysql_unbuffered_query($sql, $solrDB);
    if($result == FALSE){
    	$logger->error("Error while fetching price rise info using query : \n ". $sql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }
    while ($document = mysql_fetch_assoc($result)) {
        if (! isset($objects[$document['OBJECT_ID']])) {
            $objects[$document['OBJECT_ID']] = $document;
        }
    }

    return $objects;
}

function getProjectVideoCount()
{
    global $solrDB, $logger, $paramProjectIds, $projCmsActiveCondition, $version;

    $logger->info("Fetching the Project Videos Count ");

    $projectCondition = "";
    if (! empty($paramProjectIds))
        $projectCondition = " AND RP.PROJECT_ID IN ($paramProjectIds) ";

    $sql = <<<QRY
        SELECT count(*) AS VIDEO_COUNT, table_id AS PROJECT_ID FROM cms.video_links as V JOIN cms.resi_project AS RP ON (RP.project_id=V.table_id)
            WHERE V.table_name = "resi_project" AND RP.status in ({$projCmsActiveCondition}) AND RP.version = $version $projectCondition GROUP BY PROJECT_ID
QRY;
    $rs = mysql_unbuffered_query($sql, $solrDB);
    if ($rs == FALSE) {
    	$logger->error("Error in Executing Project Video LInks Query : \n ". $sql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }

    $videoLinks = array();
    while (($row = mysql_fetch_row($rs)) != FALSE) {
        $videoLinks[$row[1]] = $row[0];
    }

    return $videoLinks;
}
// can be removed.
// deprecated.
function getProjectImagesCount()
{
    global $logger, $paramProjectIds, $projCmsActiveCondition, $version, $solrDB;

    $logger->info("Fetching the Project Images Count ");

    $projectCondition = "";
    if (! empty($paramProjectIds))
        $projectCondition = " AND RP.PROJECT_ID IN ($paramProjectIds) ";

    $sql = <<<QRY
        SELECT count(distinct I.id) AS IMAGE_COUNT, object_id AS PROJECT_ID, IT.type AS IMAGE_TYPE
        FROM proptiger.Image I
        JOIN proptiger.ImageType IT ON (I.ImageType_id = IT.id)
        JOIN proptiger.ObjectType OT ON (IT.ObjectType_id = OT.id)
        JOIN cms.resi_project RP ON (RP.PROJECT_ID = I.object_id AND RP.RESIDENTIAL_FLAG = 'Residential')
        JOIN cms.resi_project_options RPO ON (RP.PROJECT_ID = RPO.PROJECT_ID AND RPO.OPTION_CATEGORY = 'Actual')
        WHERE OT.type = "project" AND RP.STATUS IN ({$projCmsActiveCondition}) AND RP.version = $version
        AND I.active = 1 $projectCondition
        GROUP BY object_id, IT.type

        UNION ALL

        SELECT count(distinct I.id) AS IMAGE_COUNT, RP.PROJECT_ID, IT.type AS IMAGE_TYPE
        FROM proptiger.Image I
        JOIN proptiger.ImageType IT ON (I.ImageType_id = IT.id)
        JOIN proptiger.ObjectType OT ON (IT.ObjectType_id = OT.id)
        JOIN cms.resi_project_options rpo ON (rpo.OPTIONS_ID = I.object_id AND rpo.OPTION_CATEGORY = 'Actual')
        JOIN cms.resi_project RP ON (RP.PROJECT_ID = rpo.PROJECT_ID AND RP.RESIDENTIAL_FLAG = 'Residential')
        WHERE OT.type = "property" AND RP.STATUS IN ({$projCmsActiveCondition}) AND RP.version = $version
        AND I.active = 1 $projectCondition
        GROUP BY RP.PROJECT_ID, IT.type
QRY;
    $rs = mysql_unbuffered_query($sql, $solrDB);

    if ($rs == FALSE) {
    	$logger->error("Error in Executing Project Image Count Query : \n ". $sql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
        die();
    }

    $projectImages = array();
    while (($row = mysql_fetch_row($rs)) != FALSE) {
        if (in_array($row[2], array(
            'paymentPlan',
            'specification',
            'applicationForm',
            'priceList'
        ))) {
            continue;
        }

        if (! isset($projectImages[$row[1]])) {
            $projectImages[$row[1]] = 0;
        }

        $projectImages[$row[1]] += $row[0];
    }

    return $projectImages;
}

function loadPrices()
{
    global $version, $logger, $projectPriceData, $optionsPriceData, $paramProjectIds, $solrDB;

    $logger->info("Fetching the Project Prices Details");

    $soldOutStatus = getPropertySoldOutStatus();

    $projectCondition = "";
    if (! empty($paramProjectIds)) {
        $projectCondition = " AND rpo.PROJECT_ID IN ($paramProjectIds) ";
    }

    $sql = "select IFNULL(min(lp.price_per_unit_area), 0) as price_per_unit_area, rpo.options_id, min(rpo.project_id) as project_id, IFNULL(rpo.size , 0) as size, IFNULL(rpo.carpet_area, 0) as carpet_area
				from cms.listing_prices lp
				join cms.listings l on (lp.listing_id = l.id and lp.version = $version)
				join (select l.option_id, max(lp.effective_date)  as listing_price_date
				from cms.listings l
				join cms.listing_prices lp on lp.listing_id = l.id and lp.version = $version
				where l.listing_category ='Primary'and l.status = 'Active'
				group by l.option_id) lp1
				on (lp1.listing_price_date = lp.effective_date and lp1.option_id = l.option_id)
				join cms.resi_project_options rpo
				on (lp1.option_id = rpo.options_id and rpo.option_category = 'Actual')
				join cms.resi_project rp
				on (rp.project_id = rpo.project_id and rp.version = $version and rp.residential_flag = 'Residential')
				where l.listing_category ='Primary' and l.status = 'Active' and rp.should_display_price = 1 $projectCondition
				group by rpo.options_id";

    $rs = mysql_unbuffered_query($sql, $solrDB);
    if ($rs == FALSE) {
    	$logger->error("Error in fetching project prices using query : \n ". $sql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }

    $projectPriceData = array();
    $optionsPriceData = array();

    while (($row = mysql_fetch_assoc($rs)) != FALSE) {
        $optionsId = $row['options_id'];

        // Skipping primary prices of properties that are sold out
        if (!empty($soldOutStatus[$optionsId])) {
            continue;
        }

        $optionsPriceData[$optionsId] = $row;
        $projectId = $row['project_id'];
        $size = $row['size'];
        if ($size == 0) {
            $size = $row['carpet_area'];
        }
        if (empty($projectPriceData[$projectId])) {
            $projectPriceData[$projectId] = array(
                'minPrice' => $row['price_per_unit_area'] * $size,
                'maxPrice' => $row['price_per_unit_area'] * $size,
                'minPricePerUnitArea' => $row['price_per_unit_area'],
                'maxPricePerUnitArea' => $row['price_per_unit_area']
            );
        } else {
            $existingData = $projectPriceData[$projectId];
            $projectPriceData[$projectId] = array(
                'minPrice' => min(array(
                    $row['price_per_unit_area'] * $size,
                    $existingData['minPrice']
                )),
                'maxPrice' => max(array(
                    $row['price_per_unit_area'] * $size,
                    $existingData['maxPrice']
                )),
                'minPricePerUnitArea' => min(array(
                    $row['price_per_unit_area'],
                    $existingData['minPricePerUnitArea']
                )),
                'maxPricePerUnitArea' => max(array(
                    $row['price_per_unit_area'],
                    $existingData['maxPricePerUnitArea']
                ))
            );
        }
    }
}

function getProjectFlatSupply()
{
    global $version, $logger, $solrDB;
    $sql = "SELECT rpo.PROJECT_ID, rpp.PHASE_TYPE, sum(ps.launched) AS TOTAL_UNITS
        	FROM cms.resi_project_options rpo
        	LEFT JOIN cms.listings li ON (rpo.OPTIONS_ID = li.option_id AND li.listing_category ='Primary' AND li.status='Active')
        	LEFT JOIN cms.resi_project_phase rpp on (li.phase_id = rpp.phase_id and rpp.version=$version)
        	LEFT join cms.project_supplies ps ON (ps.listing_id = li.id and ps.version= $version)
        	GROUP BY rpo.PROJECT_ID, rpp.PHASE_TYPE
        	HAVING sum(ps.launched) > 0
        	ORDER BY rpo.PROJECT_ID, FIELD(rpp.PHASE_TYPE, 'Logical', 'Actual')";

    $rs = mysql_unbuffered_query($sql, $solrDB);
    if ($rs == FALSE) {
    	$logger->error("Error in Executing Project Flat Supply query : \n ". $sql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }

    $projectTotalUnits = array();
    while (($row = mysql_fetch_assoc($rs)) != FALSE) {
        $projectId = $row['PROJECT_ID'];
        unset($row['PROJECT_ID']);
        $projectTotalUnits[$projectId] = $row;
    }
    return $projectTotalUnits;
}

// incorporate the projectImageCount code here.
// TODO
function getImageTypeCount()
{
    global $logger, $imageTypeCount, $solrDB;
    if (! empty($imageTypeCount)) {
        return $imageTypeCount;
    }
    $imageQuery = "select I.object_id as ProjectId, IT.type as ImageType, count(*) as ImageCount from Image I
                    left join ImageType IT on I.ImageType_id = IT.id
                    left join ObjectType OT on IT.ObjectType_id = OT.id
                    where I.active = 1 and OT.type = 'project'
                    group by I.object_id, IT.type";

    $rs = mysql_unbuffered_query($imageQuery, $solrDB);
    if ($rs == FALSE) {
    	$logger->error("Error in Executing Project Image Type Count Query : \n ". $imageQuery."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }

    $imageArr = array();
    $__projectId = 0;
    while (($row = mysql_fetch_assoc($rs)) != FALSE) {
        $imageArr[$row['ProjectId']][$row['ImageType']] = (int) $row['ImageCount'];
    }
    return $imageArr;
    // print_r($imageArr);
}

function getProject3DImageField()
{
    global $logger, $solrDB;
    $sql = "SELECT RPO.PROJECT_ID, COUNT(DISTINCT M.id) AS COUNT FROM proptiger.media M JOIN cms.resi_project_options RPO ON (M.object_id = RPO.OPTIONS_ID AND RPO.OPTION_CATEGORY = 'Actual')
     WHERE M.active = 1 GROUP BY RPO.PROJECT_ID HAVING COUNT(*) > 0";

    $rs = mysql_unbuffered_query($sql, $solrDB);
    if ($rs == FALSE) {
    	$logger->error("Error in Executing Project Media Query : \n ". $sql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }

    $has3DImages = array();
    while (($row = mysql_fetch_assoc($rs)) != FALSE) {
        if (isset($row['PROJECT_ID'])) {
            $has3DImages[$row['PROJECT_ID']] = true;
        }
    }
    return $has3DImages;
}
// not used.
// deprecated.
function getB2BEndMonth()
{
    global $solrDB;
    $sql = "SELECT value FROM cms.b2b_properties WHERE name='end_date'";
    $rs = mysql_unbuffered_query($sql, $solrDB);
    if($rs == FALSE){
    	$logger->error("Error in fetching B2BEndMonth using query : \n ". $sql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }
    $row = mysql_fetch_assoc($rs);
    return $row['value'];
}
// to check if added to existing project query using left join.
function getB2bAvailability($paramProjectIds)
{
    global $solrDB;
    // $b2b_end_month = getB2BEndMonth();
    $tag_total_availability = 'total_availability';

    $projectConditionNew = "";
    if (! empty($paramProjectIds))
        $projectConditionNew = " AND project_id IN ($paramProjectIds) ";

    $sql_b2b_availability = "SELECT
	project_id, sum(inventory) AS $tag_total_availability
	FROM
	cms.d_inventory_prices  WHERE effective_month=(SELECT value FROM
        cms.b2b_properties WHERE name='end_date') $projectConditionNew
	GROUP BY project_id";

    $rs = mysql_unbuffered_query($sql_b2b_availability, $solrDB);
    if ($rs == FALSE) {
    	$logger->error("Error in Executing B2bAvailability query : \n ". $sql_b2b_availability."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }

    $availability_map = array();
    while (($row = mysql_fetch_assoc($rs)) != FALSE) {
        $project_id = $row['project_id'];
        $availability = $row[$tag_total_availability];
        if (is_numeric($availability)) {
            $availability_map[$project_id] = $availability;
        }
    }
    return $availability_map;
}

// not needed.
// deprecated.
function loadCityLocalityCount()
{
    global $logger, $version, $projCmsActiveCondition, $solrDB;
    $sql = "SELECT C.CITY_ID, COUNT(DISTINCT L.LOCALITY_ID) AS LOCALITY_COUNT FROM cms.locality L JOIN cms.suburb S ON (L.SUBURB_ID = S.SUBURB_ID) JOIN cms.city C ON (C.CITY_ID = S.CITY_ID)
			JOIN cms.resi_project RP ON RP.LOCALITY_ID = L.LOCALITY_ID JOIN cms.resi_project_options RPO ON RPO.PROJECT_ID = RP.PROJECT_ID
	        WHERE RP.VERSION = $version AND RP.RESIDENTIAL_FLAG = 'Residential' AND RPO.OPTION_CATEGORY = 'Actual' AND RP.STATUS IN ({$projCmsActiveCondition}) GROUP BY C.CITY_ID HAVING COUNT(*) > 0";
    $rs = mysql_unbuffered_query($sql, $solrDB);
    if ($rs == FALSE) {
    	$logger->error("Error in Executing Locality Count query : \n ". $sql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
   }
    $cityLocalityCount = array();
    while (($doc = mysql_fetch_assoc($rs)) != FALSE) {
        if (isset($doc['CITY_ID'])) {
            $cityLocalityCount[$doc['CITY_ID']] = $doc['LOCALITY_COUNT'];
        }
    }
    return $cityLocalityCount;
}
// Optimization Possible : can be moved to project and locality method.
function populateSafetyAndLivabilityData($objectType)
{
    global $logger, $solrDB;
    global $version;
    global $version, $projCmsActiveCondition;
    if ($objectType == 'PROJECT') {
        $sql = <<<SQL
            SELECT rp.PROJECT_ID, rp.LOCALITY_ID,rp.SAFETY_SCORE , rp.LIVABILITY_SCORE FROM cms.resi_project rp LEFT JOIN cms.resi_project_options rpo ON (rp.PROJECT_ID = rpo.PROJECT_ID )
			JOIN cms.locality l ON (l.LOCALITY_ID = rp.LOCALITY_ID) WHERE rp.VERSION = $version AND rp.STATUS IN ($projCmsActiveCondition) AND rp.RESIDENTIAL_FLAG ='Residential' GROUP BY rp.PROJECT_ID, rp.LOCALITY_ID
SQL;
        $groupId = 'LOCALITY_ID';
        $objetId = 'PROJECT_ID';
    } else {
        $sql = <<<SQL
            SELECT C.CITY_ID, L.LOCALITY_ID, L.SAFETY_SCORE, L.LIVABILITY_SCORE from cms.locality L
            JOIN cms.suburb S ON (L.SUBURB_ID = S.SUBURB_ID)
            JOIN cms.city C ON (C.CITY_ID = S.CITY_ID)
            JOIN cms.resi_project RP ON (RP.LOCALITY_ID = L.LOCALITY_ID AND RP.RESIDENTIAL_FLAG = 'Residential')
            JOIN cms.resi_project_options RPO ON (RP.PROJECT_ID=RPO.PROJECT_ID AND RPO.OPTION_CATEGORY = 'Actual')
            WHERE RP.version = $version AND RP.STATUS IN ($projCmsActiveCondition)
            GROUP BY L.LOCALITY_ID
SQL;
        $groupId = 'CITY_ID';
        $objetId = 'LOCALITY_ID';
    }
    $rs = mysql_query($sql, $solrDB);
    if ($rs == FALSE) {
    	$logger->error("Error while fetching safety and livability score using query : \n ". $sql."\n");
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }

    $temp = array();
    while (($doc = mysql_fetch_assoc($rs)) != FALSE) {
        if (! empty($doc['SAFETY_SCORE'])) {
            $temp['SAFETY_RANK'][$doc[$groupId]][] = array(
                $objetId => $doc[$objetId],
                'SAFETY_SCORE' => $doc['SAFETY_SCORE'],
                'LIVABILITY_SCORE' => $doc['LIVABILITY_SCORE']
            );
        }

        if (! empty($doc['LIVABILITY_SCORE'])) {
            $temp['LIVABILITY_RANK'][$doc[$groupId]][] = array(
                $objetId => $doc[$objetId],
                'LIVABILITY_SCORE' => $doc['LIVABILITY_SCORE']
            );
        }
    }

    $rank = array();
    // keys contains the grouping IDs,ie for project rank group ID will be LOCALITY_ID
    $safetyKeys = array_keys($temp['SAFETY_RANK']);
    $livabilityKeys = array_keys($temp['LIVABILITY_RANK']);
    // Ordering project IDs grouped by LOCALITY ID and locality IDs grouped by CITY_ID
    // based on respective scores to compute rank.
    $safetyKeysLen = sizeof($safetyKeys);
    $livabilityKeysLen = sizeof($livabilityKeys);
    for ($l = 0; $l < $safetyKeysLen; $l ++) {
        $groupId = $safetyKeys[$l];
        $key1 = array();
        $key2 = array();
        if (! empty($temp['SAFETY_RANK'][$groupId])) {
            foreach ($temp['SAFETY_RANK'][$groupId] as $safetyKey => $row) {
                $key1[$safetyKey] = $row['SAFETY_SCORE'];
                $key2[$safetyKey] = $row['LIVABILITY_SCORE'];
            }
            array_multisort($key1, SORT_DESC, $key2, SORT_DESC, $temp['SAFETY_RANK'][$groupId]);
        }
    }

    for ($l = 0; $l < $livabilityKeysLen; $l ++) {
        $groupId = $livabilityKeys[$l];
        if (! empty($temp['LIVABILITY_RANK'][$groupId])) {
            $key3 = array();
            foreach ($temp['LIVABILITY_RANK'][$groupId] as $livabilityKey => $row) {
                $key3[$livabilityKey] = $row['LIVABILITY_SCORE'];
            }
            array_multisort($key3, SORT_DESC, $temp['LIVABILITY_RANK'][$groupId]);
        }
    }

    // Creating rank array using above sorted array grouped by either CITY_ID or LOCALITY_ID
    for ($i = 0; $i < $safetyKeysLen; $i ++) {
        $groupId = $safetyKeys[$i];
        if (! empty($temp['SAFETY_RANK'][$groupId])) {
            $rankVal = 1;
            $safetyOrder = $temp['SAFETY_RANK'][$groupId];
            for ($j = 0; $j < sizeof($safetyOrder); $j ++) {
                if (! empty($safetyOrder[$j]['SAFETY_SCORE'])) {
                    $rank['SAFETY_RANK'][$groupId][$safetyOrder[$j][$objetId]] = $rankVal ++;
                }
            }
            $rank[$groupId]['MIN_SAFETY_SCORE'] = $safetyOrder[sizeof($safetyOrder) - 1]['SAFETY_SCORE'];
            $rank[$groupId]['MAX_SAFETY_SCORE'] = $safetyOrder[0]['SAFETY_SCORE'];
        }
    }

    for ($i = 0; $i < $livabilityKeysLen; $i ++) {
        $groupId = $livabilityKeys[$i];
        if (! empty($temp['LIVABILITY_RANK'][$groupId])) {
            $livabilityOrder = $temp['LIVABILITY_RANK'][$groupId];
            $rankVal = 1;
            for ($j = 0; $j < sizeof($livabilityOrder); $j ++) {
                if (! empty($livabilityOrder[$j]['LIVABILITY_SCORE'])) {
                    $rank['LIVABILITY_RANK'][$groupId][$livabilityOrder[$j][$objetId]] = $rankVal ++;
                }
            }
            $rank[$groupId]['MIN_LIVABILITY_SCORE'] = $livabilityOrder[sizeof($livabilityOrder) - 1]['LIVABILITY_SCORE'];
            $rank[$groupId]['MAX_LIVABILITY_SCORE'] = $livabilityOrder[0]['LIVABILITY_SCORE'];
        }
    }
    return $rank;
}

function getProjectMainImageRandomly($projectId)
{
    $imageArray = array(
        "1/1/6/424068.jpeg",
        "1/1/6/424072.jpeg",
        "1/1/6/460012.jpeg",
        "1/1/6/460011.jpeg",
        "1/1/6/424077.jpeg",
        "1/1/6/424078.jpeg",
        "1/1/6/424079.jpeg",
        "1/1/6/424083.jpeg",
        "1/1/6/424087.jpeg",
        "1/1/6/424088.jpeg"
    );
    $len = count($imageArray);
    return $imageArray[$projectId % $len];
}

function makePriceUserReadable($price)
{
    $croreFormat = $price / 10000000;
    if ($croreFormat < 1) {
        return round($price / 100000, 2) . " Lacs";
    } else {
        return round($croreFormat, 2) . " Cr";
    }
}


function getProjectTowerDetails()
{
    global $logger, $solrDB;

    $sql = <<<QRY
            SELECT PROJECT_ID, COUNT(PROJECT_ID) NO_OF_TOWERS, ROUND(SUM(if(NO_OF_FLOORS =0,0,NO_OF_FLATS))/SUM(if(NO_OF_FLATS=0,0,NO_OF_FLOORS)), 2)             AVG_FLATS_PER_FLOOR
            FROM cms.resi_project_tower_details
            GROUP BY PROJECT_ID
QRY;

    $rs = mysql_unbuffered_query($sql, $solrDB);
    if ($rs == FALSE) {
        $logger->info("Error in Executing Project Tower Details Query :\n " . $sql."\n" );
        $logger->error("Mysql error : \n". mysql_error());
        exit();
    }

    $projectTowerDetails = array();
    while (($row = mysql_fetch_assoc($rs)) != FALSE) {
        $projectTowerDetails[$row['PROJECT_ID']] = $row;
    }

    return $projectTowerDetails;
}
function getCreatedAtForProject()
{
    global $solrDB, $logger;
    $sql = "SELECT OBJECT_ID,CREATED_AT FROM proptiger.seo_urls WHERE url_category_id=1  and status='Active'";
    $rs = mysql_unbuffered_query($sql, $solrDB);
    if($rs == FALSE){
    	$logger->error("Mysql error : \n". mysql_error());
    	die();
    }

    $projectDetails = array();
    while (($row = mysql_fetch_assoc($rs)) != FALSE) {
        $projectDetails[$row['OBJECT_ID']] = $row['CREATED_AT'];
    }
    return $projectDetails;
}
function getResaleCountData(){
	global $resaleCountData;
	$resaleCountData = array();
	$projectListingUrl = 'app/v2/project-listing?selector={"filters":{"and":[{"equal":{"isResale":true}}]}}&facets=localityId,suburbId,cityId';
	$resaleCountResponse = json_decode ( file_get_contents ( API_URL . $projectListingUrl ), true );
	$resaleCountResponse = $resaleCountResponse["data"]["facets"];
	foreach($resaleCountResponse as $entityData){
		foreach($entityData as $resaleCount){
			foreach($resaleCount as $id=>$count){
				$resaleCountData[$id] = $count;
			}
		}
	}
}
function getResaleListingCountData(){
	global $resaleListingCountData;
	$resaleListingCountData = array();
	$projectListingUrl = 'data/v3/entity/resale-listing?selector={"filters":{"and":[{"equal":{"bookingStatusId":1}}]}}&facets=cityId,suburbId,localityId';
	$resaleCountResponse = json_decode ( file_get_contents ( API_URL . $projectListingUrl ), true );
	$resaleCountResponse = $resaleCountResponse["data"]["facets"];
	foreach($resaleCountResponse as $entityData){
		foreach($entityData as $resaleCount){
			foreach($resaleCount as $id=>$count){
				$resaleListingCountData[$id] = $count;
			}
		}
	}
}

function getProjectDelay(){
    global $logger, $solrDB;

    $sql = <<<QRY
        SELECT R1.PROJECT_ID, ROUND(DATEDIFF(R2.EXPECTED_COMPLETION_DATE, R1.EXPECTED_COMPLETION_DATE)/30) DELAY
        FROM cms.resi_proj_expected_completion R1
        JOIN cms.resi_proj_expected_completion R2  ON R1.PROJECT_ID = R2.PROJECT_ID
        JOIN (SELECT  PROJECT_ID, MIN(SUBMITTED_DATE) MIN_DT, MAX(SUBMITTED_DATE) MAX_DT FROM cms.resi_proj_expected_completion GROUP BY PROJECT_ID) AS R3 ON R1.PROJECT_ID = R3.PROJECT_ID AND R1.SUBMITTED_DATE = R3.MIN_DT AND R2.SUBMITTED_DATE = R3.MAX_DT
QRY;

   $rs = mysql_unbuffered_query($sql, $solrDB);
    if ($rs == FALSE) {
        $logger->info("Error in Executing Project Delay Query :\n " . $sql."\n" );
        $logger->error("Mysql error : \n". mysql_error());
        exit();
    }

    $projectDelay = array();
    while (($row = mysql_fetch_assoc($rs)) != FALSE) {
        $projectDelay[$row['PROJECT_ID']] = $row['DELAY'];
    }

    return $projectDelay;

}

function getAvgLoadingPercentage(){
    global $logger, $solrDB;

    $sql = <<<QRY
            SELECT rpo.PROJECT_ID , ROUND((SUM(rpo.WEIGHTED_LOAD_FACTOR * ps.SUPPLY)/SUM(ps.SUPPLY) - 1)*100,2) AVG_LOADING_PERCENTAGE FROM
            (
            select t1.PROJECT_ID , t2.OPTIONS_ID OPTIONS_ID , AVG(t1.SIZE/t1.CARPET_AREA) WEIGHTED_LOAD_FACTOR from cms.resi_project_options t1
            join cms.resi_project_options t2 on t1.PROJECT_ID = t2.PROJECT_ID AND t1.BEDROOMS = t2.BEDROOMS AND t1.OPTION_CATEGORY = 'Actual' AND
            t2.OPTION_CATEGORY = 'Logical' AND t1.SIZE/t1.CARPET_AREA is not null AND t1.SIZE/t1.CARPET_AREA <> 0
            GROUP BY PROJECT_ID , OPTIONS_ID
            ) as rpo
            JOIN cms.listings lst on rpo.OPTIONS_ID = lst.OPTION_ID
            JOIN cms.project_supplies ps on ps.LISTING_ID = lst.ID and ps.VERSION = 'Website'
            group by  rpo.project_id
QRY;

   $rs = mysql_unbuffered_query($sql, $solrDB);
    if ($rs == FALSE) {
        $logger->info("Error in Executing Project Avg Loading Percentage Query :\n " . $sql."\n" );
        $logger->error("Mysql error : \n". mysql_error());
        exit();
    }

    $avgLoadingPercentage = array();
    while (($row = mysql_fetch_assoc($rs)) != FALSE) {
        $avgLoadingPercentage[$row['PROJECT_ID']] = $row['AVG_LOADING_PERCENTAGE'];
    }

    return $avgLoadingPercentage;
}

function populateProjectPropertyVerifiedListings(){
    global $logger, $solrDB;

    $sql = <<<QRY
            SELECT rp.PROJECT_ID ,min(IF(lp.price_per_unit_area > 0, lp.price_per_unit_area, floor(lp.price / IF(rpo.size > 0, rpo.size, rpo.carpet_area)))) as min_resale_price ,max(IF(lp.price_per_unit_area > 0, lp.price_per_unit_area, floor(lp.price / IF(rpo.size > 0, rpo.size, rpo.carpet_area)))) as max_resale_price, rpo.OPTIONS_ID PROPERTY_ID, COUNT(ls.ID) as LISTING_COUNT FROM cms.resi_project rp
JOIN cms.resi_project_options rpo ON rpo.PROJECT_ID = rp.PROJECT_ID AND rpo.OPTION_CATEGORY in ('Actual','Unverified') AND rp.STATUS = 'Active' AND rp.VERSION = 'website'
JOIN cms.listings ls ON rpo.OPTIONS_ID = ls.OPTION_ID AND ls.LISTING_CATEGORY = 'Resale' AND ls.STATUS = 'Active' AND ls.booking_status_id = 1
LEFT JOIN cms.listing_prices lp on (lp.id = ls.current_price_id)
GROUP BY OPTIONS_ID , PROJECT_ID
ORDER BY PROJECT_ID
QRY;

    $rs = mysql_unbuffered_query($sql, $solrDB);
    if ($rs == FALSE) {
        $logger->info("Error in Executing Project and Property verified listings Query :\n " . $sql."\n" );
        $logger->error("Mysql error : \n". mysql_error());
        exit();
    }


    $projectVerifiedListingCount = array();
    $propertyVerifiedListingCount = array();
    $listingMinPricePerUnitArea = array();
    $listingMaxPricePerUnitArea = array();

     while (($row = mysql_fetch_assoc($rs)) != FALSE) {
        // Set lising count for project
        if( isset($projectVerifiedListingCount[$row['PROJECT_ID']])){
            $projectVerifiedListingCount[$row['PROJECT_ID']] = $projectVerifiedListingCount[$row['PROJECT_ID']] + $row['LISTING_COUNT'];
        }else{
            $projectVerifiedListingCount[$row['PROJECT_ID']] = $row['LISTING_COUNT'];

        }
        // Set Listing Count for Property
        $propertyVerifiedListingCount[$row['PROPERTY_ID']] = $row['LISTING_COUNT'];
        $listingMinPricePerUnitArea[$row['PROPERTY_ID']] = $row['min_resale_price'];
        $listingMaxPricePerUnitArea[$row['PROPERTY_ID']] = $row['max_resale_price'];
    }

    return array($projectVerifiedListingCount, $propertyVerifiedListingCount, $listingMinPricePerUnitArea, $listingMaxPricePerUnitArea);
}

function getProjectIdToPopularityMap(){
	global $logger, $solrDB;

	$sql = <<<QRY
	 SELECT object_id AS PROJECT_ID, user_popularity_index as USER_POPULARITY_INDEX FROM analytics.object_popularity_table where object_type_id = 1
QRY;

	$rs = mysql_unbuffered_query($sql, $solrDB);
	if ($rs == FALSE) {
		$logger->info("Error in Executing Project Popularity Query (analtytics.object_popularity_table).\n " . $sql."\n" );
		$logger->error("Mysql error : \n". mysql_error());
		exit();
	}

	$mapProjectIdToPopularity = array();

	while (($row = mysql_fetch_assoc($rs)) != FALSE) {
		if(isset($row['PROJECT_ID']) && isset($row['USER_POPULARITY_INDEX'])){
			$mapProjectIdToPopularity[$row['PROJECT_ID']] = $row['USER_POPULARITY_INDEX'];
		}
	}

	return $mapProjectIdToPopularity;
}

function getProjectsHavingCatchementReport(){
	global $logger, $solrDB;
	$sql = <<<QRY
SELECT
    DISTINCT OBJECT_ID AS PROJECT_ID
FROM
    proptiger.media m
        JOIN
    proptiger.object_media_types omt ON m.object_media_type_id = omt.id AND omt.type = 'projectInsight' AND m.active = 1
        JOIN
    proptiger.ObjectType ot ON ot.id = omt.ObjectType_id AND ot.type = 'project'
QRY;
        $rs = mysql_unbuffered_query($sql, $solrDB);
	if ($rs == FALSE) {
		$logger->info("Error in Fetching Projects having catchment/project-insight report.\n " . $sql."\n" );
		$logger->error("Mysql error : \n". mysql_error());
		exit();
	}
        $projectReportMap = array();

	while (($row = mysql_fetch_assoc($rs)) != FALSE) {
		if(isset($row['PROJECT_ID'])){
			$projectReportMap[$row['PROJECT_ID']] = $row['PROJECT_ID'];
		}
	}

        return $projectReportMap;
}

function loadPrimaryPriceForLocalityByUnitType(){
	global $logger;
	$objectTypeId = "locality_id";
	$sql = <<<SQL
		select a.$objectTypeId, a.effective_month, a.unit_type,
                        sum(average_price_per_unit_area*ltd_supply)/sum(if(average_price_per_unit_area is null, 0, ltd_supply)) as average_price_per_unit_area
                    from cms.d_inventory_prices a
                    inner join (select $objectTypeId, min(effective_month) m1, max(effective_month) m2 , unit_type
                                from cms.d_inventory_prices
                                where average_price_per_unit_area is not null
                                group by $objectTypeId,unit_type) t
                        on a.$objectTypeId = t.$objectTypeId and (a.effective_month = t.m1 or a.effective_month = t.m2)
                    where a.unit_type=t.unit_type group by a.$objectTypeId, a.unit_type, a.effective_month
                    order by a.$objectTypeId, a.unit_type, a.effective_month
SQL;

	$rs = mysql_unbuffered_query($sql);
	if(empty($rs)){
		$logger->info("Error in fetching primary price for locality by unit type.\n " . $sql."\n" );
		$logger->error("Mysql error : \n". mysql_error());
		exit();
	}

	$localityPriceUnitType = array();
	while($row = mysql_fetch_assoc($rs)){
		if(empty($localityPriceUnitType[$row['unit_type']])){
			$localityPriceUnitType[$row[$objectTypeId]][$row['unit_type']] = array();
		}
		$localityPriceUnitType[$row[$objectTypeId]][$row['unit_type']] = $row['average_price_per_unit_area'];
	}

	return $localityPriceUnitType;
}

function getProjectsHavingPrimaryExpandedListing(){
	global $logger;
	$sql = <<<SQL
SELECT DISTINCT
    rp.PROJECT_ID
FROM
    cms.listings l
        JOIN
    cms.resi_project_options rpo

        JOIN

    cms.resi_project rp

ON rp.project_id = rpo.project_id AND rp.version = 'website' AND rp.status = 'Active' AND rpo.option_category = 'Actual' AND rpo.options_id = l.option_id AND l.listing_category = 'PrimaryExpanded' AND l.status = 'Active'
SQL;

	$rs = mysql_unbuffered_query($sql);
        if(empty($rs)){
                $logger->info("Error in fetching projects having primary expanded listing.\n " . $sql."\n" );
                $logger->error("Mysql error : \n". mysql_error());
                exit();
        }

	$primaryExpandedProjects = array();
	while($row = mysql_fetch_assoc($rs)){
		$primaryExpandedProjects[$row['PROJECT_ID']] = 1;
	}

	return $primaryExpandedProjects;
}

function getProjectPropertyBuilderLocalityWithSource(){
  global $logger;

  $sql = "SELECT
	rp.PROJECT_ID,  rp.BUILDER_ID, rp.LOCALITY_ID, rpo.OPTIONS_ID, IFNULL(src.DOMAIN_NAME, 'Proptiger') AS SOURCE_DOMAIN, src.id AS SOURCE_ID
FROM
    cms.resi_project rp
        JOIN
    cms.resi_project_options rpo ON (rp.project_id = rpo.PROJECT_ID  AND rpo.option_category = 'Actual' AND rp.version = 'website' AND rp.status = 'Active')
        LEFT JOIN
    cms.listings l ON (rpo.options_id = l.option_id AND l.status = 'Active')
       LEFT JOIN
    (SELECT
        sr.id AS id, msd.domain_name AS domain_name
    FROM
        cms.source sr
    JOIN cms.master_source_domain msd ON msd.id = sr.domain_id) src ON l.source_id = src.id

GROUP BY PROJECT_ID, OPTIONS_ID, SOURCE_DOMAIN, SOURCE_ID ";

$rs = mysql_unbuffered_query($sql);
      if(empty($rs)){
              $logger->info("Error in fetching projects and property with source \n " . $sql."\n" );
              $logger->error("Mysql error : \n". mysql_error());
              exit();
      }

      $projectSourceArray = array();
      $propertySourceArray = array();
      $builderSourceArray = array();
      $localitySourceArray = array();

      while($row = mysql_fetch_assoc($rs)){
        isset($projectSourceArray[$row['PROJECT_ID']]) ?: $projectSourceArray[$row['PROJECT_ID']] = array("SOURCE_ID" => null, "SOURCE_DOMAIN" => null);
        isset($propertySourceArray[$row['OPTIONS_ID']])?: $propertySourceArray[$row['OPTIONS_ID']] = array("SOURCE_ID" => null, "SOURCE_DOMAIN" => null);
        isset($builderSourceArray[$row['BUILDER_ID']])?: $builderSourceArray[$row['BUILDER_ID']] = array("SOURCE_ID" => null, "SOURCE_DOMAIN" => null);
        isset($localitySourceArray[$row['LOCALITY_ID']])?: $localitySourceArray[$row['LOCALITY_ID']] = array("SOURCE_ID" => null, "SOURCE_DOMAIN" => null);

        if(isset($row['SOURCE_ID'])){
          isset($projectSourceArray[$row['PROJECT_ID']]['SOURCE_ID']) ?: $projectSourceArray[$row['PROJECT_ID']]['SOURCE_ID'] = array();
          isset($propertySourceArray[$row['OPTIONS_ID']]['SOURCE_ID']) ?: $propertySourceArray[$row['OPTIONS_ID']]['SOURCE_ID'] = array();
          isset($builderSourceArray[$row['BUILDER_ID']]['SOURCE_ID']) ?: $builderSourceArray[$row['BUILDER_ID']]['SOURCE_ID'] = array();
          isset($localitySourceArray[$row['LOCALITY_ID']]['SOURCE_ID']) ?: $localitySourceArray[$row['LOCALITY_ID']]['SOURCE_ID'] = array();

          in_array($row['SOURCE_ID'], $projectSourceArray[$row['PROJECT_ID']]['SOURCE_ID']) ?: array_push($projectSourceArray[$row['PROJECT_ID']]['SOURCE_ID'], $row['SOURCE_ID']);
          in_array($row['SOURCE_ID'], $propertySourceArray[$row['OPTIONS_ID']]['SOURCE_ID']) ?: array_push($propertySourceArray[$row['OPTIONS_ID']]['SOURCE_ID'], $row['SOURCE_ID']);
          in_array($row['SOURCE_ID'], $builderSourceArray[$row['BUILDER_ID']]['SOURCE_ID']) ?: array_push($builderSourceArray[$row['BUILDER_ID']]['SOURCE_ID'], $row['SOURCE_ID']);
          in_array($row['SOURCE_ID'], $localitySourceArray[$row['LOCALITY_ID']]['SOURCE_ID']) ?: array_push($localitySourceArray[$row['LOCALITY_ID']]['SOURCE_ID'], $row['SOURCE_ID']);
        }

        if(isset($row['SOURCE_DOMAIN'])){
          isset($projectSourceArray[$row['PROJECT_ID']]['SOURCE_DOMAIN']) ?: $projectSourceArray[$row['PROJECT_ID']]['SOURCE_DOMAIN'] = array();
          isset($propertySourceArray[$row['OPTIONS_ID']]['SOURCE_DOMAIN']) ?: $propertySourceArray[$row['OPTIONS_ID']]['SOURCE_DOMAIN'] = array();
          isset($builderSourceArray[$row['BUILDER_ID']]['SOURCE_DOMAIN']) ?: $builderSourceArray[$row['BUILDER_ID']]['SOURCE_DOMAIN'] = array();
          isset($localitySourceArray[$row['LOCALITY_ID']]['SOURCE_DOMAIN']) ?: $localitySourceArray[$row['LOCALITY_ID']]['SOURCE_DOMAIN'] = array();

          in_array($row['SOURCE_DOMAIN'], $projectSourceArray[$row['PROJECT_ID']]['SOURCE_DOMAIN']) ?: array_push($projectSourceArray[$row['PROJECT_ID']]['SOURCE_DOMAIN'], $row['SOURCE_DOMAIN']);
          in_array($row['SOURCE_DOMAIN'], $propertySourceArray[$row['OPTIONS_ID']]['SOURCE_DOMAIN']) ?: array_push($propertySourceArray[$row['OPTIONS_ID']]['SOURCE_DOMAIN'], $row['SOURCE_DOMAIN']);
          in_array($row['SOURCE_DOMAIN'], $builderSourceArray[$row['BUILDER_ID']]['SOURCE_DOMAIN']) ?: array_push($builderSourceArray[$row['BUILDER_ID']]['SOURCE_DOMAIN'], $row['SOURCE_DOMAIN']);
          in_array($row['SOURCE_DOMAIN'], $localitySourceArray[$row['LOCALITY_ID']]['SOURCE_DOMAIN']) ?: array_push($localitySourceArray[$row['LOCALITY_ID']]['SOURCE_DOMAIN'], $row['SOURCE_DOMAIN']);
        }
      }

      return array('PROJECT_SOURCE_ARRAY' => $projectSourceArray, 'PROPERTY_SOURCE_ARRAY' => $propertySourceArray, 'BUILDER_SOURCE_ARRAY' => $builderSourceArray, 'LOCALITY_SOURCE_ARRAY' => $localitySourceArray);
}

?>
