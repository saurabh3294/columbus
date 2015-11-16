<?php
include_once 'locationFunctions.php';
require_once 'projectCountFunctions.php';

define("PROJECT_GROUP_ALL", "all");
define("PROJECT_GROUP_LUXURY", "luxury");
define("PROJECT_GROUP_AFFORDABLE", "affordable");
define("PROJECT_GROUP_NEW_LAUNCH", "newlaunch");
define("PROJECT_GROUP_UNDER_CONSTRUCTION", "underconst");
define("PROJECT_GROUP_COMPLETED", "completed");
define("PROJECT_GROUP_RESALE", "resale");


define("DOMAIN_OBJECT_ID_SUBURB","7");
define("DOMAIN_OBJECT_ID_LOCALITY","4");
define("DOMAIN_OBJECT_ID_BUILDER","3");

function getTypeaheadDocumentsFromDB($domain) {
	global $logger, $paramProjectIds, $citiesOnProjectIds, $suburbsOnProjectIds, $localitiesOnProjectIds, $buildersOnProjectIds;
	global $projCmsActiveCondition, $projectActiveConditionArray, $version, $solrDB;
    global $paramLocalityIds, $paramSuburbIds, $paramCityIds, $paramBuilderIds;
    global $typeaheadSolrLocalityList, $typeaheadSolrSuburbsList, $typeaheadSolrCitiesList, $typeaheadSolrBuilderList, $solrTypeaheadList, $elasticSearchClient;
    $citiesCondition = $localitiesCondition = $suburbsCondition = $buildersCondition = " RP.STATUS IN ({$projCmsActiveCondition})";
    $domain = "\"$domain\"";
    $projectCondition = "";
	if (!empty ( $paramProjectIds ) || !empty($paramLocalityIds) || !empty($paramSuburbIds) 
		|| !empty($paramCityIds) || !empty($paramBuilderIds)) {

        if(empty($paramProjectIds))
            $paramProjectIds = -1;
        $projectCond = "RP.PROJECT_ID IN ($paramProjectIds)";
		$projectCondition = " AND  $projectCond";

		if (empty ( $paramCityIds ))
			$paramCityIds = - 1;
		$citiesCondition = " (C.CITY_ID IN ($paramCityIds) OR $projectCond) ";

		if (empty ( $paramLocalityIds))
			$paramLocalityIds = - 1;
		$localitiesCondition = " (L.LOCALITY_ID IN ($paramLocalityIds) OR $projectCond)  ";

		if (empty ( $paramSuburbIds))
			$paramSuburbIds = - 1;
		$suburbsCondition = " (S.SUBURB_ID IN ($paramSuburbIds) OR $projectCond) ";

		if (empty ( $paramBuilderIds ))
			$paramBuilderIds = - 1;
		$buildersCondition = " (B.BUILDER_ID IN ($paramBuilderIds) OR $projectCond)  ";
	}
	
	$deletedDocuments = array();

	$projectCondition = appendConditionForB2b($projectCondition);

	//elastic search data
	$eSData = populateTopSearch();
 
	$documents = array ();
	$sqls = array("SELECT CONCAT('TYPEAHEAD-CITY-', C.CITY_ID) AS TYPEAHEAD_ID, 'TYPEAHEAD' AS DOCUMENT_TYPE, C.LABEL AS TYPEAHEAD_LABEL,
                    C.LABEL AS TYPEAHEAD_CITY, C.URL AS TYPEAHEAD_REDIRECT_URL,
					C.LABEL AS tp_city_new, C.LABEL AS city,
                    'CITY' AS TYPEAHEAD_TYPE, C.LABEL AS TYPEAHEAD_DISPLAY_TEXT, C.LABEL AS TYPEAHEAD_CORE_TEXT,
                    999 AS DISPLAY_ORDER, '' AS LOCALITY_URL,
                    'CITY' AS TYPE, RP.LOCALITY_ID AS LOCALITY_ID, RP.STATUS, C.CITY_ID,
                    C.CENTER_LATITUDE AS LATITUDE, C.CENTER_LONGITUDE AS LONGITUDE
                    FROM cms.resi_project RP
                    JOIN cms.locality L ON (RP.LOCALITY_ID = L.LOCALITY_ID AND RP.VERSION = $version)
					JOIN cms.suburb S ON (L.SUBURB_ID = S.SUBURB_ID)
                    JOIN cms.city C ON (C.CITY_ID = S.CITY_ID)
					JOIN cms.resi_project_options RPO ON RP.PROJECT_ID=RPO.PROJECT_ID
					JOIN cms.listings LS ON LS.option_id=RPO.OPTIONS_ID
					JOIN cms.source SRC ON SRC.id=LS.source_id
					JOIN cms.master_source_domain MSD ON MSD.id=SRC.domain_id
                    WHERE $citiesCondition AND C.URL IS NOT NULL AND C.URL != '' AND RP.RESIDENTIAL_FLAG='Residential' AND MSD.domain_name=$domain
                    GROUP BY TYPEAHEAD_ID",
                    
                    "SELECT CONCAT('TYPEAHEAD-LOCALITY-', L.LOCALITY_ID) AS TYPEAHEAD_ID, 'TYPEAHEAD' AS DOCUMENT_TYPE,
                    L.LABEL AS TYPEAHEAD_LOCALITY,
                    CONCAT_WS(' ', L.LABEL, C.LABEL) AS TYPEAHEAD_LABEL,
                    C.LABEL AS TYPEAHEAD_CITY, L.URL AS TYPEAHEAD_REDIRECT_URL, 'LOCALITY' AS TYPEAHEAD_TYPE,
                    CONCAT(L.LABEL, ' - ', C.LABEL) AS TYPEAHEAD_DISPLAY_TEXT, L.LABEL AS TYPEAHEAD_CORE_TEXT,
                    CONCAT_WS(' ', L.LABEL, C.LABEL) AS tp_locality_city, L.LABEL AS locality,
					L.LABEL AS tp_locality_new, C.LABEL AS city, S.LABEL AS suburb,
                    999 AS DISPLAY_ORDER, L.URL AS LOCALITY_URL,
                    'LOCALITY' AS TYPE, RP.LOCALITY_ID AS LOCALITY_ID, RP.STATUS, C.CITY_ID,
                    L.LATITUDE AS LATITUDE, L.LONGITUDE AS LONGITUDE
                    FROM cms.resi_project RP
                    JOIN cms.locality L ON (L.LOCALITY_ID = RP.LOCALITY_ID AND RP.VERSION = $version )
					JOIN cms.suburb S ON (L.SUBURB_ID = S.SUBURB_ID)
                    JOIN cms.city C ON (C.CITY_ID = S.CITY_ID)
                    JOIN cms.resi_project_options RPO ON (RP.PROJECT_ID=RPO.PROJECT_ID)
					JOIN cms.listings LS ON LS.option_id=RPO.OPTIONS_ID
					JOIN cms.source SRC ON SRC.id=LS.source_id
					JOIN cms.master_source_domain MSD ON MSD.id=SRC.domain_id
                    WHERE $localitiesCondition AND L.URL IS NOT NULL AND L.URL != '' AND L.LABEL != 'Other' 
                    AND RP.RESIDENTIAL_FLAG='Residential'  AND MSD.domain_name=$domain 
                    AND RP.STATUS IN ($projCmsActiveCondition) AND RP.version = 'Website' AND RPO.OPTION_CATEGORY = 'Actual'
                    GROUP BY TYPEAHEAD_ID",
                    
                    "SELECT CONCAT('TYPEAHEAD-SUBURB-', S.SUBURB_ID) AS TYPEAHEAD_ID, 'TYPEAHEAD' AS DOCUMENT_TYPE,
                    CONCAT_WS(' ', S.LABEL, C.LABEL) AS TYPEAHEAD_LABEL,
                    C.LABEL AS TYPEAHEAD_CITY, S.URL AS TYPEAHEAD_REDIRECT_URL, 'SUBURB' AS TYPEAHEAD_TYPE,
                    CONCAT(S.LABEL, ' - ', C.LABEL) AS TYPEAHEAD_DISPLAY_TEXT, S.LABEL AS TYPEAHEAD_CORE_TEXT,
                    CONCAT_WS(' ', S.LABEL, C.LABEL) AS tp_suburb_city,
					S.LABEL AS tp_suburb_new, C.LABEL AS city, S.LABEL AS suburb,
                    999 AS DISPLAY_ORDER, '' AS LOCALITY_URL,
                    'SUBURB' AS TYPE,  RP.LOCALITY_ID AS LOCALITY_ID, RP.STATUS, C.CITY_ID,
                    S.LATITUDE AS LATITUDE, S.LONGITUDE AS LONGITUDE, S.SUBURB_ID
                    FROM cms.resi_project RP
                    JOIN cms.resi_project_options RPO ON (RP.PROJECT_ID=RPO.PROJECT_ID)
		   			JOIN cms.locality L
		    		ON L.LOCALITY_ID = RP.LOCALITY_ID AND RP.VERSION = $version 
                    JOIN cms.suburb S
                    ON (S.SUBURB_ID = L.SUBURB_ID)
                    JOIN cms.city C ON (C.CITY_ID = S.CITY_ID)
					JOIN cms.listings LS ON LS.option_id=RPO.OPTIONS_ID
					JOIN cms.source SRC ON SRC.id=LS.source_id
					JOIN cms.master_source_domain MSD ON MSD.id=SRC.domain_id
                    LEFT JOIN cms.locality LL ON (LL.LABEL = S.LABEL AND LL.STATUS = 'Active')
                    WHERE $suburbsCondition AND S.URL IS NOT NULL AND S.URL != '' AND LL.LOCALITY_ID IS NULL AND RP.RESIDENTIAL_FLAG='Residential' 
                    AND RP.STATUS IN ($projCmsActiveCondition) AND RP.version = 'Website' AND RPO.OPTION_CATEGORY = 'Actual' AND MSD.domain_name=$domain
                    GROUP BY TYPEAHEAD_ID",
                    
                    "SELECT CONCAT('TYPEAHEAD-BUILDER-', B.BUILDER_ID) AS TYPEAHEAD_ID, 'TYPEAHEAD' AS DOCUMENT_TYPE, B.BUILDER_NAME AS TYPEAHEAD_LABEL,
                    D.LABEL AS TYPEAHEAD_CITY, B.URL AS TYPEAHEAD_REDIRECT_URL,
					B.BUILDER_NAME AS tp_builder_new,
                    'BUILDER' AS TYPEAHEAD_TYPE, B.BUILDER_NAME AS TYPEAHEAD_DISPLAY_TEXT, B.BUILDER_NAME AS TYPEAHEAD_CORE_TEXT,
                    999 AS DISPLAY_ORDER, '' AS LOCALITY_URL, 
                    'BUILDER' AS TYPE,  RP.LOCALITY_ID AS LOCALITY_ID, RP.STATUS, C.CITY_ID, GROUP_CONCAT(DISTINCT(C.CITY_ID)) AS BUILDER_CITY_IDS,
                    GROUP_CONCAT(DISTINCT (CONCAT_WS(':', C.CITY_ID, LOWER(C.LABEL), CONCAT_WS('/', LOWER(C.LABEL), B.URL)))) AS BUILDER_CITY_INFO,
					B.BUILDER_ID, B.BUILDER_NAME AS builder
                    FROM cms.resi_project RP
                    JOIN cms.resi_project_options RPO ON (RP.PROJECT_ID=RPO.PROJECT_ID)
                    JOIN cms.locality L ON (RP.LOCALITY_ID = L.LOCALITY_ID AND RP.VERSION = $version)
					JOIN cms.suburb S ON (L.SUBURB_ID = S.SUBURB_ID)
                    JOIN cms.city C ON (C.CITY_ID = S.CITY_ID)
                    JOIN cms.resi_builder  B ON (B.BUILDER_ID = RP.BUILDER_ID)
                    JOIN cms.city D on (B.CITY_ID = D.CITY_ID)
					JOIN cms.listings LS ON LS.option_id=RPO.OPTIONS_ID
					JOIN cms.source SRC ON SRC.id=LS.source_id
					JOIN cms.master_source_domain MSD ON MSD.id=SRC.domain_id
                    WHERE $buildersCondition AND B.URL IS NOT NULL AND B.URL != '' AND RP.RESIDENTIAL_FLAG='Residential' 
                    AND RP.STATUS IN ($projCmsActiveCondition) AND RP.version = 'Website' AND RPO.OPTION_CATEGORY = 'Actual' AND MSD.domain_name=$domain
                    GROUP BY TYPEAHEAD_ID",
                    
                    "SELECT CONCAT('TYPEAHEAD-PROJECT-', RP.PROJECT_ID) AS TYPEAHEAD_ID, RP.PROJECT_ID AS PROJECT_ID, 'TYPEAHEAD' AS DOCUMENT_TYPE,
                    CONCAT_WS(' ', B.BUILDER_NAME, RP.PROJECT_NAME, L.LABEL, C.LABEL) AS TYPEAHEAD_LABEL,
                    C.LABEL AS TYPEAHEAD_CITY, L.LABEL AS TYPEAHEAD_LOCALITY, RP.PROJECT_URL AS TYPEAHEAD_REDIRECT_URL,
                    'PROJECT' AS TYPEAHEAD_TYPE, CONCAT(B.BUILDER_NAME, ' - ', RP.PROJECT_NAME, ' - ', L.LABEL, ' - ', C.LABEL) AS TYPEAHEAD_DISPLAY_TEXT,
                    CONCAT_WS(' ', B.BUILDER_NAME, RP.PROJECT_NAME) AS TYPEAHEAD_CORE_TEXT,
                    RP.DISPLAY_ORDER, L.URL AS LOCALITY_URL, 'PROJECT' as TYPE,  RP.LOCALITY_ID AS LOCALITY_ID, RP.STATUS, C.CITY_ID,
					RP.PROJECT_NAME AS tp_project_new, C.LABEL AS city, L.LABEL AS locality, S.LABEL AS suburb, B.BUILDER_NAME AS builder,
					CONCAT_WS(' ', B.BUILDER_NAME, C.LABEL) AS tp_builder_city,
					CONCAT_WS(' ', B.BUILDER_NAME, S.LABEL) AS tp_builder_suburb,
					CONCAT_WS(' ', B.BUILDER_NAME, L.LABEL) AS tp_builder_locality,
					CONCAT_WS(' ', L.LABEL	, RP.PROJECT_NAME) AS tp_locality_project,
                    CONCAT_WS(' ', B.BUILDER_NAME, RP.PROJECT_NAME) AS tp_builder_project,	
                    CONCAT_WS(' ', B.BUILDER_NAME, RP.PROJECT_NAME, C.LABEL) AS tp_builder_project_city,
                    CONCAT_WS(' ', B.BUILDER_NAME, RP.PROJECT_NAME, L.LABEL) AS tp_builder_project_locality,
                    CONCAT_WS(' ', B.BUILDER_NAME, RP.PROJECT_NAME, L.LABEL, C.LABEL) AS tp_builder_project_locality_city,
                    CONCAT_WS(' ', B.BUILDER_NAME, RP.PROJECT_NAME, S.LABEL) AS tp_builder_project_suburb,
                    CONCAT_WS(' ', B.BUILDER_NAME, RP.PROJECT_NAME, S.LABEL, C.LABEL) AS tp_builder_project_suburb_city,
					RP.RESIDENTIAL_FLAG AS RESIDENTIAL_FLAG,
					RP.LATITUDE AS LATITUDE, RP.LONGITUDE AS LONGITUDE
					FROM cms.resi_project RP
					JOIN cms.resi_project_options rpo ON (RP.PROJECT_ID = rpo.PROJECT_ID AND rpo.OPTION_CATEGORY = 'Actual' 
                    AND rpo.option_type IN ('apartment', 'villa', 'plot') AND RP.version = $version)
                    JOIN cms.locality L ON (L.LOCALITY_ID = RP.LOCALITY_ID)
			        JOIN cms.suburb S ON (S.SUBURB_ID = L.SUBURB_ID)
                    JOIN cms.city C ON (C.CITY_ID = S.CITY_ID)
                    JOIN cms.resi_builder  B ON (B.BUILDER_ID = RP.BUILDER_ID)
                    JOIN cms.project_status_master psm ON (psm.id = RP.project_status_id)
					JOIN cms.listings LS ON LS.option_id=rpo.OPTIONS_ID
					JOIN cms.source SRC ON SRC.id=LS.source_id
					JOIN cms.master_source_domain MSD ON MSD.id=SRC.domain_id
                    WHERE RP.PROJECT_URL IS NOT NULL AND RP.PROJECT_URL != '' $projectCondition
                    AND RP.RESIDENTIAL_FLAG = 'Residential' AND RP.STATUS IN ($projCmsActiveCondition) AND MSD.domain_name=$domain
                    GROUP BY TYPEAHEAD_ID");

	/** Get extra information from other other data sources **/

// 	$logger->info("TF : Loading locality price rise info.");
// 	$localitiesPriceDetails = loadPriceRiseInfo('LOCALITY');

	$mapSuggestionFilters = getTypeaheadSuggestionFiltersFromDB();
	
	$logger->info("TF : Fetching locality project count");
	$localityProjectCountMapOfMaps = getLocalityProjectCountMaps($mapSuggestionFilters[DOMAIN_OBJECT_ID_LOCALITY]);

	$logger->info("TF : Fetching suburb project count");
	$suburbProjectCountMapOfMaps = getSuburbProjectCountMaps($mapSuggestionFilters[DOMAIN_OBJECT_ID_SUBURB]);
	
	$logger->info("TF : Fetching builder project count");
	$builderBuilderCountMapOfMaps = getBuilderProjectCountMaps($mapSuggestionFilters[DOMAIN_OBJECT_ID_BUILDER]);
	
	$logger->info("TF : Fetching project popularity from analytics.");
	$projectIdToPopularitymap =  getProjectIdToPopularityMap();
	
	$logger->info("TF : Fetching locality popularity from analytics.");
	$localityIdToPopularitymap = getLocalityIdToPopularityMap();
	
	$logger->info("TF : Fetching property (bhk) info for projects");
	$projectPropertyMap = getProjectPropertyMap();
	
	$logger->info("TF : Start document retrival from database and parsing");
	
	/** Start document retrival and parsing **/
	
	$documents = array ();
	$arraylen =count($sqls);
	$solrDocsAbsentArr = array();
	for($i = 0; $i<$arraylen;$i++) {

		$result = mysql_unbuffered_query ( $sqls[$i], $solrDB );
		if ($result) {
			while ( $document = mysql_fetch_assoc ( $result ) ) {

				$document ['id'] = $document ['TYPEAHEAD_ID'];
				$document ['TYPEAHEAD_LABEL_LOWERCASE'] = $document ['TYPEAHEAD_LABEL'];
	
				//populateLocalityUnitsInfo($document);

				if ($document ['TYPEAHEAD_TYPE'] == 'LOCALITY') {

//					if (isset ( $localitiesPriceDetails [$document ['LOCALITY_ID']] )) {
//						$localityPriceDoc = $localitiesPriceDetails [$document ['LOCALITY_ID']];
//	
// 						if (! empty ( $localityPriceDoc ['average_price_per_unit_area'] )) {
// 							$document ['TYPEAHEAD_LOCALITY_AVG_PRICE_PER_UNIT_AREA'] = $localityPriceDoc ['average_price_per_unit_area'];
// 						}
// 						if (! empty ( $localityPriceDoc ['LOCALITY_PRICE_RISE_6MONTHS'] )) {
// 							$document ['TYPEAHEAD_LOCALITY_PRICE_APPRECIATION_6MONTHS'] = $localityPriceDoc ['LOCALITY_PRICE_RISE_6MONTHS'];
// 						}
//					}
					
					$document['TYPEAHEAD_ENTITY_POPULARITY'] = 0;
					if(isset($document ['LOCALITY_ID'])) {
						handleFieldPopulationForLocalityDocument($document, $localityProjectCountMapOfMaps);
						if(isset($localityIdToPopularitymap[$document['LOCALITY_ID']])) {
							$document['TYPEAHEAD_ENTITY_POPULARITY'] = $localityIdToPopularitymap[$document['LOCALITY_ID']];
						}
					}

				}

				if ($document ['TYPEAHEAD_TYPE'] == 'SUBURB') {
					if(isset($document ['SUBURB_ID'])) {
						handleFieldPopulationForSuburbDocument($document, $suburbProjectCountMapOfMaps);
					}
				}
				
				if ($document ['TYPEAHEAD_TYPE'] == 'BUILDER'){
					if (!empty ($document ['BUILDER_CITY_IDS'])) {
						$document['BUILDER_CITY_IDS'] = explode(",", $document ['BUILDER_CITY_IDS']);
					}
	
					if (!empty ($document ['BUILDER_CITY_INFO'])) {
						$document['BUILDER_CITY_INFO'] = explode(",", $document ['BUILDER_CITY_INFO']);
					}
					
					if(isset($document ['BUILDER_ID'])) {
						handleFieldPopulationForBuilderDocument($document, $builderBuilderCountMapOfMaps);
					}
				}
				

				if ($document ['TYPEAHEAD_TYPE'] == 'PROJECT'){
					$document['TYPEAHEAD_ENTITY_POPULARITY'] = 0;
					if(isset($document['PROJECT_ID'])) {
						if(isset($projectIdToPopularitymap[$document['PROJECT_ID']])){
							$document['TYPEAHEAD_ENTITY_POPULARITY'] = $projectIdToPopularitymap[$document['PROJECT_ID']];
						}
						if (isset($projectPropertyMap[$document['PROJECT_ID']])){
							$document['TYPEAHEAD_PROJECT_PROPERTY_INFO'] = array_values($projectPropertyMap[$document['PROJECT_ID']]);
						}
					}
				}
				
				//create document map
				$typeaheadId = explode("-", $document ['TYPEAHEAD_ID']);
				$entityId = $typeaheadId[2];
				

				/** Get to-be-deleted documents **/
				unset($solrTypeaheadList[$document['id']]);
				
				/** Unset unnecessary fields **/
				unsetIfEmpty($document, 'LOCALITY_URL');
				unsetIfEmpty($document, 'LOCALITY_ID');
				unsetIfEmpty($document, 'LATITUDE');
				unsetIfEmpty($document, 'LONGITUDE');
				unset ($document ['TYPEAHEAD_ID']);
				unset ($document ['RESIDENTIAL_FLAG']);
				unset ($document ['STATUS']);
				unset ($document ['TYPE']);

				$documents[$entityId] = $document;
			}	// end-of-while

		} 
		else {
				$logger->error("Error in fetching Typeahead data using query : \n ". $sqls[$i]."\n");
				$logger->error("Mysql error : \n". mysql_error());
				die();
		}
	}		// end-of-for
	

	$solrTopSearchConfig = array("suburb"=> "TYPEAHEAD_TOP_SEARCHED_SUBURB", "locality"=>"TYPEAHEAD_TOP_SEARCHED_LOCALITY", "project"=>"TYPEAHEAD_TOP_SEARCHED_PROJECT", "builder"=>"TYPEAHEAD_TOP_SEARCHED_BUILDER");
	
	//populate documents with topsearch entitly details
	foreach ($eSData as $entityId => $objectTopSearchData) {
		if(isset($documents[$entityId]) && !empty($documents[$entityId])){
			// searchObjects are lower level entities if entity id is id of city than searchObjects will be suburb,locality,builder,project 

			foreach ($objectTopSearchData as $searchObject => $ids) {
				$fieldArr = topsearchEntityWise($searchObject, $ids, $documents, $solrDocsAbsentArr);
				$field = $solrTopSearchConfig[$searchObject];
				if(!empty($fieldArr) ) {
					$documents[$entityId][$field] = json_encode($fieldArr);
				}
				
			}
		}
	}

	$logger->info(" TF : solr docs absent for these entities which are present in elastic-search  : \n ". sizeof($solrDocsAbsentArr)."\n" );

	$documents = array_values($documents);

	/* make list of to-be-deleted douments */
	foreach($solrTypeaheadList as $key => $value){
		$deletedDocuments[] = $key;
	}
	

	return array ( $deletedDocuments, $documents);
}

function unsetIfEmpty(&$array, $key){
	global $logger;
	if (empty ( $array [$key] )) {
		unset ( $array [$key] );
	}
}

function topsearchEntityWise($searchObject, $ids, $documents, &$solrDocsAbsentArr){
	$fieldArr = array();
	foreach ($ids as $id=>$doc_count) {
		if(isset($documents[$id])) {
			$tmp = topSearchDataFormatting($id, $doc_count, $documents);
			array_push($fieldArr, $tmp);
		}
		else {
			$tmp1 = array();
			$tmp1['entity'] = $searchObject;
			$tmp1['id'] = $id;
			array_push($solrDocsAbsentArr, $tmp1);
		}
	}
	return $fieldArr;
	
}

function topSearchDataFormatting($id, $doc_count, $documents){
	$tmp = array();
	$tmp['id'] = $documents[$id]["id"];
	$tmp['score'] =  $doc_count;
	return $tmp;
	
}


function getTypeaheadSuggestionFiltersFromDB(){

	global $logger, $solrDB;
	
	$sql = "SELECT TS.entity_type_id AS DOMAIN_OBJECT_ID, M.typeahead_suggestion_type as SUGGESTION_TYPE, 
			TS.redirect_url_filters as FILTER FROM proptiger.typeahead_suggestions TS 
			JOIN master_typeahead_suggestion_types M ON (TS.suggestion_type_id = M.id)";

	$mapSuggestionFilters = array();
	$result = mysql_unbuffered_query($sql, $solrDB);
	$key = "";
	if ($result) {
		while ($document = mysql_fetch_assoc($result) ) {
			$domainObjectId = $document['DOMAIN_OBJECT_ID'];
			if(!isset($mapSuggestionFilters[$domainObjectId])){
				$mapSuggestionFilters[$domainObjectId] = array();
			}
			$mapSuggestionFilters[$domainObjectId][$document['SUGGESTION_TYPE']] = $document['FILTER'];  
		}
		return $mapSuggestionFilters;
	}
	else {
			$logger->error("Error in fetching Typeahead Suggestion filters using query : \n ". $sql ."\n");
			$logger->error("Mysql error : \n". mysql_error());
			die();
	}
}

function handleFieldPopulationForLocalityDocument(&$document, $localityProjectCountMapOfMaps){
	$localityId = $document ['LOCALITY_ID'];
	setDocumentFieldFromMap($document, 'TYPEAHEAD_ENTITY_PROJECT_COUNT_TOTAL', $localityProjectCountMapOfMaps[PROJECT_GROUP_ALL], $localityId);
	setDocumentFieldFromMap($document, 'TYPEAHEAD_ENTITY_PROJECT_COUNT_LUXURY', $localityProjectCountMapOfMaps[PROJECT_GROUP_LUXURY], $localityId);
	setDocumentFieldFromMap($document, 'TYPEAHEAD_ENTITY_PROJECT_COUNT_AFFORDABLE', $localityProjectCountMapOfMaps[PROJECT_GROUP_AFFORDABLE], $localityId);
	setDocumentFieldFromMap($document, 'TYPEAHEAD_ENTITY_PROJECT_COUNT_NEW_LAUNCH', $localityProjectCountMapOfMaps[PROJECT_GROUP_NEW_LAUNCH], $localityId);
	setDocumentFieldFromMap($document, 'TYPEAHEAD_ENTITY_PROJECT_COUNT_UNDER_CONSTRUCTION', $localityProjectCountMapOfMaps[PROJECT_GROUP_UNDER_CONSTRUCTION], $localityId);
	setDocumentFieldFromMap($document, 'TYPEAHEAD_ENTITY_PROJECT_COUNT_RESALE', $localityProjectCountMapOfMaps[PROJECT_GROUP_RESALE], $localityId);	
}


function handleFieldPopulationForSuburbDocument(&$document, $suburbProjectCountMapOfMaps){
	$suburbId = $document ['SUBURB_ID'];
	setDocumentFieldFromMap($document, 'TYPEAHEAD_ENTITY_PROJECT_COUNT_TOTAL', $suburbProjectCountMapOfMaps[PROJECT_GROUP_ALL], $suburbId);
	setDocumentFieldFromMap($document, 'TYPEAHEAD_ENTITY_PROJECT_COUNT_LUXURY', $suburbProjectCountMapOfMaps[PROJECT_GROUP_LUXURY], $suburbId);
	setDocumentFieldFromMap($document, 'TYPEAHEAD_ENTITY_PROJECT_COUNT_AFFORDABLE', $suburbProjectCountMapOfMaps[PROJECT_GROUP_AFFORDABLE], $suburbId);
	setDocumentFieldFromMap($document, 'TYPEAHEAD_ENTITY_PROJECT_COUNT_NEW_LAUNCH', $suburbProjectCountMapOfMaps[PROJECT_GROUP_NEW_LAUNCH], $suburbId);
	setDocumentFieldFromMap($document, 'TYPEAHEAD_ENTITY_PROJECT_COUNT_UNDER_CONSTRUCTION', $suburbProjectCountMapOfMaps[PROJECT_GROUP_UNDER_CONSTRUCTION], $suburbId);
	setDocumentFieldFromMap($document, 'TYPEAHEAD_ENTITY_PROJECT_COUNT_RESALE', $suburbProjectCountMapOfMaps[PROJECT_GROUP_RESALE], $suburbId);
}

function handleFieldPopulationForBuilderDocument(&$document, $builderProjectCountMapOfMaps){
	$builderId = $document ['BUILDER_ID'];
	setDocumentFieldFromMap($document, 'TYPEAHEAD_ENTITY_PROJECT_COUNT_TOTAL', $builderProjectCountMapOfMaps[PROJECT_GROUP_ALL], $builderId);
	setDocumentFieldFromMap($document, 'TYPEAHEAD_ENTITY_PROJECT_COUNT_NEW_LAUNCH', $builderProjectCountMapOfMaps[PROJECT_GROUP_NEW_LAUNCH], $builderId);
	setDocumentFieldFromMap($document, 'TYPEAHEAD_ENTITY_PROJECT_COUNT_UNDER_CONSTRUCTION', $builderProjectCountMapOfMaps[PROJECT_GROUP_UNDER_CONSTRUCTION], $builderId);
	setDocumentFieldFromMap($document, 'TYPEAHEAD_ENTITY_PROJECT_COUNT_COMPLETED', $builderProjectCountMapOfMaps[PROJECT_GROUP_COMPLETED], $builderId);
}

function setDocumentFieldFromMap(&$document, $fieldname, $map, $mapkey){
	if (isset($map[$mapkey])) {
		$document [$fieldname] = $map[$mapkey];
	}
}

function getLocalityProjectCountMaps($filterMap){
	$projectCountMapOfMaps = array();
	$projectCountMapOfMaps[PROJECT_GROUP_ALL] = getLocalityProjectCountTotal();
	$projectCountMapOfMaps[PROJECT_GROUP_LUXURY] = getLocalityProjectCount($filterMap['luxury']);
	$projectCountMapOfMaps[PROJECT_GROUP_AFFORDABLE] = getLocalityProjectCount($filterMap['affordable']);
	$projectCountMapOfMaps[PROJECT_GROUP_NEW_LAUNCH] = getLocalityProjectCount($filterMap['newLaunch']);
	$projectCountMapOfMaps[PROJECT_GROUP_UNDER_CONSTRUCTION] = getLocalityProjectCount($filterMap['underConst']);
	$projectCountMapOfMaps[PROJECT_GROUP_RESALE] = getLocalityProjectCount($filterMap['resale']);
	return $projectCountMapOfMaps;
}

function getSuburbProjectCountMaps($filterMap){
	$projectCountMapOfMaps = array();
	$projectCountMapOfMaps[PROJECT_GROUP_ALL] = getSuburbProjectCountTotal();
	$projectCountMapOfMaps[PROJECT_GROUP_LUXURY] = getSuburbProjectCount($filterMap['luxury']);
	$projectCountMapOfMaps[PROJECT_GROUP_AFFORDABLE] = getSuburbProjectCount($filterMap['affordable']);
	$projectCountMapOfMaps[PROJECT_GROUP_NEW_LAUNCH] = getSuburbProjectCount($filterMap['newLaunch']);
	$projectCountMapOfMaps[PROJECT_GROUP_UNDER_CONSTRUCTION] = getSuburbProjectCount($filterMap['underConst']);
	$projectCountMapOfMaps[PROJECT_GROUP_RESALE] = getSuburbProjectCount($filterMap['resale']);
	return $projectCountMapOfMaps;
}

function getBuilderProjectCountMaps($filterMap){
	$projectCountMapOfMaps = array();
	$projectCountMapOfMaps[PROJECT_GROUP_ALL] = getBuilderProjectCountTotal();
	$projectCountMapOfMaps[PROJECT_GROUP_NEW_LAUNCH] = getBuilderProjectCount($filterMap['upcoming']);
	$projectCountMapOfMaps[PROJECT_GROUP_UNDER_CONSTRUCTION] = getBuilderProjectCount($filterMap['underConst']);
	$projectCountMapOfMaps[PROJECT_GROUP_COMPLETED] = getBuilderProjectCount($filterMap['completed']);
	return $projectCountMapOfMaps;
}

function getTypeaheadLandmarks()
{
	global $solrDB;
	$documents = array();

	$sql = <<<QRY
		SELECT 
	    CONCAT('TYPEAHEAD-LANDMARK-', LM.id) as id,
	    'TYPEAHEAD' AS DOCUMENT_TYPE,
	    CONCAT_WS(', ', LM.NAME, C.LABEL) AS TYPEAHEAD_LABEL,
	    C.LABEL AS TYPEAHEAD_CITY,
	    C.URL AS TYPEAHEAD_REDIRECT_URL,
	    'LANDMARK' AS TYPEAHEAD_TYPE,
	    CONCAT(LM.NAME, ', ', C.LABEL) AS TYPEAHEAD_DISPLAY_TEXT,
	    CONCAT_WS(' ', LM.NAME) AS TYPEAHEAD_CORE_TEXT,
	    LM.NAME AS tp_landmark_new,
	    999 AS DISPLAY_ORDER,
		LM.latitude as LATITUDE,
	    LM.longitude as LONGITUDE
		FROM
	    	cms.landmarks as LM
		    JOIN cms.landmark_types LT ON (LM.place_type_id = LT.id)
			JOIN cms.city C ON (LM.city_id = C.city_id)
		WHERE
	    	(LT.user_maintained = 0) OR LM.status = 'active';   
QRY;

   $documents = array();
   $result = mysql_unbuffered_query($sql, $solrDB);

   if($result)
   {
       while( $document=mysql_fetch_assoc($result) )
       {
            $latitude = $document['LATITUDE'];
            $longitude = $document['LONGITUDE'];

            if( isValidGeo($latitude, $longitude) )
            {
	            array_push($documents, $document);
            }
       }

   }
   else {
   			$logger->error("Error in fetching Landmark Typeaheads using query : \n ". $sql."\n");
   			$logger->error("Mysql error : \n". mysql_error());
   			die();
   }
   return array(array(), $documents);
}

function getTypeaheadTemplates() {
	$documents = array();
	$i=0;

	/* Projects in type templates */

	$documents[$i++] = array("DOCUMENT_TYPE" => 'TYPEAHEAD',
						"id"=>'TYPEAHEAD-TEMPLATE-' . $i,	
						"TYPEAHEAD_TYPE" => 'TEMPLATE',
						"TEMPLATE_TEXT" => 'property in ',
						"DISPLAY_ORDER" => '1',	
						"TEMPLATE_TYPE" => 'PROPERTY_IN');

	$documents[$i++] = array("DOCUMENT_TYPE" => 'TYPEAHEAD',
			"id"=>'TYPEAHEAD-TEMPLATE-' . $i,
			"TYPEAHEAD_TYPE" => 'TEMPLATE',
			"TEMPLATE_TEXT" => 'new property in ',
			"DISPLAY_ORDER" => '1', 
			"TEMPLATE_TYPE" => 'NEW_PROPERTY_IN');
	
	$documents[$i++] = array("DOCUMENT_TYPE" => 'TYPEAHEAD',
			"id"=>'TYPEAHEAD-TEMPLATE-' . $i,
			"TYPEAHEAD_TYPE" => 'TEMPLATE',
			"TEMPLATE_TEXT" => 'upcoming property in ',
			"DISPLAY_ORDER" => '1', 
			"TEMPLATE_TYPE" => 'UPCOMING_PROPERTY_IN');
	
	$documents[$i++] = array("DOCUMENT_TYPE" => 'TYPEAHEAD',
			"id"=>'TYPEAHEAD-TEMPLATE-' . $i,
			"TYPEAHEAD_TYPE" => 'TEMPLATE',
			"TEMPLATE_TEXT" => 'under construction property in ',
			"DISPLAY_ORDER" => '1', 
			"TEMPLATE_TYPE" => 'UNDER_CONSTRUCTION_PROPERTY_IN');

	$documents[$i++] = array("DOCUMENT_TYPE" => 'TYPEAHEAD',
			"id"=>'TYPEAHEAD-TEMPLATE-' . $i,
			"TYPEAHEAD_TYPE" => 'TEMPLATE',
			"TEMPLATE_TEXT" => 'ready to move property in ',
			"DISPLAY_ORDER" => '1', 
			"TEMPLATE_TYPE" => 'READY_TO_MOVE_PROPERTY_IN');

	$documents[$i++] = array("DOCUMENT_TYPE" => 'TYPEAHEAD',
			"id"=>'TYPEAHEAD-TEMPLATE-' . $i,
			"TYPEAHEAD_TYPE" => 'TEMPLATE',
			"TEMPLATE_TEXT" => 'affordable property in ',
			"DISPLAY_ORDER" => '1', 
			"TEMPLATE_TYPE" => 'AFFORDABLE_PROPERTY_IN');

	$documents[$i++] = array("DOCUMENT_TYPE" => 'TYPEAHEAD',
			"id"=>'TYPEAHEAD-TEMPLATE-' . $i,
			"TYPEAHEAD_TYPE" => 'TEMPLATE',
			"TEMPLATE_TEXT" => 'luxury property in ',
			"DISPLAY_ORDER" => '1', 
			"TEMPLATE_TYPE" => 'LUXURY_PROPERTY_IN');
	
	/* Sale - Resale templates */
	
	$documents[$i++] = array("DOCUMENT_TYPE" => 'TYPEAHEAD',
			"id"=>'TYPEAHEAD-TEMPLATE-' . $i,
			"TYPEAHEAD_TYPE" => 'TEMPLATE',
			"TEMPLATE_TEXT" => 'property for sale in ',
			"DISPLAY_ORDER" => '1', 
			"TEMPLATE_TYPE" => 'PROPERTY_FOR_SALE_IN');

	$documents[$i++] = array("DOCUMENT_TYPE" => 'TYPEAHEAD',
			"id"=>'TYPEAHEAD-TEMPLATE-' . $i,
			"TYPEAHEAD_TYPE" => 'TEMPLATE',
			"TEMPLATE_TEXT" => 'property for resale in ',
			"DISPLAY_ORDER" => '1', 
			"TEMPLATE_TYPE" => 'PROPERTY_FOR_RESALE_IN');

	/* Property by buider templates */

	$documents[$i++] = array("DOCUMENT_TYPE" => 'TYPEAHEAD',
			"id"=>'TYPEAHEAD-TEMPLATE-' . $i,
			"TYPEAHEAD_TYPE" => 'TEMPLATE',
			"TEMPLATE_TEXT" => 'property by ',
			"DISPLAY_ORDER" => '1', 
			"TEMPLATE_TYPE" => 'PROPERTY_BY');

	/* Budget-Area Filter based templates */

	$temp1=array('under', 'above', 'between');
	$temp2=array('property');

	foreach ($temp2 as $v2)
	{
		foreach ($temp1 as $v1) {

			$documents[$i++] = array("DOCUMENT_TYPE" => 'TYPEAHEAD',
				"id"=>'TYPEAHEAD-TEMPLATE-' . $i,
				"TYPEAHEAD_TYPE" => 'TEMPLATE',
				"TEMPLATE_TEXT" => $v2 . ' ' . $v1,
				"DISPLAY_ORDER" => '1', 
				"TEMPLATE_TYPE" => 'PROPERTY_UNDER_BELOW_ABOVE_BETWEEN');
		}
	}
	
	return  array(array(), $documents);
}
?>
