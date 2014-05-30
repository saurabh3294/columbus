<?php
include_once 'dbConfig.php';
$sql = "UPDATE proptiger.portfolio_listings pls, 
		(SELECT COUNT(*) AS count , pl.id AS id FROM ptigercrm.LEADS lds
		INNER JOIN ptigercrm.LEAD_STATUS ldst ON  ldst.LEAD_ID= lds.LEAD_ID AND ldst.STATUS_ID NOT IN (7,8,9)
		INNER JOIN proptiger.RESI_PROJECT_TYPES rpt 
		INNER JOIN proptiger.portfolio_listings pl ON rpt.TYPE_ID=pl.type_id
		INNER JOIN proptiger.RESI_PROJECT rp ON rp.project_id=rpt.project_id
		INNER JOIN proptiger.LOCALITY ly ON ly.locality_id=rp.locality_id
		AND lds.locality=ly.label
		AND lds.bedrooms REGEXP rpt.bedrooms
		AND lds.BUDGET BETWEEN 0.85*pl.total_price AND 1.15*pl.total_price 
		AND (0.85*pl.size BETWEEN lds.min_size AND lds.max_size OR  1.15*pl.size BETWEEN lds.min_size AND lds.max_size)) 
		AS pol
		SET pls.active_enquiries_count = pol.count
		WHERE pol.id= pls.id";

$rs = mysql_query($sql);
echo $rs;
?>