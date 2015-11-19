<?php

define("SOLR_QUERY_LOG_FORMAT","q=[%s], p=[%s], range=[%s - %s]");
define("SOLR_QUERY_CURSOR_STATUS_LOG_FORMAT","cursor=[%s], doc_fetched=[%s], iterations=[%s]");


// date should be send in timestamp format.
function getDateInSolrFormat($time)
{
  if(empty($time))
    return NULL;

  return gmdate('Y-m-d\TH:i:s\Z', $time);
}

function getSolrSearchDateStr($from_time, $to_time)
{
   return "[{$from_time} TO {$to_time}]"; 
}

function getDocumentsFromSolrWithRetries ($solr, $query, $start, $rows, $params, $retryCount){
	global $logger;
	$queryResponse;
	$tryCount = 0;
	$exList = array();
	while($tryCount < $retryCount){
		try{
			$documentList = array();
			$queryResponse = $solr->search($query, $start, $rows, $params);
			foreach($queryResponse->response->docs as $document){
				$documentList[] = $document;
			}
			return $documentList;
		}
		catch(Exception $ex){
			$tryCount++;
			$queryLogLine = sprintf(SOLR_QUERY_LOG_FORMAT, $query, json_encode($params), $start, $rows);
			$logger->warn("Solr query failed. " . $queryLogLine . ". Retrying#" . $tryCount);
			$exList[] = $ex;
		}
	}
	
	throw end($exList);
}

function getDocumentCountFromSolrWithRetries($solr, $query, $params, $retryCount){
	global $logger;
	$queryResponse;
	$tryCount = 0;
	$exList = array();
	while($tryCount < $retryCount){
		try{
			$queryResponse = $solr->search($query, 0, 0, $params);
			$totalDocsPresent = $queryResponse->response->numFound;
			return $totalDocsPresent;
		}
		catch(Exception $ex){
			$tryCount++;
			$queryLogLine = sprintf(SOLR_QUERY_LOG_FORMAT, $query, json_encode($params), 0, 0);
			$logger->warn("Solr query failed. " . $queryLogLine . ". Retrying#" . $tryCount);
			$exList[] = $ex;
		}
	}
	throw end($exList);
}

function getAllDocumentsFromSolrWithCursorAndRetries ($solr, $query, $rows, $params, $retryCount){
	global $logger;
	$documentListAll = array();
	$cursor = '*';
	$ctr_docfetch = 0;
	$ctr_iterations = 0;
	while (true) {
		
		$params['cursorMark'] = $cursor;
		$queryResponse =  array();
		
		/* Fetch each chunk with retry */
		$tryCount = 0;
		while($tryCount < $retryCount){
			$documentListPage = array();
			try{
				$queryResponse = $solr->search($query, null, $rows, $params);
				foreach($queryResponse->response->docs as $document){
					$documentListPage[] = $document;
				}
				break;
			}
			catch(Exception $ex){
				$tryCount++;
				$queryLogLine = sprintf(SOLR_QUERY_LOG_FORMAT, $query, json_encode($params), $cursor, $rows);
				$logger->warn("Solr query failed. " . $queryLogLine . ". Retrying#" . $tryCount);
			}
		}
		
		/* If it fails even after several retry attempts, throw exception as we'll not get a valid cursor. */
		if($tryCount >= $retryCount){
			$queryLogLine = sprintf(SOLR_QUERY_LOG_FORMAT, $query, json_encode($params), 0, 0);
			$cursorStatusLogLine = sprintf(SOLR_QUERY_CURSOR_STATUS_LOG_FORMAT, $cursor, $ctr_docfetch, $ctr_iterations);
			$errorMsg = "Solr query failed even after . " . $retryCount . " retries. ";
			$msg = $errorMsg . "\n" . $queryLogLine . "\n" . $cursorStatusLogLine . "\n" . "Exiting.";
			$logger->warn($msg);
			throw new Exception($msg);
		}
		
		/* Add this page docs to all-docs list */
		foreach ($documentListPage as $doc){
			$documentListAll[] = $doc;
		}
		
		$newCursor = $queryResponse->nextCursorMark;
		if ($newCursor == $cursor) {
			break;
		}
		$cursor = $newCursor;
		$ctr_docfetch += (count($documentListPage));
		$ctr_iterations++;
	}
	
	$logger->info($ctr_docfetch . " documents fetched in " . $ctr_iterations . " iterations.");
	return $documentListAll;
}

function handleDocumentsToSolr($addOrDeleteDocuments, $solr){
    list($deleteDocuments, $addDocuments) = $addOrDeleteDocuments;
    deleteDocumentsToSolr($deleteDocuments, $solr);
    addDocumentsToSolr($addDocuments, $solr);
}

function addDocumentsToSolr($allDocumentsFromDB, $solr) {
    global $logger;

    foreach (array_chunk($allDocumentsFromDB, 2000) as $documentsFromDB) {
        $solrDocuments = array();
        foreach($documentsFromDB as $documentFromDB) {
            $solrDocument = new Apache_Solr_Document();
            if ($documentFromDB["DOCUMENT_TYPE"] == "TYPEAHEAD") {
                $solrDocument->setBoost(1 + 0.0001/$documentFromDB["DISPLAY_ORDER"]);
            }
            
            foreach ($documentFromDB as $key => $value) {
                if (is_array($value)) {
                    foreach ($value as $val) {
                        $solrDocument->addField($key, $val);
                    }
                }
                else {
                    $solrDocument->addField($key, $value);
                }
            }

            array_push($solrDocuments, $solrDocument);
        }

        $count = sizeof($solrDocuments);
        $logger->info("Adding $count documents");
        $solr->addDocuments($solrDocuments);
        $solr->commit();
        $logger->info("Added $count documents");
    }
}

function deleteDocumentsToSolr($allDocumentsFromDB, $solr)
{
    global $logger;
    if( sizeof($allDocumentsFromDB) < 1 )
        return;

    $count = sizeof($allDocumentsFromDB);
    $logger->info("Deleting {$count} documents");
    $response = $solr->deleteByMultipleIds($allDocumentsFromDB);
    $solr->commit();
    $logger->info("deleted {$count} documents");
}

function minPrice($a, $b){
    $c = $a;
    if($a == null){
        $c = $b;
    }
    else if($b != null){
        $c = min($a, $b);
    }

    return $c;
}

function maxPrice($a, $b){
    $c = $a;
    if($a == null){
        $c = $b;
    }
    else if($b != null){
        $c = max($a, $b);
    }

    return $c;
}
?>
