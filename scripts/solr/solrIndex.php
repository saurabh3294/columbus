<?php
ini_set("display_errors", 1);
error_reporting(E_ALL);
/**
 * Running the script, the command are :
 * php solrIndex.php deleteThenInsert    -- It will first empty entire solr Data and then insert documents.
 * php solrIndex.php                     -- It will read the message from SQS and run script as per the message.
 * php solrIndex.php proptiger/makaan/makaaniq/propguide/b2b --It will index the document of given param (Only one param is expected at a time)
 * php solrIndex.php proptiger/makaan project 654231,502704 locality 53111,51070.........
 *              
 *
 */

$documentRoot = __DIR__."/../";
require $documentRoot.'amazon-sqs/aws-autoloader.php';
require_once $documentRoot.'log4php/Logger.php';
require_once $documentRoot.'vendor/autoload.php';
require_once 'errorHandler.php';
require_once 'elasticSearchConfig.php';
require_once 'elasticSearchFunctions.php';

require_once 'Apache/Solr/Service.php';
require_once 'solrConfig.php';
require_once 'utils_functions.php';
require_once 'projectFunctions.php';
require_once 'typeaheadFunctions.php';
require_once 'propguideFunctions.php';
require_once 'locationFunctions.php';
require_once 'landmarkFunctions.php';
require_once 'solrFunctions.php';
require_once 'solrDBConfig.php';
require_once 'emailConfig.php';
require_once $documentRoot.'send_mail_amazon.php';
use Aws\Sqs\SqsClient;

define("IMG_SERVER",'https://cdn.proptiger.com/');

ini_set('display_errors', '1');
error_reporting(E_ALL);

ini_set('memory_limit', '-1');
set_time_limit(0);
define("INVALID_DATE", "0000-00-00 00:00:00");
define("TABLE_ATTRIBUTE_PROJECT_UPDATE_TIME", "D_PROJECT_UPDATION_DATE");
define("DEFAULT_DOMINANT_TYPE", "Apartment");
define("PROPERTY_RECORDS_LIMIT", 25000);
define("PROPERTY_FETCH_ROWS", 1000);
define("SUBURB", "suburb");
define("LOCALITY", "locality");
define("CITY", "city");
define("PROPERTY", "property");
define("PROJECT", "project");
define("BUILDER", "builder");
define("PROPTIGER", "proptiger");
define("PROPGUIDE", "propguide");
define("MAKAAN", "makaan");
define("MAKAANIQ", "makaaniq");
define("MIN_BUILDER_SCORE", 0.5);
define("MIN_MAX_BUILDER_SCORE", 0.95);
define("B2B", "b2b");
define("SLAVE", "slave");
//define("RESALE_INDEX", 10);

define("GLOBAL_SOLR_GET_RETRY_COUNT", 3);

define("LOG_FORMAT_ADD_DELETE_DOC_ARRAY", "ToDelete = %s, ToAdd = %s");
Logger::configure( dirname(__FILE__) . '/../log4php.xml');
$logger = Logger::getLogger("main");

$paramProjectIds = null;
$paramIds;
$solrCollectionType;
$elasticSearchClient;
$TYPEAHEAD_SOLR_SERVER_HOSTNAME_CONFIG  = TYPEAHEAD_SOLR_SERVER_HOSTNAME;
$TYPEAHEAD_SOLR_SERVER_PORT_CONFIG = TYPEAHEAD_SOLR_SERVER_PORT;

if(in_array(SLAVE, $argv)){

  $TYPEAHEAD_SOLR_SERVER_HOSTNAME_CONFIG  = TYPEAHEAD_SOLR_SLAVE_SERVER_HOSTNAME;
  $TYPEAHEAD_SOLR_SERVER_PORT_CONFIG = TYPEAHEAD_SOLR_SLAVE_SERVER_PORT;
  removeElementFromCommandParams(SLAVE, $argv);

}

$indexingStartCommand = implode(" ", $argv);
$logger->info("\n\n\n\n");

global $envName;
$logger->info("Environment = ". $envName);

try {
    $messageFromSQS = receiveMessageFromSQS();
    if(empty($argv[1])){
        $arguments = explode(' ', $messageFromSQS);
        $argv_ = array_merge($argv,$arguments);
        $argv = $argv_;
    }
    $projStatusCondition = " psm.project_status not in ('Cancelled', 'OnHold', 'NotLaunched') ";
    $elasticSearchClient = new Elasticsearch\Client($elasticParams);   //from elasticSearchConfig.php
    if(in_array("b2b",$argv)){
        $logger = Logger::getLogger("mainB2B");
    	$logger->info("Connecting to b2b Solr instance");
    	$typeaheadSolr = new Apache_Solr_Service($TYPEAHEAD_SOLR_SERVER_HOSTNAME_CONFIG, $TYPEAHEAD_SOLR_SERVER_PORT_CONFIG, '/solr/collection_b2b_clbs/');
    	$logger->info("Running for b2b instance");
        $projCmsActiveCondition = "\"Active\",\"ActiveInCms\"";                                   // Cms Active Projects Condition
        $locationFunctionProjActiveCondition = "\"Inactive\",\"Active\",\"ActiveInCms\"";         // Active Projects Condition being used in LocationFunctions
        $projectActiveConditionArray = array("Active","ActiveInCms");                             // Active Projects Condition Array being used in ProjectFunctions and TypeaheadFunctions
        $version = "\"Website\"";
        $solrCollectionType = "b2b";
    }
    else if (in_array("makaan",$argv) || in_array("makaaniq",$argv)){
        $logger->info("Connecting to makaan Solr instance");
        $typeaheadSolr = new Apache_Solr_Service($TYPEAHEAD_SOLR_SERVER_HOSTNAME_CONFIG, $TYPEAHEAD_SOLR_SERVER_PORT_CONFIG, '/solr/collection_mp_clbs/');
        $logger->info("Running for Website instance");
        $projCmsActiveCondition = "\"Active\"";
        $locationFunctionProjActiveCondition = "\"Inactive\",\"Active\"";
        $projectActiveConditionArray = array("Active");
        $version = "\"Website\"";
        $solrCollectionType = "website";
    } else{
    	$logger->info("Connecting to proptiger Solr instance");
    	$typeaheadSolr = new Apache_Solr_Service($TYPEAHEAD_SOLR_SERVER_HOSTNAME_CONFIG, $TYPEAHEAD_SOLR_SERVER_PORT_CONFIG, '/solr/collection_clbs/');
    	$logger->info("Running for Website instance");
        $projCmsActiveCondition = "\"Active\"";
        $locationFunctionProjActiveCondition = "\"Inactive\",\"Active\"";
        $projectActiveConditionArray = array("Active");
        $version = "\"Website\"";
        $solrCollectionType = "website";
    }

    if (!empty($argv[1])){
        if( $argv[1] == 'deleteThenInsert')
        {
            $typeaheadSolr->deleteByQuery("*:*");
            $typeaheadSolr->commit();
            $logger->info("Deleted documents");
        }
        else if( preg_match("/^[A-Za-z0-9A-Za-z]*$/", $argv[1]) != FALSE){
            $logger->info("Updating solr documents of the following domains: {$argv[1]}");
            $domains = stringToAssoc(strtolower($argv[1]));
        }
        
    }
    if(sizeof($argv) > 2){
        $paramLocalityIds = $paramSuburbIds = $paramCityIds = $paramProjectIds = $paramPropertyIds = $paramBuilderIds = -1;
        $variables = array(LOCALITY=>"paramLocalityIds", CITY=>"paramCityIds",
                            PROJECT=>"paramProjectIds", PROPERTY => "paramPropertyIds",
                            SUBURB=>"paramSuburbIds", BUILDER=>"paramBuilderIds");

        $len = sizeof($argv);
        for($i=2; $i<$len; $i=$i+2)
        {
            $argv[$i] = strtolower(trim($argv[$i]));
            $argv[$i+1] = trim($argv[$i+1]);
            if( preg_match("/^[A-Za-z]+$/", $argv[$i]) != FALSE){
                $domains[$argv[$i]] = 1;
            }
            // setting all the variables paramsLocalityIds, paramsCityIds, .....
            if( preg_match("/^[0-9]+(,[0-9]+)*$/", $argv[$i+1]) != FALSE && isset($variables[$argv[$i]]) ){
                $$variables[$argv[$i]] = $argv[$i+1];
            }
            else {
                unset($domains[$argv[$i]]);
                $i = $i-1;
            }
        }
        $logger->info(json_encode($domains));
        $logger->info(" PROJECT ".$paramProjectIds." PROPERTY ".$paramPropertyIds." BUILDER ".$paramBuilderIds." CITY ".$paramCityIds." SUBURB ".$paramSuburbIds." LOCALITY ".$paramLocalityIds."\n");
    }

    $solrTypeaheadList = array();
    if( empty($domains) || isset($domains[PROPTIGER]) || isset($domains[B2B]) ){
		indexTypeahead($typeaheadSolr, $solrDB, $logger,PROPTIGER);
    }

    if( empty($domains) || isset($domains[MAKAAN]) ){
        indexTypeahead($typeaheadSolr, $solrDB, $logger, MAKAAN);
    }

    if( empty($domains) || isset($domains[PROPGUIDE]) ){
        indexPropguide($typeaheadSolr, $solrDB, $logger, PROPGUIDE);
    }

    if( empty($domains) || isset($domains[MAKAANIQ]) ){
        indexPropguide($typeaheadSolr, $solrDB, $logger, MAKAANIQ);
    }    
}
catch (Exception $e) {
	$errorMsg = ("Exception while indexing : " . getExceptionLogMessage($e));
	$logger->error($errorMsg);
	sendIndexingFailureEmail($indexingStartCommand, $errorMsg);
	exit(1);
}

$logger->info("Exiting.");
sendIndexingSuccessEmail($indexingStartCommand );
exit(0);

function indexTypeahead($typeaheadSolr, $solrDB, $logger, $domain){
	global $logger;
	global $solrTypeaheadList;

	$logger->info("#### TYPEAHEAD : Indexing Start ####");

	$logger->info("Typeahead : Fetching typeahead documents from Solr");
	$solrTypeaheadList = getSolrDocumentsOfTypeahead($typeaheadSolr);
	$logger->info("Number of TYPEAHEAD documents fetched from solr = " . count($solrTypeaheadList));

	$logger->info("Typeahead : Fetching docs and sending to Solr");
	handleDocumentsToSolr(getTypeaheadDocumentsFromDB($domain), $typeaheadSolr);

	$logger->info("Typeahead : Fetching template docs and sending to Solr");
	handleDocumentsToSolr(getTypeaheadTemplates(), $typeaheadSolr);

	$logger->info("Typeahead : Optimizing Solr");
	$typeaheadSolr->optimize();

	$logger->info("#### TYPEAHEAD : Indexing End ####");
}

function indexPropguide($propguideSolr, $solrDB, $logger, $domain){
	$logger->info("#### PROPGUIDE : Indexing Start ####");

	$propguideIndexer = new PropguideIndexer($propguideSolr, $solrDB, $logger, $domain);
	$domainDocumentList = $propguideIndexer->getPropguideDocumentsFromDB();

	$logger->info("Propguide : Sending documents to Solr");
	handleDocumentsToSolr($domainDocumentList, $propguideSolr);

	$logger->info("Propguide : Optimizing Solr");
	$propguideSolr->optimize();

	$logger->info("#### PROPGUIDE : Indexing End ####");

}

function sendIndexingFailureEmail($indexingStartCommand, $errorMessage){
	$hostname = exec('hostname');
	$mailSubject = 'Solr Indexing Failure : ' . $hostname;
	$mailMessage = $errorMessage;
	$additionalEmailRecipients = explode(",", ADDITIONAL_EMAIL_RECEIPIENTS);
	sendRawEmailFromAmazon(FAILURE_EMAIL_RECEIPIENT, '', '', $mailSubject , $mailMessage, '', '', $additionalEmailRecipients);
}

function sendIndexingSuccessEmail($indexingStartCommand){
	$hostname = exec('hostname');
  global $argv;
  $emailTitlePrefix = "";
  if(!isset($argv[1]) || $argv[1] == B2B){
    $emailTitlePrefix = "Full Solr Indexing Success: ";
  }
  else{
    $emailTitlePrefix = "Partial Solr Indexing Success: ";
  }
  $mailSubject = $emailTitlePrefix . $hostname;
	$mailMessage = "Solr Indexing successfully completed on " . $hostname . "\n\n". ("Args = [" . $indexingStartCommand . "]" . "\n\n");
	$additionalEmailRecipients = explode(",", ADDITIONAL_EMAIL_RECEIPIENTS);
	sendRawEmailFromAmazon(FAILURE_EMAIL_RECEIPIENT, '', '', $mailSubject , $mailMessage, '', '', $additionalEmailRecipients);
}

# This method prints sensitive info about solr instance username/pass/port etc.
# Can be used for debugging locally. Should NOT be released in production code.
function getSolrInfoAsString($solrServiceObj){
	return ("[" . $solrServiceObj->getHost() . ":" . $solrServiceObj->getPort() ."] [" .  $solrServiceObj->getPath() . "]");
}

function removeElementFromCommandParams($element, &$arguments){
	$len = count($arguments)-1;
        $flag = 0;
        for($i=0; $i<$len; $i++){
            if($arguments[$i] == $element){
                $flag = 1;
            }
            if($flag){
                $arguments[$i] = $arguments[$i+1];
            }
        }
        unset($arguments[$len]);

}

function receiveMessageFromSQS(){
    $client = SqsClient::factory(array(
        'region'  => 'us-west-2'
    ));
    $messageBody;
    $result = $client->createQueue(array('QueueName' => 'Typeahead-Solr-Indexing'));
    $queueUrl = $result->get('QueueUrl');

    try{ 
        $messageReceived = $client->receiveMessage(array(
            'QueueUrl' => $queueUrl,   
        ));
        
        $value = (array)$messageReceived;
        $keys = array_keys($value);
        if(!isset($value[$keys[1]]["Messages"])){
            return;
        }
        $messageBody = $value[$keys[1]]["Messages"][0]["Body"];
        $receiptHandle = $value[$keys[1]]["Messages"][0]["ReceiptHandle"];

        $delete = $client->deleteMessage(array(
        'QueueUrl' => $queueUrl,
        'ReceiptHandle' => $receiptHandle,
        ));
        return $messageBody;
    }
    catch (Exception $e) {
        echo 'Exception while processing message: ',  $e->getMessage(), "\n";
    }
}

?>
