create index original_hash on proptiger.Image (original_hash);

update Image i, ImageType it, ObjectType ot, RESI_PROJECT_TYPES rpt 
set title = CONCAT(rpt.UNIT_NAME, ' - ', rpt.SIZE, ' sq ft')
where i.ImageType_id = it.id 
and it.ObjectType_id = ot.id 
and ot.type = 'property'
and rpt.id = i.object_id
and rpt.size != 0
update Image i, ImageType it, ObjectType ot, RESI_PROJECT_TYPES rpt set title = rpt.UNIT_NAME where i.ImageType_id = it.id and it.ObjectType_id = ot.id and ot.type = 'property' and rpt.type_id = i.object_id and rpt.size = 0;

/* TODO add if condition if tagged months is null. if tower_id is null . all four cases.*/
update proptiger.Image I Join cms.project_plan_images PPI ON (I.id=PPI.service_image_id) set I.title = CONCAT_WS(" - ", tower_id, date_format(PPI.tagged_month, "%b \'%Y")), I.taken_at=PPI.tagged_month where PPI.plan_type = "construction status";
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "constructionStatus") JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) JOIN cms.project_plan_images PPI ON (PPI.project_id = RP.project_id AND PPI.plan_type='Construction Status') set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, I.title);

/*************** alt text ****************/
/* City */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 6) set I.title = IT.type where I.title is NULL or I.title = "";

update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 6) JOIN CITY C ON (I.object_id = C.city_id) set I.alt_text = CONCAT_WS(" ", C.label, I.title);

/* Locality */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 4) set I.title = IT.type where I.title is NULL or I.title = "";

update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 4) JOIN LOCALITY L ON (L.locality_id = I.object_id) JOIN CITY C ON (L.city_id = C.city_id) set I.alt_text = CONCAT_WS(" ", L.label, C.label, I.title);

/* Project */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "main") JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, "(Elevation)"), I.title = "Elevation";

/* BANK Home Loan */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 5 AND IT.type = "logo") JOIN BANK_LIST B ON (I.object_id=B.bank_id) set I.alt_text = CONCAT_WS(" ", B.bank_name, "Home Loan");


/* Project SitePlan */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "sitePlan") JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, "Site Plan"), title="Site Plan";

/*  Project location Plan */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "locationPlan") JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, "Location Plan"), title="Location Plan";

/*  Project Master Plan */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "masterPlan") JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, "Master Plan"), title="Master Plan";

/*  Project Payment Plan */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "paymentPlan") JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, "Payment Plan"), title="Payment Plan";

/*  Project Price List */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "priceList") JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, "Price List"), title="Price List";

/*  Project Cluster Plan */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "clusterPlan") JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, "Cluster Plan"), title="Cluster Plan";

/* Project Layout Plan */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "layoutPlan") JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, "Layout Plan"), title="Layout Plan";

/* Property Floor Plan */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 2 AND IT.type = "floorPlan") JOIN RESI_PROJECT_TYPES RPT ON (RPT.type_id=I.object_id) JOIN RESI_PROJECT RP ON (RP.project_id = RPT.project_id) set I.title = CONCAT_WS(" ", RPT.UNIT_NAME, IF(RPT.SIZE>0, CONCAT_WS(" ", RPT.SIZE, RPT.MEASURE), ""));

update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 2 AND IT.type = "floorPlan") JOIN RESI_PROJECT_TYPES RPT ON (RPT.type_id=I.object_id) JOIN RESI_PROJECT RP ON (RP.project_id = RPT.project_id) set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, "Floor Plan", I.title);

/* Construction Updates */
/* TODO add if condition if tagged months is null. if tower_id is null . all four cases.*/


/* duplicate builder image deactivate across all builders. */
/*update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id) set I.active = 0 where (I.original_hash, IT.objecttype_id) IN ( select Im.original_hash, OT.id from Image Im JOIN ImageType IT ON (Im.imagetype_id=IT.id) JOIN ObjectType OT ON (IT.objecttype_id=OT.id AND OT.type="builder") where Im.active = 1 group by Im.original_hash, OT.id having count(*) > 1);*/



