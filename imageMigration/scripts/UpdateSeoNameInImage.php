<?php
	$username = "root";
	$password = "root";
	$hostname = "localhost";

	$isProduction = true;
	if ($isProduction) {
		$username = "qa";
		$password = "Proptiger123";
		$hostname = "noida-1.proptiger-ws.com";
	} 

	$pattern = '/[^a-zA-Z0-9]+/';
	$replacement = '-';

	$dbhandle = mysql_connect($hostname, $username, $password)
	 or die("Unable to connect to MySQL");

	$selected = mysql_select_db("proptiger",$dbhandle)
	  or die("Could not select examples");

	$result = mysql_query("SELECT id, alt_text,watermark_name FROM Image");

	if($result === FALSE) {
	    die(mysql_error()); 
	}

	while ($row = mysql_fetch_array($result)) {
		$id = $row['id'];
		$altText = $row['alt_text'];
		$watermarkName = $row['watermark_name'];

		echo "\nID:".$id."    ALT_TEXT:".$altText."     WATERMARK NAME: ".$watermarkName."\n";

		#Replacing special character with hyphen
		$str = preg_replace($pattern, $replacement, $altText);

		$str= ltrim ($str,'-');

		#If last character is hyphen, then concating alt-text and watermark name
		#else separating them by hyphen
		if (strlen($str) == 0) {
			$seoName = $watermarkName;
		}
		else {
			if (strcmp (substr($str, -1), $replacement) == 0) {
				$seoName = strtolower($str).$watermarkName;
			}
			else {
				$seoName = strtolower($str).'-'.$watermarkName;
			}
		}

		echo "SEO NAME:".$seoName."\n";

		#Updating seo name column with alttext-watermarkname
		$query = "Update Image set seo_name = '".$seoName."' Where id =".$id;
		mysql_query($query);
	}

	mysql_close($dbhandle);
?>

