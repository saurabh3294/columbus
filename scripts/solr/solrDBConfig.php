<?php
$solrDB = mysql_connect ( "localhost", "root", "root" );
$dblink = mysql_select_db ( "proptiger", $solrDB );
mysql_query ( "set wait_timeout=3600;", $solrDB );
?>
