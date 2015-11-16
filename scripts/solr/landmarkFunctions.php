<?php
function getLocalityAmenityFromDB()
{
    global $logger, $paramProjectIds, $citiesOnProjectIds, $solrDB;
    $landmarkLocalityProjectMap = fetchNearByLocalitiesAndProjects();

    $sql = <<<QRY
        SELECT  CONCAT('LANDMARK-', LM.id) as id, "LANDMARK" AS DOCUMENT_TYPE, LM.id as LANDMARK_ID,
            LM.CITY_ID as LANDMARK_CITY_ID, LM.name as LANDMARK_NAME, LM.address as LANDMARK_ADDRESS, LM.FUTURE_FLAG as IS_FUTURE_INFRASTRUCTURE,
            LM.latitude as LANDMARK_LATITUDE, LM.longitude as LANDMARK_LONGITUDE, LM.vicinity as LANDMARK_VICINITY,
            LM.boundaryEncode as ENCODED_POLYLINE, LM.svg_data as LANDMARK_SVG,
            LM.priority as LANDMARK_PRIORITY, LM.website as LANDMARK_WEBSITE_URL, LM.rest_details as REST_DETAILS,
            LM.phone_number as LANDMARK_PHONE_NUMBER, LT.description as LANDMARK_TYPE_DESCRIPTION, LT.name as LANDMARK_TYPE,
            LT.id as LANDMARK_TYPE_ID, LT.display_name as LANDMARK_DISPLAY_TYPE
        FROM cms.landmarks as LM JOIN cms.landmark_types LT ON (LM.place_type_id=LT.id) WHERE  (LT.user_maintained=0)  OR LM.status = "active";
QRY;

   $documents = array();
   $result = mysql_unbuffered_query($sql, $solrDB);

   if($result)
   {
       while( $document=mysql_fetch_assoc($result) )
       {
            $latitude = $document['LANDMARK_LATITUDE'];
            $longitude = $document['LANDMARK_LONGITUDE'];

            if( isValidGeo($latitude, $longitude) )
            {
                $document['GEO'] = $latitude.",".$longitude;
                $document['HAS_GEO'] = 1;
            }
            else
            {
                unset($document['LANDMARK_LATITUDE']);
                unset($document['LANDMARK_LONGITUDE']);
            }

            if(empty($document['LANDMARK_ADDRESS']))
                unset($document['LANDMARK_ADDRESS']);
            if(empty($document['LANDMARK_VICINITY']))
                unset($document['LANDMARK_VICINITY']);
            if(empty($document['LANDMARK_WEBSITE_URL']))
                unset($document['LANDMARK_WEBSITE_URL']);
            if(empty($document['LANDMARK_PHONE_NUMBER']))
                unset($document['LANDMARK_PHONE_NUMBER']);
            if(empty($document['LANDMARK_PRIORITY'])){
                unset($document['LANDMARK_PRIORITY']);
            }
            else{
            // also set rating if priority is present
                $document['LANDMARK_RATING'] = 6 - $document['LANDMARK_PRIORITY'];
            }
            if (empty($document['LANDMARK_DISPLAY_TYPE']))
            	unset($document['LANDMARK_DISPLAY_TYPE']);
            if(empty($document['REST_DETAILS']))
                unset($document['REST_DETAILS']);
            if(empty($document['IS_FUTURE_INFRASTRUCTURE']))
                unset($document['IS_FUTURE_INFRASTRUCTURE']);
            if(empty($document['ENCODED_POLYLINE']))
                unset($document['ENCODED_POLYLINE']);
            if(empty($document['LANDMARK_SVG']))
                unset($document['LANDMARK_SVG']);
            if(isset($landmarkLocalityProjectMap[$document['LANDMARK_ID']])){
                if (isset($landmarkLocalityProjectMap[$document['LANDMARK_ID']]['PROJECT_IDS'])){
                    $document['NEARBY_PROJECT_IDS'] = $landmarkLocalityProjectMap[$document['LANDMARK_ID']]['PROJECT_IDS'];
                }
                if (isset($landmarkLocalityProjectMap[$document['LANDMARK_ID']]['PROJECT_DISTANCES'])){
                    $document['NEARBY_PROJECT_DISTANCE'] =  $landmarkLocalityProjectMap[$document['LANDMARK_ID']]['PROJECT_DISTANCES'];
                }
                if (isset($landmarkLocalityProjectMap[$document['LANDMARK_ID']]['LOCALITY_IDS'])){
                    $document['NEARBY_LOCALITY_IDS'] = $landmarkLocalityProjectMap[$document['LANDMARK_ID']]['LOCALITY_IDS'];
                }
                if (isset($landmarkLocalityProjectMap[$document['LANDMARK_ID']]['LOCALITY_DISTANCES'])){
                    $document['NEARBY_LOCALITY_DISTANCE'] = $landmarkLocalityProjectMap[$document['LANDMARK_ID']]['LOCALITY_DISTANCES'];
                }
            }

            array_push($documents, $document);
       }

   }
   else {
   		$logger->error("Error in fetching Landmark data using query : \n ". $sql."\n");
   		$logger->error("Mysql error : \n". mysql_error());
   		die();
   }

   return array( array(), $documents);
}

function fetchNearByLocalitiesAndProjects(){
    global $logger, $paramProjectIds, $citiesOnProjectIds, $solrDB;

    define("LOCALITY_OBJECT_TYPE_ID", 4);
    define("PROJECT_OBJECT_TYPE_ID", 1);

    $sql = <<<QRY
    SELECT
      LANDMARK_ID, OBJECT_ID, OBJECTTYPE_ID, ROADDISTANCE, TIME
    FROM
      cms.future_infrastructure_mapping;
QRY;

     $result = mysql_unbuffered_query($sql, $solrDB) or die($logger->error("Error in fetching infrastructure data using query : \n ". $sql."\nMysql error :". mysql_error()));
     $landmarkLocalityProjectMap = array();
     while($res = mysql_fetch_assoc($result) ){
        isset($landmarkLocalityProjectMap[$res['LANDMARK_ID']]) ?: $landmarkLocalityProjectMap[$res['LANDMARK_ID']] = array();

        if( $res["OBJECTTYPE_ID"] == PROJECT_OBJECT_TYPE_ID){
            isset($landmarkLocalityProjectMap[$res['LANDMARK_ID']]['PROJECT_IDS']) ?: $landmarkLocalityProjectMap[$res['LANDMARK_ID']]['PROJECT_IDS'] = array();
            isset($landmarkLocalityProjectMap[$res['LANDMARK_ID']]['PROJECT_DISTANCES']) ?: $landmarkLocalityProjectMap[$res['LANDMARK_ID']]['PROJECT_DISTANCES'] = array();

            array_push($landmarkLocalityProjectMap[$res['LANDMARK_ID']]['PROJECT_IDS'], $res['OBJECT_ID']);
            $dis = array('projectId' => $res['OBJECT_ID'], 'roadDistance' => $res['ROADDISTANCE'], 'time' => $res['TIME'] );
            array_push($landmarkLocalityProjectMap[$res['LANDMARK_ID']]['PROJECT_DISTANCES'], $dis);
        }
        else if ($res["OBJECTTYPE_ID"] == LOCALITY_OBJECT_TYPE_ID) {
            isset($landmarkLocalityProjectMap[$res['LANDMARK_ID']]['LOCALITY_IDS']) ?: $landmarkLocalityProjectMap[$res['LANDMARK_ID']]['LOCALITY_IDS'] = array();
            isset($landmarkLocalityProjectMap[$res['LANDMARK_ID']]['LOCALITY_DISTANCES']) ?: $landmarkLocalityProjectMap[$res['LANDMARK_ID']]['LOCALITY_DISTANCES'] = array();

            array_push($landmarkLocalityProjectMap[$res['LANDMARK_ID']]['LOCALITY_IDS'], $res['OBJECT_ID']);
            $dis = array('localityId' => $res['OBJECT_ID'], 'roadDistance' => $res['ROADDISTANCE'], 'time' => $res['TIME'] );
            array_push($landmarkLocalityProjectMap[$res['LANDMARK_ID']]['LOCALITY_DISTANCES'], $dis);
        }
     }

     foreach ($landmarkLocalityProjectMap as $landmarkId => $value) {
       if (isset($value['LOCALITY_DISTANCES'])){
          $landmarkLocalityProjectMap[$landmarkId]['LOCALITY_DISTANCES'] = json_encode($value['LOCALITY_DISTANCES']);
       }

       if (isset($value['PROJECT_DISTANCES'])){
          $landmarkLocalityProjectMap[$landmarkId]['PROJECT_DISTANCES'] = json_encode($value['PROJECT_DISTANCES']);
       }
     }

     return $landmarkLocalityProjectMap;
}
?>
