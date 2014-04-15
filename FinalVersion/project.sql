.mode column
.headers ON
--Testing


create table customer (cust_id integer auto_increment, cust_name varchar(200), address varchar(250), phone bigint, email varchar(50), total_boxes int, total_price float, primary key(cust_id asc));

insert into customer(cust_name, address, phone, email, total_boxes, total_price) values ("Long Pham", "906 W Wabash Ave", 9783190044, "long@test.com", 10, 40.00);
insert into customer(cust_name, address, phone, email, total_boxes, total_price) values ("Rudy Duarte", "301 W Wabash Ave", 1234567777, "rudy@test.com", 20, 100.00);
insert into customer(cust_name, address, phone, email, total_boxes, total_price) values ("Albert Li", "302 W Wabash Ave", 1112223333, "albert@test.com", 5, 20.00);
insert into customer(cust_name, address, phone, email, total_boxes, total_price) values ("Jack Yuan", "123 W Wabash Ave", 2223334444, "jack@test.com", 5, 20.00);
insert into customer(cust_name, address, phone, email, total_boxes, total_price) values ("Luke Walker", "900 W Wabash Ave", 5557779999, "luke@test.com", 20, 120.00);

create table c_order (order_id integer auto_increment, cust_id int, scout_id int, cookie_id int, amount int, paid boolean, picked_up boolean, primary key(order_id asc), foreign key(cust_id) references customer(cust_id), foreign key(scout_id) references scout(scout_id), foreign key(cookie_id) references cookie(cookie_id));

create table scout (scout_id integer auto_increment, scout_name varchar(200), password varchar(200), scout_address varchar(250), age int, primary key(scout_id asc));

insert into scout(scout_name, password, scout_address, age) values ("Emily", "cookiesale", "123 Main St", 11);
insert into scout(scout_name, password, scout_address, age) values ("Kate", "cookiesale", "165 Grant Ave", 12);
insert into scout(scout_name, password, scout_address, age) values ("Jenny", "cookiesale", "321 Jennison St", 10);
insert into scout(scout_name, password, scout_address, age) values ("Molly", "cookiesale", "216 Pike St", 9);

create table cookie (cookie_id int auto_increment, type varchar(200), price float, primary key(cookie_id));

insert into cookie (type, price) values ("Cookie Drop", 5.00);
insert into cookie (type, price) values ("Savannah Smiles", 4.00);
insert into cookie (type, price) values ("Trefoils", 4.00);
insert into cookie (type, price) values ("DoSiDos", 4.00);
insert into cookie (type, price) values ("Samoas", 4.00);
insert into cookie (type, price) values ("Dulce", 4.00);
insert into cookie (type, price) values ("Thank U BerryMunch", 4.00);
insert into cookie (type, price) values ("Tagalongs", 4.00);
insert into cookie (type, price) values ("Thin Mints", 4.00);