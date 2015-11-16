<?php

define("ERROR_CONSOLE_MESSAGE", "\nErrorHandler(%s) :: (%s)-(%s)@(%s:%s).\n");

$envName = getenv("solr_env_name");

if($envName && in_array($envName, array("PRODUCTION"))) {
	define("FAILURE_EMAIL_RECEIPIENT", "site_errors@proptiger.com");
	define("ADDITIONAL_EMAIL_RECEIPIENTS", "rahul.malviya@proptiger.com,anuj.kaushik@proptiger.com,azitabh.ajit@proptiger.com,mukand.agarwal@proptiger.com");
}
else if($envName && in_array($envName, array("BETA", "QA"))) {
	define("FAILURE_EMAIL_RECEIPIENT", "anuj.kaushik@proptiger.com");
	define("ADDITIONAL_EMAIL_RECEIPIENTS", "rahul.malviya@proptiger.com,anuj.kaushik@proptiger.com,azitabh.ajit@proptiger.com,mukand.agarwal@proptiger.com");
}
else {
	define("FAILURE_EMAIL_RECEIPIENT", "");
	define("ADDITIONAL_EMAIL_RECEIPIENTS", "");
}

printRecipientInfoToConsole();

function printRecipientInfoToConsole(){
	print("\n\n");
	print("ENVIRONMENT_NAME : " . $envName . "\n");
	print("FAILURE_EMAIL_RECEIPIENT : " . FAILURE_EMAIL_RECEIPIENT . "\n");
	print("ADDITIONAL_EMAIL_RECEIPIENTS : " . ADDITIONAL_EMAIL_RECEIPIENTS . "\n");
	print("\n\n");
}


?>
