<?php
$solrDB = mysql_connect ( "10.10.0.15", "root", "root" );
$dblink = mysql_select_db ( "proptiger", $solrDB );
mysql_query ( "set wait_timeout=3600;", $solrDB );
?>
