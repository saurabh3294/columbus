<?php
include_once 'locationFunctions.php';
include_once 'utils_functions.php';

define("PARENT_ID_FAQ", "20");
define("PARENT_ID_GLOSSARY", "12");
define("PARENT_ID_FORM", "14");

define("SOLR_FIELD_VALUE_PGD_TYPE_POST", "POST");
define("SOLR_FIELD_VALUE_PGD_TYPE_FAQ", "FAQs");
define("SOLR_FIELD_VALUE_PGD_TYPE_GLOSSARY", "A-Z GLOSSARY");
define("SOLR_FIELD_VALUE_PGD_TYPE_FORM", "FORMs");

define("DB_FIELD_TERM_ID", "term_id");
define("DB_FIELD_TERM_PARENT_ID", "parent");
define("DB_FIELD_TERM_NAME", "name");

define("CHARSET", "utf8");

define("PDG_TYPE_SUGGESTION", "Suggestion");

class PropguideIndexer {

	private $solr;
	private $solrDB;
	private $logger;
	private $solrCurrentDocumentList;
	private $domain;
	
	function __construct($solr, $solrDB, $logger, $domain) {
		$this->solr = $solr;
		$this->solrDB = $solrDB;
		$this->logger = $logger;
		$this->domain = $domain;
	}

	function getPropguideDocumentsFromDB() {
		$this->solrCurrentDocumentList = $this->getSolrDocumentsOfPropguide();
			
		$this->logger->info("Propguide : Fetching documents from DB");
		
		/* Set client charset as utf-8 
		 * We need utf8 for text in propguide document fields. This is reset to default charset later.*/
		$oldCharSet = $this->changeMySqlClientCharset(CHARSET, $this->solrDB);
		
		/* Fetch categories and tags */
		$mapOfMapTagAndCategory = $this->getCategoryAndTagsForPosts();
		$mapPostIdToTag = $mapOfMapTagAndCategory['tag'];
		$mapPostIdToCategory = $mapOfMapTagAndCategory['category'];
		$mapPostIdToRootCategory = $mapOfMapTagAndCategory['root-category'];
		$allTags = $mapOfMapTagAndCategory['allTags'];
		$propguideDocumentTypeDefault = SOLR_FIELD_VALUE_PGD_TYPE_POST;
		
		/* Fetching posts from DB and re-structuring them as solr docs */
		$sqlGetPosts = "SELECT CONCAT('PROPGUIDE-POST-', P.ID) AS id, 'PROPGUIDE' AS DOCUMENT_TYPE, '$propguideDocumentTypeDefault' AS PGD_TYPE, 
					P.ID AS PGD_ID,  P.post_title AS PGD_TITLE, P.post_excerpt AS PGD_EXCERPT, P.post_content AS PGD_CONTENT, 
					P.post_date AS PGD_DATE, P.post_type AS PGD_POST_TYPE, P.post_name AS PGD_POST_NAME, P.guid AS PGD_GUID 
					FROM propguide.wp_posts P 
					JOIN propguide.wp_term_relationships TR ON P.ID = TR.object_id
					JOIN propguide.wp_term_taxonomy TT ON TR.term_taxonomy_id=TT.term_taxonomy_id
					JOIN propguide.wp_terms T ON TT.term_id=T.term_id
					WHERE (T.name=\"$this->domain\" AND ((P.post_status = 'publish' AND P.post_type = 'post') OR  P.post_type= 'attachment'))";
		
		$currentDocuments = array();
		$result = mysql_unbuffered_query ( $sqlGetPosts, $this->solrDB );
		$count = 0;
		if ($result) {
			while ( $document = mysql_fetch_assoc ($result)) {
				checkValidAndSetDate($document, 'PGD_DATE');
				
				/* Setting tags and categories */
				if(isset($document['PGD_ID'])) {
					$count =$count +1;
					if(isset($mapPostIdToTag[$document['PGD_ID']])) {
						$document['PGD_TAGS'] = $mapPostIdToTag[$document['PGD_ID']];
					}
					if(isset($mapPostIdToCategory[$document['PGD_ID']])){
						$document['PGD_CATEGORY'] = $mapPostIdToCategory[$document['PGD_ID']];
					}
					if(isset($mapPostIdToRootCategory[$document['PGD_ID']])){
						$document['PGD_ROOT_CATEGORY_ID'] = $mapPostIdToRootCategory[$document['PGD_ID']];
						$this->modifyPropguideDocumentType($document);
					}
					else{
						continue;
					}
				}
				
				if($document['PGD_POST_TYPE'] == 'attachment' && !in_array(PARENT_ID_FORM, $document['PGD_ROOT_CATEGORY_ID'])  ){
					continue;
				}
				
				unset($this->solrCurrentDocumentList[$document['id']]);
				array_push($currentDocuments, $document);
				
			}
		}
		else {
			$this->logger->error("Error in posts from database using query : \n ". $sqlGetPosts . "\n");
			$this->logger->error("Mysql error : \n". mysql_error());
			die();
		}
		/* Add Propguide Suggestion documents */
		$suggestionDocuments = $this->getPropguideSuggestions($allTags);
		$currentDocuments = array_merge($currentDocuments, $suggestionDocuments);
	
	
		/* Resetting the char set to original one */
		$this->changeMySqlClientCharset($oldCharSet, $this->solrDB);
		
		/* Make list of to-be-deleted douments */
		$toBeDeletedDocuments = array ();
		foreach ( $this->solrCurrentDocumentList as $key => $value ) {
			$toBeDeletedDocuments [] = $key;
		}
		
		$this->logger->info("Propguide : " . sprintf(LOG_FORMAT_ADD_DELETE_DOC_ARRAY,
				count($toBeDeletedDocuments), count($currentDocuments)));
		
		return array ($toBeDeletedDocuments, $currentDocuments);
	}
	
	private function getSolrDocumentsOfPropguide(){
		$this->logger->info("Propguide : Fetching documents from Solr");
	
		$pgdocIdList = array();
		$query = "DOCUMENT_TYPE:PROPGUIDE";
		$field = "id";
		$fl = array("$field");
			
		$solrQueryParams = array ('fl' => $fl, 'sort' =>  "id asc");
		$rows = 1000;
		$documentList = array();
		try {
			$documentList = getAllDocumentsFromSolrWithCursorAndRetries($this->solr, $query, $rows, $solrQueryParams, GLOBAL_SOLR_GET_RETRY_COUNT);
		}
		catch(Exception $e){
			$errorMsg = "Error while fetching propguide objects from solr : " . getExceptionLogMessage($e);
			$logger->error($errorMsg);
			trigger_error($errorMsg, E_USER_ERROR);
			die();
		}
		
		$data = array();
		$len = sizeof($documentList);
		for($i=0; $i<$len; $i++){
			$responseDoc = $documentList[$i];
			$data[$responseDoc->$field] = 1;
		}
		
		$this->logger->info("Propguide : Documents fetched from solr = " . count($data));
		return $data;
	}
	
	/**
	 * Changes propguideDocumentType (PGD_TYPE) to FAQ or GLOSSARY only if a post belongs to 
	 * these categories doesn't do anything otherwise.
	 */
	private function modifyPropguideDocumentType(&$document){
		$categories = $document['PGD_ROOT_CATEGORY_ID'];
		if(in_array(PARENT_ID_FAQ, $categories)){
			$document['PGD_TYPE'] =  SOLR_FIELD_VALUE_PGD_TYPE_FAQ;
		}
		else if(in_array(PARENT_ID_GLOSSARY, $categories)){
			$document['PGD_TYPE'] = SOLR_FIELD_VALUE_PGD_TYPE_GLOSSARY;
		}
		else if(in_array(PARENT_ID_FORM, $categories)){
			$document['PGD_TYPE'] = SOLR_FIELD_VALUE_PGD_TYPE_FORM;
		}
	}
	
	/**
	 * Gets categories and tags corresponding to posts
	 * Output :: 
	 * map of maps :: {'tag':map<pgid,tags>, 'category':map<pgid,categories>}
	 * 'tags' and 'categories' for each post_id is a dlim (,) separated string.
	 */
	private function getCategoryAndTagsForPosts(){
				
		$sql = "SELECT TR.object_id AS PGD_ID, TT.taxonomy AS taxonomy,  
				GROUP_CONCAT(TT.term_id) AS term_ids,
				GROUP_CONCAT(TT.parent) AS parent_ids,  
	            GROUP_CONCAT(T.name) AS names 
					FROM
				propguide.wp_terms T
	        		JOIN
	    		propguide.wp_term_taxonomy TT ON (T.term_id = TT.term_id AND (TT.taxonomy = 'category' OR TT.taxonomy = 'post_tag'))
				    JOIN
	    		propguide.wp_term_relationships TR ON (TT.term_taxonomy_id = TR.term_taxonomy_id)
					GROUP BY
				TR.object_id, TT.taxonomy";
		
		$mapCategoryIdToCategory = $this->getCategoryIdToCategoryMapFromDB();
		$mapCategoryToRoot = $this->getCategoryToRootMap($mapCategoryIdToCategory);
			
		$mapPostIdToTag = array();
		$mapPostIdToCategory = array();
		$mapPostIdToRootCategory = array();
		$tagsFromPost = array();

		$idListString;
		$idList;
		$result = mysql_unbuffered_query ( $sql, $this->solrDB );
		if ($result) {
			while ( $document = mysql_fetch_assoc ( $result ) ) {
				if(isset($document['PGD_ID'])){
					if ($document['taxonomy'] == 'post_tag') {
						$mapPostIdToTag[$document['PGD_ID']] = explode(',', $document['names']);
						$tagsFromPost[] = explode(',', $document['names']);
					}
					else if($document['taxonomy'] == 'category') {
						$mapPostIdToCategory[$document['PGD_ID']] = explode(',', $document['names']);
						$idListString = $document['term_ids'];
						$parentNameList = $this->getRootIdListFromGroupedCategoryIds($mapCategoryToRoot, $mapCategoryIdToCategory, $idListString);
						$mapPostIdToRootCategory[$document['PGD_ID']] = $parentNameList;
					}
				}
			}
		}
		else {
			$this->logger->error("Error in getting tags and categories from database using query : \n ". $sql . "\n");
			$this->logger->error("Mysql error : \n". mysql_error());
			die();
		}

		$newTags=array();
		foreach($tagsFromPost as $key => $value){
			foreach ($value as $key2 => $value2) {
				$newTags[] = $value2;
			}
		}

		$uniqueTags = array_unique($newTags);
		$mapOfMap = array();
		$mapOfMap['tag'] = $mapPostIdToTag;
		$mapOfMap['category'] = $mapPostIdToCategory;
		$mapOfMap['root-category'] = $mapPostIdToRootCategory;
		$mapOfMap['allTags'] =$uniqueTags;
		return $mapOfMap;
	}
	
	private function getRootIdListFromGroupedCategoryIds($mapCategoryToRoot, $mapCategoryIdToCategory, $idListString){
		$idList = explode(",", $idListString);
		$rootList;
		foreach($idList as $id){
			$rootList[] = $mapCategoryIdToCategory[$mapCategoryToRoot[$id]][DB_FIELD_TERM_ID];
		}
		return array_unique($rootList);
	}
	
	private function getRootNameListFromGroupedCategoryIds($mapCategoryToRoot, $mapCategoryIdToCategory, $idListString){
		$idList = explode(",", $idListString);
		$rootList;
		foreach($idList as $id){
			$rootList[] = $mapCategoryIdToCategory[$mapCategoryToRoot[$id]][DB_FIELD_TERM_NAME];
		}
		return array_unique($rootList);
	}
	
	/* map of every category to one of the root categories */
	private function getCategoryToRootMap($mapCategoryToCategory){
	
		$mapCategoryToRoot = array();
		foreach ($mapCategoryToCategory as $key => $value){
			$mapCategoryToRoot[$key] = $this->getCategoryRoot($mapCategoryToCategory, $key);
		}
		return $mapCategoryToRoot;
	}
	
	/* map of every category-id to catergory-info(id, parent_id, name) */
	private function getCategoryIdToCategoryMapFromDB(){
		
		$sql = "SELECT 
				TT.term_id AS ". DB_FIELD_TERM_ID .", 
				TT.parent AS ". DB_FIELD_TERM_PARENT_ID .",
				T.name AS ". DB_FIELD_TERM_NAME ."   
				FROM propguide.wp_term_taxonomy TT 
				JOIN propguide.wp_terms T ON (T.term_id = TT.term_id) 
				WHERE taxonomy='category'";
		
		$mapCategoryToParent = array();
		$result = mysql_unbuffered_query ( $sql, $this->solrDB );
		if ($result) {
			while ( $document = mysql_fetch_assoc ( $result ) ) {
				if(isset($document[DB_FIELD_TERM_ID])){
					$mapCategoryToParent[$document[DB_FIELD_TERM_ID]] = $document;
				}
			}
		}
		else {
			$this->logger->error("Error in getting category map from database using query : \n ". $sql . "\n");
			$this->logger->error("Mysql error : \n". mysql_error());
			die();
		}
		return $mapCategoryToParent;
	}
	
	/* get root category of a single category */
	private function getCategoryRoot($mapCategoryIdToCategory, $categoryId){
	 	while(true){
			$parentId = $mapCategoryIdToCategory[$categoryId][DB_FIELD_TERM_PARENT_ID];
			if($categoryId == $parentId){
				$this->logger->error("Invalid category-parent mapping found. " . $categoryId);
				return $categoryId;
			}
			else if($parentId == 0){
				return $categoryId;
			}
			else{
				$categoryId = $parentId;
			}
	 	}
	}
	
	/* Changes MySQL client char set. Returns the prev char set */
	private function changeMySqlClientCharset($charset, $connection){
	
		$oldCharSet = mysql_client_encoding($connection);
		$error = mysql_set_charset($charset, $connection);
		$this->logger->info("Setting mysql-client-charset to ". $charset . " FROM " . $oldCharSet);
		if($error == false){
			$this->logger->error("Error while setting mysql-client-charset to ". $charset . " FROM " . $oldCharSet . ". FATAL ERROR.");
			die();
		}
		return $oldCharSet;
	}
	
	
	/*******************************************************
	 *  SUGGESTIONS
	 ******************************************************/
	
	private function getPropguideSuggestions($allTags){
		$suggestionList = $this->getSuggestionsBasedOnPostTags($allTags);
		return $suggestionList;
	}
	
	private function getSuggestionsBasedOnPostTags($allTags){
		$suggestionList = array();
	
		$sql = "SELECT T.term_id AS " . DB_FIELD_TERM_ID . ",
				T.name AS " . DB_FIELD_TERM_NAME. "
				FROM propguide.wp_terms T
				JOIN propguide.wp_term_taxonomy TT ON (T.term_id = TT.term_id)
				WHERE TT.taxonomy='post_tag'";
	
		$result = mysql_unbuffered_query ( $sql, $this->solrDB );

		$count = 0;
		if ($result) {
			while ( $document = mysql_fetch_assoc ( $result ) ) {
				if(in_array($document[DB_FIELD_TERM_NAME], $allTags)){
					$count = $count + 1;
					$document = mysql_fetch_assoc ( $result ); 
					$suggestion = array();
					$suggestion['id'] = "PROPGUIDE-SUGGESTION-TAG-" . $document[DB_FIELD_TERM_ID];
					$suggestion['DOCUMENT_TYPE'] = 'PROPGUIDE';
					$suggestion['PGD_TYPE'] = PDG_TYPE_SUGGESTION;
					$suggestion['PGD_TITLE'] = $document[DB_FIELD_TERM_NAME];
					array_push($suggestionList, $suggestion);
					unset($this->solrCurrentDocumentList[$suggestion['id']]);	
				}
			}
		}
		else {
			$this->logger->error("Error in getting all tags from database using query : \n ". $sql . "\n");
			$this->logger->error("Mysql error : \n". mysql_error());
			die();
		}
		return $suggestionList;
	}

}
?>
