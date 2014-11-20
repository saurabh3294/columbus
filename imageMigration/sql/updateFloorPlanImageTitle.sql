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

/*************** alt text ****************/

/* Property Floor Plan */
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 2 AND IT.type = "floorPlan") JOIN cms.resi_project_options RPT ON (RPT.options_id=I.object_id) JOIN cms.resi_project RP ON (RP.project_id = RPT.project_id and RP.version='Website') set I.title = CONCAT_WS(" ", RPT.OPTION_NAME, IF(RPT.SIZE>0, CONCAT_WS(" ", RPT.SIZE, 'sq ft'), ""));

update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id AND IT.objectType_id = 2 AND IT.type = "floorPlan") JOIN cms.resi_project_options RPT ON (RPT.options_id=I.object_id) JOIN cms.resi_project RP ON (RP.project_id = RPT.project_id and RP.version = 'Website')  join cms.resi_builder RB ON (RP.builder_id=RB.builder_id) set I.alt_text = CONCAT_WS(" ", RB.builder_name, RP.project_name, IF( LOCATE("Floor Plan", I.title)=0, "Floor Plan", ""), I.title);

