create table portfolio_listings(
id int not null auto_increment,
type_id int not null,
user_id bigint not null,
tower int,
unit_no varchar(255),
floor_no int,
purchased_date date,
property_name varchar(255),
base_price double not null,
total_price double not null,
goal_amount double,
purchased_for enum('SELF','INVESTMENT','OTHERS'),
payment_plan enum('DP','CLP','FP'),
loan_status enum('AVAILED','NOT_AVAILED','PAID'),
loan_amount double,
loan_availed_amount double,
transaction_type enum('PRIMARY','RESALE'),
created_at datetime not null,
updated_at datetime not null,
foreign key fk_project_type (type_id) references RESI_PROJECT_TYPES(TYPE_ID),
foreign key fk_user (user_id) references FORUM_USER(USER_ID),
primary key(id)
);


create table portfolio_listings_price(
id int not null auto_increment,
portfolio_listings_id int,
amount double not null,
component_name varchar(255),
created_at datetime not null,
updated_at datetime not null,
foreign key fk_portfolio_listings (portfolio_listings_id) references portfolio_listings(id),
primary key(id)
);

create table portfolio_listings_payment_plan (
id int not null auto_increment,
portfolio_listings_id int not null,
installment_no int,
amount double not null,
due_date datetime,
is_paid enum('TRUE', 'FALSE'),
payment_source enum ('SELF', 'EXTERNAL'),
payment_date datetime,
installment_name varchar(255),
component_name varchar(255),
component_value float,
created_at datetime not null,
updated_at datetime not null,
foreign key fk_portfolio_listings (portfolio_listings_id) references portfolio_listings (id),
primary key (id)
);