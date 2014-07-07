/*create index original_hash on proptiger.Image (original_hash);
create index service_image_id on cms.project_plan_images (service_image_id);*/

/*update Image i, ImageType it, ObjectType ot, RESI_PROJECT_TYPES rpt 
set title = CONCAT(rpt.UNIT_NAME, ' - ', rpt.SIZE, ' sq ft')
where i.ImageType_id = it.id 
and it.ObjectType_id = ot.id 
and ot.type = 'property'
and rpt.type_id = i.object_id
and rpt.size != 0;
update Image i, ImageType it, ObjectType ot, RESI_PROJECT_TYPES rpt set title = rpt.UNIT_NAME where i.ImageType_id = it.id and it.ObjectType_id = ot.id and ot.type = 'property' and rpt.type_id = i.object_id and rpt.size = 0;*/

/* TODO add if condition if tagged months is null. if tower_id is null . all four cases.*/
update proptiger.Image I LEFT Join cms.project_plan_images PPI ON (I.id=PPI.service_image_id and PPI.plan_type = "construction status" ) LEFT JOIN cms.resi_project_tower_details RPTD ON (PPI.tower_id=RPTD.tower_id) set I.title = CONCAT_WS(" - ", RPTD.tower_name, IF(PPI.tagged_month is NULL or PPI.tagged_month = '0000-00-00 00:00:00' , 'Before Oct \'13' , date_format(PPI.tagged_month, "%b \'%y") ) )  where  I.imagetype_id = 3 and (I.title is NULL OR I.title = '');

update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "constructionStatus") JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) LEFT JOIN cms.project_plan_images PPI ON (PPI.project_id = RP.project_id AND PPI.plan_type='Construction Status') set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, I.title) where (I.alt_text is NULL OR I.alt_text = '') and I.active = 1;

/*************** alt text ****************/
/* City */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 6) set I.title = IT.type where I.title is NULL or I.title = "";

update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 6) JOIN CITY C ON (I.object_id = C.city_id) set I.alt_text = CONCAT_WS(" ", C.label, I.title) where I.alt_text is NULL OR I.alt_text = '';

/* Locality */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 4) set I.title = IT.type where I.title is NULL or I.title = "";

update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 4) JOIN LOCALITY L ON (L.locality_id = I.object_id) JOIN CITY C ON (L.city_id = C.city_id) set I.alt_text = CONCAT_WS(" ", L.label, C.label, I.title) where I.alt_text is NULL OR I.alt_text = '';

/* Project */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "main" ) JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) LEFT JOIN cms.project_plan_images PPI ON (PPI.service_image_id=I.id AND PPI.plan_type="Project Image" AND PPI.title like '%elevation%') set I.title = IF( lower(PPI.title) regexp "^.*elevation.*$", replace(PPI.title, 'elevation', 'Elevation'), NULL) where I.title is NULL OR I.title = '' ;

update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "main" ) JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id)  set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, I.title) where I.alt_text is NULL OR I.alt_text = '';


/* BANK Home Loan */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 5 AND IT.type = "logo") JOIN cms.bank_list B ON (I.object_id=B.bank_id) set I.alt_text = CONCAT_WS(" ", B.bank_name, "Home Loan") where I.alt_text is NULL OR I.alt_text = '';


/* Project SitePlan */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "sitePlan") JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, "Site Plan"), title=IF(title is NULL or title = '', "Site Plan", title) where I.alt_text is NULL OR I.alt_text = '';

/*  Project location Plan */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "locationPlan") JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, "Location Plan"), title=IF(title is NULL or title = '', "Location Plan", title) where I.alt_text is NULL OR I.alt_text = '';

/*  Project Master Plan */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "masterPlan") JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, "Master Plan"), title=IF(title is NULL or title = '', "Master Plan", title) where I.alt_text is NULL OR I.alt_text = '';

/*  Project Payment Plan */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "paymentPlan") JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, "Payment Plan"), title=IF(title is NULL or title = '', "Payment Plan", title) where I.alt_text is NULL OR I.alt_text = '';

/*  Project Price List */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "priceList") JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, "Price List"), title=IF(title is NULL or title = '', "Price List", title) where I.alt_text is NULL OR I.alt_text = '';

/*  Project Cluster Plan */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "clusterPlan") JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) LEFT JOIN cms.project_plan_images PPI ON (I.id=PPI.service_image_id) set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, IF(PPI.title is NOT NULL AND PPI.title != "", PPI.title, "Cluster Plan")), I.title=IF(PPI.title is NOT NULL AND PPI.title != "", PPI.title, "Cluster Plan") where I.alt_text is NULL OR I.alt_text = '';

/* Project Layout Plan */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "layoutPlan") JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, "Layout Plan"), title=IF(title is NULL or title='', "Layout Plan", title) where I.alt_text is NULL or I.alt_text = '';

/* Property Floor Plan */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 2 AND IT.type = "floorPlan") JOIN RESI_PROJECT_TYPES RPT ON (RPT.type_id=I.object_id) JOIN RESI_PROJECT RP ON (RP.project_id = RPT.project_id) set I.title = CONCAT_WS(" ", RPT.UNIT_NAME, IF(RPT.SIZE>0, CONCAT_WS(" ", RPT.SIZE, RPT.MEASURE), "")) where I.title is NULL or I.title = '';

update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 2 AND IT.type = "floorPlan") JOIN RESI_PROJECT_TYPES RPT ON (RPT.type_id=I.object_id) JOIN RESI_PROJECT RP ON (RP.project_id = RPT.project_id) set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, "Floor Plan", I.title) where I.alt_text is NULL or I.alt_text = '';

/* PROJECT SPECIFICATION*/
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 1 AND IT.type = "specification") JOIN RESI_PROJECT RP ON (RP.project_id = I.object_id) set I.alt_text = CONCAT_WS(" ", RP.builder_name, RP.project_name, "Specification") where I.alt_text is NULL or I.alt_text = '';

/* BUILDER LOGO */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 3 ) JOIN cms.resi_builder RB ON (RB.BUILDER_ID=I.object_id) set I.alt_text = RB.BUILDER_NAME where I.alt_text is NULL or I.alt_text = '';

update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 7 ) JOIN SUBURB S ON (S.SUBURB_ID=I.object_id) set I.alt_text = CONCAT_WS(" ", IF(I.title is NULL, IT.type, I.title), "in", S.label) where I.alt_text is NULL or I.alt_text = '';

