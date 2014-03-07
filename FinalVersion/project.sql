.mode column
.headers ON
--Testing
create table customer (cust_id integer, cust_name varchar(200), address varchar(250), phone bigint, email varchar(50), total_boxes int, total_price numeric(12,2), primary key(cust_id asc));

insert into customer(cust_name, address, phone, email, total_boxes, total_price) values ("Long Pham", "906 W Wabash Ave", 9783190044, "long@test.com", 10, 40.00);
insert into customer(cust_name, address, phone, email, total_boxes, total_price) values ("Rudy Duarte", "301 W Wabash Ave", 1234567777, "rudy@test.com", 20, 100.00);
insert into customer(cust_name, address, phone, email, total_boxes, total_price) values ("Albert Li", "302 W Wabash Ave", 1112223333, "albert@test.com", 5, 20.00);
insert into customer(cust_name, address, phone, email, total_boxes, total_price) values ("Jack Yuan", "123 W Wabash Ave", 2223334444, "jack@test.com", 5, 20.00);
insert into customer(cust_name, address, phone, email, total_boxes, total_price) values ("Luke Walker", "900 W Wabash Ave", 5557779999, "luke@test.com", 20, 120.00);

create table c_order (order_id integer, cust_id int, scout_id int, type varchar(50), amount int, paid int, picked_up int, primary key(order_id asc), foreign key(cust_id) references customer(cust_id), foreign key(scout_id) references scout(scout_id), foreign key(type) references cookie(type));

create table scout (scout_id integer, scout_name varchar(200), scout_address varchar(250), age int, primary key(scout_id asc));

insert into scout(scout_name, scout_address, age) values ("Emily", "123 Main St", 11);
insert into scout(scout_name, scout_address, age) values ("Kate", "165 Grant Ave", 12);
insert into scout(scout_name, scout_address, age) values ("Jenny", "321 Jennison St", 10);
insert into scout(scout_name, scout_address, age) values ("Molly", "216 Pike St", 9);

create table cookie (type varchar(200), price float, primary key(type));

insert into cookie values ("Cookie Drop", 5.00);
insert into cookie values ("Savannah Smiles", 4.00);
insert into cookie values ("Trefoils", 4.00);
insert into cookie values ("DoSiDos", 4.00);
insert into cookie values ("Samoas", 4.00);
insert into cookie values ("Dulce", 4.00);
insert into cookie values ("Thank U BerryMunch", 4.00);
insert into cookie values ("Tagalongs", 4.00);
insert into cookie values ("Thin Mints", 4.00);