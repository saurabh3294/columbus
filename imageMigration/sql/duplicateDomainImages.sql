/* duplicate Builder Images.*/
select I.id  from  Image I JOIN ImageType IT ON (I.imagetype_id=IT.id) JOIN ( select Im.original_hash, OTm.id from Image Im JOIN ImageType ITm ON (Im.imagetype_id=ITm.id) JOIN ObjectType OTm ON (ITm.objecttype_id=OTm.id AND OTm.type="builder") where Im.active = 1 group by Im.original_hash, OTm.id having count(*) > 5 ) T ON (I.original_hash = T.original_hash and IT.objecttype_id = T.id)  INTO OUTFILE '/tmp/duplicateBuilderImage.csv' ;
Select I.id from Image I JOIN Image DI ON (I.original_hash=DI.original_hash) where DI.id in (4644,2757,388702,374700,4166,3298,141661,1170,5143,179308,4450,2600,1731) and I.imagetype_id = 13 and I.active = 1 INTO OUTFILE '/tmp/duplicateBuilderImage1.csv';
/* deleting duplicate images across builders.*/
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id) JOIN ( select Im.original_hash, OTm.id from Image Im JOIN ImageType ITm ON (Im.imagetype_id=ITm.id) JOIN ObjectType OTm ON (ITm.objecttype_id=OTm.id AND OTm.type="builder") where Im.active = 1 group by Im.original_hash, OTm.id having count(*) > 5 ) T ON (I.original_hash = T.original_hash and IT.objecttype_id = T.id) set I.active = 0;

update Image I JOIN Image DI ON (I.original_hash=DI.original_hash) set I.active = 0 where DI.id in (4644,2757,388702,374700,4166,3298,141661,1170,5143,179308,4450,2600,1731) and I.imagetype_id = 13 and I.active = 1;

/* duplicate Project Images.*/
select I.id  from  Image I JOIN ImageType IT ON (I.imagetype_id=IT.id) JOIN ( select Im.original_hash, OTm.id from Image Im JOIN ImageType ITm ON (Im.imagetype_id=ITm.id) JOIN ObjectType OTm ON (ITm.objecttype_id=OTm.id AND OTm.type="project") where Im.active = 1 group by Im.original_hash, OTm.id having count(*) > 4 ) T ON (I.original_hash = T.original_hash and IT.objecttype_id = T.id)  INTO OUTFILE '/tmp/duplicateProjectImage.csv' ;

select I.id from Image I JOIN Image DI ON (I.original_hash=DI.original_hash) where DI.id in (288512,281265,281341,384466,280194,280223,281814,259771,281654,283091,303155,324698,281790,312897,281981,282768,261477,281634,291505,316961,284350,305054,408947,367073,288474,311750,357043,349662,281505,348270,275906,261053,401545,359608) and I.active = 1 INTO OUTFILE '/tmp/duplicateProjectImage1.csv';

/* deleting duplicate images across builders.*/
update Image I JOIN ImageType IT ON (I.imagetype_id=IT.id) JOIN ( select Im.original_hash, OTm.id from Image Im JOIN ImageType ITm ON (Im.imagetype_id=ITm.id) JOIN ObjectType OTm ON (ITm.objecttype_id=OTm.id AND OTm.type="project") where Im.active = 1 group by Im.original_hash, OTm.id having count(*) > 4 ) T ON (I.original_hash = T.original_hash and IT.objecttype_id = T.id) set I.active = 0;

update Image I JOIN Image DI ON (I.original_hash=DI.original_hash) set I.active = 0 where DI.id in (288512,281265,281341,384466,280194,280223,281814,259771,281654,283091,303155,324698,281790,312897,281981,282768,261477,281634,291505,316961,284350,305054,408947,367073,288474,311750,357043,349662,281505,348270,275906,261053,401545,359608) and I.active = 1;
