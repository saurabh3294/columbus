-- Truncate Image Table
truncate table Image;

-- Reset Migration statuses
UPDATE `proptiger`.`BANK_LIST` SET migration_status = '';
UPDATE `proptiger`.`RESI_BUILDER` SET migration_status = '';
UPDATE `proptiger`.`LOCALITY_IMAGE` SET migration_status = '';
UPDATE `proptiger`.`RESI_FLOOR_PLANS` SET migration_status = '';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES` SET migration_status = '';
