<?php
	//varibles
	$file_name = "Locality_polygon.csv";
	if (isset($argv[1])){
	    $city_names = strtolower($argv[1]);
	}
	else {
	    echo "\nProvide city names with comma separated whose encoded polygon needs to be migrated\n";
            die;
	}

	echo $city_names ."\n";
    
	//DB Credentials
	$user_name	= "root";
	$password	= "root";
	$host_name	= "localhost";
        $db_name	= "cms"; 
	//DB Handle
	$db = mysql_connect($host_name, $user_name, $password)
	      or die("Unable to get DB Handle");
	
	//Select Database
	mysql_select_db($db_name, $db)
	      or die("Unable to select Database"); 

	$data = readDataFromCsvFile($file_name, $city_names);

	foreach($data as $locality_id => $encoded_polygon) {
	     $update_query = "UPDATE locality set encoded_polygon = '" . $encoded_polygon . "' WHERE locality_id =". $locality_id;
	     mysql_query($update_query);
	}

	function readDataFromCsvFile($file_name, $city_names){
	     $result = array();
             $file_handle = fopen($file_name, "r");
	     while ( !feof($file_handle) ) {
		$line_of_text = fgetcsv( $file_handle, 1024 );
                if (isset($line_of_text[1]) && strpos($city_names, strtolower($line_of_text[1])) !== FALSE 
                    && !empty($line_of_text[2]) && $line_of_text[2] != '#N/A') {
		    $result[$line_of_text[2]] = $line_of_text[6];
		}
	    }
	    return $result;
	}
?>
