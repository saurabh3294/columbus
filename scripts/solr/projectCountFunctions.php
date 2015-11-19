<?php 

include_once "apiConfig.php";

define("FACET_LOCALITY_ID", "localityId");
define("FACET_SUBURB_ID", "suburbId");
define("FACET_BUILDER_ID", "builderId");

define("API_URL_TEMPLATE_PROJECT_COUNT_TOTAL", 'app/v2/project-listing?selector={"paging":{"start":0,"rows":0}}&facets=%s');
define("API_URL_TEMPLATE_PROJECT_LISTING", 'app/v2/project-listing?selector={"filters":%s,"paging":{"start":0,"rows":0}}&facets=%s');

function getLocalitySoldPercentageMap() {

	$priceTrendAPIUrlTemplate = 'data/v1/trend/current?fields=sumInventory,sumLtdLaunchedUnit&group=localityId&start=%u&rows=%u&sort=localityId';
	$priceTrendAPIUrl = "";
	$start = 0;
	$rows = 1000;
	$localitySoldPercentageMap = array();
	$sumLtdlaunchedUnit = 0;
	$sumInventory = 0;
	$soldPercentage = 0.0;

	$break = false;

	while(!$break){
		$priceTrendAPIUrl = sprintf($priceTrendAPIUrlTemplate, $start, $rows);
		$apiResponse = json_decode ( file_get_contents ( API_URL . $priceTrendAPIUrl ), true );
		$localitiesInventoryInfoPage = $apiResponse ["data"];
		if($localitiesInventoryInfoPage == NULL || empty($localitiesInventoryInfoPage)){
			$break = true;
		}
		else{
			foreach($localitiesInventoryInfoPage as $key => $value){
				$sumLtdlaunchedUnit = $value['0']['extraAttributes']['sumLtdLaunchedUnit'];
				$sumInventory = $value['0']['extraAttributes']['sumInventory'];
				if(isset($sumInventory) && isset($sumLtdlaunchedUnit) && $sumInventory > 0 && $sumLtdlaunchedUnit > $sumInventory){
					$soldPercentage = (($sumInventory*100)/$sumLtdlaunchedUnit);
					$localitySoldPercentageMap[$key] = $soldPercentage;
				}
			}
			$start = $start + $rows;
		}
	}
	return $localitySoldPercentageMap;
}


function getProjectPropertyMap() {
	
	global $logger;
	
	$projectListingAPIUrlTemplate = '/app/v2/project-listing?selector={"paging":{"start":%s,"rows":%s},"fields":["projectId","properties","propertyId","bedrooms","unitType","URL"]}';
	$projectListingAPIUrl = "";
	$start = 0;
	$rows = 1000;
	$projectPropertyMap = array();
	
	$break = false;
	
	while(!$break){
		
		$projectListingAPIUrl = sprintf($projectListingAPIUrlTemplate, $start, $rows);
		$apiResponse = json_decode ( file_get_contents ( API_URL . $projectListingAPIUrl ), true );

		if(!isset($apiResponse['data']) || !isset($apiResponse['data']['items'])){
			$logger->error("Could not fetch properties for projects.");
			return $projectPropertyMap;
		}
		
		$projectList = $apiResponse['data']['items'];
		if ($projectList == NULL || empty($projectList)){
			$break = true;
		}
		else {
		   	foreach ($projectList as $projectItem){
				$projectId = $projectItem['projectId'];
		   		$propertyList = $projectItem['properties'];
		   		$propertyMap = array();
		   		foreach($propertyList as $propertyItem){
		   			if(isset($propertyItem['bedrooms']) && isset($propertyItem['URL']) && $propertyItem['unitType'] == 'Apartment'){
						$propertyMap[$propertyItem['bedrooms']] = ($propertyItem['bedrooms'] . ";" . $propertyItem['URL'] . ";" . $propertyItem['propertyId']);
		   			}
		   		}
		   		$projectPropertyMap[$projectId] = $propertyMap;
		   }
		   $start = $start + $rows;
		}
	}
	
	return $projectPropertyMap;
}

function cleanFilter($filter){
	$filter = sprintf($filter, "");
	$filter = str_replace(' ', '%20', $filter);
	return $filter;
}

/** LOCALITY Specific **/
 
function getLocalityProjectCount($filter){

	$filter = cleanFilter($filter);
	$apiUrlCountProject = sprintf(API_URL_TEMPLATE_PROJECT_LISTING, $filter, FACET_LOCALITY_ID);
	$mapEntityIdToCountProject = getFacetedApiResponseAsFacetMap($apiUrlCountProject, FACET_LOCALITY_ID);
	return $mapEntityIdToCountProject;
}

function getLocalityProjectCountTotal(){

	$apiUrlCountProject = sprintf(API_URL_TEMPLATE_PROJECT_COUNT_TOTAL, FACET_LOCALITY_ID);
	$mapEntityIdToCountProject = getFacetedApiResponseAsFacetMap($apiUrlCountProject, FACET_LOCALITY_ID);
	return $mapEntityIdToCountProject;
}

/** SUBURUB Specific **/

function getSuburbProjectCount($filter){
	
	$filter = cleanFilter($filter);
	$apiUrlCountProject = sprintf(API_URL_TEMPLATE_PROJECT_LISTING, $filter, FACET_SUBURB_ID);
	$mapEntityIdToCountProject = getFacetedApiResponseAsFacetMap($apiUrlCountProject, FACET_SUBURB_ID);
	return $mapEntityIdToCountProject;
}

function getSuburbProjectCountTotal(){

	$apiUrlCountProject = sprintf(API_URL_TEMPLATE_PROJECT_COUNT_TOTAL, FACET_SUBURB_ID);
	$mapEntityIdToCountProject = getFacetedApiResponseAsFacetMap($apiUrlCountProject, FACET_SUBURB_ID);
	return $mapEntityIdToCountProject;
}

/** BUILDER  Specific **/

function getBuilderProjectCount($filter){

	$filter = cleanFilter($filter);
	$apiUrlCount = sprintf(API_URL_TEMPLATE_PROJECT_LISTING, $filter, FACET_BUILDER_ID);
	$mapEntityIdToCount = getFacetedApiResponseAsFacetMap($apiUrlCount, FACET_BUILDER_ID);
	return $mapEntityIdToCount;
}

function getBuilderProjectCountTotal(){

	$apiUrlCount = sprintf(API_URL_TEMPLATE_PROJECT_COUNT_TOTAL, FACET_BUILDER_ID);
	$mapEntityIdToCount = getFacetedApiResponseAsFacetMap($apiUrlCount, FACET_BUILDER_ID);
	return $mapEntityIdToCount;
}

/** Internal functions **/

function getFacetedApiResponseAsFacetMap($apiUrl, $facetName){
	$mapFacet = array();
	$apiResponse = json_decode ( file_get_contents ( API_URL . $apiUrl ), true );
	$apiData = $apiResponse ["data"];
	
	if($apiData == NULL || empty($apiData)){
		return $mapFacet;
	}	
	
	$apiFacets = $apiData["facets"];
	$apiFacetList = $apiFacets[$facetName];
	foreach($apiFacetList as $apiFacet){
		$mapFacet = $mapFacet + $apiFacet;
	}
	return $mapFacet;
}

?>