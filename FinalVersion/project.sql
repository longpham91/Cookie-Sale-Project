.mode column
.headers ON
--Testing
create table customer (cust_id integer, cust_name varchar(200), address varchar(250), phone bigint, email varchar(50), total_boxes int, total_price numeric(12,2), primary key(cust_id asc));

create table c_order (order_id integer, cust_id int, scout_id int, type varchar(50), amount int, paid int, picked_up int, primary key(order_id asc), foreign key(cust_id) references customer(cust_id), foreign key(scout_id) references scout(scout_id), foreign key(type) references cookie(type));

create table scout (scout_id integer, scout_name varchar(200), scout_address varchar(250), age int, primary key(scout_id asc));

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