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
