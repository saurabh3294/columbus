-----------------------------------------------
-- test locality insert 
insert into cms.locality values(0, 10051, 'locality', null, 'Active', 'hi', 1, 23.2, 74.4, null, null, null, null, false, 506, now(), now(), null, null, null);

-- test suburb insert
insert into cms.suburb values(0,20, 'suburb', null, 'Active', '', 1, 23.4, 74.4, 560, now(), now(), 1, null);

-- test city insert
insert into cms.city values(0, 'city', 'Active', null, 1, '', null, null, null, null, null, null, 1, 1, 560, now(), now(), 1, 1, 1, 1, null, now());

-- test builder insert
INSERT INTO cms.`resi_builder` VALUES (0,'Ramprastha1','Ramprastha1','/ramprastha/ramprastha.jpg','description','','ramprastha-100001',3,'114, Sec-44, Gurgaon\r\nHaryana-122002\r\nTel.: 0124 - 4333444\r\nFax: 0124 - 4333433',11,122002,'0000-00-00','Balwant Singh',0,0,0,9,'www.rampastha.com',0,0,506,'2013-11-13 23:33:40','2013-11-13 18:03:40',28,NULL,0);

-- test resi_project insert
insert into cms.resi_project_ids values(0);
INSERT INTO cms.`resi_project` VALUES (0,last_insert_id(),'Website',100002,51419,'Express Greens','DLF Express Greens located at sector 1 MA. DLF Express Greens offers 3 and 4 bedroom apartment at very affordable prices. It is just 300 Meters from NH-8, 1Km from Proposed Metro station and ISBT.','Sector 1, Manesar, Gurgaon.','/dlf/express-greens/dlf-expressgreens-1-small.jpg',28.35436821,76.94712067,999,'Active',1,'','Above mentioned area is Super built-up area. All prices mentioned above are approximate. PLC, Car Parking, Maintenance, Club charges etc as applicable.','0000-00-00','2008-06-01','http://www.dlf.in/dlf/wcm/connect/residential/homes/residential/projects/premium+homes/express+green',999,999,'vzn3JwNgg2Y','','',32,2,1,'',15,0,'2014-12-01','Residential',NULL,0,7,1,28,2,857466,'0000-00-00','OPTIONS_DESC: 2BHK, 3BHK, 4BHK  LOCATION_DESC: Located in Sector 1, Manesar, Gurgaon.  FEATURED: 1  PROJECT_REMARK: tower detail according to elevation.19floor/tower,4apat/floor,total tower 15,cluster plan & specification N/A....in this project 2, 3 & 4 BHK Apartments; Independent Floors and Town Houses.shahbaz khan....  REASON_UNLAUNCHED_UNITS:','2013-08-12',5250,'2013-02-01 00:00:00',0,558,'2014-02-10 16:28:01','2014-11-10 19:28:12',NULL,7.2,5.8,7.8,0);
----------------------------------------------

CREATE  TABLE IF NOT EXISTS `notification`.`event_type_to_subscriber_mapping` (
  `id` INT NOT NULL ,
    `event_type_id` INT NOT NULL ,
      `subscriber_id` VARCHAR(45) NOT NULL ,
        `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
          PRIMARY KEY (`id`) )
ENGINE = InnoDB;
alter table subscriber add column last_event_generated_id int ;
insert into subscriber values(0, 'Seo', null, null);
insert into subscriber_config values (0, 2, 'MaxVerifedEventCount', 10);

insert into event_type_to_subscriber_mapping values (0, 5, 2, now()),(0, 6, 2, now()),(0, 7, 2, now()),(0, 8, 2, now()),(0, 9, 2, now()),(0, 10, 2, now()),(0, 11, 2, now()),(0, 12, 2, now()),(0, 13, 2, now()),(0, 14, 2, now()),(0, 15, 2, now()),(0, 16, 2, now()),(0, 17, 2, now()),(0, 18, 2, now()),(0, 19, 2, now()),(0, 20, 2, now()),(0, 21, 2, now()),(0, 22, 2, now());

CREATE  TABLE IF NOT EXISTS `seodb`.`url_property_types` (
  `id` INT NOT NULL AUTO_INCREMENT ,
    `url_property_category_id` INT NOT NULL ,
      `name` VARCHAR(45) NULL ,
        `url_sub_part` VARCHAR(255) NULL ,
          PRIMARY KEY (`id`) ,
            INDEX `fk_url_property_types_1` (`url_property_category_id` ASC) ,
              CONSTRAINT `fk_url_property_types_1`
                  FOREIGN KEY (`url_property_category_id` )
                      REFERENCES `seodb`.`url_property_type_category` (`id` )
                          ON DELETE NO ACTION
                              ON UPDATE NO ACTION)
ENGINE = InnoDB;
CREATE  TABLE IF NOT EXISTS `seodb`.`url_property_type_category` (
  `id` INT NOT NULL AUTO_INCREMENT ,
    `name` VARCHAR(128) NULL ,
      PRIMARY KEY (`id`) )
ENGINE = InnoDB;
CREATE  TABLE IF NOT EXISTS `seodb`.`url_categories` (
  `id` INT NOT NULL AUTO_INCREMENT ,
    `name` VARCHAR(255) NOT NULL ,
      `content_call` VARCHAR(1000) NOT NULL ,
        `object_type_id` INT NOT NULL ,
          `redirection_parent_url_category_id` INT NOT NULL ,
            PRIMARY KEY (`id`) ,
              UNIQUE INDEX `url_type` () )
ENGINE = InnoDB;
CREATE  TABLE IF NOT EXISTS `seodb`.`seo_urls` (
  `id` INT NOT NULL AUTO_INCREMENT ,
    `url` VARCHAR(255) NOT NULL ,
      `url_type_id` INT NOT NULL ,
        `object_id` INT NOT NULL ,
          `status` ENUM('Active', 'Deleted') NULL ,
            `number_of_results` INT NULL ,
              `urlInfo` ENUM('New', 'ContentChange') NULL ,
                PRIMARY KEY (`id`) ,
                  UNIQUE INDEX `url` (`url` ASC) )
ENGINE = InnoDB;

alter table event_type change column is_mergeable strategy enum('MERGE', 'SUPPRESS', 'NO_STRATEGY') default 'NO_STRATEGY';
update event_type set strategy = 'SUPPRESS' where id in (1,2,3);

insert into notification.raw_event_table_details values(3, 'localhost', 'cms', '_t_resi_project', 'PROJECT_ID', '_t_transaction_id', NULL, '_t_transaction_date', '{"version":["website"]}', NULL, now());
insert into notification.raw_event_table_details values(4, 'localhost', 'cms', '_t_resi_builder', 'BUILDER_ID', '_t_transaction_id', NULL, '_t_transaction_date', null, NULL, now());
insert into notification.raw_event_table_details values(5, 'localhost', 'cms', '_t_locality', 'LOCALITY_ID', '_t_transaction_id', NULL, '_t_transaction_date', null, NULL, now());
insert into notification.raw_event_table_details values(6, 'localhost', 'cms', '_t_suburb', 'SUBURB_ID', '_t_transaction_id', NULL, '_t_transaction_date', null, NULL, now());
insert into notification.raw_event_table_details values(7, 'localhost', 'cms', '_t_city', 'CITY_ID', '_t_transaction_id', NULL, '_t_transaction_date', null, NULL, now());
insert into notification.raw_event_table_details values(8, 'localhost', 'cms', '_t_resi_project_options', 'OPTIONS_ID', '_t_transaction_id', NULL, '_t_transaction_date', '{"option_category":["Actual"]}', NULL, now());
insert into notification.raw_event_table_details values(9, 'localhost', 'cms', '_t_resi_project', 'PROJECT_ID', '_t_transaction_id', NULL, '_t_transaction_date', '{"version":["Website"],"status":["Active"],"residential_flag":["Residential"]}', NULL, now());

insert into notification.event_type values(5, 'locality_url_generation', 'NO_STRATEGY', 0, 0, 0, NULL, NULL);
insert into notification.event_type values(6, 'suburb_url_generation', 'NO_STRATEGY', 0, 0, 0, NULL, 'locality_url_generation');
insert into notification.event_type values(7, 'city_url_generation', 'NO_STRATEGY', 0, 0, 0, NULL, 'locality_url_generation');
insert into notification.event_type values(8, 'builder_url_generation', 'NO_STRATEGY', 0, 0, 0, NULL, NULL);
insert into notification.event_type values(9, 'project_url_generation', 'SUPPRESS', 0, 0, 0, NULL, NULL);
insert into notification.event_type values(10, 'property_url_generation', 'SUPPRESS', 0, 0, 0, NULL, NULL);

insert into notification.event_type values(11, 'locality_url_delete', 'NO_STRATEGY', 0, 0, 0, NULL, NULL);
insert into notification.event_type values(12, 'suburb_url_delete', 'NO_STRATEGY', 0, 0, 0, NULL, NULL);
insert into notification.event_type values(13, 'city_url_delete', 'NO_STRATEGY', 0, 0, 0, NULL, NULL);
insert into notification.event_type values(14, 'builder_url_delete', 'NO_STRATEGY', 0, 0, 0, NULL, NULL);
insert into notification.event_type values(15, 'project_url_delete', 'SUPPRESS', 0, 0, 0, NULL, NULL);
insert into notification.event_type values(16, 'property_url_delete', 'SUPPRESS', 0, 0, 0, NULL, NULL);

insert into notification.event_type values(17, 'locality_url_content_change', 'MERGE', 1, 0, 0, NULL, NULL);
insert into notification.event_type values(18, 'suburb_url_content_change', 'MERGE', 1, 0, 0, NULL, NULL);
insert into notification.event_type values(19, 'city_url_content_change', 'MERGE', 1, 0, 0, NULL, NULL);
insert into notification.event_type values(20, 'builder_url_content_change', 'MERGE', 1, 0, 0, NULL, NULL);
insert into notification.event_type values(21, 'project_url_content_change', 'MERGE', 1, 0, 0, NULL, NULL);
insert into notification.event_type values(22, 'property_url_content_change', 'MERGE', 1, 0, 0, NULL, NULL);

/*
-- handle it at the seo subscriber level.
--insert into notification.event_type values(23, 'locality_url_content_count', 0, 0, 0, 0, NULL, NULL);
--insert into notification.event_type values(24, 'suburb_url_content_count', 0, 0, 0, 0, NULL, NULL);
--insert into notification.event_type values(25, 'city_url_content_count', 0, 0, 0, 0, NULL, NULL);
--insert into notification.event_type values(26, 'builder_url_content_count', 0, 0, 0, 0, NULL, NULL);
--insert into notification.event_type values(27, 'project_url_content_count', 0, 0, 0, 0, NULL, NULL);
--insert into notification.event_type values(28, 'property_url_content_count', 0, 0, 0, 0, NULL, NULL);
*/

insert into notification.event_type values(29, 'locality_url_insertion', 'NO_STRATEGY', 0, 1, 0, NULL, 'locality_url_generation');
insert into notification.event_type values(30, 'suburb_url_insertion', 'NO_STRATEGY', 0, 1, 0, NULL,'locality_url_generation' );
insert into notification.event_type values(31, 'city_url_insertion', 'NO_STRATEGY', 0, 1, 0, NULL, 'locality_url_generation' );
insert into notification.event_type values(32, 'builder_url_insertion', 'NO_STRATEGY', 0, 1, 0, NULL, 'builder_url_generation');
insert into notification.event_type values(33, 'project_url_insertion', 'NO_STRATEGY', 0, 1, 0, NULL, 'project_url_generation');
-------------------- URL GENERATION ---------------------------------------------------

---- addition of events for project_url_generation.
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'version', 9, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'STATUS', 9, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'RESIDENTIAL_FLAG', 9, 3, now());

---- addition of events for project_url_insertion.
insert into notification.raw_event_to_event_type_mapping values(0, 'INSERT', 'All', 33, 3, now());

--- addition of events for locality_url_generation
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'STATUS', 5, 5, now());

--- addition of events for locality_url_insertion
insert into notification.raw_event_to_event_type_mapping values(0, 'INSERT', 'All', 29, 5, now());

--- addition of events for suburb_url_generation
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'STATUS', 6, 6, now());

--- addition of events for suburb_url_insertion
insert into notification.raw_event_to_event_type_mapping values(0, 'INSERT', 'All', 30, 6, now());

--- addition of events for city_url_generation
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'STATUS', 7, 7, now());

--- addition of events for city_url_insertion
insert into notification.raw_event_to_event_type_mapping values(0, 'INSERT', 'All', 31, 7, now());

--- addition of events for builder_url_generation
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'builder_status', 8, 4, now());

--- addition of events for builder_url_insertion
insert into notification.raw_event_to_event_type_mapping values(0, 'INSERT', 'All', 32, 4, now());

--- addition of events for property_url_generation
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'OPTION_CATEGORY', 10, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'OPTION_TYPE', 10, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'INSERT', 'All', 10, 8, now());

-------------------- URL DELETION ---------------------------------------------------

--- deletion of events for project_url_delete.
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'version', 15, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'STATUS', 15, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'RESIDENTIAL_FLAG', 15, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'DELETE', NULL, 15, 3, now());

--- deletion of events for locality_url_delete
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'STATUS', 11, 5, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'DELETE', NULL, 11, 5, now());

--- deletion of events for suburb_url_delete
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'STATUS', 12, 6, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'DELETE', NULL, 12, 6, now());

--- deletion of events for city_url_delete
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'STATUS', 13, 7, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'DELETE', NULL, 13, 7, now());

--- deletion of events for builder_url_delete
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'builder_status', 14, 4, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'DELETE', NULL, 14, 4, now());

--- deletion of events for property_url_delete
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'OPTION_CATEGORY', 16, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'OPTION_TYPE', 16, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'DELETE', NULL, 16, 8, now());

-------------------- URL CONTENT CHANGE ---------------------------------------------------

--- events for project_url_content_change.
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'BUILDER_ID', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'LOCALITY_ID', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'PROJECT_NAME', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'PROJECT_DESCRIPTION', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'PROJECT_ADDRESS', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'LATITUDE', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'LONGITUDE', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'DISPLAY_ORDER', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'PROJECT_STATUS_ID', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'PROJECT_URL', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'PRE_LAUNCH_DATE', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'LAUNCH_DATE', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'DISPLAY_ORDER_LOCALITY', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'DISPLAY_ORDER_SUBURB', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'PROJECT_SIZE', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'PROJECT_TYPE_ID', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'PROMISED_COMPLETION_DATE', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'SAFETY_SCORE', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'LIVABILITY_SCORE', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'PROJECT_LOCALITY_SCORE', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'PROJECT_SOCIETY_SCORE', 21, 3, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'INSERT', NULL, 21, 3, now());

--- events for locality_url_content_change
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'LABEL', 17, 5, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'DESCRIPTION', 17, 5, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'PRIORITY', 17, 5, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'LATITUDE', 17, 5, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'LONGITUDE', 17, 5, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'SAFETY_SCORE', 17, 5, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'LIVABILITY_SCORE', 17, 5, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'TAG_LINE', 17, 5, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'URL', 17, 5, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'INSERT', NULL, 17, 5, now());

---  events for suburb_url_content_change
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'LABEL', 18, 6, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'DESCRIPTION', 18, 6, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'PRIORITY', 18, 6, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'LATITUDE', 18, 6, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'LONGITUDE', 18, 6, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'SUBURB_TAG_LINE', 18, 6, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'URL', 18, 6, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'INSERT', NULL, 18, 6, now());

--- events for city_url_content_change
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'LABEL', 19, 7, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'DESCRIPTION', 19, 7, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'DISPLAY_ORDER', 19, 7, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'DISPLAY_PRIORITY', 19, 7, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'CENTER_LATITUDE', 19, 7, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'CENTER_LONGITUDE', 19, 7, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'CITY_TAG_LINE', 19, 7, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'URL', 19, 7, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'CITY_POPULATION', 19, 7, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'POPULATION_SURVEY_DATE', 19, 7, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'MIN_PRICE_PER_UNIT', 19, 7)
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'MAX_PRICE_PER_UNIT', 19, 7, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'INSERT', NULL, 19, 7, now());

--- events for builder_url_content_change
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'BUILDER_NAME', 20, 4, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'DESCRIPTION', 20, 4, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'URL', 20, 4, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'DISPLAY_ORDER', 20, 4, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'ADDRESS', 20, 4, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'CITY_ID', 20, 4, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'ESTABLISHED_DATE', 20, 4, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'WEBSITE', 20, 4, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'ESTABLISHED_DATE', 20, 4, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'INSERT', NULL, 20, 4, now());

--- events for property_url_content_change
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'OPTION_NAME', 22, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'BEDROOMS', 22, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'BATHROOMS', 22, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'STUDY_ROOM', 22, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'SERVANT_ROOM', 22, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'BALCONY', 22, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'POOJA_ROOM', 22, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'SIZE', 22, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'DISPLAY_CARPET_AREA', 22, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'VILLA_PLOT_AREA', 22, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'VILLA_NO_FLOORS', 22, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'VILLA_TERRACE_AREA', 22, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'VILLA_GARDEN_AREA', 22, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'CARPET_AREA', 22, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'LENGTH_OF_PLOT', 22, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'BREADTH_OF_PLOT', 22, 8, now());
insert into notification.raw_event_to_event_type_mapping values(0, 'INSERT', NULL, 22, 8, now());

/*
-------------------- URL CONTENT COUNT ---------------------------------------------------
-- handle it at the seo subscriber level.
----- events for project_url_content_count.
--insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'version', 27, 3, now());
--insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'STATUS', 27, 3, now());
--insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'RESIDENTIAL_FLAG', 27, 3, now());
--insert into notification.raw_event_to_event_type_mapping values(0, 'DELETE', NULL, 27, 3, now());
--insert into notification.raw_event_to_event_type_mapping values(0, 'INSERT', NULL, 27, 3, now());
--
----- events for locality_url_content_count
--insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'STATUS', 23, 5, now());
--insert into notification.raw_event_to_event_type_mapping values(0, 'DELETE', NULL, 23, 5, now());
--insert into notification.raw_event_to_event_type_mapping values(0, 'INSERT', NULL, 23, 5, now());
--
-----  events for suburb_url_content_count
--insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'STATUS', 24, 6, now());
--insert into notification.raw_event_to_event_type_mapping values(0, 'DELETE', NULL, 24, 6, now());
--insert into notification.raw_event_to_event_type_mapping values(0, 'INSERT', NULL, 24, 6, now());
--
----- events for city_url_content_count
--insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'STATUS', 25, 7, now());
--insert into notification.raw_event_to_event_type_mapping values(0, 'DELETE', NULL, 25, 7, now());
--insert into notification.raw_event_to_event_type_mapping values(0, 'INSERT', NULL, 25, 7, now());
--
----- deletion of events for builder_url_delete
--insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'STATUS', 26, 4, now());
--insert into notification.raw_event_to_event_type_mapping values(0, 'DELETE', NULL, 26, 4, now());
--insert into notification.raw_event_to_event_type_mapping values(0, 'INSERT', NULL, 26, 4, now());
--
----- deletion of events for property_url_delete
--insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'OPTION_CATEGORY', 28, 8, now());
--insert into notification.raw_event_to_event_type_mapping values(0, 'UPDATE', 'OPTION_TYPE', 28, 8, now());
--insert into notification.raw_event_to_event_type_mapping values(0, 'DELETE', NULL, 28, 8, now());
--insert into notification.raw_event_to_event_type_mapping values(0, 'INSERT', NULL, 28, 8, now());
*/
