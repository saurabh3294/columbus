CREATE TABLE IF NOT exists dashboards(
id INT NOT NULL auto_increment,
name varchar(255) NOT NULL,
total_row tinyint,
total_column tinyint,
user_id bigint,
created_at DATETIME,
updated_at DATETIME,
PRIMARY KEY (id),
FOREIGN KEY (user_id) references FORUM_USER(USER_ID),
INDEX(user_id)
);

CREATE TABLE IF NOT exists widgets(
id INT NOT NULL auto_increment,
name varchar(255),
tag varchar(255),
content text NOT null,
type enum('DEFAULT', 'USER_DEFINED'),
created_at DATETIME,
updated_at DATETIME,
PRIMARY KEY (id)
);

create table dashboard_widget_mapping(
id INT NOT null auto_increment,
dashboard_id INT not null,
widget_id INT not null,
widget_row_position tinyint,
widget_column_position tinyint,
status enum('MIN','MAX','HIDDEN'),
created_at DATETIME,
updated_at DATETIME,
PRIMARY KEY (id),
FOREIGN KEY (dashboard_id) references dashboards(id),
FOREIGN KEY (widget_id) references widgets(id)
);


INSERT INTO `proptiger`.`dashboards` (`id`, `name`, `total_row`, `total_column`, `user_id`, `created_at`, `updated_at`) 
VALUES (4, 'portfolio', 2, 1, 7, '2013-10-03 00:00:00', '2013-10-03 00:00:00');

INSERT INTO `proptiger`.`dashboards` (`id`, `name`, `total_row`, `total_column`, `user_id`, `created_at`, `updated_at`) 
VALUES (5, 'property', 5, 1, 7, '2013-10-03 00:00:00', '2013-10-03 00:00:00');


INSERT INTO `proptiger`.`widgets` (`id`, `name`, `tag`, `content`, `type`, `created_at`, `updated_at`) 
VALUES (1, 'Actual VS Current Price', 'pt-portfolioatglance', '{dataView:{type:\'ByTime\',interval:null,segment:null},dateRange:{startDate:\'\',endDate:\'\',grain:\'LastYear\'},displayInfo:{subtype:\'pie\',metricList:[\'CurrentPrice\'],series:[{data:[[\'Original\',90.0],{name:\'Appreciation\',y:23.0,sliced:true,selected:true}]}],entities:[\'CurrentPrice\']}}', 'DEFAULT', '2013-10-03 00:00:00', '2013-10-03 00:00:00');

INSERT INTO `proptiger`.`widgets` (`id`, `name`, `tag`, `content`, `type`, `created_at`, `updated_at`) 
VALUES (2, 'Price Trend', 'pt-propertylist', '{dataView:{type:\'ByTime\',interval:null,segment:null},dateRange:{startDate:\'\',endDate:\'\',grain:\'LastYear\'},displayInfo:{subtype:\'pie\',metricList:[\'CurrentPrice\'],series:[{data:[[\'Original\',90.0],{name:\'Appreciation\',y:23.0,sliced:true,selected:true}]}],entities:[\'CurrentPrice\']}}', 'DEFAULT', '2013-10-03 00:00:00', '2013-10-03 00:00:00');

INSERT INTO `proptiger`.`widgets` (`id`, `name`, `tag`, `content`, `type`, `created_at`, `updated_at`) 
VALUES (3, 'Property Detail', 'pt-propertydetail', '{dataView:{type:\'ByTime\',interval:null,segment:null},dateRange:{startDate:\'\',endDate:\'\',grain:\'LastYear\'},displayInfo:{subtype:\'pie\',metricList:[\'CurrentPrice\'],series:[{data:[[\'Original\',90.0],{name:\'Appreciation\',y:23.0,sliced:true,selected:true}]}],entities:[\'CurrentPrice\']}}', 'DEFAULT', '2013-10-03 00:00:00', '2013-10-03 00:00:00');

INSERT INTO `proptiger`.`dashboard_widget_mapping` 
(`id`, `dashboard_id`, `widget_id`, `widget_row_position`, `widget_column_position`, `status`, `created_at`, `updated_at`) 
VALUES (1, 4, 1, 2, 1, 'MAX', '2013-10-03 00:00:00', '2013-10-03 00:00:00');

INSERT INTO `proptiger`.`dashboard_widget_mapping` 
(`id`, `dashboard_id`, `widget_id`, `widget_row_position`, `widget_column_position`, `status`, `created_at`, `updated_at`) 
VALUES (2, 4, 2, 1, 1, 'MAX', '2013-10-03 00:00:00', '2013-10-03 00:00:00');
