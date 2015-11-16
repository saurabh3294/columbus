<?php

function isValidGeo($latitude, $longitude) {
    return (!in_array(intval($latitude),  array('', 1, 0, 2, 3, 5)) &&
                    !in_array(intval($longitude), array('', 1, 0, 2, 3, 5)) &&
                    ($latitude <= 90 && $latitude >= -90) &&
                    ($longitude <= 180 && $longitude >= -180));
}

function createAllBedroomsString($allBedrooms) {
    $types = explode(',', $allBedrooms);
    sort($types);
    $details = array();
    foreach ($types as $type) {
        $typeDetails = explode('-', $type);
        if (!isset($details[$typeDetails[0]])) {
            $details[$typeDetails[0]] = array();
        }

        $details[$typeDetails[0]][] = $typeDetails[1];
    }

    $strings = array();
    foreach ($details as $type => $bhkArr) {
        sort($bhkArr);
        $validBHKs = array();
        foreach ($bhkArr as $bhk) {
            if ($bhk) {
                $validBHKs[] = $bhk;
            }
        }

        if ($validBHKs) {
            $strings[] = implode(',', $validBHKs) . " BHK $type";
        }
        else {
            $strings[] = $type;
        }
    }

    return implode(', ', $strings);
}

function checkValidAndSetDate(&$document, $key)
{
    if(!empty($document[$key]) && $document[$key] !== INVALID_DATE && $document[$key] !== '0000-00-00')
        $document[$key] = getDateInSolrFormat( strtotime($document[$key]) );
    else
        unset($document[$key]);

    if (empty($document[$key])) {
        unset($document[$key]);
    }
}

function setValidLaunchDate(&$document)
{
    if( isProjectUpcoming($document['PROJECT_STATUS']) )
        $document['VALID_LAUNCH_DATE'] = isset($document['PRE_LAUNCH_DATE'])? $document['PRE_LAUNCH_DATE']: NULL;
    else
        $document['VALID_LAUNCH_DATE'] = isset($document['LAUNCH_DATE'])? $document['LAUNCH_DATE']: NULL;

    if( empty($document['VALID_LAUNCH_DATE']) )
        unset($document['VALID_LAUNCH_DATE']);
}

function stringToAssoc($str)
{
    $offset = $start = 0;
    $domains = array();

    while( ($offset = strpos($str, ',', $offset) ) !== FALSE )
    {
        $domains[ substr($str, $start, $offset-$start) ] = 1;
        $offset++;
        $start = $offset;
    }
    $offset = strlen($str);
    $domains[ substr($str, $start, $offset-$start) ] = 1;

    return $domains;
}

function appendConditionForB2b($condition)
{
	global $solrCollectionType;
        global $projStatusCondition;
	if($solrCollectionType == "b2b"){
		if (empty ( $condition ) ){
			$condition = " AND $projStatusCondition";
		}
		else {
			$condition .= " AND $projStatusCondition";
		}
	}
	return $condition;
}

function arrayCopy($arr){
    $newArr = array();
    foreach($arr as $key => $value){
        $newArr[$key] = $value;
    }

    return $newArr;
}

function getExceptionLogMessage(Exception $e){
	$logMsg = ("\n------ Message: " . $e->getMessage() . "\n------ Trace : \n" . $e->getTraceAsString());
	return $logMsg;
}

?>
