
/* deleting duplicate images across builders.*/
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id) JOIN ( select Im.original_hash, OTm.id from Image Im JOIN ImageType ITm ON (Im.imagetype_id=ITm.id) JOIN ObjectType OTm ON (ITm.objecttype_id=OTm.id AND OTm.type="builder") where Im.active = 1 group by Im.original_hash, OTm.id having count(*) > 5 ) T ON (I.original_hash = T.original_hash and IT.objecttype_id = T.id) set I.active = 0;
