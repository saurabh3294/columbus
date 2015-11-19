<?php
define("TOPSEARCH_COUNT", 10);
define("CITY_COUNT", 100);
define("SUBURB_COUNT", 500);
define("LOCALITY_COUNT", 2000);
define("BUILDER_COUNT", 5000);
define("PROJECT_COUNT", 20000);

function populateTopSearch() {
	global $logger, $elasticSearchClient, $solrTypeaheadList, $solrDB;

	$objectConfigArray = array("city"=> array("suburb", "locality", "project", "builder"),
						"suburb"=> array("locality", "project", "builder"),
						"locality"=> array("project", "builder"),
						"builder"=> array("project"));
	$objectArr = array("city", "suburb", "locality", "builder");
	$solrTopSearchConfig = array("suburb"=> "TYPEAHEAD_TOP_SEARCHED_SUBURB", "locality"=>"TYPEAHEAD_TOP_SEARCHED_LOCALITY", "project"=>"TYPEAHEAD_TOP_SEARCHED_PROJECT", "builder"=>"TYPEAHEAD_TOP_SEARCHED_BUILDER");

/****
	$returnData[objectId]['lowerEntityType'] = array(lowerEntityIds)
	$returnData[objectId]['lowerEntityType'] = array(lowerEntityId => counts)
**/

	$returnData = array();

	$sql = "select config_value from cms.config where group_name='Search' and config_name='topSearchToSolrCount'";
	$res = mysql_query($sql, $solrDB);
	$data = mysql_fetch_assoc($res);
	if($data['config_value']){
		$elastic_search_result_count = $data['config_value'];
	}
	else{
		$elastic_search_result_count = TOPSEARCH_COUNT;
		$logger->info("ESearch : Top search count value get from db failed, using default value");
	}
	$elasticSearchQuery = getElasticSearchQuery($elastic_search_result_count);

	$params['index'] = 'analyticsprocess';
	$params['type']  = 'user_autosearch';
	$params['search_type']  = 'count';
	$params['body']  = $elasticSearchQuery;
	try{ 
		$results = $elasticSearchClient->search($params);
		$elasticdocumentArr = array();

		foreach ($objectArr as $object) {
			$objectBuckets = array();
			$objectBuckets = $results['aggregations'][$object]['buckets'];
			foreach ($objectBuckets as $objectBucket) {
				$objectId = $objectBucket['key'];
				foreach ($objectConfigArray[$object] as $searchObject) {
					$searchObjectBuckets = array();
					$searchObjectBuckets = $objectBucket['filter_by_'.$searchObject][$searchObject]['buckets'];
					$fieldArr = array();
					foreach ($searchObjectBuckets as $searchObjectBucket) {
						$fieldArr[$searchObjectBucket['key']] = $searchObjectBucket['doc_count'];
					}
					if(!empty($fieldArr)){
						if(!isset($returnData[$objectId]))
							$returnData[$objectId] = array();
						$returnData[$objectId][$searchObject] = $fieldArr;
					}
				}
			}
		}

	}	
	catch (Exception $e) {
	    $logger->error("Exception while querying elastic-search", $e);
	    trigger_error("Exception while querying elastic-search", E_USER_WARNING);
	}
	return $returnData;
	
}

/****
This query aggregates elastic search data based on entities (city,suburb,locality and builder) and brings top searches of lowere hierarchial entities. eg, 
aggregates data based on city ids and get top suburbs, locality, builder and projects
						suburb ids and get top locality, builder, projects
						locality and get top builder, projects
						builder and get top projects 

**/

function getElasticSearchQuery($elastic_search_result_count){
	
	$objectTypeId = array('city'=> 6, 'suburb'=> 7, 'locality'=> 4, 'builder'=> 3, 'project'=> 1);
	$elasticSearchQuery = '{
  
			        "aggs" : {
			        	 
			            	"city":{	
			                          "terms" : { 
			                              "field" : "city_id", "size" : '.CITY_COUNT.'
			                          },
			                          "aggs" : {
			        	 
			                            "filter_by_suburb" : {
			                                "filter":{
			                                    "term":{"page_type_id":'.$objectTypeId['suburb'].'} 
			                                },
			                                "aggs":{
			                                    "suburb":{	
			                                      "terms" : { 
			                                          "field" : "suburb_id", "size":'.$elastic_search_result_count.'
			                                      }
			                                    }  
			                                }  
			                            },
			                            "filter_by_locality" : {
			                                "filter":{
			                                    "term":{"page_type_id":'.$objectTypeId['locality'].'} 
			                                },
			                                "aggs":{
			                                    "locality":{	
			                                      "terms" : { 
			                                          "field" : "locality_id", "size":'.$elastic_search_result_count.'
			                                      }
			                                    }  
			                                }  
			                            },
			                            "filter_by_builder" : {
			                                "filter":{
			                                    "term":{"page_type_id":'.$objectTypeId['builder'].'} 
			                                },
			                                "aggs":{
			                                    "builder":{	
			                                      "terms" : { 
			                                          "field" : "builder_id", "size":'.$elastic_search_result_count.'
			                                      }
			                                    }  
			                                }  
			                            },
			                            "filter_by_project" : {
			                                "filter":{
			                                    "term":{"page_type_id":'.$objectTypeId['project'].'} 
			                                },
			                                "aggs":{
			                                    "project":{	
			                                      "terms" : { 
			                                          "field" : "project_id", "size":'.$elastic_search_result_count.'
			                                      }
			                                    }  
			                                }  
			                            }
			                            
			                        
			                   		}
			                          
			                 },
			                 
			                 "suburb":{	
			                          "terms" : { 
			                              "field" : "suburb_id", "size" : '.SUBURB_COUNT.'
			                          },
			                          "aggs" : {
			        	 
			                            "filter_by_locality" : {
			                                "filter":{
			                                    "term":{"page_type_id":'.$objectTypeId['locality'].'} 
			                                },
			                                "aggs":{
			                                    "locality":{	
			                                      "terms" : { 
			                                          "field" : "locality_id", "size":'.$elastic_search_result_count.'
			                                      }
			                                    }  
			                                }  
			                            },
			                            "filter_by_builder" : {
			                                "filter":{
			                                    "term":{"page_type_id":'.$objectTypeId['builder'].'} 
			                                },
			                                "aggs":{
			                                    "builder":{	
			                                      "terms" : { 
			                                          "field" : "builder_id", "size":'.$elastic_search_result_count.'
			                                      }
			                                    }  
			                                }  
			                            },
			                            "filter_by_project" : {
			                                "filter":{
			                                    "term":{"page_type_id":'.$objectTypeId['project'].'} 
			                                },
			                                "aggs":{
			                                    "project":{	
			                                      "terms" : { 
			                                          "field" : "project_id", "size": '.$elastic_search_result_count.'
			                                      }
			                                    }  
			                                }  
			                            }
			                            
			                        
			                   		}
			                          
			                 },
			                 
			                 "locality":{	
			                          "terms" : { 
			                              "field" : "locality_id", "size" : '.LOCALITY_COUNT.'
			                          },
			                          "aggs" : {
			        	 
			                            
			                            "filter_by_builder" : {
			                                "filter":{
			                                    "term":{"page_type_id":'.$objectTypeId['builder'].'} 
			                                },
			                                "aggs":{
			                                    "builder":{	
			                                      "terms" : { 
			                                          "field" : "builder_id", "size":'.$elastic_search_result_count.'
			                                      }
			                                    }  
			                                }  
			                            },
			                            "filter_by_project" : {
			                                "filter":{
			                                    "term":{"page_type_id":'.$objectTypeId['project'].'} 
			                                },
			                                "aggs":{
			                                    "project":{	
			                                      "terms" : { 
			                                          "field" : "project_id", "size":'.$elastic_search_result_count.'
			                                      }
			                                    }  
			                                }  
			                            }
			                            
			                        
			                   		}
			                          
			                 },
			                 
			                 "builder":{	
			                          "terms" : { 
			                              "field" : "builder_id", "size" : '.BUILDER_COUNT.'
			                          },
			                          "aggs" : {
			        	 
			                            "filter_by_project" : {
			                                "filter":{
			                                    "term":{"page_type_id":'.$objectTypeId['project'].'} 
			                                },
			                                "aggs":{
			                                    "project":{	
			                                      "terms" : { 
			                                          "field" : "project_id", "size":'.$elastic_search_result_count.'
			                                      }
			                                    }  
			                                }  
			                            }
			                            
			                        
			                   		}
			                          
			                 }
			               
			                
			            
			        }
			      
			   
			}';
	return $elasticSearchQuery;

}


?>