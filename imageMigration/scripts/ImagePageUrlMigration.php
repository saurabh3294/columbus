<?php
	//variables
	$pattern 	= '/[^a-zA-Z0-9]+/';
	$hyphen		= "-";
	$gallery	= "gallery";
	$slash		= "/";
	$dot		= ".";
	
	
	//DB Credentials
	$user_name	= "root";
	$password	= "root";
	$host_name	= "localhost";
        $db_name	= "proptiger"; 
	//DB Handle
	$db = mysql_connect($host_name, $user_name, $password)
	      or die("Unable to get DB Handle");
	
	//Select Database
	mysql_select_db($db_name, $db)
	      or die("Unable to select Database");

	
	// MYSQL Query
	$sql = "SELECT I.id, I.alt_text, I.watermark_name, OT.type, I.object_id FROM Image I JOIN ImageType IT ON (I.ImageType_id = IT.id)
                JOIN ObjectType OT ON (OT.id = IT.ObjectType_id) WHERE I.active = 1";
	
	//Fetching query result from DB
	$query_result = mysql_query($sql);
	
	if ($query_result === FALSE) {
		die(mysql_error());
	}

	while($row = mysql_fetch_array($query_result)){
		$image_id 		= $row['id'];
		$alt_text 		= $row['alt_text'];
		$watermark_name 	= $row['watermark_name'];
		$domain_name		= $row['type'];
		$object_id		= $row['object_id'];
		
		//replace special character with hyphen
		$alt_text_with_hyphen	= strtolower(preg_replace($pattern, $hyphen, $alt_text));
		$alt_text_with_hyphen	= ltrim($alt_text_with_hyphen, $hyphen);
		$alt_text_with_hyphen	= rtrim($alt_text_with_hyphen, $hyphen);
		
		if (strlen($alt_text_with_hyphen) > 0) {
			$alt_text_with_hyphen = $alt_text_with_hyphen . $hyphen;
		}
		
		//page_url construction from input
		$page_url	= $gallery . 
				  $slash . 
				  $alt_text_with_hyphen .
				  $object_id . 
				  $hyphen . 
				  $image_id;
		/*echo "\n\nRAW INPUT => [". 
                     $alt_text		.	", "	.
		     $object_id		. 	", "	.  
                     $image_id		.	", "	.
                     $watermark_name	.	"] "	.
		     "\nPAGE URL =>"	. 	$page_url;*/
		
		#Updating page_url column with gallery/alt_text-domain-id
		$update_query	= "UPDATE Image set page_url = '" . $page_url . "' WHERE id = ". $image_id;
		mysql_query($update_query);
	}
?>
