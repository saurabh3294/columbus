-- All of the following sqls should yield `0` as count
-- ## Bank ##
select count(*) from `proptiger`.`BANK_LIST` where migration_status!='Done';
-- ## Builder ##
select count(*) from `proptiger`.`RESI_BUILDER` where migration_status!='Done';
-- ## Locality ##
select count(*) from `proptiger`.`LOCALITY_IMAGE` where IMAGE_CATEGORY IS NOT NULL AND IMAGE_CATEGORY != '' AND migration_status!='Done';
-- ## Property ##
SELECT
    COUNT(*)
FROM
    `proptiger`.`RESI_FLOOR_PLANS` AS FP INNER JOIN `proptiger`.`RESI_PROJECT_TYPES` AS PT
    ON FP.TYPE_ID = PT.TYPE_ID
WHERE
    FP.migration_status!='Done';
-- ## Project ##
SELECT count(*) FROM `proptiger`.`PROJECT_PLAN_IMAGES` WHERE STATUS='1' AND migration_status!='Done';
