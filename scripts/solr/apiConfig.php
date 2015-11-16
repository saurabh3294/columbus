<?php

$HOST = 'http://qa.proptiger-ws.com/';
$priceTrendAPIUrl = '/data/v1/price-trend/hitherto?fields=sumLaunchedUnit,sumUnitsSold,sumUnitsDelivered&group=localityId&monthDuration=6&sort=localityId';

if( !defined("API_URL") ){
	define("API_URL", $HOST);
}

?>