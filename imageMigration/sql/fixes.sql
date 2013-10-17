-- # Bug in CMS that adds project plan images with blank plan type
UPDATE PROJECT_PLAN_IMAGES SET PLAN_TYPE = 'Location Plan' WHERE STATUS='1' AND migration_status='' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%loc-plan%';
UPDATE PROJECT_PLAN_IMAGES SET PLAN_TYPE = 'Layout Plan' WHERE STATUS='1' AND migration_status='' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%layout-plan%';
UPDATE PROJECT_PLAN_IMAGES SET PLAN_TYPE = 'Site Plan' WHERE STATUS='1' AND migration_status='' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%site-plan%';
UPDATE PROJECT_PLAN_IMAGES SET PLAN_TYPE = 'Master Plan' WHERE STATUS='1' AND migration_status='' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%master-plan%';
UPDATE PROJECT_PLAN_IMAGES SET PLAN_TYPE = 'Project Image' WHERE STATUS='1' AND migration_status='' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%large%';
UPDATE PROJECT_PLAN_IMAGES SET PLAN_TYPE = 'Cluster Plan' WHERE STATUS='1' AND migration_status='' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%cluster-plan%';
UPDATE PROJECT_PLAN_IMAGES SET PLAN_TYPE = 'Construction Status' WHERE STATUS='1' AND migration_status='' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%const-status%';
UPDATE PROJECT_PLAN_IMAGES SET PLAN_TYPE = 'Payment Plan' WHERE STATUS='1' AND migration_status='' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%payment-plan%';
UPDATE PROJECT_PLAN_IMAGES SET PLAN_TYPE = 'Specification' WHERE STATUS='1' AND migration_status='' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%specification%';
UPDATE PROJECT_PLAN_IMAGES SET PLAN_TYPE = 'Price List' WHERE STATUS='1' AND migration_status='' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%price-list%';
UPDATE PROJECT_PLAN_IMAGES SET PLAN_TYPE = 'Application Form' WHERE STATUS='1' AND migration_status='' AND PLAN_TYPE='' AND PLAN_IMAGE LIKE '%app-form%';
