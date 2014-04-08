
select I.id  from  Image I JOIN ImageType IT ON (I.imagetype_id=IT.id) JOIN ( select Im.original_hash, OTm.id from Image Im JOIN ImageType ITm ON (Im.imagetype_id=ITm.id) JOIN ObjectType OTm ON (ITm.objecttype_id=OTm.id AND OTm.type="builder") where Im.active = 1 group by Im.original_hash, OTm.id having count(*) > 5 ) T ON (I.original_hash = T.original_hash and IT.objecttype_id = T.id)  INTO OUTFILE '/tmp/duplicateBuilders.csv' ;

/* deleting duplicate images across builders.*/
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id) JOIN ( select Im.original_hash, OTm.id from Image Im JOIN ImageType ITm ON (Im.imagetype_id=ITm.id) JOIN ObjectType OTm ON (ITm.objecttype_id=OTm.id AND OTm.type="builder") where Im.active = 1 group by Im.original_hash, OTm.id having count(*) > 5 ) T ON (I.original_hash = T.original_hash and IT.objecttype_id = T.id) set I.active = 0;

update Image I JOIN Image DI ON (I.original_hash=DI.original_hash) set I.active = 0 where DI.id in (4644,2757,388702,374700,4166,3298,141661,1170,5143,179308,4450,2600,1731) and I.imagetype_id = 13 and I.active = 1;
