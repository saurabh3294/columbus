-- Truncate Image Table
truncate table `proptiger`.`Image`;

-- Reset SERVICE_IMAGE_ID's
UPDATE `project`.`bank_list` SET SERVICE_IMAGE_ID = NULL;
UPDATE `project`.`resi_builder` SET SERVICE_IMAGE_ID = NULL;
UPDATE `project`.`locality_image` SET SERVICE_IMAGE_ID = NULL;
UPDATE `project`.`resi_floor_plans` SET SERVICE_IMAGE_ID = NULL;
UPDATE `project`.`project_plan_images` SET SERVICE_IMAGE_ID = NULL;

-- Reset Migration statuses
UPDATE `proptiger`.`BANK_LIST` SET migration_status = '';
UPDATE `proptiger`.`RESI_BUILDER` SET migration_status = '';
UPDATE `proptiger`.`LOCALITY_IMAGE` SET migration_status = '';
UPDATE `proptiger`.`RESI_FLOOR_PLANS` SET migration_status = '';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES` SET migration_status = '';

UPDATE `proptiger`.`BANK_LIST_MIGRATION` SET migration_status = '';
UPDATE `proptiger`.`RESI_BUILDER_MIGRATION` SET migration_status = '';
UPDATE `proptiger`.`LOCALITY_IMAGE_MIGRATION` SET migration_status = '';
UPDATE `proptiger`.`RESI_FLOOR_PLANS_MIGRATION` SET migration_status = '';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES_MIGRATION` SET migration_status = '';
