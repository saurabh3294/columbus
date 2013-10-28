-- # Fixes for a bug in CMS that adds project plan images with blank plan type

-- # Fixes for project database
UPDATE `project`.`project_plan_images` SET PLAN_TYPE = 'Location Plan' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%loc-plan%';
UPDATE `project`.`project_plan_images` SET PLAN_TYPE = 'Layout Plan' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%layout-plan%';
UPDATE `project`.`project_plan_images` SET PLAN_TYPE = 'Site Plan' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%site-plan%';
UPDATE `project`.`project_plan_images` SET PLAN_TYPE = 'Master Plan' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%master-plan%';
UPDATE `project`.`project_plan_images` SET PLAN_TYPE = 'Project Image' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%large%';
UPDATE `project`.`project_plan_images` SET PLAN_TYPE = 'Cluster Plan' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%cluster-plan%';
UPDATE `project`.`project_plan_images` SET PLAN_TYPE = 'Construction Status' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%const-status%';
UPDATE `project`.`project_plan_images` SET PLAN_TYPE = 'Payment Plan' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%payment-plan%';
UPDATE `project`.`project_plan_images` SET PLAN_TYPE = 'Specification' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%specification%';
UPDATE `project`.`project_plan_images` SET PLAN_TYPE = 'Price List' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%price-list%';
UPDATE `project`.`project_plan_images` SET PLAN_TYPE = 'Application Form' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%app-form%';


-- # Fixes for proptiger database
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES` SET PLAN_TYPE = 'Location Plan' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%loc-plan%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES` SET PLAN_TYPE = 'Layout Plan' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%layout-plan%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES` SET PLAN_TYPE = 'Site Plan' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%site-plan%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES` SET PLAN_TYPE = 'Master Plan' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%master-plan%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES` SET PLAN_TYPE = 'Project Image' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%large%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES` SET PLAN_TYPE = 'Cluster Plan' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%cluster-plan%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES` SET PLAN_TYPE = 'Construction Status' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%const-status%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES` SET PLAN_TYPE = 'Payment Plan' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%payment-plan%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES` SET PLAN_TYPE = 'Specification' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%specification%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES` SET PLAN_TYPE = 'Price List' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%price-list%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES` SET PLAN_TYPE = 'Application Form' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%app-form%';

UPDATE `proptiger`.`PROJECT_PLAN_IMAGES_MIGRATION` SET PLAN_TYPE = 'Location Plan' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%loc-plan%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES_MIGRATION` SET PLAN_TYPE = 'Layout Plan' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%layout-plan%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES_MIGRATION` SET PLAN_TYPE = 'Site Plan' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%site-plan%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES_MIGRATION` SET PLAN_TYPE = 'Master Plan' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%master-plan%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES_MIGRATION` SET PLAN_TYPE = 'Project Image' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%large%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES_MIGRATION` SET PLAN_TYPE = 'Cluster Plan' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%cluster-plan%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES_MIGRATION` SET PLAN_TYPE = 'Construction Status' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%const-status%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES_MIGRATION` SET PLAN_TYPE = 'Payment Plan' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%payment-plan%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES_MIGRATION` SET PLAN_TYPE = 'Specification' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%specification%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES_MIGRATION` SET PLAN_TYPE = 'Price List' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%price-list%';
UPDATE `proptiger`.`PROJECT_PLAN_IMAGES_MIGRATION` SET PLAN_TYPE = 'Application Form' WHERE STATUS='1' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%app-form%';
