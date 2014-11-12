<?php
	include_once 'dbConfig.php';
        // MYSQL Query
        $sql = "select trpo.options_id, trpo.project_id from cms._t_resi_project_options trpo left outer join 
                proptiger.DELETED_RESI_PROJECT_TYPES drpt on (drpt.type_id = trpo.options_id) 
                where trpo._t_operation = 'D' and drpt.type_id is null";

        //Fetching query result from DB
        $query_result = mysql_query($sql);

        if ($query_result === FALSE) {
                die(mysql_error());
        }
	
	while($row = mysql_fetch_array($query_result)) {
		$options_id	= $row['options_id'];
		$project_id	= $row['project_id'];
		
		//echo $options_id. " " . $project_id ."\n";
		//inserting into DELETED_RESI_PROJECT_TYPES;
		$insert_query = "insert into proptiger.DELETED_RESI_PROJECT_TYPES(TYPE_ID, PROJECT_ID) 
                                 values (". $options_id. "," . $project_id .")";
		mysql_query($insert_query);
	}
?>
