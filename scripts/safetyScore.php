<?php

// Database Connectivity
$dbProperties = parse_ini_file(dirname(__DIR__).'/data/src/main/resources/application.properties', false, INI_SCANNER_RAW );
$dbUrl = explode("/", $dbProperties['db.url']);
preg_match("/(.*):/", $dbUrl[2], $results);
$db = mysql_connect($results[1],$dbProperties['db.username'], $dbProperties['db.password']);
$dblink = mysql_select_db("proptiger", $db);
//Logging configurations
require_once 'log4php/Logger.php';
Logger::configure(dirname(__FILE__) . '/log4php.xml');
$logger = Logger::getLogger("main");

getSafetyScore();
function getSafetyScore(){
	global $accessToken, $logger;
	$apiKey = "proptiger";   // Public Key
	$apiSecret = "proptiger";   // Private Key
	$tokenTimeout = time() + 36000; // This will generate token valid for next 1 hour
	$apiHash = sha1($apiKey.$apiSecret.$tokenTimeout);

	$tokenUrl = "http://safetipin.com/api/Token";
	$opts = array(
			'http'=>array(
					'method'=>"GET",
					'header'=>"X-APIKEY: $apiKey\r\n".
					"X-APIHASH: $apiHash\r\n".
					"X-TOKENTIMEOUT: $tokenTimeout\r\n"
			)
	);

	$tokenResponse = file_get_contents($tokenUrl, false, stream_context_create($opts));
	$accessToken = json_decode($tokenResponse, true)['accesstoken'];   // Access Token for Safety Score
	$logger->info("\n\n\nAccessToken".$accessToken);
	getScoreProjectAndLocality('PROJECT');
	getScoreProjectAndLocality('LOCALITY');
}

function getScoreProjectAndLocality($objectType){
	global $accessToken, $logger;
	if($objectType=='PROJECT'){
	$sql = "SELECT distinct(p.PROJECT_ID), p.LATITUDE, p.LONGITUDE, p.SAFETY_SCORE
			FROM cms.resi_project p INNER JOIN cms.locality l on p.locality_id = l.locality_id
 			inner join cms.suburb s on s.suburb_id=l.suburb_id 
			WHERE p.LATITUDE NOT IN (0,1) AND p.LONGITUDE NOT IN (0,1) AND s.CITY_ID IN (6,8,11,20,88) 
			AND p.STATUS IN ('Active','ActiveInCms')";
	}
	else if($objectType=='LOCALITY'){
 	$sql = 'SELECT l.LOCALITY_ID, l.LATITUDE, l.LONGITUDE, l.SAFETY_SCORE 
 			FROM cms.locality l INNER JOIN cms.suburb s on l.suburb_id = s.suburb_id
	        WHERE l.LATITUDE NOT IN (0,1) AND l.LONGITUDE NOT IN (0,1) AND s.CITY_ID IN (6,8,11,20,88)';
	}
	$result = mysql_unbuffered_query($sql);
	$documents = array();
	
	$logger->info("Fetching Safety Scores");
	
 	while ($document = mysql_fetch_assoc($result)) {
	$url = "http://safetipin.com/api/GetScore?";
	$latitude  = $document['LATITUDE'];
	$longitude = $document['LONGITUDE'];
	$safetyScoreUrl = $url."GetScore[lat]=".$latitude."&GetScore[lng]=".$longitude;
	$opts = array(
			'http'=>array(
					'method'=>"GET",
					'header'=>"X-TOKEN: $accessToken\r\n"
			)
	);
	
	$file = file_get_contents($safetyScoreUrl, false, stream_context_create($opts));
	$object = json_decode($file, true)['score'];
	if(json_decode($file, true)['success'] == "false"){
		$logger->info("Either the request failed or results not found for lattitude: ".$document['LATITUDE']." and longitude: ".$document['LONGITUDE']);
	}

	if(empty($object)){
		$document['SAFETY_SCORE'] = null;
	}
	else {
		$document['SAFETY_SCORE'] = $object;
	}
	array_push($documents, $document);
 	}
 	
 	$logger->info("Updating Safety Scores");
 	$len = count($documents);
 	for($i=0; $i<$len; $i++){
 	$document = &$documents[$i];
 	if($objectType=='PROJECT'){
 		$updateSql = "UPDATE cms.resi_project set SAFETY_SCORE = "."ROUND((".$document['SAFETY_SCORE']."*2),1)"." where PROJECT_ID = ".$document['PROJECT_ID'];
    	//Logging details for debugging
 		$logger->info("\n\n ProjectId: ".$document['PROJECT_ID']." Latitude: ".$document['LATITUDE'].
 		" Longitude: ".$document['LONGITUDE']." SafetyScore: ".$document['SAFETY_SCORE']);
 	}
 	else if($objectType=='LOCALITY'){
 		$updateSql = "UPDATE cms.locality set SAFETY_SCORE = "."ROUND((".$document['SAFETY_SCORE']."*2),1)"." where LOCALITY_ID = ".$document['LOCALITY_ID'];
 		//Logging details for debugging
 	 	$logger->info("\n\n LocalityId: ".$document['LOCALITY_ID']." Latitude: ".$document['LATITUDE'].
 		" Longitude: ".$document['LONGITUDE']." SafetyScore: ".$document['SAFETY_SCORE']);
 	} 	
 	$query = mysql_query($updateSql);
 	}
}
?>